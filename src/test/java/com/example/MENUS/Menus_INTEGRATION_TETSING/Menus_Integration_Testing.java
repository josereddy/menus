package com.example.MENUS.Menus_INTEGRATION_TETSING;



import com.example.MENUS.DTO.Menus_Post_DTO;
import com.example.MENUS.DTO.Menus_Put_DTO;
import com.example.MENUS.Document.Items;
import com.example.MENUS.Document.Menus_MDB;
import com.example.MENUS.Document.OpenHours;
import com.example.MENUS.Entity.Interceptor_Data_DB;
import com.example.MENUS.Entity.User_Data_DB;
import com.example.MENUS.Repository.Interceptor_Repository;
import com.example.MENUS.Repository.Menus_Repository;
import com.example.MENUS.Repository.User_Data_Repository;
import com.example.MENUS.Services.RemoteRequest;
import com.example.MENUS.Services.SequenceGeneratorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Menus_Integration_Testing {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;



    ///repositories
    @Autowired
    private Menus_Repository menus_repository;
    @Autowired
    private User_Data_Repository user_data_repository;
    @Autowired
    private Interceptor_Repository interceptor_repository;
    @Autowired
    private SequenceGeneratorService seq_service;

///////services

    @MockBean
    private RemoteRequest remoteRequest;

    private User_Data_DB user_data_db = new User_Data_DB();
    private Menus_Post_DTO menus_post_dto =new Menus_Post_DTO();
    private Menus_MDB menus_mdb =new Menus_MDB();

    List<Items> items_list = new ArrayList<Items>();

    @BeforeEach
    public void setup(){
        items_list.add(new Items("Cicken","parrota"));
        menus_repository.deleteAll();
        user_data_repository.deleteAll();
        //user_details
        user_data_db.setUserpassword((new BCryptPasswordEncoder().encode("jose@")));
        user_data_db.setUsername("jose");
        user_data_db.setUserroll("ADMIN");
        user_data_repository.save(user_data_db);
        menus_post_dto.setRestaurant_code("usa-test-1000");
        menus_post_dto.setRestaurant_name("usa-test-chicken");
        menus_post_dto.setItems(items_list);
        menus_post_dto.setOpenhours(new OpenHours("10:00-12:00","10:00-12:00","10:00-12:00","10:00-12:00","10:00-12:00","10:00-12:00","10:00-12:00"));
        menus_post_dto.setRestaurant_type("veg");
        ////////DB object
        menus_mdb.setId(seq_service.generateSequence(Menus_MDB.SEQUENCE_NAME));
        menus_mdb.setRestaurantcode("usa-test-1000");
        menus_mdb.setRestaurantname("usa-test-chicken");
        menus_mdb.setItems(items_list);
        menus_mdb.setRestauranttype("veg");
        menus_mdb.setOpenhours(menus_post_dto.getOpenhours());
    }


    /////////Menus_post

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Post_Menus_TEST:1")
    @Test
    @Order(1)
    public void Menus_Post_Positive() throws Exception {

        // given - precondition or setup
        String Expected = "MENU Data Successfully Added to DataBase";

        given(remoteRequest.remote_check_get_menus_location(any(String.class))).willReturn(0);
//         when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(post(URI.create("/menus/post/add_menus"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menus_post_dto)));

        // then - verify the result or output using assert statements
        response.andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(Expected));



    }

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Post_Menus_TEST:2 EXCEPTION WITH NULL IN USER DATA")
    @Test
    @Order(2)
    public void Menus_Post_test2() throws Exception {

        // given - precondition or setup
        String Expected= "Given Menus_Data is incorrect";
        given(remoteRequest.remote_check_get_menus_location(any(String.class))).willReturn(0);
        menus_post_dto.setItems(null);
        //         when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(post(URI.create("/menus/post/add_menus"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menus_post_dto)));

        // then - verify the result or output using assert statements
        response.andDo(print())
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message",CoreMatchers.is(Expected)));



    }



    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Post_Menus_TEST:3 EXCEPTION WITH INVALID  TIME FORMAT")
    @Test
    @Order(3)
    public void Menus_Post_test3() throws Exception {

        // given - precondition or setup
        String Expected = "Please check all time formats are 24hrs ex:00:00-23:30 and must be in a half hour time interval";
        given(remoteRequest.remote_check_get_menus_location(any(String.class))).willReturn(0);
        menus_post_dto.setOpenhours(new OpenHours("10:00-12:00", "10:00-12:00", "10:00-12:00", "10:00-12:00", "10:00-12:00", "10:00-12:00", "10:00-12:222"));

        //         when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(post(URI.create("/menus/post/add_menus"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menus_post_dto)));

        // then - verify the result or output using assert statements
        response.andDo(print())
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message",CoreMatchers.is(Expected)));
    }



    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Post_Menus_TEST:4 EXCEPTION WITH DUPLICATE USER")
    @Test
    @Order(4)
    public void Menus_Post_test4() throws Exception {

        // given - precondition or setup
        menus_repository.save(menus_mdb);
        String Expected = "RestaurantCode Already In Use:  Please Use Different Code  for Location";
        given(remoteRequest.remote_check_get_menus_location(any(String.class))).willReturn(0);
        menus_post_dto.setOpenhours(new OpenHours("10:00-12:00", "10:00-12:00", "10:00-12:00", "10:00-12:00", "10:00-12:00", "10:00-12:00", "10:00-12:00"));

        //         when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(post(URI.create("/menus/post/add_menus"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menus_post_dto)));

        // then - verify the result or output using assert statements
        response.andDo(print())
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message",CoreMatchers.is(Expected)));
    }




    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Post_Menus_TEST:5 IF REMOTE RESTAURANT CODE  DONOT MATCH")
    @Test
    @Order(5)
    public void Menus_Post_test5() throws Exception {

        // given - precondition or setup
        String Expected = "Restaurant code cannot be found use proper one look back location DB";
        given(remoteRequest.remote_check_get_menus_location(any(String.class))).willReturn(1);
        menus_post_dto.setOpenhours(new OpenHours("10:00-12:00", "10:00-12:00", "10:00-12:00", "10:00-12:00", "10:00-12:00", "10:00-12:00", "10:00-12:00"));

        //         when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(post(URI.create("/menus/post/add_menus"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menus_post_dto)));

        // then - verify the result or output using assert statements
        response.andDo(print())
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.message",CoreMatchers.is(Expected)));
    }











