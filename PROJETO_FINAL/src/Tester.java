import java.io.FileNotFoundException;
import java.util.Objects;

public class Tester {

    public static void main(String[] args) throws FileNotFoundException {
        final int NUMBER_OF_DAYS = 1000, NUMBER_OF_TABS = 5;
        String[] dates = new String[NUMBER_OF_DAYS];
        int[][] data = new int[NUMBER_OF_DAYS][NUMBER_OF_TABS];
        String nameFile = "CasosTotais.csv";
        Projeto.readData(nameFile, data, dates);
        Projeto.verifyDateFormatAndAdapt(dates);

        runTestes(dates, data);

    }

    public static boolean test_determineArrayLength(int[][] array, int expectedArrayLenght) {
        return Projeto.determineArrayLength(array) == expectedArrayLenght;
    }

    public static boolean test_getPeriodBetweenDatesPredict(String wanted_day, String[] dates, int[] expectedPeriod) {
        int[] values = Projeto.getPeriodBetweenDatesPredict(wanted_day, dates);
        for (int number = 0; number < values.length; number++)
            if (values[number] != expectedPeriod[number])
                return false;
        return true;
    }

    public static boolean test_getTabsNumbersFromDay(int[][] data, int line, int[] expectedTabsNumbers) {
        int[] values = Projeto.getTabsNumbersFromDay(data, line);
        for (int number = 0; number < expectedTabsNumbers.length; number++)
            if (values[number] != expectedTabsNumbers[number])
                return false;
        return true;
    }

    public static boolean test_calculateNumberPredict(double[] probability, double[] expectedNumberPredict) {
        for (int number = 0; number < probability.length; number++)
            if (probability[number] != expectedNumberPredict[number])
                return false;
        return true;
    }

    public static boolean test_getLastLineOfFile(String[] dates, String expectedLastLine) {
        return Objects.equals(Projeto.getLastLineOfFile(dates), expectedLastLine);
    }

    public static boolean test_createIdentity(int order, double[][] expectedIdentity) {
        double[][] values = Projeto.createIdentity(order);
        for (int line = 0; line < values.length; line++)
            for (int number = 0; number < values[0].length; number++)
                if (values[line][number] != expectedIdentity[line][number])
                    return false;
        return true;
    }

    public static boolean test_subtractIdentityByMatrix(double[][] Q_matrix, double[][] expectedSubtraction) {
        double[][] values = Projeto.subtractIdentityByMatrix(Q_matrix);
        for (int line = 0; line < values.length; line++)
            for (int number = 0; number < values[0].length; number++)
                if (values[line][number] != expectedSubtraction[line][number])
                    return false;
        return true;
    }

    public static boolean test_reverseDateFormat(String date, String expectedString) {
        return Projeto.reverseDateFormat(date).equals(expectedString);
    }

    public static boolean test_validateDate(int line, String[] dates, int expectedLine) {
        return Projeto.validateDate(line, dates) == expectedLine;
    }

    public static boolean test_findLineOfTheDate(String date_to_compare, String[] dates, int expectedDay_Test) {
        return Projeto.findLineOfTheDate(date_to_compare, dates) == expectedDay_Test;
        // inserir data AAAA-MM-DD    17 → linha 16
    }

    public static boolean test_getDayOfTheWeek(int line, String[] dates, int expectedWeek_day) {
        return Projeto.getDayOfTheWeek(line, dates) == expectedWeek_day;
    }

    public static boolean test_weeksLimits(int day1_line, int day2_line, String[] dates, int[] expectedDayValues) {
        int[] values = Projeto.weeksLimits(day1_line, day2_line, dates);

        for (int number = 0; number < values.length; number++)
            if (values[number] != expectedDayValues[number])
                return false;
        return true;
    }

    public static boolean test_validateWeeksLimits(int line1, int line2, String[] dates, int[] expectedValidation) {
        int[] values = Projeto.validateWeeksLimits(line1, line2, dates);
        for (int number = 0; number < values.length; number++)
            if (values[number] != expectedValidation[number])
                return false;
        return true;
    }

    public static boolean test_firstLimitMonth(int line, String[] dates, int expectedLineValue) {
        return Projeto.firstLimitMonth(line, dates) == expectedLineValue;
    }

