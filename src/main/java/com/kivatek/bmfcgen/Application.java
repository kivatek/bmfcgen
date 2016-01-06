package com.kivatek.bmfcgen;

/**
 * Created by kivatek on 2016/01/06.
 */
public class Application {
    public static void main(String[] args) {
        Application.create().start(args);
    }

    private String sourceFile = "source.xlsx";

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
            String arg = args[index++];
            if (arg.startsWith("--")) {
                switch (arg) {
                    case "--file":
                        // 入力ファイル名の指定
                        sourceFile = args[++index];
                        break;
                    case "--output":
                        // 出力ファイル名の指定
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

    }
}
