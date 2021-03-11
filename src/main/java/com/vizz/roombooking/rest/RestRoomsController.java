package com.vizz.roombooking.rest;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vizz.roombooking.data.RoomRepository;
import com.vizz.roombooking.model.entities.Room;

@RestController
@RequestMapping("/api/rooms")
public class RestRoomsController {
	
	@Autowired
	private RoomRepository roomRepository;
	
	@GetMapping
	public List<Room> getAllRooms(@CookieValue(value = "token", defaultValue = "empty") String token, HttpServletResponse response) throws InterruptedException {
		System.out.println("token recived by cookie is");
		return roomRepository.findAll();
	}
	
	@GetMapping("/{id}")
	public Room getRoom(@PathVariable("id") Long id) {
		 return roomRepository.findById(id).get();
	}
	
	@PostMapping()
	public Room newRoom(@RequestBody Room room) {
		return roomRepository.save(room);
	}
	
	@PutMapping()
	public Room updateRoom(@RequestBody Room updatedRoom) {
		Room originalRoom = roomRepository.findById(updatedRoom.getId()).get();
		
		originalRoom.setName(updatedRoom.getName());
		originalRoom.setLocation(updatedRoom.getLocation());
		originalRoom.setCapacities(updatedRoom.getCapacities());
		return roomRepository.save(originalRoom);
	}
	
	@DeleteMapping("/{id}")
	public void deleteRoom(@PathVariable("id") Long id) {
		roomRepository.deleteById(id);
	}
}