/////////////// get

   @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Get_MENUS TEST:1 default")
    @Test
    @Order(6)
    public void Menus_get_BY_PAGINATION_TEST1() throws Exception {

        // given - precondition or setup
        List<Menus_MDB> list_menus_mdb = new ArrayList();
        list_menus_mdb.add(menus_mdb);
        list_menus_mdb.add(new Menus_MDB(seq_service.generateSequence(Menus_MDB.SEQUENCE_NAME),
        "usa-test-1001","usa-burger-king","veg"
                , items_list,menus_post_dto.getOpenhours()));
        menus_repository.saveAll(list_menus_mdb);
        Integer size = 2, offset = 0, pageSize = 2;

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/menus/get/pagination_sort_filtering/{offset}/{pageSize}", offset, pageSize));

        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.size", CoreMatchers.is(size)))
                .andExpect(jsonPath("$.content[0].restaurantcode", CoreMatchers.is("usa-test-1000")))
                .andExpect(jsonPath("$.content[1].restaurantcode", CoreMatchers.is("usa-test-1001")));
    }




    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Get_MENUS TEST:2 FILTER BY restaurantcode")
    @Test
    @Order(7)
    public void Menus_get_BY_PAGINATION_TEST2() throws Exception {

        // given - precondition or setup
        List<Menus_MDB> list_menus_mdb = new ArrayList();
        list_menus_mdb.add(menus_mdb);
        list_menus_mdb.add(new Menus_MDB(seq_service.generateSequence(Menus_MDB.SEQUENCE_NAME),
                "usa-test-1001","usa-burger-king","veg"
                , items_list,menus_post_dto.getOpenhours()));
        menus_repository.saveAll(list_menus_mdb);
        Integer size = 2, offset = 0, pageSize = 2;
        MultiValueMap<String, String> filter_fields = new LinkedMultiValueMap<>();
        filter_fields.add("filter_fields", "restaurantcode");



        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/menus/get/pagination_sort_filtering/{offset}/{pageSize}", offset, pageSize).params(filter_fields));

        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.size", CoreMatchers.is(size)))
                .andExpect(jsonPath("$.content[0].restaurantcode", CoreMatchers.is("usa-test-1000")))
                .andExpect(jsonPath("$.content[1].restaurantcode", CoreMatchers.is("usa-test-1001")));
    }








    ////////////////////get by field

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Get_MENUS_By_ID TEST:1 Default")
    @Test
    @Order(8)
    public void Menus_get_BY_Field_TEST1() throws Exception {

        // given - precondition or setup
        menus_repository.save(menus_mdb);
        String value= String.valueOf(menus_mdb.getId());
//        MultiValueMap<String, String> filter_fields = new LinkedMultiValueMap<>();
//        filter_fields.add("filter_fields", "restaurantcode");



        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/menus/get/search_menus/{value}", value));

        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantcode", CoreMatchers.is("usa-test-1000")));
                    }





    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Get_MENUS_By_ID TEST:2 PROVIDING SEARCH AND FILTER FIELD")
    @Test
    @Order(9)
    public void Menus_get_BY_Field_TEST2() throws Exception {

        // given - precondition or setup
        menus_repository.save(menus_mdb);
        String value= menus_mdb.getRestaurantcode();
        MultiValueMap<String, String> filter_fields = new LinkedMultiValueMap<>();
        filter_fields.add("filter_fields", "restaurantcode");



        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/menus/get/search_menus/{value}", value)
                .params(filter_fields).param("search_field","restaurantcode"));

        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantcode", CoreMatchers.is("usa-test-1000")));
    }



    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Get_LOCATION_BY_FIELD_ TEST:3   EXCEPTION HANDLING VALUE NOT FOUND")
    @Test
    @Order(10)
    public void Location_get_FIELD_TEST3() throws Exception {

        // given - precondition or setup
        String value = "codenotpresent";
        menus_repository.save(menus_mdb);
        MultiValueMap<String, String> filter_fields = new LinkedMultiValueMap<>();
        filter_fields.add("filter_fields", "restaurantcode");
        String search_field = "restaurantcode";
        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/menus/get/search_menus/{value}", value).params(filter_fields).param("search_field", search_field));

        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", CoreMatchers.is("Cannot find the requested data for the given value: " + value)));
    }


    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Get_LOCATION_BY_FIELD_ TEST:4   EXCEPTION HANDLING FIELD NOT FOUND")
    @Test
    @Order(11)
    public void Location_get_BYFIELD_TEST4() throws Exception {

        // given - precondition or setup
        String value = menus_mdb.getRestaurantcode();
        menus_repository.save(menus_mdb);
        MultiValueMap<String, String> filter_fields = new LinkedMultiValueMap<>();
        filter_fields.add("filter_fields", "restaurant");
        String search_field = "field not valid";
        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/menus/get/search_menus/{value}", value).params(filter_fields).param("search_field", search_field));
        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", CoreMatchers.is("Field:  " + search_field + " Not present please Select valid Field")));
    }





