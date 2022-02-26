package com.example.MENUS.Services;

import com.example.MENUS.DTO.*;
import com.example.MENUS.Exception.UnauthorisedException;
import org.springframework.data.domain.PageRequest;
import com.example.MENUS.Document.Menus_MDB;
import com.example.MENUS.Entity.Interceptor_Data_DB;
import com.example.MENUS.Exception.DuplicateLocationCodeFoundException;
import com.example.MENUS.Exception.NoFieldPresentException;
import com.example.MENUS.Exception.UserNotFoundException;
import com.example.MENUS.Repository.Interceptor_Repository;
import com.example.MENUS.Repository.Menus_Repository;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
@Data
public class CrudServices {


    private static final Logger log = LogManager.getLogger(CrudServices.class.getName());


    CrudServices(Menus_Repository menus_repository, Interceptor_Repository interceptor_repository,
                 Check_ConvertService check_convertService, RemoteRequest remote_request_service) {
        this.cc_service = check_convertService;
        this.menus_repository = menus_repository;
        this.interceptor_repository = interceptor_repository;
        this.remote_request_service = remote_request_service;

    }

    ///repositories

    private Menus_Repository menus_repository;
    private Interceptor_Repository interceptor_repository;


    //////services
    private Check_ConvertService cc_service;
    private RemoteRequest remote_request_service;


    private Menus_MDB menus_mdb;
    private Remote_Put_location_Menus_Reservation_DTO remote_put_location_menus_reservation_dto;


    ///SAVE SERVICE
    public boolean save_menu(Menus_Post_DTO menus_post_dto) {

        log.info("CRUD_SERVICE: Entered the AddMenu to database");

        if (cc_service.check_menus_post_dto(menus_post_dto)) {
            Integer remote_check_response = remote_request_service.remote_check_get_menus_location(
                    menus_post_dto.getRestaurant_code() + "/" + menus_post_dto.getRestaurant_name());
            if (remote_check_response == 0)
                menus_mdb = cc_service.DtoDocument_convert(menus_post_dto);
            else if (remote_check_response == 1)
                throw new UserNotFoundException("Restaurant code cannot be found use proper one look back location DB");

            else if (remote_check_response == 2)
                throw new UserNotFoundException("Restaurant name cannot be found user proper one look back location DB");
            try {
                menus_repository.save(menus_mdb);
            } catch (DataIntegrityViolationException e) {
                throw new DuplicateLocationCodeFoundException("RestaurantCode Already In Use:  Please Use Different Code  for Location");
            }

            log.debug("CRUD_SERVICE: Successfully Exited the AddMenu to database");
            return true;
        } else
            return false;

    }


