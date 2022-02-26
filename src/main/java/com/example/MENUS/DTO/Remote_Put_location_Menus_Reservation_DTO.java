package com.example.MENUS.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Remote_Put_location_Menus_Reservation_DTO {
    private String old_restaurant_code;
    private String updated_restaurant_code;
    private String updated_restaurant_name;


}
