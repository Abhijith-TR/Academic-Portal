package org.example.utils;

public class Utils {
    public static int getGradeValue(String grade) {
        switch (grade) {
            case "A":
                return 10;
            case "A-":
                return 9;
            case "B":
                return 8;
            case "B-":
                return 7;
            case "C":
                return 6;
            case "C-":
                return 5;
            case "D":
                return 4;
            case "E":
                return 2;
            default:
                return 0;
        }
    }

    public static void prettyPrint(String[][] printableContent) {
        int rows = printableContent.length;
        if (rows == 0) return;
        int columns = printableContent[0].length;
        int[] maximumFieldLength = new int[columns];

        String formatString = "";
        for (int i=0; i<columns; i++) {
            formatString += '%';
        }
    }
}
