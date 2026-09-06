package net.minecraft.src;


public class ItemTexture extends Item implements IItemTexture {
    public String texturePath;

    public ItemTexture(int id, String s) {
        super(id);
        texturePath = s;
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public String getTextureFile() {
        return texturePath;
    }
}
