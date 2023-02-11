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

    public static void prettyPrintGrades(int year, int semester, String[][] records) {
        System.out.printf("Records - Year: %d Semester: %d\n", year, semester);
        prettyPrint(new String[]{"Course Code", "Course Title", "Grade"}, records);
    }

    // Pretty printing matrices of strings (not jagged multidimensional arrays)
    public static void prettyPrint(String[] headings, String[][] printableContent) {
        int rows = printableContent.length;
        if (rows == 0) return;
        int columns = printableContent[0].length;
        int[] maximumFieldLength = new int[columns];

        for (int i=0; i<headings.length; i++) maximumFieldLength[i] = Math.max(headings[i].length(), maximumFieldLength[i]);

        for (int row=0; row<rows; row++) {
            for (int column=0; column<columns; column++) {
                maximumFieldLength[column] = Math.max(maximumFieldLength[column], printableContent[row][column].length());
            }
        }
        for (int i=0; i<columns; i++) maximumFieldLength[i] += 3;


        String formatString = "";
        for (int i=0; i<columns; i++) {
            formatString += "%-" + maximumFieldLength[i] + "s";
        }
        formatString += "\n";

        System.out.format(formatString, (Object[]) headings);
        for (final Object[] rowContent : printableContent) {
            System.out.format(formatString, rowContent);
        }
        System.out.println();
    }
}
