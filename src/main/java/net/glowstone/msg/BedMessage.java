package net.glowstone.msg;

public class BedMessage extends Message {
    private final int entityId, x, y, z;
    private final boolean inBed;
    public BedMessage(int entityId, boolean inBed, int x, int y, int z) {
        this.entityId = entityId;
        this.inBed = inBed;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public int getEntityId() {
        return entityId;
    }
    
    public boolean getInBed() {
        return inBed;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "BedMessage{id=" + entityId + ",inBed=" + inBed + ",x=" + x + ",y=" + y + ",z=" + z + "}";
    }
}
