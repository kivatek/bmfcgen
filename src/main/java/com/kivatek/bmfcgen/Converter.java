package com.kivatek.bmfcgen;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Converter {
    public List<FontRect> doConvert(String fileName, String sheetName) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileName);
            Workbook wb = WorkbookFactory.create(inputStream);

            // シートを取得
            Sheet sheet = wb.getSheet(sheetName);
            if (sheet == null) {
                System.out.println("Sheet not found.");
                return new ArrayList<>();
            }

            // 何列目にどの項目があるかの情報を取得
            Row nameRow = sheet.getRow(0);
            short numberOfCells = nameRow.getLastCellNum();
            Map<Integer, String> fieldMap = new HashMap<>();
            for (int i = 1; i < numberOfCells; i++) {
                try {
                    Cell nameCell = nameRow.getCell(i);
                    if (PoiHelper.isBlankCell(nameCell) == false) {
                        fieldMap.put(i, nameCell.getStringCellValue());
                    }
                } catch (NullPointerException e) {
                }
            }

            List<FontRect> resultSet = new ArrayList<>();
            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    // 空行はスキップ
                    continue;
                }
                // 先頭列に「#」が記入されている場合はコメント行と見なしスキップ
                // TODO メタ情報を読み出す処理を追加
                Cell cell = row.getCell(0, Row.RETURN_BLANK_AS_NULL);
                if (cell != null) {
                    String content = PoiHelper.getContentString(cell);
                    if (content.trim().startsWith("#")) {
                        continue;
                    }
                }
                // ２列目にidが割り振られていない行はスキップ
                cell = row.getCell(1, Row.RETURN_BLANK_AS_NULL);
                if (cell == null) {
                    continue;
                }
                String id = "";
                if (cell != null) {
                    id = PoiHelper.getContentString(cell);
                    if (StringUtils.isEmpty(id)) {
                        continue;
                    }
                }
                FontRect fontRect = new FontRect();
                byte[] bytes = id.getBytes(StandardCharsets.UTF_16BE);
                fontRect.id = 0;
                for (byte ch : bytes) {
                    fontRect.id <<= 8;
                    fontRect.id |= (ch & 0xff);
                }
                for (int colIndex = 2; colIndex < row.getLastCellNum(); colIndex++) {
                    cell = row.getCell(colIndex, Row.RETURN_BLANK_AS_NULL);
                    int value = PoiHelper.getIntValue(cell);
                    switch (colIndex) {
                        case 2:
                            fontRect.x = value;
                            break;
                        case 3:
                            fontRect.y = value;
                            break;
                        case 4:
                            fontRect.width = value;
                            break;
                        case 5:
                            fontRect.height = value;
                            break;
                        case 6:
                            fontRect.xoffset = value;
                            break;
                        case 7:
                            fontRect.yoffset = value;
                            break;
                        case 8:
                            fontRect.xadvance = value;
                            break;
                        case 9:
                            fontRect.page = value;
                            break;
                        case 10:
                            fontRect.chnl = value;
                            break;
                    }
                }
                resultSet.add(fontRect);
            }
            return resultSet;
        } catch (InvalidFormatException | IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return new ArrayList<>();
    }
}
