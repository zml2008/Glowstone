package net.glowstone.block.data;

public abstract class ToggleableAttachable extends Attachable {

    public ToggleableAttachable(int id, PlaceRequirement requirement) {
        super(id, requirement);
    }

    public abstract int toggleOpen(int existing);

    public abstract boolean isOpen(int existing);
    
}
