package com.kivatek.bmfcgen;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PoiHelper {
    /**
     * 空白セルかどうかを判定する
     * @param cell
     * @return
     */
    public static boolean isBlankCell(Cell cell) {
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                    return true;
                case Cell.CELL_TYPE_STRING:
                    return StringUtils.isBlank(cell.getStringCellValue());
            }
        }
        return false;
    }

    /**
     * Cellの内容を文字列として取得。数値もいったん文字列で取得する。
     *
     * @param cell
     * @return
     */
    public static String getContentString(Cell cell) {
        // poiでは内容がないcellの情報は本当にないものとして扱われる。
        // ところが長さ0の文字列など見かけの情報がなくてもcellの情報が存在することはあり別途チェックすることになる。
        if (cell != null && isBlankCell(cell) == false) {
            int cellType = cell.getCellType();
            if (cellType == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
                Date date = cell.getDateCellValue();
                String timeStamp = formatTime.format(date);
                if (timeStamp.equals("00:00")) {
                    return cell.toString();
                } else {
                    return timeStamp;
                }
            }
            if ((cellType & Cell.CELL_TYPE_STRING) != 0) {
                return cell.getStringCellValue().trim();
            } else if ((cellType & Cell.CELL_TYPE_BOOLEAN) != 0) {
                return String.valueOf(cell.getBooleanCellValue());
            } else {
                try {
                    Double value;
                    if (cell.getCellStyle().getDataFormatString().contains("%")) {
                        value = cell.getNumericCellValue() * 100;
                    } else {
                        value = cell.getNumericCellValue();
                    }
                    return String.valueOf(Double.valueOf(value));
                } catch (Exception e) {
                }
            }
        }
        return "";
    }

    /**
     * Cellの情報を整数値として読み出す
     * @param cell
     * @return
     */
    public static int getIntValue(Cell cell) {
        try {
            String content = getContentString(cell);
            int value = (int) Float.parseFloat(content);
            return value;
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * Cellの数値を小数点値として読み出す
     * @param cell
     * @return
     */
    public static float getFloatValue(Cell cell) {
        try {
            String content = getContentString(cell);
            float value = Float.parseFloat(content);
            return value;
        } catch (Exception e) {
        }
        return 0;
    }
}
