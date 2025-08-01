package modloader.konstruct;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

public class LoadFiles {

    public static List<File> loadBlueprintFiles() {
        File blueprintsDir = new File(System.getProperty("user.dir") + "/konstruct/blueprints");


        if (!blueprintsDir.exists()) {
            blueprintsDir.mkdirs();
        }

        File[] blueprintFiles = blueprintsDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".kbp");
            }
        });


        if (blueprintFiles != null) {
            Arrays.sort(blueprintFiles);
        }

        return Arrays.asList(blueprintFiles != null ? blueprintFiles : new File[0]);
    }
}
