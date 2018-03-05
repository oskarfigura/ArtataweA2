package com.group1.artatawe.utils;

import java.util.regex.Pattern;

/**
 * Validate different attributes
 */
public class ValidationUtil {
	
	/**
	 * Validate that a postcode is a valid UK postcode
	 * 
	 * @see https://stackoverflow.com/questions/164979/uk-postcode-regex-comprehensive
	 * @param postcode - The postcode to be validated.
	 * @return True if valid, else False.
	 */
	public static boolean validatePostcode(String postcode) {
		String regex = "^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][ABD-HJLNP-UW-Z]{2}$"; 
		return Pattern.compile(regex)
				.matcher(postcode)
				.matches();
	}
	
	/**
	 * Validate that a phone number is a valid phone number
	 * 
	 * @see https://stackoverflow.com/questions/25155970/validating-uk-phone-number-regex-c
	 * @param phone - The phone number to be validated.
	 * @return True if valid, else False.
	 */
	public static boolean validatePhone(String phone) {
		String regex = "^(((\\+44\\s?\\d{4}|\\(?0\\d{4}\\)?)\\s?\\d{3}\\s?\\d{3})"
				+ "|((\\+44\\s?\\d{3}|\\(?0\\d{3}\\)?)\\s?\\d{3}\\s?\\d{4})|"
				+ "((\\+44\\s?\\d{2}|\\(?0\\d{2}\\)?)\\s?\\d{4}\\s?\\d{4}))(\\s?\\#(\\d{4}|\\d{3}))?$";
		return Pattern.compile(regex)
				.matcher(phone)
				.matches();
	}
}