    //PAGINATION SERVICE
    public MappingJacksonValue findLocationsWithPaginationSorting_filtering_menus(int offset, int pageSize, Optional<String> sort_field, Optional<Set<String>> filter_field) {


        log.info("CRUD_SERVICE: Entered into the Menus PaginationAndSorting and filtering");
        Page<Menus_MDB> menus_page = menus_repository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(sort_field.orElse("id"))));
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(filter_field.orElse(cc_service.getAllMenusFields()));
        FilterProvider filters = new SimpleFilterProvider().addFilter("ModelMenus", filter);
        MappingJacksonValue mapping = new MappingJacksonValue(menus_page);
        mapping.setFilters(filters);
        log.debug("CRUD_SERVICE: Exited from Menus the PaginationAndSorting and filtering");
        return mapping;

    }


    ///////////Get by feild

    public MappingJacksonValue find_value(String value, Optional<String> search_field) {

        log.info("CRUD_SERVICE: Entered into the Menus GET BY ID SERVICE");
        if (search_field.orElse("id").equals("restaurantcode")) {
            menus_mdb = menus_repository.findByRestaurantcode(value);
        } else if (search_field.orElse("id").equals("id")) {
            Long id_val = Long.parseLong(value);
            Optional<Menus_MDB> menus_optional = menus_repository.findById(id_val);
            if (!(menus_optional.isPresent())) {
                throw new UserNotFoundException("Cannot find the requested data for the given value: " + value);
            }
            menus_mdb = menus_optional.get();

        } else {
            throw new NoFieldPresentException("Field:  " + search_field.get() + " Not present please Select valid Field");
        }
        if (menus_mdb == null) {
            throw new UserNotFoundException("Cannot find the requested data for the given value: " + value);
        } else {
            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(cc_service.getAllMenusFields());
            FilterProvider filters = new SimpleFilterProvider().addFilter("ModelMenus", filter);
            MappingJacksonValue mapping = new MappingJacksonValue(menus_mdb);
            mapping.setFilters(filters);
            log.debug("CRUD_SERVICE: SUCCESSFULLY  EXITED FROM GET BY ID SERVICE");
            return mapping;
        }
    }


    ////update Service
    public boolean update_service_menus(Menus_Put_DTO menus_put_dto) {

        log.info("CRUD_SERVICE: Entered into Menu UPDATE SERVICE");
        Integer check_sum = 0;
        if (!(menus_repository.findById(menus_put_dto.getId()).isPresent()))
            throw new UserNotFoundException("Menu with ID " + menus_put_dto.getId() + " not present");
        Optional<Menus_MDB> menus_optional = menus_repository.findById(menus_put_dto.getId());
        if ((menus_optional.get().getRestaurantcode().equals(menus_put_dto.getRestaurant_code())) &&
                (menus_optional.get().getRestaurantname().equals(menus_put_dto.getRestaurant_name()))) {
            //checking the open hours format
            cc_service.check_open_hour_pattern(menus_put_dto.getOpenhours());
            ///updating
            menus_repository.findById(menus_put_dto.getId()).map(menu_record -> {
                menu_record.setRestaurantcode(menus_put_dto.getRestaurant_code());
                menu_record.setRestaurantname(menus_put_dto.getRestaurant_name());
                menu_record.setRestauranttype(menus_put_dto.getRestaurant_type());
                menu_record.setItems(menus_put_dto.getItems());
                menu_record.setOpenhours(menus_put_dto.getOpenhours());
                return menus_repository.save(menu_record);
            });

            //updating the remote data
            if (!(remote_request_service.remote_put_menus_reservation(menus_put_dto.getRestaurant_code()))) {
                log.info("remote updated from menus to reservation not happened successfully");
                throw new UserNotFoundException("remote updated from menus to reservation not happened successfully");
            }
        } else {
            throw new UnauthorisedException("U cannot able to modify restaurant name and code only way access location api to do it");
        }
        log.info("CRUD_SERVICE: EXITED FROM  UPDATE SERVICE");
        return true;
    }


    /////////////Deleted  service
    public Boolean delete_menus(Long id) {
        log.info("CRUD_SERVICE: Entered into DELETED SERVICE");
        if (!(menus_repository.findById(id).isPresent()))
            throw new UserNotFoundException("Menu with ID " + id + " not present");
        menus_mdb = menus_repository.findById(id).get();
        if (remote_request_service.remote_delete_menus_reservation(menus_mdb.getRestaurantcode())) {
            menus_repository.deleteById(id);
            //////////////////////handle reservations///////////////////////////////////////////////////////////////////////
            log.info("CRUD_SERVICE: Exited into DELETED SERVICE");
            return true;
        } else
            return false;
    }


