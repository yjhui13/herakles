package com.lcw.herakles.platform.product.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import com.lcw.herakles.platform.common.constant.ApplicationConsts;
import com.lcw.herakles.platform.common.service.AbstractExcelService;
import com.lcw.herakles.platform.product.dto.ProductDto;

/**
 * 导出excel
 * 
 * @author chenwulou
 *
 */
@Service
public class ProductInfoExcelExportService extends AbstractExcelService {

	@Override
	protected void buildExcelData(Map<String, Object> model, Workbook workbook) {
		@SuppressWarnings("unchecked")
		List<ProductDto> list = (List<ProductDto>) model.get(ApplicationConsts.REPORT_DATA);
		int rownum = 1;
		Row row = null;
		Cell cell = null;
		Sheet sheet = workbook.getSheetAt(0);
		CellStyle style = this.getStyle(workbook);
		for (ProductDto dto : list) {
			// 创建行
			row = sheet.createRow(rownum);
			// 序号
			cell = row.createCell(0);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(rownum);
			cell.setCellStyle(style);

			// 名称
			cell = row.createCell(1);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(dto.getName());
			cell.setCellStyle(style);

			// 描述
			cell = row.createCell(2);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(dto.getDescr());
			cell.setCellStyle(style);

			rownum++;
		}
	}

}
