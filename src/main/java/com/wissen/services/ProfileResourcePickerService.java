package com.wissen.services;

import java.io.IOException;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.wissen.util.HackerRankException;

@Service
public class ProfileResourcePickerService
{
	private static Resource[] resources;

	private static Logger LOG = LoggerFactory.getLogger(ProfileResourcePickerService.class);

	@Autowired
	ResourcePatternResolver resourceResolver;

	public Resource getResource(String[] args) throws HackerRankException
	{
		try
		{
			if (args.length == 1)
			{
				int idx = Integer.parseInt(args[0]);
				return resources[idx - 1];

			} else if (args.length == 2 && args[0].equals("--spring.output.ansi.enabled=always"))
			{

				int idx = Integer.parseInt(args[1]);
				return resources[idx];
			} else
			{
				throw new HackerRankException("Usage Exception");
			}

		} catch (Exception e)
		{
			warnArgsUsage();
			throw new HackerRankException(e);
		}
	}

	public Resource getWeeklyQuestions() {
		return resources[2];
	}

	private void warnArgsUsage()
	{

		StringBuilder sb = new StringBuilder();

		if (resources.length == 0)
		{
			sb.append("No profile excel file found in src/main/resources/*.xlsx");

		} else
		{
			for (int i = 0; i < resources.length; i++)
			{
				sb.append(i + 1).append(". ").append(resources[i].getFilename()).append("\n");
			}

			sb.append("Run with args  1<= N <= ").append(resources.length);
		}
		LOG.info(sb.toString());
	}

	@PostConstruct
	public void getAllResource()
	{
		try
		{
			if (resources == null)
				resources = resourceResolver.getResources("classpath:*.xlsx");

			Arrays.sort(resources, (a, b) ->
			{
				return a.getFilename().compareTo(b.getFilename());
			});

			LOG.info(Arrays.asList(resources).toString());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
