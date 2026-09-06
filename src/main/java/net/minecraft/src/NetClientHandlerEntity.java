package net.minecraft.src;

public class NetClientHandlerEntity {
	public Class<? extends Entity> entityClass = null;
	public boolean entityHasOwner = false;

	public NetClientHandlerEntity(Class<? extends Entity> class1, boolean flag) {
		this.entityClass = class1;
		this.entityHasOwner = flag;
	}
}
