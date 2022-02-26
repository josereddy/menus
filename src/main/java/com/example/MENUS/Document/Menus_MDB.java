package com.example.MENUS.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Transient;
import java.util.List;

@Document(collection = "MENUS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonFilter("ModelMenus")
public class Menus_MDB {

    @Transient
    public static final String SEQUENCE_NAME = "Menu_sequence";


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Indexed(unique = true)
    private String restaurantcode;

    private String restaurantname;

    private String restauranttype;

    private List<Items> items;

    private OpenHours openhours;


}