//    ///////////////update

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("PUT_MENU_TEST:1 default")
    @Test
    @Order(12)
    public void Location_update_test() throws Exception {

        // given - precondition or setup

        given(remoteRequest.remote_put_menus_reservation(any(String.class))).willReturn(true);
        menus_repository.save(menus_mdb);

        Menus_Put_DTO  menus_put_dto =new  Menus_Put_DTO();
        menus_put_dto.setId(menus_mdb.getId());
        menus_put_dto.setRestaurant_code(menus_mdb.getRestaurantcode());
        menus_put_dto.setRestaurant_name(menus_mdb.getRestaurantname());
        menus_put_dto.setRestaurant_type("nonn-veg");
        menus_put_dto.setOpenhours(menus_mdb.getOpenhours());
        menus_put_dto.setItems(menus_mdb.getItems());

        String expectedresult = "Data updated Successfully";

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(put("/menus/put/update_menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menus_put_dto)));
        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(expectedresult));
    }






    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("PUT_MENU_TEST:2 EXCEPTION IF INVALID ID IS GIVEN")
    @Test
    @Order(13)
    public void Location_update_test2() throws Exception {

        // given - precondition or setup

        given(remoteRequest.remote_put_menus_reservation(any(String.class))).willReturn(true);
        menus_repository.save(menus_mdb);

        Menus_Put_DTO  menus_put_dto =new  Menus_Put_DTO();
        menus_put_dto.setId(0l);
        menus_put_dto.setRestaurant_code(menus_mdb.getRestaurantcode());
        menus_put_dto.setRestaurant_name(menus_mdb.getRestaurantname());
        menus_put_dto.setRestaurant_type("nonn-veg");
        menus_put_dto.setOpenhours(menus_mdb.getOpenhours());
        menus_put_dto.setItems(menus_mdb.getItems());

        String expectedresult = "Menu with ID "+menus_put_dto.getId()+" not present";
        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(put("/menus/put/update_menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menus_put_dto)));
        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message",CoreMatchers.is(expectedresult)));
    }



    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("PUT_MENU_TEST:3 EXCEPTION IF TRYING TO MODIFY RESTUARANT CODE")
    @Test
    @Order(14)
    public void Location_update_test3() throws Exception {

        // given - precondition or setup

        given(remoteRequest.remote_put_menus_reservation(any(String.class))).willReturn(true);
        menus_repository.save(menus_mdb);

        Menus_Put_DTO  menus_put_dto =new  Menus_Put_DTO();
        menus_put_dto.setId(menus_mdb.getId());
        menus_put_dto.setRestaurant_code("usa-121");
        menus_put_dto.setRestaurant_name(menus_mdb.getRestaurantname());
        menus_put_dto.setRestaurant_type("nonn-veg");
        menus_put_dto.setOpenhours(menus_mdb.getOpenhours());
        menus_put_dto.setItems(menus_mdb.getItems());

        String expectedresult = "U cannot able to modify restaurant name and code only way access location api to do it";
        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(put("/menus/put/update_menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menus_put_dto)));
        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",CoreMatchers.is(expectedresult)));
    }


    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("PUT_MENU_TEST:4 EXCEPTION IF TRYING TO UPDATE WRONG TIME FORMAT")
    @Test
    @Order(15)
    public void Location_update_test4() throws Exception {

        // given - precondition or setup

        given(remoteRequest.remote_put_menus_reservation(any(String.class))).willReturn(true);
        menus_repository.save(menus_mdb);

        Menus_Put_DTO  menus_put_dto =new  Menus_Put_DTO();
        menus_put_dto.setId(menus_mdb.getId());
        menus_put_dto.setRestaurant_code(menus_mdb.getRestaurantcode());
        menus_put_dto.setRestaurant_name(menus_mdb.getRestaurantname());
        menus_put_dto.setRestaurant_type("Non-veg");
        menus_put_dto.setItems(menus_mdb.getItems());
        menus_put_dto.setOpenhours(new OpenHours("10:00-12:2220", "10:00-12:00", "10:00-12:00", "10:00-12:00", "10:00-12:00", "10:00-12:00", "10:00-12:00"));



        String expectedresult = "Please check all time formats are 24hrs ex:00:00-23:30 and must be in a half hour time interval";
        ResultActions response = mockMvc.perform(put("/menus/put/update_menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menus_put_dto)));
        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",CoreMatchers.is(expectedresult)));
    }





//    //////////////delete

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("DELETE_MENUS_RECORD_TEST:1 default")
    @Test
    @Order(16)
    public void Menus_delete_test() throws Exception {

        // given - precondition or setup

        given(remoteRequest.remote_delete_menus_reservation(any(String.class))).willReturn(true);

        menus_repository.save(menus_mdb);
        Long id = menus_mdb.getId();
        String expectedresult = "Record with id -->" + id + " deleted";


        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(delete("/menus/delete/delete_menus/{id}", id));


        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(expectedresult));
    }



    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("DELETE_MENUS_RECORD_TEST:2 EXCEPTION IF RECORD NOT PRESENT ")
    @Test
    @Order(17)
    public void Menus_delete_test2() throws Exception {

        // given - precondition or setup

        given(remoteRequest.remote_delete_menus_reservation(any(String.class))).willReturn(true);

        menus_repository.save(menus_mdb);
        Long id = 0l;
        String expectedresult = "Menu with ID " + id + " not present";


        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(delete("/menus/delete/delete_menus/{id}", id));


        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message",CoreMatchers.is(expectedresult)));
    }






//    /////////////////////////api timings get methods

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("Get_PAGINATION API CALLS TEST1:default")
    @Test
    @Order(18)
    public void API_get_BY_PAGINATION_TEST1() throws Exception {

        // given - precondition or setup
        interceptor_repository.deleteAll();
        List<Interceptor_Data_DB> list_interceptor_data = new ArrayList<>();
        list_interceptor_data.add(new Interceptor_Data_DB(0, "MENUS", "POST", "add_menus", new Date(), 112l));
        list_interceptor_data.add(new Interceptor_Data_DB(0, "MENUS", "POST", "add_menus", new Date(), 112l));
        interceptor_repository.saveAll(list_interceptor_data);
        Long offset = 0l, pageSize = 2l;
        String Microservice = "MENUS";

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/menus/get/api_timing/{offset}/{pageSize}/{Microservice}", offset, pageSize,Microservice));

        // then - verify the result or output using assert statements
        response.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.size", CoreMatchers.is(2)))
                .andExpect(jsonPath("$.content[0].servicename", CoreMatchers.is("POST")))
                .andExpect(jsonPath("$.content[1].url", CoreMatchers.is("add_menus")));
    }






}
