package Play;

import java.util.ArrayList;

public class LootTable<T> {

	private ArrayList<Row> table = new ArrayList<Row>(); // The table holding each row
	private double totalWeight = 0.0; // The total weight of all of the items

	/**
	 * Returns true if the given item is a part of this loot table, and false if it is not.
	 * 
	 * @param item The item to be checked for.
	 */
	public boolean contains(T item) {
		for (Row row : table) {
			if ((row.item == null && item == null) || ((item != null) && item.equals(row.item))) return true;
		}
		return false;
	}

	/**
	 * Adds the specified item to the table with the given weight, but only if the item is not already in the table.
	 * 
	 * @param item   The new item to be added to the table.
	 * @param weight The weight that the item should have. Must be greater than 0.
	 * @return This loot table.
	 */
	public LootTable<T> add(T item, double weight) {

		if (weight <= 0) {
			System.out.println("The weight of the item (" + item + ") must be greater than zero!");
			return this;
		}

		if (contains(item)) {
			System.out.println("This loot table already contains this item!");
			return this;
		}

		totalWeight += weight;
		table.add(new Row(item, weight));
		return this;
	}

	/**
	 * Adds a set of items to the loot table. If the length of the weights array is less than that of the items array, the weights will cycle through until the
	 * end of the loop. For example, if weights = {1, 2}, then for five items they will be assigned weights {1, 2, 1, 2, 1} respectively.
	 * 
	 * @param items   The array of items to be added.
	 * @param weights The array of weights of the corresponding items. Can be cycled around.
	 * @return This loot table.
	 */
	public LootTable<T> addSet(T[] items, double[] weights) {

		if (items.length >= weights.length) {
			for (int i = 0; i < items.length; i++) {
				add(items[i], weights[i % weights.length]);
			}
		} else {
			int min = items.length;
			for (int i = 0; i < min; i++) {
				add(items[i], weights[i]);
			}
			System.out.println("Only added the first " + min + " items! Your arrays should be of equal lengths!");
		}

		return this;
	}

	/**
	 * Sets the given item's weight in the loot table to the given weight. If the loot table does not contain this item, then it adds it to the table with the
	 * given weight.
	 * 
	 * @param item   The item whose weight should be set.
	 * @param weight The new weight that the item should have. Must be greater than 0.
	 * @return This loot table.
	 */
	public LootTable<T> set(T item, double weight) {

		if (weight <= 0) {
			System.out.println("The weight of the item (" + item + ") must be greater than zero!");
			return this;
		}

		if (contains(item)) {
			Row row = null;
			for (Row r : table) {
				if ((r.item == null && item == null) || (item != null && item.equals(r.item))) {
					row = r;
					break;
				}
			}
			totalWeight += (weight - row.weight);
			row.weight = weight;
		} else add(item, weight);
		return this;
	}

	/**
	 * Removes the specified item from the loot table.
	 * 
	 * @param item The item to be removed.
	 * @return This loot table.
	 */
	public LootTable<T> remove(T item) {

		if (!contains(item)) {
			System.out.println("This loot table does not contain the item (" + item + ")!");
			return this;
		}

		int index = -1;
		for (int i = 0, n = table.size(); i < n; i++) {
			Row r = table.get(i);
			if ((r.item == null && item == null) || (item != null && item.equals(r.item))) {
				totalWeight -= r.weight;
				index = i;
				break;
			}
		}

		table.remove(index);
		return this;
	}

	/**
	 * Returns an item from the loot table based on a random number generator and all of the weights of the items.
	 */
	public T get() {

		if (table.size() == 0) return null;

		double r = Math.random();
		double weightSoFar = 0.0;
		for (Row row : table) {
			weightSoFar += row.weight / totalWeight;
			if (r <= weightSoFar) return row.item;
		}

		return null;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		for (int i = 0, n = table.size(); i < n; i++) {
			Row row = table.get(i);
			if (i != n - 1) s.append("Row " + (i + 1) + " | Item: \"" + row.item + "\", Weight: " + row.weight + ".\n");
			else s.append("Row " + (i + 1) + " | Item: \"" + row.item + "\", Weight: " + row.weight + ".");
		}
		return s.toString();
	}

	private class Row {

		T item; // The item in this row
		double weight; // The weight of the item

		/**
		 * @param item   The item of the row.
		 * @param weight The weight of the item.
		 */
		public Row(T item, double weight) {
			this.item = item;
			this.weight = weight;
		}

	}

}
