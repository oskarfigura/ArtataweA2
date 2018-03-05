package com.group1.artatawe.managers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.Scanner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.group1.artatawe.Main;
import com.group1.artatawe.accounts.Account;
import com.group1.artatawe.accounts.Address;

import javafx.scene.image.Image;

/**
 * Responsible for loading, saving and storing all the accounts in the system.
 */
public class AccountManager {

	private static final String ACCOUNT_FILE = "accounts.json";

	//Can be null if no account is logged in
	private Account loggedInAccount;

	private final LinkedList<Account> allAccounts = new LinkedList<>();

	/**
	 * Construct a AccountManager
	 * Will load all the accounts from the file
	 */
	public AccountManager() {
		this.loadAccounts();
	}

	/**
	 * Get the logged in account
	 * @return The currently logged in Account. Will be null if no account is logged in
	 */
	public Account getLoggedIn() {
		return this.loggedInAccount;	
	}
	
	/**
	 * Log an account in
	 * @param account- The account to log in
	 */
	public void login(Account account) {
		this.loggedInAccount = account;
		account.login();
		
		this.saveAccountFile();
	}

	/**
	 * Logout the current account
	 */
	public void logoutCurrentAccount() {
		this.loggedInAccount = null;
		this.saveAccountFile();
		Main.switchScene("AccountSelect");
	}

	/**
	 * Get the account with a matching username
	 * 
	 * @param userName - The username to find the account for
	 * @return The account. Will be null if no account has this username
	 */
	public Account getAccount(String userName) {
		return this.allAccounts.stream()
				.filter(account -> account.getUserName().equalsIgnoreCase(userName))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Get all Accounts in the system
	 * @return List of all Accounts
	 */
	public LinkedList<Account> getAccounts() {
		return this.allAccounts;
	}

	/**
	 * Check if a username has not been used
	 * 
	 * @param userName - The username to check
	 * @return True if the username hasn't been used, else False
	 */
	public boolean isUniqueUsername(String userName) {
		return this.getAccount(userName) == null;
	}

	/**
	 * Construct a new account and save it in the system
	 */
	public Account createAccount(String userName, String firstName, String lastName, String mobileNum, Address address, Image avatar) {
		Account newAccount = new Account(userName, firstName, lastName, mobileNum, address, avatar, System.currentTimeMillis());
		this.allAccounts.add(newAccount);
		
		this.saveAccountFile();
		return newAccount;
	}
	
	/**
	 * Save all the accounts back to the file
	 */
	public void saveAccountFile() {
		StringBuilder data = new StringBuilder();
		
		this.allAccounts.forEach(account -> data.append(account.toJsonObject().toString() + "\n"));
		
		File accountsFile = new File(ACCOUNT_FILE);
		
		try {
			Files.write(accountsFile.toPath(), data.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Open and load all the accounts out of the file.
	 */
	private void loadAccounts() {
		File temp = new File(ACCOUNT_FILE);
		Scanner scanner = null;
		try {
			scanner = new Scanner(temp);

			while(scanner.hasNextLine()) {
				String nextLine = scanner.nextLine().trim();

				if(! nextLine.isEmpty()) {
					this.loadAccount(nextLine);
				}
			}
		} catch (FileNotFoundException e) {
			try {
				new File(ACCOUNT_FILE).createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			if(scanner != null) { 
				scanner.close(); 
			}
		}
	}

	/**
	 * Loads an account from a Json String into the system
	 * 
	 * @param jsonString - The Json String that is turned into an Account
	 */
	private void loadAccount(String jsonString) {
		JsonParser jp = new JsonParser();
		try {
			JsonObject jo = (JsonObject) jp.parse(jsonString);
			
			Account account = new Account(jo);
			this.allAccounts.add(account);
		} catch(Exception e) {
			System.out.println("Parse error on string: \n" + jsonString + "\nThe account has not been loaded.");
		}
	}
}