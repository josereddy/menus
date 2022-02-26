package com.example.MENUS.Menus_Controller;


import com.example.MENUS.DTO.Menus_Post_DTO;
import com.example.MENUS.DTO.Menus_Put_DTO;
import com.example.MENUS.DTO.Remote_Put_Location_Menus_DTO;
import com.example.MENUS.DTO.Remote_Put_Reservation_Menus_DTO;
import com.example.MENUS.Document.Menus_MDB;
import com.example.MENUS.Entity.Interceptor_Data_DB;
import com.example.MENUS.Entity.User_Data_DB;
import com.example.MENUS.Exception.UserDataIncorrectFormatException;
import com.example.MENUS.Services.Check_ConvertService;
import com.example.MENUS.Services.CrudServices;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping("/menus")
@Tag(name = "MENUS", description = "Manages All MENUS related data about restaurant")
@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Operation Success",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "400", description = "CHECK FOR SCHEMA BEFORE SENDING",
                content = @Content(schema = @Schema(implementation = Menus_MDB.class),
                        mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "401", description = "Login credentials mismatch",
                content = @Content(schema = @Schema(implementation = User_Data_DB.class),
                        mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "403", description = "Login USER LEVEL mismatch",
                content = @Content(schema = @Schema(implementation = User_Data_DB.class),
                        mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = "Mismatch with RULES While entering details",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
})
public class Menus_controller {


    private static final Logger log = LogManager.getLogger(Menus_controller.class.getName());
    @Autowired
    private CrudServices cr_service;
    @Autowired
    private Check_ConvertService cc_service;


    ///Add MENU API
    @Operation(summary = "[POST Menus DATA TO MONGO DB]", description = "New record wil be Added into the database locations")
    @SecurityRequirement(name = "check")
    @PostMapping("/post/add_menus")
    public String add_menu(@RequestBody Menus_Post_DTO menu_data) {

        log.info("REST CALL: ENTERED ADD MENU DATA ");
        if (cr_service.save_menu(menu_data)) {
            log.debug("REST CALL: ADD MENU DATA Successfully EXITED ");
            return "MENU Data Successfully Added to DataBase";
        } else
            throw new UserDataIncorrectFormatException("Given Menus_Data is incorrect");


    }


    ///Pagination and Sorting And FILTERING  API
    @Operation(summary = "[GET PAGINATED Menu DATA with sorted by and filtering]", description = "MENU data from Menus table in pagination with sorting,filtering is obtained ")
    @Parameter(name = "offset", example = "0", required = true, description = "PAGE OFFSET", in = ParameterIn.PATH)
    @Parameter(name = "pageSize", example = "5", required = true, description = "PAGE SIZE", in = ParameterIn.PATH)
    @Parameter(name = "sorting_field", example = "restaurantcode", required = false, description = "SORTING FIELD/   default=id/    ex:id,restaurantcode,restaurantname.....", in = ParameterIn.QUERY)
    @Parameter(name = "filter_fields", required = false, description = "Filtering FIELDS/    default=all fields available/     ex:id,restaurantcode,restaurantname.....", in = ParameterIn.QUERY)
    @SecurityRequirement(name = "check")
    @GetMapping("/get/pagination_sort_filtering/{offset}/{pageSize}")
    private MappingJacksonValue getProductsWithPagination_SortAndFiltering_menus(@PathVariable("offset") int offset, @PathVariable("pageSize") int pageSize,
                                                                                 @RequestParam("sorting_field") Optional<String> sorting_field, @RequestParam Optional<Set<String>> filter_fields) {
        log.info("REST CALL: Entered Pagination and Sorting and filtering");
        MappingJacksonValue value = cr_service.findLocationsWithPaginationSorting_filtering_menus(offset, pageSize, sorting_field, filter_fields);
        log.debug("REST CALL:Exited Pagination AND Sorting AND Filtering");
        return value;

    }


    //////Search by field API
    @SecurityRequirement(name = "check")
    @Operation(summary = "[GET the record by field and value]", description = "Get the single record for given field and value ")
    @Parameter(name = "value", example = "1", required = true, description = "Field Value", in = ParameterIn.PATH)
    @Parameter(name = "search_field", required = false, description = "Search Field Name/default=id", in = ParameterIn.QUERY)
    @GetMapping("/get/search_menus/{value}")
    public MappingJacksonValue search_value(@PathVariable("value") String value, @RequestParam Optional<String> search_field) {
        log.info("REST API: Entered SEARCH Service");
        MappingJacksonValue menus_jackson = cr_service.find_value(value, search_field);
        log.debug("REST API: Exited Search Service");
        return menus_jackson;
    }


    /////////   Update location API
    @SecurityRequirement(name = "check")
    @Operation(summary = "[Update the record based on id]", description = "A new value for the particular value is appeared in Database ")
    @PutMapping("/put/update_menus")
    public String update_menus(@RequestBody Menus_Put_DTO menus_post_dto) {
        log.info("REST API: Entered  Update Service");
        if (cr_service.update_service_menus(menus_post_dto))
            return "Data updated Successfully";
        else
            return "Data updation_service failed";
    }


    ///////////   Delete Location Api
    @SecurityRequirement(name = "check")
    @Operation(summary = "[Delete the Menu record based on id]", description = "No more data available in the database with the chosen id")
    @Parameter(name = "id", example = "1", required = true, description = "Id VALUE", in = ParameterIn.PATH)
    @DeleteMapping("/delete/delete_menus/{id}")
    public String delete_location(@PathVariable("id") Long id) {
        log.info("REST API: Entered Deleted Location ");
        if (cr_service.delete_menus(id)) {
            log.debug("REST API: Exited Deleted Location");
            return "Record with id -->" + id + " deleted";
        } else
            return "Record with id--->" + id + " not deleted";

    }


    ///////////////////// Interceptor API TIMINGS

    @GetMapping("/get/api_timing/{offset}/{pageSize}/{Microservice}")
    @SecurityRequirement(name = "check")
    @Operation(summary = "[GET The All Menus Api timings]", description = "Retrieve data in pagination format from db")
    @Parameter(name = "offset", example = "0", required = true, description = " PAGE OFFSET", in = ParameterIn.PATH)
    @Parameter(name = "pageSize", example = "5", required = true, description = "PAGE SIZE", in = ParameterIn.PATH)
    @Parameter(name = "Microservice", example = "MENUS", required = true, description = "Microservice name", in = ParameterIn.PATH)

    public Page<Interceptor_Data_DB> get_api_timing(@PathVariable("offset") int offset, @PathVariable("pageSize") int pageSize, @PathVariable("Microservice") String name) {
        log.info("REST API:Entered into Api TIME sender");
        Page<Interceptor_Data_DB> data = cr_service.api_timing(offset, pageSize, name);
        log.debug("REST API:Exited FROM API TIME SENDER");
        return data;
    }


    ///////////  Remote API
    ///reservation-menu
    @Hidden
    @SecurityRequirement(name = "check")
    @PutMapping("/put/check_reservation_menus")
    public Integer check_reserve(@RequestBody Remote_Put_Reservation_Menus_DTO reservation_data) {

        log.info("REST API:Inside the MENU_REMOTE --->RESERVATION-->MENU-->CHECKING SERVICE");
        return cr_service.check_reserve_menus(reservation_data);
    }


    ///location-menu
    @Hidden
    @SecurityRequirement(name = "check")
    @PutMapping("/put/update_location_menus")
    public Integer update_remote_request(@RequestBody Remote_Put_Location_Menus_DTO remote_dto_update) {

        log.info("REST API:Inside the MENU_REMOTE --->LOCATION-->MENU-->UPDATE");
        return cr_service.remote_put_location_menus(remote_dto_update);
    }


    @Hidden
    @SecurityRequirement(name = "check")
    @DeleteMapping("/delete/delete_location_menus/{code}")
    public Integer remote_delete_request(@PathVariable("code") String code) {

        log.info("REST API:Inside the MENU_REMOTE --->LOCATION-->MENU-->DELETE");
        return cr_service.remote_delete_location_menus(code);
    }


}