    public static boolean test_secondLimitMonth(int line, String[] dates, int number_of_days_month, int expectedLine) {
        int value = Projeto.secondLimitMonth(line, dates, number_of_days_month);
        return Projeto.secondLimitMonth(line, dates, number_of_days_month) == expectedLine;
    }

    public static boolean test_getMonthNumber(String[] dates, int line, int expectedMonthNumber) {
        int value = Projeto.getMonthNumber(dates, line);
        return Projeto.getMonthNumber(dates, line) == expectedMonthNumber;
    }

    public static boolean test_calculateNumberOfDaysInAMonth(int line, String[] dates, int expectedNumberOfDays) {
        return Projeto.calculateNumberOfDaysInAMonth(line, dates) == expectedNumberOfDays;
    }

    public static boolean test_validateDateForDifference(int line, String[] dates, int expectedDateForDifference) {
        int values = Projeto.validateDateForDifferences(406, dates);
        if (values != expectedDateForDifference)
            return false;
        return true;
    }

    public static boolean test_calculateDailyNewCases(int line_of_1st_date, int line_of_2nd_date, int[][] data, int[][] expectedDailyNewCases) {
        int[][] values = Projeto.calculateDailyNewCases(line_of_1st_date, line_of_2nd_date, data);
        for (int line = 0; line < values.length; line++)
            for (int number = 0; number < values[0].length; number++)
                if (values[line][number] != expectedDailyNewCases[line][number])
                    return false;
        return true;
    }

    public static boolean test_calculateWeeklyNewCases(int[][] difference_matrix, int[][] expectedWeeklyNewCases) {
        for (int line = 0; line < difference_matrix.length; line++)
            for (int number = 0; number < difference_matrix[0].length; number++)
                if (difference_matrix[line][number] != expectedWeeklyNewCases[line][number])
                    return false;
        return true;
    }

    public static boolean test_calculateMonthlyNewCases(int[][] difference_matrix, int[][] expectedMonthlyNewCases) {
        for (int line = 0; line < difference_matrix.length; line++)
            for (int number = 0; number < difference_matrix[0].length; number++)
                if (difference_matrix[line][number] != expectedMonthlyNewCases[line][number])
                    return false;
        return true;
    }

    public static boolean test_numberOfDaysBetweenTwoDates(int line_of_1st_date, int line_of_2nd_date, int expectedValue) {
        return Projeto.numberOfDaysBetweenTwoDates(line_of_1st_date, line_of_2nd_date) == expectedValue;
    }

    public static boolean test_numberOfDaysToCompare(int time_period_1, int time_period_2, int expectedValue) {
        return Projeto.numberOfDaysToCompare(time_period_1, time_period_2) == expectedValue;
    }

    public static boolean test_OrderDates(int line1, int line2, int[] lines_in_order, int[] expectedLinesInOrder) {
        int[] values = Projeto.orderDates(line1, line2, lines_in_order);
        for (int number = 0; number < values.length; number++)
            if (values[number] != expectedLinesInOrder[number])
                return false;
        return true;
    }

    public static boolean test_calculateDifferenceArrays(int[][] periodTime1, int[][] periodTime2, int number_of_days_compare, int[][] expectedResults) {
        int[][] values = Projeto.calculateDifferenceArrays(periodTime1, periodTime2, number_of_days_compare);
        for (int line = 0; line < values.length; line++)
            for (int number = 0; number < values[0].length; number++)
                if (values[line][number] != expectedResults[line][number])
                    return false;
        return true;
    }

    public static boolean test_defineTotalArray(int line1, int line2, int[][] data, int number_of_days_to_compare, int[][] expectedTotalArray) {
        int[][] values = Projeto.defineTotalArray(line1, line2, data, number_of_days_to_compare);
        for (int line = 0; line < values.length; line++)
            for (int number = 0; number < values[0].length; number++)
                if (values[line][number] != expectedTotalArray[line][number])
                    return false;
        return true;
    }

    public static boolean test_averageForComparison(int[][] period_user_differences, int number_of_days_to_compare, double[] expectedAverageForComparison) {
        double[] values = Projeto.averageForComparison(period_user_differences, number_of_days_to_compare);
        for (int line = 0; line < values.length; line++)
            if (values[line] != expectedAverageForComparison[line])
                return false;
        return true;
    }

