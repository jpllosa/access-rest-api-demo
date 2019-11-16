package com.example.demo;

import java.io.IOException;
import java.net.URI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@Controller
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@GetMapping("/postcode")
	public String getPostcode(@RequestParam(name="p", required=false, defaultValue="") String postcode,
			Model model,
			RestTemplate restTemplate) throws Throwable {
		String url = "https://api.postcodes.io/postcodes/" + postcode;
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
		URI uri = builder.build().toUri();
		String json = restTemplate.getForObject(uri, String.class); // better if json is mapped to a pojo automatically
		model.addAttribute("primary_care_trust", getJsonValue("primary_care_trust", json));
		model.addAttribute("json", json);

		return "primary-care-trust";
	}

	private String getJsonValue(String key, String json) {
		ObjectMapper mapper = new ObjectMapper();

		try {

		    JsonNode jsonNode = mapper.readValue(json, JsonNode.class);
		    JsonNode resultNode = jsonNode.get("result");
		    JsonNode primaryCareTrustNode = resultNode.get(key);
		    String value = primaryCareTrustNode.asText();
		    if (null != value) {
		    	return value;
		    }

		} catch (IOException e) {
		    e.printStackTrace();
		}

		return "";
	}

}
