package com.example.MENUS.Service;

import static org.assertj.core.api.Assertions.*;

import com.example.MENUS.Document.Items;
import com.example.MENUS.Document.Menus_MDB;
import com.example.MENUS.Document.OpenHours;
import com.example.MENUS.Repository.Menus_Repository;
import com.example.MENUS.Services.Check_ConvertService;
import com.example.MENUS.Services.CrudServices;
import com.example.MENUS.Services.RemoteRequest;
import com.example.MENUS.Services.SequenceGeneratorService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.BDDMockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CrudService_Test {


    @Mock
    private Menus_Repository menus_repository;
    @Mock
    private Check_ConvertService cc_Service;
    @Mock
    private RemoteRequest remoteRequest;
    @Mock
    private SequenceGeneratorService seq_service;

    @InjectMocks
    private CrudServices crudService;


    List<Items> items_list = new ArrayList<Items>();
    private Menus_MDB menus_mdb = new Menus_MDB();

    @BeforeEach
    public void setup_data() {

        items_list.add(new Items("Cicken", "parrota"));
        menus_mdb.setId(1l);
        menus_mdb.setRestaurantcode("usa-test-1000");
        menus_mdb.setRestaurantname("usa-test-chicken");
        menus_mdb.setItems(items_list);
        menus_mdb.setRestauranttype("veg");
        menus_mdb.setOpenhours(new OpenHours("10:00-12:00", "10:00-12:00", "10:00-12:00", "10:00-12:00", "10:00-12:00", "10:00-12:00", "10:00-12:00"));

    }


//    //////////////delete

    @WithMockUser(username = "jose", password = "jose@", roles = "ADMIN")
    @DisplayName("DELETE_MENUS_RECORD_TEST:1 default")
    @Test
    public void Menus_delete_test() throws Exception {

        // given - precondition or setup

        given(menus_repository.findById(any(Long.class))).willReturn(Optional.ofNullable(menus_mdb));
        given(remoteRequest.remote_delete_menus_reservation(any(String.class))).willReturn(true);
        willDoNothing().given(menus_repository).deleteById(any(Long.class));
        Long id = menus_mdb.getId();
        String expectedresult = "Record with id -->" + id + " deleted";


        // when - action or behaviour that we are going test
        Boolean response = crudService.delete_menus(id);


        // then - verify the result or output using assert statements
        assertThat(response).isTrue();
    }


}