    public static boolean test_standardDeviationForComparison(int[][] period_user_diffferences, double[] average, int number_of_days_to_compare, double[] expectedStandardDeviation) {
        double[] values = Projeto.standardDeviationForComparison(period_user_diffferences, average, number_of_days_to_compare);
        for (int line = 0; line < values.length; line++)
            if (values[line] != expectedStandardDeviation[line])
                return false;
        return true;
    }

    public static void runTestes(String[] dates, int[][] data) {
        // USAR O MÉTODO verifyDateFormatAndAdapt

        System.out.println("test_determineArrayLength");
        System.out.print("Result: ");
        int[][] array = {{1, 2}, {2, 3}};
        System.out.println(test_determineArrayLength(array, 2) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_weeksLimits");
        System.out.print("Result: ");
        int[] expectedDayValues = {8, 14};
        System.out.println(test_weeksLimits(3, 20, dates, expectedDayValues) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_firstLimitMonth");
        System.out.print("Result: ");
        System.out.println(test_firstLimitMonth(28, dates, 30) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_secondLimitMonth");
        System.out.print("Result: ");
        System.out.println(test_secondLimitMonth(28, dates, 31, 0) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_calculateDailyNewCases");
        System.out.print("Result: ");
        int[][] expectedValues = {{-3332, 3197, 63, 20, 6}, {-2668, 2591, -5, 26, 4}, {-3864, 3702, 102, 12, -8}, {-1936, 1731, 129, 13, 15}, {896, -1040, 91, -9, -1}};
        // expectedValues == difference_Matrix
        System.out.println(test_calculateDailyNewCases(5, 9, data, expectedValues) ? "OK" + "\n" : "NOT OK" + "\n");
        //System.out.println(test_calculateDailyNewCases(5, 9, data, expectedValues) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_calculateWeeklyNewCases");
        System.out.print("Result: ");
        int[][] difference_matrix = {{1, 2}, {2, 3}};
        int[][] expectedValuesWeekly = {{1, 2}, {2, 3}};
        System.out.println(test_calculateWeeklyNewCases(difference_matrix, expectedValuesWeekly) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_numberOfDaysBetweenTwoDates");
        System.out.print("Result: ");
        System.out.println(test_numberOfDaysBetweenTwoDates(12, 14, 2) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_numberOfDaysToCompare");
        System.out.print("Result: ");
        System.out.println(test_numberOfDaysToCompare(5, 3, 3) ? "OK" + "\n" : "NOT OK" + "\n");
        // random values
        System.out.println("test_reverseDateFormat");
        System.out.print("Result: ");
        System.out.println(test_reverseDateFormat("2020-04-17", "17-04-2020") ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_validateDate");
        System.out.print("Result: ");
        System.out.println(test_validateDate(16, dates, 16) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_findLineOfTheDate");
        System.out.print("Result: ");
        System.out.println(test_findLineOfTheDate("17-11-2020", dates, 16) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_getDayOfTheWeek");
        System.out.print("Result: ");
        System.out.println(test_getDayOfTheWeek(16, dates, 2) ? "OK" + "\n" : "NOT OK" + "\n");

        // random
        System.out.println("test_calculateDiffrenceArrays");
        System.out.print("Result: ");
        int[][] periodTime1 = {{1, 1, 1, 1, 1}, {2, 2, 2, 2, 2}, {3, 3, 3, 3, 3}};
        int[][] periodTime2 = {{1, 1, 1, 1, 1}, {2, 2, 2, 2, 2}, {3, 3, 3, 3, 3}};
        int[][] expectedResults = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};
        System.out.println(test_calculateDifferenceArrays(periodTime1, periodTime2, 2, expectedResults) ? "OK" + "\n" : "NOT OK" + "\n");

        //random
        System.out.println("test_calculateMonthlyNewCases");
        System.out.print("Result: ");
        int[][] expectedMonthTotals = {{-4434, 4245, 124, 11, 10}, {-7239, 7029, 132, 14, -4}};
        int[][] difference_matrix_monthly = {{-4434, 4245, 124, 11, 10}, {-7239, 7029, 132, 14, -4}};
        System.out.println(test_calculateMonthlyNewCases(difference_matrix_monthly, expectedMonthTotals) ? "OK" + "\n" : "NOT OK" + "\n");

        //random
        System.out.println("test_verifyAndAOrderDates");
        System.out.print("Result: ");
        int[] lines_in_order = {0, 0};
        int[] expectedLinesInOrder = {12, 23};
        System.out.println(test_OrderDates(23, 12, lines_in_order, expectedLinesInOrder) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_getLastLineOfFile");
        System.out.print("Result: ");
        System.out.println(test_getLastLineOfFile(dates, "07-01-2022") ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_defineTotalArray");
        System.out.print("Result: ");
        int[][] expectedTotalArray = {{0, 32480, 357, 73, 4}, {0, 32867, 345, 66, 9}, {0, 33039, 323, 62, 5}};
        System.out.println(test_defineTotalArray(368, 370, data, 2, expectedTotalArray) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_calculateNumberPredict");
        System.out.print("Result: ");
        double[] probability = {12, 216};
        double[] expectedNumberPredict = {12, 216};
        System.out.println(test_calculateNumberPredict(probability, expectedNumberPredict) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_averageForComparison");
        System.out.print("Result: ");
        int[][] period_user_differences = {{10, 32480, 357, 73, 4}, {20, 32867, 345, 66, 9}, {30, 33039, 323, 62, 5}};
        double[] expectedAverageForComparison = {19677.2, 205.0, 40.2, 3.6};
        System.out.println(test_averageForComparison(period_user_differences, 4, expectedAverageForComparison) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_standardDeviationForComparison");
        System.out.print("Result: ");
        int[][] period_user_diffferences_standardDeviation = {{10, 32480, 357, 73, 4}, {20, 32867, 345, 66, 9}, {30, 33039, 323, 62, 5}};
        double[] average = {19677.2, 205.0, 40.2, 3.6};
        double[] expectedStandardDeviation = {11362.440441208042, 118.98319209031165, 23.541028014935964, 2.7964262908219126};
        System.out.println(test_standardDeviationForComparison(period_user_diffferences_standardDeviation, average, 4, expectedStandardDeviation) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_getTabsNumbersFromDay");
        System.out.print("Result: ");
        int[] expectedTabsNumbers = {10011860, 136020, 971, 151, 12};
        System.out.println(test_getTabsNumbersFromDay(data, 423, expectedTabsNumbers) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_validateDateForDifference");
        System.out.print("Result: ");
        int expectedDateForDifference = 406;
        System.out.println(test_validateDateForDifference(406, dates, expectedDateForDifference) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_calculateNumberOfDaysInAMonth");
        System.out.print("Result: ");
        System.out.println(test_calculateNumberOfDaysInAMonth(10, dates, 30) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_getMonthNumber");
        System.out.print("Result: ");
        System.out.println(test_getMonthNumber(dates, 10, 10) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_getPeriodBetweenDatesPredict");
        System.out.print("Result: ");
        int[] expectedPeriod = {432, 23};
        System.out.println(test_getPeriodBetweenDatesPredict("30-01-2022", dates, expectedPeriod) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_createIdentity");
        System.out.print("Result: ");
        double[][] expectedIdentity = {{1.0, 0, 0,0}, {0, 1.0, 0, 0}, {0, 0, 1.0, 0}, {0, 0, 0, 1,0}};
        System.out.println(test_createIdentity(4, expectedIdentity) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_validateWeeksLimits");
        System.out.print("Result: ");
        int[] expectedValidation = {15, 21};
        System.out.println(test_validateWeeksLimits(10, 22, dates, expectedValidation) ? "OK" + "\n" : "NOT OK" + "\n");

        System.out.println("test_subtractIdentityByMatrix");
        System.out.print("Result: ");
        double[][] Q_Matriz = {{1.0, 10, 3, 4}, {2, 3, 4, 3}, {2, 3, 4, 2}, {2, 0, 3, 0}};
        double[][] expectedValidationMatriz = {{0, -10, -3, -4}, {-2, -2, -4, -3}, {-2, -3, -3, -2}, {-2, 0, -3, 1}};
        System.out.println(test_subtractIdentityByMatrix(Q_Matriz, expectedValidationMatriz) ? "OK" + "\n" : "NOT OK" + "\n");

    }
}