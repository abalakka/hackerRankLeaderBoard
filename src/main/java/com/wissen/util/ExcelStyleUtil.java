package com.wissen.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelStyleUtil
{
	public CellStyle cellStyle(Workbook wb, IndexedColors colour)
	{
		CellStyle style = wb.createCellStyle();
		style.setFillForegroundColor(colour.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		return style;
	}

	public void cellColour(int weekTotal, Cell cell, CellStyle green, CellStyle amber, CellStyle red)
	{
		if (weekTotal >= 6)
			cell.setCellStyle(green);
		else if (weekTotal > 3)
			cell.setCellStyle(amber);
		else
			cell.setCellStyle(red);
	}
}
