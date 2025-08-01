package modloader.konstruct;

import java.io.File;
import java.util.List;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class patchminecraft {

    public static void patchMinecraftJar(List<File> blueprintFiles) {

        String minecraftTargetDir = System.getProperty("user.dir") + "/minecraft/jar/";

        for (File blueprint : blueprintFiles) {
            System.out.println("Applying patch from: " + blueprint.getName());
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(blueprint))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (!entry.isDirectory()) {

                        File outFile = new File(minecraftTargetDir + entry.getName());
                        outFile.getParentFile().mkdirs();
                        try (FileOutputStream fos = new FileOutputStream(outFile)) {
                            byte[] buffer = new byte[4096];
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                        }
                        System.out.println("Patched: " + outFile.getAbsolutePath());
                    }
                    zis.closeEntry();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void applyPatchFromFile(File blueprintFile) {
        System.out.println("Applying patch from: " + blueprintFile.getName());

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(blueprintFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {

                    File outFile = new File(System.getProperty("user.dir") + "/konstruct/tmp/" + entry.getName());
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
