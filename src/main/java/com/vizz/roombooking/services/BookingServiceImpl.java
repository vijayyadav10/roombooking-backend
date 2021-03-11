package com.vizz.roombooking.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.vizz.roombooking.data.BookingRepository;
import com.vizz.roombooking.model.BookingCommand;
import com.vizz.roombooking.model.Layout;
import com.vizz.roombooking.model.entities.Booking;

public class BookingServiceImpl implements BookingService {

	@Autowired
	BookingRepository bookingRepository;
	
	@Autowired
	RoomService roomService;
	
	@Autowired
	UserService userService;
	
	private Map<String,Object> getBookingFormModel(Booking booking) {
        Map<String,Object> model = new HashMap<>();
        model.put("booking",new BookingCommand(booking));
        model.put("rooms", roomService.findRooms());
        model.put("layouts", Layout.values());
        model.put("users", userService.findUsers());
        return model;
    }
	
//	@Override
//	public Map<String,Object> findRoomById(long roomId) {
//		return getBookingFormModel(bookingRepository.findById(roomId).get());
//	}

}
