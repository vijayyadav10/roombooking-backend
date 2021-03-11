package com.vizz.roombooking.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vizz.roombooking.data.RoomRepository;
import com.vizz.roombooking.model.entities.Room;

@Service
public class RoomServiceImpl implements RoomService {

	@Autowired
	RoomRepository roomRepository;
	
	@Override
	public List<Room> findRooms() {
		return roomRepository.findAll();
	}

	@Override
	public Room findRoomById(Long roomId) {
		return roomRepository.findById(roomId).get();
	}

	@Override
	public void saveRoom(Room room) {
		roomRepository.save(room);
	}

	@Override
	public void deleteRoomById(Long roomId) {
		roomRepository.deleteById(roomId);
	}

}
