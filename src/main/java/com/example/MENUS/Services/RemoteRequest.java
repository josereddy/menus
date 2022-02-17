package com.example.MENUS.Services;

import com.example.MENUS.DTO.Remote_Put_location_Menus_Reservation_DTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RemoteRequest {


    private static final Logger log = LogManager.getLogger(RemoteRequest.class.getName());

    public Integer remote_check_get_menus_location(String url_data) {

        log.info("REMOTE REQUEST: ENTERED INTO THE CHECK_REMOTE_DATA");
        HttpHeaders headers = getHeaders();
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8081/locations/get/check_menus_location/"+url_data;
        System.out.println(url);
        HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
        ResponseEntity<Integer> response_entity=restTemplate.exchange(url, HttpMethod.GET, requestEntity, Integer.class,1);

        log.debug("REMOTE REQUEST: EXITED FROM THE CHECK_REMOTE_DATA");
        return  response_entity.getBody();


    }

    public boolean remote_put_location_menus_reservation(Remote_Put_location_Menus_Reservation_DTO data) {

        log.info("REMOTE REQUEST: ENTERED INTO THE CHECK_REMOTE_DATA");
        HttpHeaders headers = getHeaders();
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8083/reservation/put/remote_update_location_menus_reservation";
        System.out.println(url);
        HttpEntity<Remote_Put_location_Menus_Reservation_DTO> requestEntity = new HttpEntity<>(data,headers);
        ResponseEntity<Boolean> response_entity=restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Boolean.class);

        log.debug("REMOTE REQUEST: EXITED FROM THE CHECK_REMOTE_DATA");
        return  response_entity.getBody();

    }



    public boolean remote_put_menus_reservation(String code) {

        log.info("REMOTE REQUEST: ENTERED INTO THE CHECK_REMOTE_DATA");
        HttpHeaders headers = getHeaders();
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8083/reservation/put/remote_update_menus_reservation/"+code;
        System.out.println(url);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Boolean> response_entity=restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Boolean.class);

        log.debug("REMOTE REQUEST: EXITED FROM THE CHECK_REMOTE_DATA");
        return  response_entity.getBody();

    }








    public boolean remote_delete_location_menus_reservation(String code) {

        log.info("REMOTE REQUEST: ENTERED INTO THE CHECK_REMOTE_DATA");
        HttpHeaders headers = getHeaders();
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8083/reservation/delete/remote_delete_location_menus_reservation/"+code;
        System.out.println(url);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Boolean> response_entity=restTemplate.exchange(url, HttpMethod.DELETE, requestEntity,Boolean.class,1);

        log.debug("REMOTE REQUEST: EXITED FROM THE CHECK_REMOTE_DATA");
        return  response_entity.getBody();

    }





    public boolean remote_delete_menus_reservation(String code) {

        log.info("REMOTE REQUEST: ENTERED INTO THE CHECK_REMOTE_DATA");
        HttpHeaders headers = getHeaders();
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8083/reservation/delete/remote_delete_menus_reservation/"+code;
        System.out.println(url);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Boolean> response_entity=restTemplate.exchange(url, HttpMethod.DELETE, requestEntity,Boolean.class,1);

        log.debug("REMOTE REQUEST: EXITED FROM THE CHECK_REMOTE_DATA");
        return  response_entity.getBody();

    }




    private HttpHeaders getHeaders()
    {
        log.info("REMOTE REQUEST: Entered into the getHeaders");
        String  credentials ="jose:jose@";
        String encodeCredential = new String(Base64.encodeBase64(credentials.getBytes()));
        HttpHeaders header =new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.add("Authorization","Basic "+encodeCredential);
        log.debug("REMOTE REQUEST: EXITED FROM THE getHeaders");
        return header;
    }


}
