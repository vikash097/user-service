package com.demo.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.demo.user_service.feignclient.UserServiceClient;

@RestController
@RequestMapping("/user")
@RefreshScope
public class UserController {

	@Autowired
	private DiscoveryClient discoveryClient;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	UserServiceClient feignClient;

	@GetMapping("/resttemplate-direct")
	public String callAccountService() {
		RestTemplate restTemplate = new RestTemplate();
		String serviceUrl = discoveryClient.getInstances("ACCOUNT-SERVICE").stream().findFirst()
				.map(si -> si.getUri().toString()).orElseThrow(() -> new RuntimeException("Service not found"));
		System.out.println("Base URl of ACCOUNT_SERVICE - " + serviceUrl);
		return restTemplate.getForObject(serviceUrl + "/account/resttemplate", String.class);
	}

	//Manual selecting of instance to make Rest call
	@GetMapping("/resttemplate-config")
	public String displayConfigMessage() {

		RestTemplate restTemplate = new RestTemplate();
		String serviceUrl = discoveryClient.getInstances("ACCOUNT-SERVICE").stream().findFirst()
				.map(si -> si.getUri().toString()).orElseThrow(() -> new RuntimeException("Service not found"));
		System.out.println("Base URl of ACCOUNT_SERVICE - " + serviceUrl);
		return restTemplate.getForObject(serviceUrl + "/account/config", String.class);
	}

	// Spring Cloud LoadBalancer will resolve ACCOUNT-SERVICE to an actual instance
	// using the discovery client.
	@GetMapping("/loadbalancer-hello")
	public String displayMessage() {
		return restTemplate.getForObject("http://ACCOUNT-SERVICE/account/hello", String.class);
	}

	// Feign client has built in support for Eureka and load balancer
	@GetMapping("/feign")
	public String displayMessageByFeignClient() {
		return feignClient.displayMessageByFeignClient();
	}
}