///////////////Rest api timing service

    public void add_interceptor_data(List data) {
        log.info("CRUD SERVICES:Entered into the add_interceptor data ");
        Interceptor_Data_DB interceptor_data_db = new Interceptor_Data_DB();
        interceptor_data_db.setTimemillisec((Long) data.get(0));
        interceptor_data_db.setUrl((String) data.get(2));
        interceptor_data_db.setId(0);
        interceptor_data_db.setDate(new Date());
        interceptor_data_db.setApiname("MENUS");
        interceptor_data_db.setServicename((String) data.get(1));
        interceptor_repository.save(interceptor_data_db);
        log.debug("CRUD SERVICES:Exited from the add_interceptor data");

    }

    public Page<Interceptor_Data_DB> api_timing(int offset, int pageSize, String name) {
        log.info("CRUD_SERVICE: Entered into the api timing sender");
        Page<Interceptor_Data_DB> data = interceptor_repository.findByApiname(name, PageRequest.of(offset, pageSize));
        log.debug("CRUD_SERVICE: Exited from the api timing sender");
        return data;
    }


    ///remote --api call


    /////updated  from Location-MENUS/////and then Menus-Reservation
    public Integer remote_put_location_menus(Remote_Put_Location_Menus_DTO remote_put_location_menus_dto) {

        log.info("CRUD_SERVICE: Entered into the remote_put_location_menus");
        remote_put_location_menus_reservation_dto = new Remote_Put_location_Menus_Reservation_DTO();

        if ((menus_mdb = menus_repository.findByRestaurantcode(remote_put_location_menus_dto.getOld_restaurant_code())) == null) {
        } else {


            ///setting values for sending data from menus to remote reservation
            remote_put_location_menus_reservation_dto.setUpdated_restaurant_code(remote_put_location_menus_dto.getUpdated_restaurant_code());
            remote_put_location_menus_reservation_dto.setUpdated_restaurant_name(remote_put_location_menus_dto.getUpdated_restaurant_name());
            remote_put_location_menus_reservation_dto.setOld_restaurant_code(remote_put_location_menus_dto.getOld_restaurant_code());

            if (!(remote_request_service.remote_put_location_menus_reservation(remote_put_location_menus_reservation_dto)))
                return 1;

            /////updating menu
            menus_mdb.setRestaurantcode(remote_put_location_menus_reservation_dto.getUpdated_restaurant_code());
            menus_mdb.setRestaurantname(remote_put_location_menus_reservation_dto.getUpdated_restaurant_name());
            menus_repository.save(menus_mdb);

            log.info("CRUD_SERVICE: Exited from  the remote_put_location_menus");
        }
        return 0;


    }


    //delete Location-Menus//////Menus-Reservation
    public Integer remote_delete_location_menus(String code) {

        log.info("CRUD_SERVICE: Entered into the remote_delete_location_menus");
        Menus_MDB menus_mdb;
        if ((menus_mdb = menus_repository.findByRestaurantcode(code)) == null) {
            return 0;
        } else {


            if (!(remote_request_service.remote_delete_location_menus_reservation(code)))
                return 1;
            menus_repository.deleteById(menus_mdb.getId());

            log.info("CRUD_SERVICE: Exited from  the remote_delete_location_menus");
            return 0;
        }

    }


    //////////remote reservation request
    public Integer check_reserve_menus(Remote_Put_Reservation_Menus_DTO reservation_put_reservation_menus_dto) {


        log.info("CRUD_SERVICE: Entered into the remote_check_reserve_menus");

        menus_mdb = menus_repository.findByRestaurantcode(reservation_put_reservation_menus_dto.getRestaurant_code());
        if (menus_mdb == null) {

            return 1;
        }
        if (!(menus_mdb.getRestaurantname().equals(reservation_put_reservation_menus_dto.getRestaurant_name())))
            return 2;
        String day = reservation_put_reservation_menus_dto.getReservation_day();
        String openhours = null;
        switch (day) {
            case "SUNDAY":
                openhours = menus_mdb.getOpenhours().getSunday();
                break;
            case "MONDAY":
                openhours = menus_mdb.getOpenhours().getMonday();
                break;
            case "TUESDAY":
                openhours = menus_mdb.getOpenhours().getTuesday();
                break;
            case "WEDNESDAY":
                openhours = menus_mdb.getOpenhours().getWednesday();
                break;
            case "THURSDAY":
                openhours = menus_mdb.getOpenhours().getThursday();
                break;
            case "FRIDAY":
                openhours = menus_mdb.getOpenhours().getFriday();
                break;
            case "SATURDAY":
                openhours = menus_mdb.getOpenhours().getSaturday();
        }

        ///check for valid working hours

        Pattern p = Pattern.compile("\\-");
        Matcher m = p.matcher(openhours);
        if (m.find()) {
            String s1[] = p.split(openhours);
            String s2[] = p.split(reservation_put_reservation_menus_dto.getBooking_time());
            if (!((s1[0].compareTo(s2[0])) <= 0)) {

                return 3;
            }
            if (!(((s1[1].compareTo(s2[1])) >= 0))) {
                return 3;
            }
        } else
            return 4;

        return 0;

    }


}
