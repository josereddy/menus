package com.example.MENUS.Repository;

import com.example.MENUS.Entity.User_Data_DB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface User_Data_Repository extends JpaRepository<User_Data_DB,Integer> {

    User_Data_DB findByUsername(String username);
}

