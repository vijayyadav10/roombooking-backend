package com.vizz.roombooking.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vizz.roombooking.data.UserRepository;
import com.vizz.roombooking.model.entities.User;

@Service
class UserServiceImpl implements UserService{

	@Autowired
	UserRepository userRepository;
	
	@Override
	public void deleteUserById(long userId) {
		userRepository.deleteById(userId);
	}

	@Override
	public void resetUserPW(User user) {
		userRepository.save(user);
	}

	@Override
	public List<User> findUsers() {
		return userRepository.findAll();
	}

	@Override
	public void saveUser(User user) {
		userRepository.save(user);
	}

	@Override
	public User findUserById(long userId) {
		return userRepository.findById(userId).get();
	}
	
	
}