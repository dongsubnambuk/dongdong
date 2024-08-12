package com.capstone_ex.chat_server.Service.Communication;

import com.capstone_ex.chat_server.DTO.ExternalDTO.ExternalUserInfoDTO;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Service
public class CommunicationServiceImpl implements CommunicationService{

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;


    public CommunicationServiceImpl(DiscoveryClient discoveryClient, RestTemplate restTemplate) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = restTemplate;
    }


    //Login-Server 유저 정보 받아오기
    @Override
    public ExternalUserInfoDTO getUserInfo(String uniqueId) {
        List<ServiceInstance> instances = discoveryClient.getInstances("LOGIN-SERVER");
        if (instances == null || instances.isEmpty()){
            throw new IllegalStateException("No LOGIN-SERVER instances available");
        }
        ServiceInstance loginService = instances.get(0);
        URI uri = URI.create(loginService.getUri() + "/api/user/id/" + uniqueId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<ExternalUserInfoDTO> response;
        try {
            response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, ExternalUserInfoDTO.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send request to LOGIN-SERVER", e);
        }

        if (response.getStatusCode().is2xxSuccessful()) {
            ExternalUserInfoDTO userDTO = response.getBody();
            if (userDTO != null) {
                System.out.println("Received response from LOGIN-SERVER: " + userDTO);
                return userDTO;
            }
        } else {
            throw new IllegalStateException("Failed to fetch user info from LOGIN-SERVER");
        }
        return null;
    }
}
