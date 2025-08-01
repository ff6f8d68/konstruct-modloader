package modloader.konstruct;

import modloader.konstruct.fundation.mod;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import modloader.konstruct.fundation.ModInfo;

public class loader implements Runnable {

    @Override
    public void run() {
        try {
            loadMods();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        public void loadMods() throws Exception {
            List<File> blueprintFiles = LoadFiles.loadBlueprintFiles();
            patchminecraft.patchMinecraftJar(blueprintFiles);

            File modlistFile = new File(System.getProperty("user.dir") + "/konstruct/modlist.txt");
            if (!modlistFile.exists()) {
                modlistFile.createNewFile();
            }

            try (FileWriter modlistWriter = new FileWriter(modlistFile, true)) {
                for (File blueprint : blueprintFiles) {
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
                System.err.println("main.class nicht gefunden in " + blueprintFile.getName());
                return null;
            }


            String className = getClassNameFromFile(tempDir, mainClassFile);


            URLClassLoader classLoader = new URLClassLoader(new URL[]{tempDir.toURI().toURL()});
            Class<?> clazz = classLoader.loadClass(className);

            Object instance = clazz.getDeclaredConstructor().newInstance();
            if (instance instanceof mod) {
                return (mod) instance;
            } else {
                System.err.println("Geladene Klasse ist kein mod: " + className);
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
