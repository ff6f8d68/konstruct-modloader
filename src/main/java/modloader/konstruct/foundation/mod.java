package modloader.konstruct.foundation;

public abstract class mod {
    protected ModInfo info;

    public mod(ModInfo info) {
        this.info = info;
    }

    public ModInfo getInfo() {
        return info;
    }
}