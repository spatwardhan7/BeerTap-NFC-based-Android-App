package com.example.navigator;

public class MenuItemDetails {
	String name;
	String description;
	int spiceLevel;
	double price;
	
	public MenuItemDetails(String n, String d, int sl, double p) {
		this.description = d;
		this.spiceLevel = sl;
		this.price      = p;
		this.name = n;
	}

	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}

	public int getSpiceLevel() {
		return spiceLevel;
	}

	public double getPrice() {
		return price;
	}
	
	public MenuItemDetails(String name) {
		this.name = name;
		this.description = "Description Not Available";
		this.spiceLevel = 0;
		this.price = 0.0;
	}
	
	@Override
	public String toString() {
		String print = this.getName() + "|" +this.getDescription() + "|" + 
						this.getPrice() + "|" + this.getSpiceLevel();
		return print;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MenuItemDetails other = (MenuItemDetails) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}