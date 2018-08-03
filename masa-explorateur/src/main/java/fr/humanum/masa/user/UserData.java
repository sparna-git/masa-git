package fr.humanum.masa.user;

import java.util.List;


public class UserData {

	public static final String KEY = UserData.class.getCanonicalName();

	protected List<User> userList;
	
	protected String msg;
	
	protected User user;
	
	
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}
