package com.youedata.daas.rest.common;

import com.youedata.daas.rest.exception.BizExceptionEnum;
import com.youedata.daas.rest.exception.BussinessException;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExcelUtil {
    /**
     * 解析Excel
     * 获取前五行文件内容
     * @param filePath 文件路径
     *
     * @return String
     */
    public static String parseExcelData(String filePath) {
        // 结果
        StringBuffer rtnStr = new StringBuffer();
        try {
            InputStream is = HdfsUtil.getInstance().openFile(filePath);
            // 获取Workbook
            Workbook wb = WorkbookFactory.create(is);
            /** 得到第一个shell */
            Sheet sheet = wb.getSheetAt(0);
            /** 得到Excel的行数 */
            int totalRows = sheet.getPhysicalNumberOfRows();
            /** 循环Excel的行 */
            for (int r = 0; r < (totalRows > 4 ? 5 : totalRows); r++) {
                rtnStr.append("\n");
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }
                List<String> rowLst = new ArrayList<String>();
                /** 循环Excel的列 */
                for (int c = 0; c < row.getPhysicalNumberOfCells(); c++) {
                    Cell cell = row.getCell(c);
                    String cellValue = "";
                    if (null != cell) {
                        // 以下是判断数据的类型
                        switch (cell.getCellType()) {
                            case HSSFCell.CELL_TYPE_NUMERIC: // 数字
                                cellValue = cell.getNumericCellValue() + "";
                                break;
                            case HSSFCell.CELL_TYPE_STRING: // 字符串
                                cellValue = cell.getStringCellValue();
                                break;
                            case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
                                cellValue = cell.getBooleanCellValue() + "";
                                break;
                            case HSSFCell.CELL_TYPE_FORMULA: // 公式
                                cellValue = cell.getCellFormula() + "";
                                break;
                            case HSSFCell.CELL_TYPE_BLANK: // 空值
                                cellValue = "";
                                break;
                            case HSSFCell.CELL_TYPE_ERROR: // 故障
                                cellValue = "非法字符";
                                break;
                            default:
                                cellValue = "未知类型";
                                break;
                        }
                    }
                    rowLst.add(cellValue.replace("\n","").replace(",",""));
                }
                /** 保存第r行的第c列 */
                if(rowLst.size() == 0) {
                    continue;
                }
                String rowData = StringUtils.join(Arrays.asList(rowLst), ",");
                rtnStr.append(rowData.substring(0, rowData.length()-1).substring(1));
            }

            return rtnStr == null ? null : rtnStr.substring(1).toString();

        } catch (Exception e) {
            throw new BussinessException(BizExceptionEnum.HDFS_FILE_TYPE_NOT_SUPPORT_PREVIEW);
        }
    }
}
