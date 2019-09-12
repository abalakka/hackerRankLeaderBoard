package com.wissen.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.poi.ss.usermodel.Font;
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

		// newgrads tracking => jigar's cookie
		// employees tracking => anirudh's cookie
		// based on "profileFilename" parameter in dataFor()

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

	public String dataFor(InputStream fIP,String profileFilename) {
		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();

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
		int maxProfiles = profileSheet.getLastRowNum();
		for (Row row : profileSheet) {
			if (rowNum++ > maxProfiles)
				break;

			try{
				String name = row.getCell(0).getStringCellValue();
				String profile = row.getCell(1).getStringCellValue();
				System.out.println(name + " -> " + profile);
				nameToProfile.put(profile.toLowerCase(), name);
				profileToCount.put(profile.toLowerCase(), new HashMap<>());
			}catch(NullPointerException e){
				break;
			}
		}

		System.out.println("\n\n\n\n");

		rowNum = 1;
		// for (Row row : profileSheet) {
		for (String profile : nameToProfile.keySet()) {
			if (rowNum++ > maxProfiles)
				break;

			// String profile = row.getCell(1).getStringCellValue();

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
				Thread.sleep((long) (Math.random() * 250));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("\n\n\n\n");
		System.out.println("Starting the partaaay");
		rowNum = 1;
		// allQuestions.forEach(questionUrl -> {
		for (String questionUrl : allQuestions) {
			String leaderBoardUrl = leadboardUrlFor(questionUrl);

			// if(rowNum++ > 20)
			// break;

			HttpHeaders headers = setCookie();

			HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
			boolean infiniteLoop = true;
			ResponseEntity<LeaderboardDTO> respEntity=null;

			while(infiniteLoop){

				try{
					respEntity = restTemplate.exchange(leaderBoardUrl, HttpMethod.GET, entity,LeaderboardDTO.class);

					infiniteLoop = false;

				}catch(IllegalStateException e){
					System.out.println("Arghh!! Bad Gateway for "+ questionUrl);
				}
			}
			System.out.println("==============" + questionUrl + "================ " + rowNum++);

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
				Thread.sleep((long) (Math.random() * 100));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// )
		;

		rowNum = 1;
		int maxNumCharacters = 0;
		for (Entry<String, Map<LocalDate, Integer>> currProfile : profileToCount.entrySet()) {

			Row boardRow = leaderboardSheet.createRow(rowNum++);
			maxNumCharacters = Math.max(maxNumCharacters,currProfile.getKey().length());

			boardRow.createCell(0)
					.setCellValue(nameToProfile.getOrDefault(currProfile.getKey(), currProfile.getKey().toLowerCase()));

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

			if(profileFilename.contains("grads"))
				startDate = LocalDate.parse("2019-09-09");
			else
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

		//so column doesn't get squeezed, this way is better than using autoSizeColumn()
		int width = ((int)(maxNumCharacters * 1.14388)) * 256;
		leaderboardSheet.setColumnWidth(0, width);

		Font boldFont = leaderboardWorkbook.createFont();
		boldFont.setBold(true);
		CellStyle boldStyle = leaderboardWorkbook.createCellStyle();
		boldStyle.setFont(boldFont);
		headerRow.setRowStyle(boldStyle);

		FileOutputStream opFile = null;
		try {

			opFile = new FileOutputStream("leaderboard_"+profileFilename);

			leaderboardWorkbook.write(opFile);
			opFile.close();
			leaderboardWorkbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "done";
	}

}
