package com.demo.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/user")
@RefreshScope
public class UserController {

	@Autowired
	private DiscoveryClient discoveryClient;

	@Value("${location.name}")
	private String commonEnvName;
//	@Value("${user.ms.location.name}")
//	private String userMsEnvName;

	@GetMapping
	public String callAccountService() {

		RestTemplate restTemplate = new RestTemplate();
		String serviceUrl = discoveryClient.getInstances("ACCOUNT-SERVICE").stream().findFirst()
				.map(si -> si.getUri().toString()).orElseThrow(() -> new RuntimeException("Service not found"));
		return restTemplate.getForObject(serviceUrl + "/account", String.class);
	}

	@GetMapping("/properties")
	public String displayProperties() {
//		"User-MS properties - " + userMsEnvName 
		return  ", Common-Properties - " + commonEnvName;
	}
}
