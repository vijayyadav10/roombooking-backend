package com.vizz.roombooking.services;

import java.util.List;

import com.vizz.roombooking.model.entities.User;

public interface UserService {
	public List<User> findUsers();
	
	public User findUserById(long userId);
	
	public void saveUser(User user);
	
	public void deleteUserById(long userId);
	
	public void resetUserPW(User user);
}
