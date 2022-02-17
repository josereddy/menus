package com.example.MENUS.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Remote_Put_Reservation_Menus_DTO {
    private String restaurant_code;
    private String restaurant_name;
    private String reservation_day;
    private String booking_time;

}
