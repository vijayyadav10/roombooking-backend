package com.vizz.roombooking.services;

import java.util.List;

import com.vizz.roombooking.model.entities.Room;

public interface RoomService {

	public List<Room> findRooms();

	public Room findRoomById(Long roomId);

	public void saveRoom(Room room);

	public void deleteRoomById(Long roomId);

}
