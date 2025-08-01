package com.examplemod;

import modloader.konstruct.fundation.mod;
import modloader.konstruct.fundation.ModInfo;

public class main extends mod {

    public main() {
        super(new ModInfo(
                "Example Mod",
                "1.0.0",
                "Example Author",
                "An example mod for konstruct"
        ));
    }

    @Override
    public void mainhook() {
        System.out.println("Example mod loaded successfully!");
    }
}