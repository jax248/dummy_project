package com.pracify.db.tableClasses;

public class UserDetails {

	private String email_id, user_name;
	private int is_logged_in;

	public UserDetails() {
		// Do nothing
	}

	public UserDetails(String email_id, String user_name, int is_logged_in) {
		this.setEmail_id(email_id);
		this.setUser_name(user_name);
		this.setIs_logged_in(is_logged_in);
	}

	public String getEmail_id() {
		return email_id;
	}

	public void setEmail_id(String email_id) {
		this.email_id = email_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public int getIs_logged_in() {
		return is_logged_in;
	}

	public void setIs_logged_in(int is_logged_in) {
		this.is_logged_in = is_logged_in;
	}
}
