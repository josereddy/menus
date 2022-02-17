package com.example.MENUS.Services;


import com.example.MENUS.DTO.Menus_Put_DTO;
import com.example.MENUS.Document.Menus_MDB;
import com.example.MENUS.Document.OpenHours;
import com.example.MENUS.Exception.InvalidTimeFormatException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Check_ConvertService {


    private static final Logger log = LogManager.getLogger(Check_ConvertService.class.getName());

    private Menus_MDB menus;
    @Autowired
    private SequenceGeneratorService seq_service;




//////////////////////////////////////checking valid data


    public boolean check_menus_put_dto(Menus_Put_DTO menus_put_dto) {
        log.info("CHECK_CONVERT_SERVICE: Entered MENU Data check Service");


        if (menus_put_dto.getRestaurant_code() == null || menus_put_dto.getRestaurant_name() == null ||
                menus_put_dto.getItems() == null|| menus_put_dto.getOpenhours()==null||menus_put_dto.getRestaurant_type()==null)
            return false;
        if(check_open_hour_pattern(menus_put_dto.getOpenhours()))
        {
            log.debug("CHECK_CONVERT_SERVICE: Successfully Exited MENU Data check Service");
            return true;
        }
        else return false;
    }



///////checking valid open_hours
    public boolean check_open_hour_pattern(OpenHours openHours)
    {
        String pattern="[0-2][0-9](\\:)[03][0](\\-)[0-2][0-9](\\:)[03][0]";
        List<String> dayhours = new ArrayList();
        dayhours.add(openHours.getSunday());
        dayhours.add(openHours.getMonday());
        dayhours.add(openHours.getTuesday());
        dayhours.add(openHours.getWednesday());
        dayhours.add(openHours.getThursday());
        dayhours.add(openHours.getFriday());
        dayhours.add(openHours.getSaturday());
        for(String day_openhours:dayhours) {

                    Pattern p1 =Pattern.compile(pattern);
                    Matcher m1=p1.matcher(day_openhours);
                    if(!(day_openhours.equals("closed")))
                        if(m1.find())
                    {
                        Pattern p2=Pattern.compile("\\-");
                        String[] timers =p2.split(m1.group());
                        if((timers[0].compareTo(timers[1]))<0){
                            Pattern p3 = Pattern.compile("[01][0-9]:[03][0]");
                            Pattern p4 =Pattern.compile("[2][0-3]:[03][0]");
                            Matcher m2 = p3.matcher(timers[0]);
                            Matcher m3 = p3.matcher(timers[1]);
                            if(!(m2.find())){
                                Matcher m4 = p4.matcher(timers[0]);
                                if(!(m4.find()))
                                    throw  new InvalidTimeFormatException("Please check all time formats are 24hrs ex:00:00-23:30");
                            }
                            if(!(m3.find()))
                            {

                                Matcher m5 = p4.matcher(timers[1]);
                                if(!(m5.find()))
                                    throw  new InvalidTimeFormatException("Please check all time formats are 24hrs ex:00:00-23:30 invalid time format exception");
                            }
                        }
                        else
                           throw  new InvalidTimeFormatException("Please check all time formats are 24hrs ex:00:00-23:30 and must be in a half hour time interval");
                                              }
                    else
                        throw new InvalidTimeFormatException("Please check all time formats are 24hrs ex:00:00-23:30 and must be in a half hour time interval");
        }

            return true;
    }








    ////////////////////////////////Dto Document converter

    public Menus_MDB DtoDocument_convert(Menus_Put_DTO menus_put_dto) {
        log.info("CHECK_CONVERT_SERVICE: Entered INTO DATA TO ENTITY Conversion SERVICE");
        menus = new Menus_MDB();
        menus.setId(seq_service.generateSequence(Menus_MDB.SEQUENCE_NAME));
        menus.setRestaurantcode(menus_put_dto.getRestaurant_code());
        menus.setRestaurantname(menus_put_dto.getRestaurant_name());
        menus.setRestauranttype(menus_put_dto.getRestaurant_type());
        menus.setItems(menus_put_dto.getItems());
        menus.setOpenhours(menus_put_dto.getOpenhours());
        log.info("CHECK_CONVERT_SERVICE: EXITED FROM Location DATA to Entity Conversion Service");

        return menus;

    }


    public Set<String> getAllMenusFields() {
        log.info("CHECK_CONVERT_SERVICE: Entered into get All fields Service");

        Set<String> fields = new HashSet<>();
        fields.add("id");
        fields.add("restaurantcode");
        fields.add("restaurantsname");
        fields.add("items");
        fields.add("openhours");
        fields.add("restauranttype");
        log.info("CHECK_CONVERT_SERVICE: Exited from get All fields Service");
        return fields;
    }
}