package com.vizz.roombooking.control;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//import com.vizz.roombooking.data.RoomRepository;
import com.vizz.roombooking.model.entities.Room;
import com.vizz.roombooking.services.RoomService;

@Controller
@RequestMapping("/rooms")
public class RoomController {

//    @Autowired
//    private RoomRepository roomRepository;

    @Autowired
    private RoomService roomService;
    
    @RequestMapping("")
    public ModelAndView listRooms() {
    	return new ModelAndView("rooms/list", "rooms", roomService.findRooms());
//        return new ModelAndView("rooms/list", "rooms", roomRepository.findAll());
    }

    @RequestMapping("/add")
    public ModelAndView addRoom() {
        Map<String,Object> model = new HashMap<>();
        model.put("room", new Room("",""));
        return new ModelAndView("rooms/edit", model);
    }

    @RequestMapping("/edit")
    public ModelAndView editRoom(@RequestParam Long roomId) {
//        Room room = roomRepository.findById(roomId).get();
        Room room = roomService.findRoomById(roomId);
        Map<String,Object> model = new HashMap<>();
        model.put("room", room);
        return new ModelAndView("rooms/edit", model);
    }

    @PostMapping("/save")
    public Object saveRoom(@Valid Room room, BindingResult bindingResult, RedirectAttributes attributes) {

        if (bindingResult.hasErrors()) {
            Map<String,Object> model = new HashMap<>();
            model.put("room", room);
            return new ModelAndView("rooms/edit", model);
        }

//        roomRepository.save(room);
        roomService.saveRoom(room);
        return "redirect:/rooms";
    }

    @RequestMapping("/delete")
    public String deleteRoom(@RequestParam Long roomId) {
//        roomRepository.deleteById(roomId);
        roomService.deleteRoomById(roomId);
        return "redirect:/rooms";
    }
}
