package org.far.twoduiproject;

public class Category {
	
	private int categoryId;
	private String categoryName;
	private int isEnabled;
	
	public Category(int categoryId, String categoryName, int isEnabled) {
		
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.isEnabled = isEnabled;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public int isEnabled() {
		return isEnabled;
	}

	public void setEnabled(int isEnabled) {
		this.isEnabled = isEnabled;
	}

}
