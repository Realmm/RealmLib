package net.jamesandrew.realmlib.inventory;

/**
 * Represents the size of a single {@link InventoryPanel}
 */
public enum InventorySize {

    ONE_ROW(9),
    TWO_ROWS(18),
    THREE_ROWS(27),
    FOUR_ROWS(36),
    FIVE_ROWS(45),
    SIX_ROWS(54);

    private final int size;

    InventorySize(int i) {
        this.size = i;
    }

    public int getSize() {
        return size;
    }

}
