package com.wissen;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.wissen.services.DataService;

@SpringBootApplication
public class HackerRankLeaderboardApplication implements CommandLineRunner {

	@Autowired
	DataService dataService;

	@Autowired
	ResourcePatternResolver resourceResolver;

	static Resource[] rs;
	private static Logger LOG = LoggerFactory.getLogger(HackerRankLeaderboardApplication.class);

	public static void main(String[] args) {

		SpringApplication.run(HackerRankLeaderboardApplication.class, args);

	}

	@Override
	public void run(String... args) throws Exception {
		if (args.length == 1 && args[0].equals("--spring.output.ansi.enabled=always")) {
			help(true);
		} else if (args.length == 1) {
			int idx = getFileNumber(args[0]);

			help(false);

			if (idx < 0 || idx > rs.length) {
				help(true);
			} else {
				_run(idx);
			}

		} else if (args.length == 2 && args[0].equals("--spring.output.ansi.enabled=always")) {

			int idx = getFileNumber(args[1]);
			help(false);
			if (idx < 0 || idx > rs.length) {
				help(true);
			} else {

				_run(idx);
			}

		} else {
			help(true);
		}
	}

	private void _run(int idx) throws IOException {
		System.out.println(dataService.dataFor(rs[idx-1].getInputStream(),idx));		
	}

	private int getFileNumber(String arg) {
		try {
			return Integer.parseInt(arg);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private void help(boolean print) {

		StringBuilder sb = new StringBuilder();

		try {
			if (rs == null)
				rs = resourceResolver.getResources("classpath:*.xlsx");
			
			System.out.println(Arrays.asList(rs));
			
			if (print) {
				if (rs.length == 0) {
					sb.append("No profile excel file found in src/main/resources/*.xlsx");

				} else {
					for (int i = 0; i < rs.length; i++) {
						sb.append(i + 1).append(". ").append(rs[i].getFilename()).append("\n");
					}

					sb.append("Run with args  1<= N <= ").append(rs.length);
				}
				System.out.println(sb);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
