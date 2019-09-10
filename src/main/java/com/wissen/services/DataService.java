package com.wissen.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wissen.dto.LeaderboardDTO;
import com.wissen.dto.LeaderboardModel;
import com.wissen.dto.QuestionsDTO;
import com.wissen.dto.QuestionsModel;

@Service
public class DataService {

	public CellStyle cellStyle(Workbook wb, IndexedColors colour) {
		CellStyle style = wb.createCellStyle();
		style.setFillForegroundColor(colour.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		return style;
	}

	public String leadboardUrlFor(String questionUrl) {
		return "https://www.hackerrank.com/rest/contests/master/challenges/" + questionUrl
				+ "/leaderboard/filter?offset=0&limit=100&include_practice=true&friends=follows&filter_kinds=friends";
	}

	public String questionsUrlFor(String profile) {
		return "https://www.hackerrank.com/rest/hackers/" + profile
				+ "/recent_challenges?limit=1000&cursor=&response_version=v1";
	}

	public HttpHeaders setCookie() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", "{COOKIE}");
		return headers;
	}

	public void cellColour(int weekTotal, Cell cell, CellStyle green, CellStyle amber, CellStyle red) {
		if (weekTotal >= 6)
			cell.setCellStyle(green);
		else if (weekTotal > 3)
			cell.setCellStyle(amber);
		else
			cell.setCellStyle(red);
	}

