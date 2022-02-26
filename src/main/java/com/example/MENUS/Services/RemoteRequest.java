package com.example.MENUS.Services;

import com.example.MENUS.DTO.Remote_Put_location_Menus_Reservation_DTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class RemoteRequest {
    @Value("${Remote.url_check}")
    private String url_check;

    @Value("${Remote.url_update_remote}")
    private String url_update_remote;

    @Value("${Remote.url_delete_remote}")
    private String url_delete_remote;

    @Value("${Remote.url_update}")
    private String url_update;

    @Value("${Remote.url_delete}")
    private String url_delete;


    private static final Logger log = LogManager.getLogger(RemoteRequest.class.getName());

    public Integer remote_check_get_menus_location(String url_data) {

        log.info("REMOTE REQUEST: ENTERED INTO THE CHECK_REMOTE_DATA");
        HttpHeaders headers = getHeaders();
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
        ResponseEntity<Integer> response_entity = restTemplate.exchange(url_check + url_data, HttpMethod.GET, requestEntity, Integer.class, 1);

        log.debug("REMOTE REQUEST: EXITED FROM THE CHECK_REMOTE_DATA");
        return response_entity.getBody();


    }

    public boolean remote_put_location_menus_reservation(Remote_Put_location_Menus_Reservation_DTO data) {

        log.info("REMOTE REQUEST: ENTERED INTO THE CHECK_REMOTE_DATA");
        HttpHeaders headers = getHeaders();
        RestTemplate restTemplate = new RestTemplate();


        HttpEntity<Remote_Put_location_Menus_Reservation_DTO> requestEntity = new HttpEntity<>(data, headers);
        ResponseEntity<Boolean> response_entity = restTemplate.exchange(url_update_remote, HttpMethod.PUT, requestEntity, Boolean.class);

        log.debug("REMOTE REQUEST: EXITED FROM THE CHECK_REMOTE_DATA");
        return response_entity.getBody();

    }


    public boolean remote_put_menus_reservation(String code) {

        log.info("REMOTE REQUEST: ENTERED INTO THE CHECK_REMOTE_DATA");
        HttpHeaders headers = getHeaders();
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Boolean> response_entity = restTemplate.exchange(url_update + code, HttpMethod.PUT, requestEntity, Boolean.class);

        log.debug("REMOTE REQUEST: EXITED FROM THE CHECK_REMOTE_DATA");
        return response_entity.getBody();

    }


    public boolean remote_delete_location_menus_reservation(String code) {

        log.info("REMOTE REQUEST: ENTERED INTO THE CHECK_REMOTE_DATA");
        HttpHeaders headers = getHeaders();
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Boolean> response_entity = restTemplate.exchange(url_delete_remote + code, HttpMethod.DELETE, requestEntity, Boolean.class, 1);

        log.debug("REMOTE REQUEST: EXITED FROM THE CHECK_REMOTE_DATA");
        return response_entity.getBody();

    }


    public boolean remote_delete_menus_reservation(String code) {

        log.info("REMOTE REQUEST: ENTERED INTO THE CHECK_REMOTE_DATA");
        HttpHeaders headers = getHeaders();
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Boolean> response_entity = restTemplate.exchange(url_delete + code, HttpMethod.DELETE, requestEntity, Boolean.class, 1);

        log.debug("REMOTE REQUEST: EXITED FROM THE CHECK_REMOTE_DATA");
        return response_entity.getBody();

    }


    private HttpHeaders getHeaders() {
        log.info("REMOTE REQUEST: Entered into the getHeaders");
        String credentials = "jose:jose@";
        String encodeCredential = new String(Base64.encodeBase64(credentials.getBytes()));
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.add("Authorization", "Basic " + encodeCredential);
        log.debug("REMOTE REQUEST: EXITED FROM THE getHeaders");
        return header;
    }


}
