package com.wissen.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wissen.dto.UserModel;

public class ExcelReaderUtil
{
	private static Logger LOG = LoggerFactory.getLogger(ExcelReaderUtil.class);

	public List<UserModel> getValidProfiles(InputStream fIP) throws HackerRankException
	{
		List<UserModel> users = new ArrayList<UserModel>();
		LOG.info("Read Input Profile Excel");
		try (XSSFWorkbook profileWorkbook = new XSSFWorkbook(fIP))
		{
			Sheet profileSheet = profileWorkbook.getSheetAt(0);
			int maxProfiles = profileSheet.getPhysicalNumberOfRows();
			if (maxProfiles < 1)
				return users;
			for (Row row : profileSheet)
			{
				try
				{
					UserModel user = new UserModel();
					boolean isEliminated = (int) row.getCell(2).getNumericCellValue() == 0 ? false : true;
					if (!isEliminated)
					{
						String name = row.getCell(0).getStringCellValue();
						String profile = row.getCell(1).getStringCellValue();
						user.setHacker(name);
						user.setProfile(profile);

						Cell c = row.getCell(3);
						if (c != null && c.getCellType() != CellType.BLANK)
						{
							String collegeName = row.getCell(3).getStringCellValue();
							user.setCollege(collegeName);
						}
						users.add(user);
					}

				} catch (NullPointerException e)
				{
					break;
				}
			}
		} catch (Exception e)
		{
			throw new HackerRankException(e);
		}
		return users;
	}

	public Set<String> requiredQuestions(InputStream inputStream) {
		Set<String> questions = new HashSet<>();

		XSSFWorkbook workbook = null;
		try {
			workbook = new XSSFWorkbook(inputStream);
		} catch (IOException e) {
			LOG.info(e.getMessage());
		}

		Sheet sheet = workbook.getSheetAt(0);

		boolean firstRow = true;
		for (Row row : sheet) {
			if (firstRow) {
				firstRow = false;
				continue;
			}
			try {
				String questionSlug = row.getCell(1).getStringCellValue();
				questions.add(questionSlug.split("/")[4]);
			} catch (NullPointerException npe) {
				LOG.info(npe.getMessage());
				break;
			} catch (Exception e) {
				LOG.info(e.getMessage());
			}

		}
		LOG.info("Finished reading required questions excel");

		return questions;
	}
}
