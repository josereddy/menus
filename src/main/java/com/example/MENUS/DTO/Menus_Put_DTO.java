package com.example.MENUS.DTO;


import com.example.MENUS.Document.Items;
import com.example.MENUS.Document.OpenHours;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@AllArgsConstructor
public class Menus_Put_DTO {

    public Menus_Put_DTO()
    {
        final Logger log = LogManager.getLogger(Menus_Put_DTO.class.getName());
    }


    private String restaurant_code;

    private String restaurant_name;
    private String restaurant_type;
    private List<Items> items;
    private OpenHours openhours;


}