	public String dataFor() {
		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();

		File file = new File(this.getClass().getResource("/profiles.xlsx").getFile());

		FileInputStream fIP = null;
		try {
			fIP = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		XSSFWorkbook profileWorkbook = null;
		try {
			profileWorkbook = new XSSFWorkbook(fIP);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Sheet profileSheet = profileWorkbook.getSheetAt(0);

		Workbook leaderboardWorkbook = new XSSFWorkbook();
		Sheet leaderboardSheet = leaderboardWorkbook.createSheet();

		Row headerRow = leaderboardSheet.createRow(0);
		Cell headerCells = headerRow.createCell(0);
		headerCells.setCellValue("Name");

		CellStyle red = cellStyle(leaderboardWorkbook, IndexedColors.RED);
		CellStyle amber = cellStyle(leaderboardWorkbook, IndexedColors.YELLOW);
		CellStyle green = cellStyle(leaderboardWorkbook, IndexedColors.GREEN);

		for (int i = 1; i <= 50; i++)
			headerRow.createCell(i);

		Set<String> allQuestions = new HashSet<>();

		// <Name, profile>
		Map<String, String> nameToProfile = new HashMap<>();

		// <profile, <Week, Solved>>
		Map<String, Map<LocalDate, Integer>> profileToCount = new HashMap<>();

		int rowNum = 1;
		int maxProfiles = 77;
		for (Row row : profileSheet) {
			if (rowNum > maxProfiles)
				break;

			String name = row.getCell(0).getStringCellValue();
			String profile = row.getCell(1).getStringCellValue();
			nameToProfile.put(profile, name);

			profileToCount.put(profile, new HashMap<>());
		}

		rowNum = 1;
		for (Row row : profileSheet) {
			if (rowNum++ > maxProfiles)
				break;

			String profile = row.getCell(1).getStringCellValue();

			String url = questionsUrlFor(profile);
			System.out.println("url is: " + url);
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

			QuestionsDTO resp = null;
			try {
				resp = mapper.readValue(response.getBody(), new TypeReference<QuestionsDTO>() {
				});
			} catch (IOException e) {
				e.printStackTrace();
			}

			List<QuestionsModel> submissions = resp.getModels();
			for (QuestionsModel currSubmission : submissions) {
				if (!allQuestions.contains(currSubmission.getCh_slug()))
					allQuestions.add(currSubmission.getCh_slug());
			}

			try {
				Thread.sleep((long) (Math.random() * 777));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Starting the partaaay");
		rowNum = 1;
		// allQuestions.forEach(questionUrl -> {
		for (String questionUrl : allQuestions) {
			String leaderBoardUrl = leadboardUrlFor(questionUrl);

			// if(rowNum++ > 20)
			// break;

			HttpHeaders headers = setCookie();

			HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
			ResponseEntity<LeaderboardDTO> respEntity = restTemplate.exchange(leaderBoardUrl, HttpMethod.GET, entity,
					LeaderboardDTO.class);

			System.out.println("\n==============" + questionUrl + "================ " + rowNum++);

			List<LeaderboardModel> friendsLeaderboard = respEntity.getBody().getModels();
			friendsLeaderboard.forEach(curr -> {
				String currProfile = curr.getHacker().toLowerCase();
				// System.out.println(currProfile + " Current profile");
				Map<LocalDate, Integer> dateToCount = profileToCount.getOrDefault(currProfile, new HashMap<>());

				if (curr.getRank().equals("1")) {

					LocalDate date = Instant.ofEpochSecond(curr.getTime_taken())
							.atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
					// if(date.compareTo(LocalDate.parse("2019-01-01")) < 0)
					// System.out.println(date);
					dateToCount.putIfAbsent(date, 0);
					dateToCount.put(date, dateToCount.get(date) + 1);
					// System.out.println(currProfile + " " + questionUrl + " TEMP: " + temp);
				}
			});
			try {
				Thread.sleep((long) (Math.random() * 777));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// )
		;

		rowNum = 1;
		for (Entry<String, Map<LocalDate, Integer>> currProfile : profileToCount.entrySet()) {

			Row boardRow = leaderboardSheet.createRow(rowNum++);
			boardRow.createCell(0).setCellValue(nameToProfile.getOrDefault(currProfile.getKey(), currProfile.getKey()));

			int total = 0;
			Map<LocalDate, Integer> solvedPerDay = currProfile.getValue();

			// first week is till 25 Aug, 19[Sun]
			LocalDate startDate = LocalDate.parse("2019-08-16");
			LocalDate currWeekStart = startDate;
			int currWeek = 1;
			while (currWeekStart.compareTo(LocalDate.parse("2019-08-26")) < 0) {
				int weekTotal = 0;
				LocalDate currDate = currWeekStart;
				LocalDate currWeekEnd = LocalDate.parse("2019-08-26");
				while (currDate.compareTo(currWeekEnd) < 0) {
					weekTotal += solvedPerDay.getOrDefault(currDate, 0);
					currDate = currDate.plusDays(1);
				}

				total += weekTotal;

				headerRow.getCell(currWeek).setCellValue("Week " + currWeek);
				boardRow.createCell(currWeek).setCellValue(weekTotal);

				Cell cell = boardRow.getCell(currWeek);
				cellColour(weekTotal, cell, green, amber, red);

				currWeekStart = currWeekEnd;
			}

			startDate = LocalDate.parse("2019-08-26");
			currWeekStart = startDate;
			currWeek = 2;
			while (currWeekStart.compareTo(LocalDate.now()) < 0) {
				int weekTotal = 0;
				LocalDate currDate = currWeekStart;
				LocalDate currWeekEnd = currWeekStart.plusDays(7);
				while (currDate.compareTo(currWeekEnd) < 0) {
					weekTotal += solvedPerDay.getOrDefault(currDate, 0);
					currDate = currDate.plusDays(1);
				}

				total += weekTotal;

				headerRow.getCell(currWeek).setCellValue("Week " + currWeek);
				boardRow.createCell(currWeek).setCellValue(weekTotal);

				Cell cell = boardRow.getCell(currWeek);
				cellColour(weekTotal, cell, green, amber, red);

				currWeek++;
				currWeekStart = currWeekEnd;
			}
			headerRow.getCell(currWeek).setCellValue("Total");
			boardRow.createCell(currWeek).setCellValue(total);

			System.out.println("Calc done for: " + currProfile.getKey());
		}

		FileOutputStream opFile = null;
		try {
			opFile = new FileOutputStream("leaderboard.xlsx");
			leaderboardWorkbook.write(opFile);
			opFile.close();
			leaderboardWorkbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "done";
	}

}
