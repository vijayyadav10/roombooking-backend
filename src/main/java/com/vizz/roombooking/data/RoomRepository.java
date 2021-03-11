package com.vizz.roombooking.data;

import com.vizz.roombooking.model.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
