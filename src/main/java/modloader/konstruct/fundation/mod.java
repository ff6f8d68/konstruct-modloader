package modloader.konstruct.fundation;

public abstract class mod {
    protected ModInfo info;

    public mod(ModInfo info) {
        this.info = info;
    }

    public ModInfo getInfo() {
        return info;
    }

    public abstract void mainhook();
}