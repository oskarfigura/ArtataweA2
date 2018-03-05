package com.group1.artatawe.utils;

import java.text.NumberFormat;
import java.util.function.Predicate;

/**
 * Number Util for turning Strings into numbers
 */
public class NumUtil {
	
	public static final Predicate<Number> POSITIVE = num -> { return num.doubleValue() > 0; };
	public static final Predicate<Number> POSITIVE_OR_ZERO = num -> { return num.doubleValue() >= 0; };
	
	/**
	 * Turn a string into a double, and apply a Predicate.<br>
	 * Combines multiple operations into one method. <br>
	 * If no Exception is thrown, the operation was a success.
	 * 
	 * @param number - The String to turn into a number
	 * @param too    - The class of the Number
	 * @param predicate - The predicate to apply to the number. <b>Can be null</b>
	 * @return The casted number
	 * @throws Exception <br>- If the parse fails. <br>
	 * 					 - If the predicate fails. <br>
	 *                   - If the cast fails.
	 */
	public static double toDouble(String number, Predicate<Number> predicate) throws Exception {
		Number num = NumberFormat.getInstance().parse(number);

		if(predicate != null && ! predicate.test(num)) { 
			throw new Exception();
		}
		
		return num.doubleValue();
	}
	
	/**
	 * Turn a string into a double, and apply a Predicate.<br>
	 * Combines multiple operations into one method. <br>
	 * If no Exception is thrown, the operation was a success.
	 * 
	 * @param number - The String to turn into a number
	 * @param too    - The class of the Number
	 * @param predicate - The predicate to apply to the number. <b>Can be null</b>
	 * @return The casted number
	 * @throws Exception <br>- If the parse fails. <br>
	 * 					 - If the predicate fails. <br>
	 *                   - If the cast fails.
	 */
	public static int toInt(String number, Predicate<Number> predicate) throws Exception {
		Number num = NumberFormat.getInstance().parse(number);

		if(predicate != null && ! predicate.test(num)) { 
			throw new Exception();
		}
		
		return num.intValue();
	}
}