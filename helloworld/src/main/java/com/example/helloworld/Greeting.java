package com.example.helloworld;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Greeting {
	@RequestMapping("/greeting")
	public @ResponseBody String greeting() 
	{
		return "Hello world!";
	}
}
