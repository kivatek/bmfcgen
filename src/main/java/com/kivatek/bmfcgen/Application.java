package com.kivatek.bmfcgen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kivatek on 2016/01/06.
 */
public class Application {
    public static void main(String[] args) {
        Application.create().start(args);
    }

    private String sourceFile = "source.xlsx";
    private String outputFile = "output.fnt";

    public static Application create() {
        return new Application();
    }

    private void start(String[] args) {
        if (parseOptions(args)) {
            process();
        }
    }

    private boolean parseOptions(String[] args) {
        boolean result = true;
        for (int index = 0; index < args.length; index++) {
            String arg = args[index];
            if (arg.startsWith("--")) {
                switch (arg) {
                    case "--file":
                        // 入力ファイル名の指定
                        sourceFile = args[++index];
                        break;
                    case "--output":
                        // 出力ファイル名の指定
                        outputFile = args[++index];
                        break;
                    case "--version":
                        System.out.println("version prototype");
                        result = false;
                        break;
                }
            }
        }
        return result;
    }

    private void process() {
        List<FontRect> resultSet = (new Converter()).doConvert(sourceFile, "Sheet1");
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
//            System.out.println(objectMapper.writeValueAsString(resultSet));
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

        // 文字コード順に出力したい場合は次のソート処理を有効にする
        resultSet.sort((a, b) -> (a.id - b.id));
        List<String> contents = new ArrayList<>();
        contents.add("chars count=" + resultSet.size());
        for (FontRect fontRect : resultSet) {
            StringBuilder sb = new StringBuilder();
            int valWidth = columnWidth(fontRect.id);
            sb.append("char id=").append(fontRect.id);
            for (int i = 0; i < (6 - valWidth); i++) {
                sb.append(" ");
            }
            valWidth = columnWidth(fontRect.x);
            sb.append("x=").append(fontRect.x);
            for (int i = 0; i < (5 - valWidth); i++) {
                sb.append(" ");
            }
            valWidth = columnWidth(fontRect.y);
            sb.append("y=").append(fontRect.y);
            for (int i = 0; i < (5 - valWidth); i++) {
                sb.append(" ");
            }
            valWidth = columnWidth(fontRect.width);
            sb.append("width=").append(fontRect.width);
            for (int i = 0; i < (5 - valWidth); i++) {
                sb.append(" ");
            }
            valWidth = columnWidth(fontRect.height);
            sb.append("height=").append(fontRect.height);
            for (int i = 0; i < (5 - valWidth); i++) {
                sb.append(" ");
            }
            valWidth = columnWidth(fontRect.xoffset);
            sb.append("xoffset=").append(fontRect.xoffset);
            for (int i = 0; i < (5 - valWidth); i++) {
                sb.append(" ");
            }
            valWidth = columnWidth(fontRect.yoffset);
            sb.append("yoffset=").append(fontRect.yoffset);
            for (int i = 0; i < (5 - valWidth); i++) {
                sb.append(" ");
            }
            valWidth = columnWidth(fontRect.xadvance);
            sb.append("xadvance=").append(fontRect.xadvance);
            for (int i = 0; i < (5 - valWidth); i++) {
                sb.append(" ");
            }
            valWidth = columnWidth(fontRect.page);
            sb.append("page=").append(fontRect.page);
            for (int i = 0; i < (5 - valWidth); i++) {
                sb.append(" ");
            }
            sb.append("chnl=").append(fontRect.chnl);
            contents.add(sb.toString());
        }
        writeFile(outputFile, contents);
    }

    public static int columnWidth(int value) {
        int width = 0;
        if (value == 0) {
            return 1;
        }
        if (value < 0) {
            width++;
        }
        while (value > 0) {
            value /= 10;
            width++;
        }
        return width;
    }

    /**
     * 文字列リストをファイルへ書き出す
     *
     * @param fileName 出力ファイル名
     * @param contents 出力する文字列
     */
    public static void writeFile(String fileName, List<String> contents) {
        try {
            Path path = Paths.get(fileName);
            Files.deleteIfExists(path);
            Files.write(path, contents, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        } catch (IOException e) {
        }
    }

}
