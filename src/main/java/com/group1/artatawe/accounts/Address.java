package com.group1.artatawe.accounts;

/**
 * Store all information in an Address
 */
public class Address {

	private String line1;
	private String line2 = "";
	private String line3 = "";
	private String city;
	private String postcode;

	/**
	 * Construct a new Address
	 * 
	 * @param line1    - The first line of the users address
	 * @param line2    - The second line of the users address
	 * @param line3    - The last line of the users address
	 * @param city     - The city of the address
	 * @param postcode - The postcode of the address
	 */
	public Address(String line1, String line2, String line3, String city, String postcode) {
		this.line1 = line1;
		this.line2 = line2;
		this.line3 = line3;
		this.city = city;
		this.postcode = postcode;
	}

	/**
	 * Set a lines of the address
	 * 
	 * @param line - The line of the address to set (1, 2 or 3)
	 * @param text - The text to set the line to
	 */
	public void setLine(int line, String text) {
		switch(line) {
			case 1: line1 = text; break;
			case 2: line2 = text; break;
			case 3: line3 = text; break;
			default: throw new IllegalArgumentException("Address line is between 1 - 3");
		}
	}

	/**
	 * Set the city of the user
	 * @param city - The users city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Set the postcode of the user
	 * @param postcode - The users postcode
	 */
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	/**
	 * Gets the address of the user
	 * @param line - The line in the address (1, 2 or 3)
	 */
	public String getLine(int line) {
		switch(line) {
			case 1: return line1;
			case 2: return line2;
			case 3: return line3;
			default: throw new IllegalArgumentException("Address line is between 1 - 3");
		}
	}

	/**
	 * Get the city of the user
	 * @param city - The users city
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * Get the postcode of the user
	 * @param postcode - The users postcode
	 */
	public String getPostcode() {
		return this.postcode;
	}
}
