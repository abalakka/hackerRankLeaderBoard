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
import com.wissen.dto.QuestionsModel;

import ch.qos.logback.core.rolling.helper.DateTokenConverter;

import com.wissen.dto.LeaderboardDTO;
import com.wissen.dto.LeaderboardModel;
import com.wissen.dto.QuestionsDTO;

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
				+ "/leaderboard/filter?offset=0&limit=20&include_practice=true&friends=follows&filter_kinds=friends";
	}

	public String questionsUrlFor(String profile) {
		return "https://www.hackerrank.com/rest/hackers/" + profile
				+ "/recent_challenges?limit=1000&cursor=&response_version=v1";
	}

	public HttpHeaders setCookie() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie",
				"_ga=GA1.2.272114498.1565022969; _fbp=fb.1.1565022968736.2058434127; _mkto_trk=id:487-WAY-049&token:_mch-hackerrank.com-1565022972588-51486; hackerrank_mixpanel_token=f3ffdc85-12fd-4316-bab1-f95b32403af3; _biz_uid=8e17c68264bd4f62b31f49c7873e36b3; enableIntellisenseUserPref=true; hacker_editor_theme=light; remember_hacker_token=BAhbCFsGaQPnmBFJIiIkMmEkMTAkQ2FwRkxiay5GcVVuaU5nbER6dGZXTwY6BkVUSSIXMTU2NjAxNjk3Mi4zNTM4NDc1BjsARg%3D%3D--a1d429a89c3842b5226bad3ca9357bc5246bc279; __utmz=74197771.1566017972.23.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); hackerrankx_mixpanel_token=f3ffdc85-12fd-4316-bab1-f95b32403af3; mp_dcd74fdb7c65d92ce5d036daddac0a25_mixpanel=%7B%22distinct_id%22%3A%20%22f3ffdc85-12fd-4316-bab1-f95b32403af3%22%2C%22%24device_id%22%3A%20%2216c9df45581207-01b4beee1f7826-7373e61-100200-16c9df455821ad%22%2C%22%24user_id%22%3A%20%22f3ffdc85-12fd-4316-bab1-f95b32403af3%22%2C%22%24search_engine%22%3A%20%22google%22%2C%22%24initial_referrer%22%3A%20%22https%3A%2F%2Fwww.google.com%2F%22%2C%22%24initial_referring_domain%22%3A%20%22www.google.com%22%7D; _biz_flagsA=%7B%22Version%22%3A1%2C%22Mkto%22%3A%221%22%7D; __zlcmid=tpiT3tAuksMArG; h_r=submissions; h_l=_default; h_v=_default; _biz_nA=13; _biz_pendingA=%5B%5D; react_var=true__trm4; react_var2=true__trm4; hrc_l_i=T; metrics_user_identifier=1198e7-3a05143008d3027b38189dcee0100490db798267; _hrank_session=d594d96c5a3c370d9c725918cad8a6b045fe6ce6be23f027431cde0abe0e93dc91bcedd7b326141c846c4cd00d46547861545f7c25f31944d4ef02f36a9a2110; user_type=hacker; __utma=74197771.272114498.1565022969.1567049610.1567221814.48; __utmc=74197771; mp_bcb75af88bccc92724ac5fd79271e1ff_mixpanel=%7B%22distinct_id%22%3A%20%22f3ffdc85-12fd-4316-bab1-f95b32403af3%22%2C%22%24device_id%22%3A%20%2216c62a349dafe-0a874e5d5e2fe6-c343162-100200-16c62a349db3c0%22%2C%22%24search_engine%22%3A%20%22google%22%2C%22%24initial_referrer%22%3A%20%22https%3A%2F%2Fwww.google.com%2F%22%2C%22%24initial_referring_domain%22%3A%20%22www.google.com%22%2C%22%24user_id%22%3A%20%22f3ffdc85-12fd-4316-bab1-f95b32403af3%22%7D; mp_86cf4681911d3ff600208fdc823c5ff5_mixpanel=%7B%22distinct_id%22%3A%20%2216c62a37811498-08275056632d84-c343162-100200-16c62a3781276d%22%2C%22%24device_id%22%3A%20%2216c62a37811498-08275056632d84-c343162-100200-16c62a3781276d%22%2C%22%24initial_referrer%22%3A%20%22https%3A%2F%2Fwww.hackerrank.com%2Faccess-account%2F%3Fh_r%3Dhome%26h_l%3Dheader%22%2C%22%24initial_referring_domain%22%3A%20%22www.hackerrank.com%22%2C%22%24search_engine%22%3A%20%22google%22%7D; __utmt_candidate_company=1; __utmb=74197771.4.10.1567223086932");
		return headers;
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
		for (Row row : profileSheet) {
			if (rowNum > 77)
				break;

			String name = row.getCell(0).getStringCellValue();
			String profile = row.getCell(1).getStringCellValue();
			nameToProfile.put(profile, name);

			profileToCount.put(profile, new HashMap<>());
		}

		rowNum = 1;
		int maxProfiles = 1;
		for (Row row : profileSheet) {
			if (rowNum > maxProfiles)
				break;
			
			String profile = row.getCell(1).getStringCellValue();

			String url = questionsUrlFor(profile);

			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

			QuestionsDTO resp = null;
			try {
				resp = mapper.readValue(response.getBody(), new TypeReference<QuestionsDTO>() {});
			} catch (IOException e) {
				e.printStackTrace();
			}

			List<QuestionsModel> submissions = resp.getModels();
			for (QuestionsModel currSubmission : submissions) {
				if (!allQuestions.contains(currSubmission.getCh_slug()))
					allQuestions.add(currSubmission.getCh_slug());
			}
		}
		
		
		allQuestions.forEach(questionUrl -> {
			String leaderBoardUrl = leadboardUrlFor(questionUrl);
			HttpHeaders headers = setCookie();

			HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
			ResponseEntity<LeaderboardDTO> respEntity = restTemplate.exchange(leaderBoardUrl, HttpMethod.GET, entity,
					LeaderboardDTO.class);

			System.out.println("/n==============" + questionUrl + "================");
			List<LeaderboardModel> friendsLeaderboard = respEntity.getBody().getModels();
			friendsLeaderboard.forEach(curr -> {
				String currProfile = curr.getHacker();
				Map<LocalDate, Integer> dateToCount = profileToCount.get(currProfile);

				if (curr.getRank() == 1) {
					LocalDate date = LocalDate.ofInstant(Instant.ofEpochSecond(curr.getTimestamp()),
							TimeZone.getDefault().toZoneId());
					dateToCount.putIfAbsent(date, 0);
					dateToCount.put(date, dateToCount.get(date) + 1);
				}
			});
		});

		
		rowNum = 1;
		for(Entry<String, Map<LocalDate, Integer>> currProfile: profileToCount.entrySet()) {
		
			Row boardRow = leaderboardSheet.createRow(rowNum++);
			boardRow.createCell(0).setCellValue(nameToProfile.get(currProfile.getKey()));
			
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
				if (weekTotal >= 6)
					cell.setCellStyle(green);
				else if (weekTotal > 3)
					cell.setCellStyle(amber);
				else
					cell.setCellStyle(red);
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
				if (weekTotal >= 6)
					cell.setCellStyle(green);
				else if (weekTotal > 3)
					cell.setCellStyle(amber);
				else
					cell.setCellStyle(red);
				currWeek++;
				currWeekStart = currWeekEnd;
			}
			headerRow.getCell(currWeek).setCellValue("Total");
			boardRow.createCell(currWeek).setCellValue(total);

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
