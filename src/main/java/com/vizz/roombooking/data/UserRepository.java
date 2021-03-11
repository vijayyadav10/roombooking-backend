package com.vizz.roombooking.data;

import com.vizz.roombooking.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
