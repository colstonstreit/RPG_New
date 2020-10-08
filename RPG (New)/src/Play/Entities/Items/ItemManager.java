package Play.Entities.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ItemManager {

	public static enum Items { MONEY, HEALTH, APPLE, ORANGE }

	private static ArrayList<ItemSlot> inventory = new ArrayList<ItemSlot>(); // ArrayList representing the inventory
	private static final int MAX_AMOUNT_PER_SLOT = 100; // The maximum number of items that can be inside a given slot
	private static final int MAX_NUM_SLOTS = 100; // The maximum number of slots the inventory can have
	public static boolean AUTOMATICALLY_SORT = false;

	private static final Comparator<ItemSlot> sorter = new Comparator<ItemSlot>() {

		public int compare(ItemSlot o1, ItemSlot o2) {
			if (o1.item.ordinal() > o2.item.ordinal()) return 1;
			else if (o1.item.ordinal() < o2.item.ordinal()) return -1;
			else return (o1.amount > o2.amount) ? -1 : (o1.amount < o2.amount) ? 1 : 0;
		}
	};

	/**
	 * This function attempts to insert the given item with the given amount to the inventory. It returns the number of items it could successfully add.
	 * 
	 * @param item   The Item ID of the number to be given.
	 * @param amount The amount of the item that should be given.
	 * @return The number of items successfully given to the player.
	 */
	public static int giveItem(Items item, int amount) {

		// Validate arguments
		if (item == null || amount <= 0) {
			System.out.println("Either the given item is null, or the amount is less than or equal to zero!");
			return 0;
		}

		// Loop through all of the existing slots to see if items can be inserted
		int size = inventory.size();
		for (int i = 0; i < size; i++) {
			ItemSlot slot = inventory.get(i);
			if (slot.item == item) {
				int remainingSpace = MAX_AMOUNT_PER_SLOT - slot.amount;
				if (remainingSpace >= amount) {
					slot.amount += amount;
					return amount;
				} else if (remainingSpace > 0) {
					slot.amount += remainingSpace;
					return remainingSpace + giveItem(item, amount - remainingSpace);
				}
			}
		}

		// Check to see if there's room to create a new slot, since an existing one hasn't been found
		if (size >= MAX_NUM_SLOTS) {
			System.out.println("Inventory full! Could not give item (" + item + ", " + amount + ").");
			return 0;
		} else {
			// If the amount exceeds the max amount of room per slot, fill a new slot and then try to fill another in a second call.
			// Otherwise, just add whatever amount to the slot.
			if (amount > MAX_AMOUNT_PER_SLOT) {
				ItemSlot slot = new ItemSlot(item, MAX_AMOUNT_PER_SLOT);
				inventory.add(slot);
				if (AUTOMATICALLY_SORT) sort();
				return MAX_AMOUNT_PER_SLOT + giveItem(item, amount - MAX_AMOUNT_PER_SLOT);
			} else {
				ItemSlot slot = new ItemSlot(item, amount);
				inventory.add(slot);
				if (AUTOMATICALLY_SORT) sort();
				return amount;
			}
		}
	}

	/**
	 * This function checks to see if the inventory currently contains a given amount of a given item. It returns true if it does, and false if it doesn't.
	 * 
	 * @param item   The item to look for.
	 * @param amount The requested amount.
	 * @return True if contains this amount of this item, and false if not. Also returns false if item or amount parameters are invalid.
	 */
	public static boolean hasItem(Items item, int amount) {

		// Validate arguments
		if (item == null || amount < 1) {
			System.out.println("Either the item is null, or the amount is less than 1!");
			return false;
		}

		// Search through slots and count how many items of the given type are present.
		int numMatchingItems = 0;
		for (ItemSlot slot : inventory) {
			if (slot.item == item) numMatchingItems += slot.amount;
			if (numMatchingItems >= amount) return true;
		}

		return numMatchingItems >= amount;
	}

	/**
	 * Removes the given amount of the given item from the inventory, and returns the number of items removed. Returns 0 if either of the arguments are invalid
	 * or if the inventory does not contain enough items to be removed.
	 * 
	 * @param item   The item ID to be removed.
	 * @param amount The amount of the given item to be removed.
	 * @return The amount of items successfully removed.
	 */
	public static int takeItem(Items item, int amount) {

		// Validate arguments
		if (item == null || amount <= 0) {
			System.out.println("Either item is null, or the amount to remove is not less than 1!");
			return 0;
		} else if (!hasItem(item, amount)) {
			System.out.println("Inventory does not have enough items to remove the requested amount!");
			return 0;
		}

		// Search through slots in reverse order (so smaller stacks are removed first) and sequentially remove items.
		int numRemoved = 0;
		ArrayList<Integer> slotsToRemove = new ArrayList<Integer>();
		for (int i = inventory.size() - 1; i >= 0; i--) {
			ItemSlot slot = inventory.get(i);
			if (slot.item == item) {
				if (amount < slot.amount) {
					slot.amount -= amount;
					numRemoved += amount;
					amount -= amount;
				} else if (amount == slot.amount) {
					slotsToRemove.add(i);
					numRemoved += amount;
					amount -= amount;
				} else {
					slotsToRemove.add(i);
					numRemoved += slot.amount;
					amount -= slot.amount;
				}

				if (amount == 0) break;
			}
		}

		// Sort the list of indices of slots to be removed, then remove them in reverse order.
		Collections.sort(slotsToRemove);
		for (int i = slotsToRemove.size() - 1; i >= 0; i--) {
			inventory.remove(slotsToRemove.get(i).intValue());
		}

		if (AUTOMATICALLY_SORT) sort();
		return numRemoved;
	}

	/**
	 * Sorts the inventory in increasing order of ItemID, then in decreasing order of amount in the slot.
	 */
	public static void sort() { inventory.sort(sorter); }

	/**
	 * Prints out the contents of the inventory to the console.
	 */
	public static void printContents() {
		StringBuilder builder = new StringBuilder();
		builder.append("------Inventory Contents------\n");
		for (int i = 0, n = inventory.size(); i < n; i++) {
			ItemSlot slot = inventory.get(i);
			builder.append("(" + (i + 1) + ") Item: " + slot.item.name() + ", Amount: " + slot.amount + ".\n");
		}
		System.out.print(builder.toString());
	}

	private static class ItemSlot {

		Items item; // The ID of the item in this slot
		int amount; // How many of this item are in this slot

		public ItemSlot(Items item, int amount) {
			this.item = item;
			this.amount = amount;
		}
	}

}
