package com.avic.chs.cis.entity;

public class UserInfo {
	private int id;
	private String fullname;
	private boolean isLock;
	private String secretlevel;
	private String username;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public boolean isLock() {
		return isLock;
	}
	public void setLock(boolean isLock) {
		this.isLock = isLock;
	}
	public String getSecretlevel() {
		return secretlevel;
	}
	public void setSecretlevel(String secretlevel) {
		this.secretlevel = secretlevel;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
