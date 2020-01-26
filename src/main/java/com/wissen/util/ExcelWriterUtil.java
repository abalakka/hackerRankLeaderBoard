package com.wissen.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wissen.dto.UserRankModel;

public class ExcelWriterUtil
{
	private static final String FILE_SUFFIX = "_" + LocalDate.now() +  ".xlsx";

	private static Logger LOG = LoggerFactory.getLogger(ExcelWriterUtil.class);

	private ExcelStyleUtil excelStyleUtil = new ExcelStyleUtil();

	public void writeToExcel(TreeSet<UserRankModel> rankList, boolean trackingForGrads, LocalDate trackingStartDate)
	{
		Workbook leaderboardWorkbook = formatWorkBook(rankList, trackingForGrads, trackingStartDate);
		saveExcel(leaderboardWorkbook, trackingForGrads);
	}

	public Workbook formatWorkBook(TreeSet<UserRankModel> rankList, boolean trackingForGrads,
			LocalDate trackingStartDate)
	{
		int maxNumNameCharacters = 0;
		int maxNumCollegeNameCharacters = 0;
		int rowNum = 1;

		Workbook leaderboardWorkbook = new XSSFWorkbook();
		Sheet leaderboardSheet = leaderboardWorkbook.createSheet();

		Row headerRow = leaderboardSheet.createRow(0);
		Cell headerCells = headerRow.createCell(0);
		headerCells.setCellValue("Name");

		for (int i = 1; i <= 50; i++)
			headerRow.createCell(i);

		CellStyle red = excelStyleUtil.cellStyle(leaderboardWorkbook, IndexedColors.RED);
		CellStyle amber = excelStyleUtil.cellStyle(leaderboardWorkbook, IndexedColors.YELLOW);
		CellStyle green = excelStyleUtil.cellStyle(leaderboardWorkbook, IndexedColors.GREEN);

		int offset = 2;
		int solvedCell = 1;

		if (trackingForGrads) {
			offset = 3;
			solvedCell = 2;
		}

		int totalWeek = 0;
		for (UserRankModel hacker : rankList)
		{

			String hackerProfile = hacker.getModel().getProfile();

			Row boardRow = leaderboardSheet.createRow(rowNum++);
			String name = hacker.getModel().getHacker();
			maxNumNameCharacters = Math.max(maxNumNameCharacters, name.length());

			String collegeName = "";
			if (trackingForGrads)
			{
				collegeName = hacker.getModel().getCollege();
				maxNumCollegeNameCharacters = Math.max(maxNumCollegeNameCharacters, collegeName.length());
			}

			boardRow.createCell(0).setCellValue(name);

			if (trackingForGrads)
			{
				headerRow.getCell(1).setCellValue("College Name");
				boardRow.createCell(1).setCellValue(collegeName);
			}

			// keeping the start of first week same for both
			// first week is till 25 Aug, 19[Sun]
			LocalDate startDate = trackingStartDate;
			LocalDate currWeekStart = startDate;

			LocalDate firstWeekEnd = null;

			if (trackingForGrads)
				// first week end for grads was this (club started later for them)
				firstWeekEnd = LocalDate.parse("2019-09-09");
			else
				firstWeekEnd = LocalDate.parse("2019-08-26");

			int totalWeeksUntillNow = (int) Math.ceil(ChronoUnit.DAYS.between(firstWeekEnd, LocalDate.now()) / 7.0);
			totalWeeksUntillNow++;

			int total = 0;
			int currWeek = 1;

			Map<LocalDate, Integer> solvedPerDay = hacker.getDateToCount();

			int weekIdx = offset + totalWeeksUntillNow - currWeek + 1;

			while (currWeekStart.compareTo(firstWeekEnd) < 0)
			{
				int weekTotal = 0;
				LocalDate currDate = currWeekStart;
				LocalDate currWeekEnd = firstWeekEnd;
				while (currDate.compareTo(currWeekEnd) < 0)
				{
					weekTotal += solvedPerDay.getOrDefault(currDate, 0);
					currDate = currDate.plusDays(1);
				}

				total += weekTotal;

				headerRow.getCell(weekIdx).setCellValue(currWeek);
				boardRow.createCell(weekIdx).setCellValue(weekTotal);

				Cell cell = boardRow.getCell(weekIdx);
				excelStyleUtil.cellColour(weekTotal, cell, green, amber, red);

				currWeekStart = currWeekEnd;
			}

			startDate = firstWeekEnd;

			currWeekStart = startDate;
			currWeek = 2;
			while (currWeekStart.compareTo(LocalDate.now()) < 0)
			{
				weekIdx = offset + totalWeeksUntillNow - currWeek + 1;

				int weekTotal = 0;
				LocalDate currDate = currWeekStart;
				LocalDate currWeekEnd = currWeekStart.plusDays(7);
				while (currDate.compareTo(currWeekEnd) < 0 && currDate.compareTo(LocalDate.now()) < 0)
				{
					weekTotal += solvedPerDay.getOrDefault(currDate, 0);
					currDate = currDate.plusDays(1);
				}

				total += weekTotal;

				headerRow.getCell(weekIdx).setCellValue(currWeek);
				boardRow.createCell(weekIdx).setCellValue(weekTotal);

				Cell cell = boardRow.getCell(weekIdx);
				excelStyleUtil.cellColour(weekTotal, cell, green, amber, red);

				currWeek++;
				currWeekStart = currWeekEnd;
			}
			totalWeek = currWeek;
			headerRow.getCell(offset).setCellValue("Total");
			boardRow.createCell(offset).setCellValue(total);

			headerRow.getCell(solvedCell).setCellValue("Solved");
			boardRow.createCell(solvedCell).setCellValue(hacker.getModel().isSolvedReqdQuestions());

			LOG.info("Calc done for: " + hackerProfile);
		}

		// so column doesn't get squeezed, this way is better than using
		// autoSizeColumn()
		int width = ((int) (maxNumNameCharacters * 1.14388)) * 256;
		leaderboardSheet.setColumnWidth(0, width);

		if (trackingForGrads)
		{
			width = ((int) (maxNumCollegeNameCharacters * 1.14388 * 0.85)) * 256;
			leaderboardSheet.setColumnWidth(1, width);
		}

		Font boldFont = leaderboardWorkbook.createFont();
		boldFont.setBold(true);
		CellStyle boldStyle = leaderboardWorkbook.createCellStyle();
		boldStyle.setFont(boldFont);
		headerRow.setRowStyle(boldStyle);

		return leaderboardWorkbook;

	}

	private void saveExcel(Workbook leaderboardWorkbook, boolean trackingForGrads)
	{
		FileOutputStream opFile = null;
		try
		{

			String baseDir = System.getenv("BASE_DIR");
			if (baseDir == null)
				baseDir = "";
			else
				baseDir += "/";

			String filePath = baseDir + "leaderBoard" + FILE_SUFFIX;
			if(trackingForGrads)
				filePath = baseDir + "leaderBoard_grads" + FILE_SUFFIX;

			opFile = new FileOutputStream(filePath);

			leaderboardWorkbook.write(opFile);
			opFile.close();
			leaderboardWorkbook.close();
		} catch (IOException e)
		{
			LOG.error(e.getMessage());
			throw new RuntimeException();
		}
	}

}
