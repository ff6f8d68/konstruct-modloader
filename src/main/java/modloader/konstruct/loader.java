package modloader.konstruct;

import modloader.konstruct.foundation.mod;
import modloader.konstruct.foundation.ModInfo;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class loader implements Runnable {

    private JFrame statusFrame;
    private JLabel statusLabel;

    public loader() {
        statusFrame = new JFrame("Konstruct Loader Status");
        statusLabel = new JLabel("Starting...", SwingConstants.CENTER);
        statusFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        statusFrame.setSize(400, 100);
        statusFrame.setLayout(new BorderLayout());
        statusFrame.add(statusLabel, BorderLayout.CENTER);
        statusFrame.setLocationRelativeTo(null);
        statusFrame.setVisible(true);
    }

    private void updateStatus(String status) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(status));
    }

    @Override
    public void run() {
        try {
            loadMods();
            updateStatus("Done!");
        } catch (Exception e) {
            updateStatus("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadMods() throws Exception {
        updateStatus("Creating/verifying folders...");
        List<File> blueprintFiles = LoadFiles.loadBlueprintFiles();

        updateStatus("Patching Minecraft...");
        patchminecraft.patchMinecraftJar(blueprintFiles);

        updateStatus("Reading mods...");
        File modlistFile = new File(System.getProperty("user.dir") + "/konstruct/modlist.txt");
        if (!modlistFile.exists()) {
            modlistFile.createNewFile();
        }

        try (FileWriter modlistWriter = new FileWriter(modlistFile, true)) {
            for (File blueprint : blueprintFiles) {
                updateStatus("Loading mod: " + blueprint.getName());
                mod modInstance = loadModInfoFromBlueprint(blueprint);
                if (modInstance != null) {
                    ModInfo info = modInstance.getInfo();
                    modlistWriter.write(String.format("%s - %s by %s (%s)\n",
                            info.title, info.description, info.author, info.version));
                    modInstance.mainhook();
                }
            }
        }
    }

    private mod loadModInfoFromBlueprint(File blueprintFile) {
        try {
            File tempDir = new File(System.getProperty("user.dir") + "/konstruct/tmp/" + blueprintFile.getName().replace(".kbp", ""));
            if (!tempDir.exists()) tempDir.mkdirs();

            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(blueprintFile))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (!entry.isDirectory()) {
                        File outFile = new File(tempDir, entry.getName());
                        outFile.getParentFile().mkdirs();
                        try (FileOutputStream fos = new FileOutputStream(outFile)) {
                            byte[] buffer = new byte[4096];
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                        }
                    }
                    zis.closeEntry();
                }
            }

            File[] files = tempDir.listFiles((dir, name) -> name.equals("main.class"));
            File mainClassFile = (files != null && files.length > 0) ? files[0] : null;

            if (mainClassFile == null) {
                mainClassFile = findMainClassFile(tempDir);
            }

            if (mainClassFile == null) {
                System.err.println("main.class not found in " + blueprintFile.getName());
                return null;
            }

            String className = getClassNameFromFile(tempDir, mainClassFile);

            URLClassLoader classLoader = new URLClassLoader(new URL[]{tempDir.toURI().toURL()});
            Class<?> clazz = classLoader.loadClass(className);

            Object instance = clazz.getDeclaredConstructor().newInstance();
            if (instance instanceof mod) {
                return (mod) instance;
            } else {
                System.err.println("Loaded class is not a mod: " + className);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private File findMainClassFile(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return null;
        for (File file : files) {
            if (file.isDirectory()) {
                File found = findMainClassFile(file);
                if (found != null) return found;
            } else if (file.getName().equals("main.class")) {
                return file;
            }
        }
        return null;
    }

    private String getClassNameFromFile(File root, File classFile) {
        String absRoot = root.getAbsolutePath();
        String absClass = classFile.getAbsolutePath();
        String relPath = absClass.substring(absRoot.length() + 1).replace(File.separatorChar, '.');
        return relPath.substring(0, relPath.length() - ".class".length());
    }
}