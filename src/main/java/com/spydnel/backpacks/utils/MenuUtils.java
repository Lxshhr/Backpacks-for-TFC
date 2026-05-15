package com.spydnel.backpacks.utils;

public class MenuUtils {
    /**
     * Overrides in case you want a custom layout for certain slots
     * Stores slotSize and size of the first row
     */
    private static final int[][] FIXED_LAYOUTS = new int[][]{
            {1, 1},
            {2, 1},
            {3, 1},
            {9, 3},
            {18, 3},
            {27, 3},
    };

    /**
     * Returns the slot layout as an int array where each entry slot count per row.
     * Makes it so slots an odd number of slot's don't look weird
     */
    public static int[] getLayout(int slotSize) {
        for (int[] fixedSlotLayout: FIXED_LAYOUTS) {
            if (fixedSlotLayout[0] == slotSize) {
                return buildRows(slotSize, slotSize / fixedSlotLayout[1]);
            }
        }

        if (slotSize <= 18) {
            if (slotSize % 2 == 0) {
                return buildRows(slotSize, slotSize / 2);
            }
            else {
                return buildRows(slotSize, (int) Math.ceil(slotSize / 2.0));
            }
        }

        if (slotSize <= 27) {
            if (slotSize % 3 == 0) {
                return buildRows(slotSize, slotSize / 3);
            }
            else {
                return buildRows(slotSize, (int)Math.ceil(slotSize / 3.0));
            }
        }

        return buildRows(slotSize, 9);
    }

    /**
     * Splits slotSize into rows.
     */
    private static int[] buildRows(int slotSize, int rowSize) {
        int numRows = (int) Math.ceil((double) slotSize / rowSize);
        int[] rows = new int[numRows];
        int remaining = slotSize;
        for (int i = 0; i < numRows; i++) {
            rows[i] = Math.min(rowSize, remaining);
            remaining -= rows[i];
        }
        return rows;
    }

    /**
     * Returns a centered y value for the first row
     */
    public static int getY(int rows) {
        return switch (rows) {
            case 1 -> 35;
            case 2 -> 26;
            default -> 17;
        };
    }

    /**
     * Returns a centered x value for the first column
     */
    public static int getCenteredX(int slotsInRow) {
        int centeredSlot = 79;
        int slots = ((slotsInRow * 18) - 18) / 2;
        return centeredSlot - slots;
    }


    public static int getRowCount(int slotSize) {
        return getLayout(slotSize).length;
    }

}
