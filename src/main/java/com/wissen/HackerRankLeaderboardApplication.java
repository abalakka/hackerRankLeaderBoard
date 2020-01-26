package com.wissen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;

import com.wissen.services.DataService;
import com.wissen.services.ProfileResourcePickerService;
import com.wissen.util.HackerRankException;

@SpringBootApplication
public class HackerRankLeaderboardApplication implements CommandLineRunner
{

	@Autowired
	DataService dataService;

	@Autowired
	ProfileResourcePickerService pickerService;

	private static Logger LOG = LoggerFactory.getLogger(HackerRankLeaderboardApplication.class);

	public static void main(String[] args)
	{

		SpringApplication.run(HackerRankLeaderboardApplication.class, args);

	}

	@Override
	public void run(String... args) throws Exception
	{

		try
		{
			long startTime = System.currentTimeMillis();

			LOG.info("Strating Service");

			LOG.info("Trying to Pick Resource");
			Resource resource = pickerService.getResource(args);
			Resource weeklyQuestions = pickerService.getWeeklyQuestions();
			LOG.info("Picked Resource Successfully");

			LOG.info("Invoking Data Service to write LeadBoard");
			dataService.service(resource, weeklyQuestions);
			LOG.info("LeadBoard Written Successfully");

			long endTime = System.currentTimeMillis();

			System.exit(1);
		} catch (HackerRankException e)
		{
			LOG.error(e.getMessage(), e);
			System.exit(1);
		}
	}
}
