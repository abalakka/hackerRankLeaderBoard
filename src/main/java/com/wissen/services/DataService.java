package com.wissen.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wissen.dto.CustomDTO;
import com.wissen.dto.Model;

@Service
public class DataService {

	public String dataFor() {
		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();

		File file = new File(this.getClass().getResource("/profiles.xlsx").getFile());
//		File file = null;
//		try {
//			file = new ClassPathResource("profiles.xlsx").getFile();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		FileInputStream fIP = null;
		try {
			fIP = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		XSSFWorkbook workbook = null;
		try {
			workbook = new XSSFWorkbook(fIP);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Sheet sheet = workbook.getSheetAt(0);

		Workbook leaderboard = new XSSFWorkbook();
		Sheet boardSheet = leaderboard.createSheet();

		Row headerRow = boardSheet.createRow(0);
		Cell headerCells = headerRow.createCell(0);
		headerCells.setCellValue("Name");
//		headerCells = headerRow.createCell(1);
//		headerCells.setCellValue("Profile");
//		headerCells.setCellValue("Week 1, 2019-08-16");

		for (int i = 1; i <= 50; i++)
			headerRow.createCell(i);

		int rowNum = 1;
		for (Row row : sheet) {
			if(rowNum > 67)	
				break;
			
			String hacker = row.getCell(0).getStringCellValue();
			String profile = row.getCell(1).getStringCellValue();
			Row boardRow = boardSheet.createRow(rowNum++);

			boardRow.createCell(0).setCellValue(hacker);
			

			String url = "https://www.hackerrank.com/rest/hackers/" + profile
					+ "/recent_challenges?limit=1000&cursor=&response_version=v1";
			System.out.println(url);
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

			CustomDTO resp = null;
			try {
				resp = mapper.readValue(response.getBody(), new TypeReference<CustomDTO>() {
				});
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Map<LocalDate, Integer> solvedPerDay = new HashMap<>();
			List<Model> submissions = resp.getModels();
			for (Model curr : submissions) {
				LocalDate date = LocalDate.parse(curr.getCreated_at().split("T")[0]);
				solvedPerDay.merge(date, 1, (x, y) -> solvedPerDay.get(date) + 1);
			}
			

			LocalDate startDate = LocalDate.parse("2019-08-16");
			LocalDate currWeekStart = startDate;
			int currWeek = 1;
			while (currWeekStart.compareTo(LocalDate.now()) < 0) {
				int weekTotal = 0;
				LocalDate currDate = currWeekStart;
				LocalDate currWeekEnd = currWeekStart.plusDays(7);
				while (currDate.compareTo(currWeekEnd) < 0) {
					weekTotal += solvedPerDay.getOrDefault(currDate, 0);
					currDate = currDate.plusDays(1);
				}
				headerRow.getCell(currWeek).setCellValue(currWeek + "[" + currWeekStart.toString() + "]");
				boardRow.createCell(currWeek).setCellValue(weekTotal);
				currWeek++;
				currWeekStart = currWeekEnd;
			}

			try {
				System.out.println("Calculation done for: " + hacker);
				Thread.sleep(777);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}

		FileOutputStream opFile = null;
		try {
			opFile = new FileOutputStream("leaderboard.xlsx");
			leaderboard.write(opFile);
			opFile.close();
			leaderboard.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		return resp.getModels().toString();
		return "done";
	}

}
