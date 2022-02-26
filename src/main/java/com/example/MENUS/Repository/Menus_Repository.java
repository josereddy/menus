package com.example.MENUS.Repository;

import com.example.MENUS.Document.Menus_MDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Menus_Repository extends MongoRepository<Menus_MDB, Long> {
    Menus_MDB findByRestaurantcode(String val);

}
