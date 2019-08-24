package com.wissen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wissen.services.DataService;

@Controller
@RequestMapping(value = "/leaderboard")
public class DataController {

	@Autowired
	DataService dataService;
	
	@GetMapping
	public ResponseEntity<?> userData() {
		String res = dataService.dataFor();
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
}
