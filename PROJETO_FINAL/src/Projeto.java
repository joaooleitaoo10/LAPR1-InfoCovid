import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Scanner;
import java.util.Date;

public class Projeto {

    static final Scanner sc = new Scanner(System.in);
    static final Calendar calendar = Calendar.getInstance();
    static final int INVALIDATION_VALUE = -1;
    static final int WEEK_INCREASE = 7, DAY_INCREASE = 1, NUMBER_OF_TABS = 5, NUMBER_OF_LINES_IN_TWO_PERIODS = 4;
    static final int LINES_MARKOV = 5, COLUMNS_MARKOV = 5;
    static final int NUMBER_OF_DAYS = 1000, MONTH_INCREASE = 30, MINIMUM_DAYS_IN_A_MONTH = 28; //Mais dias que a pandemia (Number of days)

    public static void main(String[] args) throws FileNotFoundException {
        //INICIO DECLARAÇÕES

        int[] lines_in_order = new int[2];
        String[] dates_accumulated = new String[NUMBER_OF_DAYS];
        String[] dates_totals = new String[NUMBER_OF_DAYS];
        int[][] data_accumulated = new int[NUMBER_OF_DAYS][NUMBER_OF_TABS];
        int[][] data_totals = new int[NUMBER_OF_DAYS][NUMBER_OF_TABS];
        int line_of_1st_date, line_of_2nd_date;
        double[] days_to_death = new double[LINES_MARKOV - 1];
        double[][] P_matrix = new double[LINES_MARKOV][COLUMNS_MARKOV];
        String name_file_input;


        if (args.length > 1) { // NAO INTERATIVO
            nonInteractiveMode(args, data_accumulated, data_totals, dates_totals, dates_accumulated, P_matrix, days_to_death);

        } else { //INICIO MODO INTERATIVO

            final int BACK = -2;
            int option = 0, check_if_default = 0;

            do {
                if (checkIfFirstTime(check_if_default)) {
                    firstMenu();
                    option = sc.nextInt();
                } else {
                    check_if_default = 0;
                }

                switch (option) {
                    case 1:// CARREGAR UM FICHEIRO
                        do {
                            if (checkIfFirstTime(check_if_default)) {
                                chooseFileTypeMenu();
                                option = sc.nextInt();
                            } else {
                                check_if_default = 0;
                            }

                            switch (option) {
                                case 1: //ACUMULADOS
                                    name_file_input = chooseNameFileMenu();
                                    verifyAndReadFile(name_file_input, data_accumulated, dates_accumulated);
                                    option = INVALIDATION_VALUE;
                                    break;
                                case 2: //TOTAIS
                                    name_file_input = chooseNameFileMenu();
                                    verifyAndReadFile(name_file_input, data_totals, dates_totals);
                                    option = INVALIDATION_VALUE;
                                    break;
                                case 3: //MARKOV
                                    name_file_input = chooseNameFileMenu();
                                    verifyAndReadFileForMarkov(name_file_input, P_matrix);
                                    option = INVALIDATION_VALUE;
                                    break;
                                case 4: //VOLTAR
                                    option = INVALIDATION_VALUE;
                                    break;
                                default:
                                    option = validateOption();
                                    check_if_default = 1;
                                    break;
                            }

                        } while (option != INVALIDATION_VALUE);
                        option = BACK;
                        break;
                    case 2: // OBTER E VISUALIZAR
                        do {
                            if (checkIfFirstTime(check_if_default)) {
                                totalOrNewMenu();
                                option = sc.nextInt();
                            } else {
                                check_if_default = 0;
                            }

                            switch (option) {
                                case 1: // TOTAIS NUM DIA
                                    if (data_totals[0][0] == 0) {
                                        System.out.println();
                                        System.out.println("E necessário carregar um ficheiro para poder prosseguir!");
                                        option = INVALIDATION_VALUE;
                                    } else {
                                        MainMenu();
                                        System.out.println("Escolha a data que deseja: ");
                                        line_of_1st_date = validateDate(findLineOfTheDate(askDate(), dates_totals), dates_totals);
                                        int data_option = chooseWhatDataToPrint();
                                        do {

                                            while (5 < data_option || data_option < 1) {
                                                validateOption();
                                                data_option = sc.nextInt();
                                            }

                                            if (checkIfFirstTime(check_if_default)) {
                                                ShowStoreOrBothMenu();
                                                option = sc.nextInt();
                                            } else {
                                                check_if_default = 0;
                                            }

                                            switch (option) {
                                                case 1:
                                                    printerInfo(dates_totals, data_totals, line_of_1st_date, data_option);
                                                    break;
                                                case 2:
                                                    printerToFileTotals(dates_totals, data_totals, line_of_1st_date, data_option);
                                                    break;
                                                case 3:
                                                    printerToFileTotals(dates_totals, data_totals, line_of_1st_date, data_option);
                                                    printerInfo(dates_totals, data_totals, line_of_1st_date, data_option);
                                                    break;
                                                case 4:
                                                    option = INVALIDATION_VALUE;
                                                    break;
                                                default:
                                                    option = validateOption();
                                                    check_if_default = 1;
                                                    break;
                                            }
                                        } while (option != INVALIDATION_VALUE);
                                        option = BACK;
                                    }
                                    break;
                                case 2:// VARIAÇÃO ENTRE DUAS DATAS
                                    if (data_accumulated[0][0] == 0) {
                                        System.out.println("E necessário carregar um ficheiro para poder prosseguir!");
                                        option = INVALIDATION_VALUE;
                                    } else {
                                        MainMenu();
                                        System.out.println("Para a primeira data introduza os dados: ");
                                        line_of_1st_date = validateDateForDifferences(findLineOfTheDate((askDate()), dates_accumulated), dates_accumulated);
                                        System.out.println();
                                        System.out.println("Para a segunda data introduza os dados: ");
                                        line_of_2nd_date = validateDateForDifferences(findLineOfTheDate((askDate()), dates_accumulated), dates_accumulated);
                                        orderDates(line_of_1st_date, line_of_2nd_date, lines_in_order);
                                        line_of_1st_date = lines_in_order[0];
                                        line_of_2nd_date = lines_in_order[1];
                                        do {
                                            if (checkIfFirstTime(check_if_default)) {
                                                TypeOfAnalysisMenu();
                                                option = sc.nextInt();
                                            } else {
                                                check_if_default = 0;
                                            }
                                            int[][] daily_new_cases_array;
                                            switch (option) {
                                                case 1:// DIARIA
                                                    daily_new_cases_array = calculateDailyNewCases(line_of_1st_date, line_of_2nd_date, data_accumulated);
                                                    chooseWhatToDoWithTheData(line_of_1st_date, dates_accumulated, daily_new_cases_array, DAY_INCREASE);
                                                    break;
                                                case 2: // SEMANAL
                                                    int[] limits = validateWeeksLimits(line_of_1st_date, line_of_2nd_date, dates_accumulated);
                                                    daily_new_cases_array = calculateDailyNewCases(limits[0], limits[1], data_accumulated);
                                                    int[][] weekly_totals = calculateWeeklyNewCases(daily_new_cases_array);
                                                    chooseWhatToDoWithTheData(limits[0], dates_accumulated, weekly_totals, WEEK_INCREASE);
                                                    break;
                                                case 3: // MENSAL
                                                    int[] month_limits = {firstLimitMonth(line_of_1st_date, dates_accumulated), secondLimitMonth(line_of_2nd_date, dates_accumulated, calculateNumberOfDaysInAMonth(line_of_2nd_date, dates_accumulated))};
                                                    validateMonthLimits(dates_accumulated, MINIMUM_DAYS_IN_A_MONTH, month_limits, lines_in_order);
                                                    daily_new_cases_array = calculateDailyNewCases(month_limits[0], month_limits[1], data_accumulated);
                                                    int[][] month_totals_matrix = calculateMonthlyNewCases(daily_new_cases_array, dates_accumulated, MONTH_INCREASE, month_limits[0]);
                                                    chooseWhatToDoWithTheData(month_limits[0], dates_accumulated, month_totals_matrix, MONTH_INCREASE);
                                                    break;
                                                case 4: // VOLTAR
                                                    option = INVALIDATION_VALUE;
                                                    break;
                                                default:
                                                    option = validateOption();
                                                    check_if_default = 1;
                                                    break;
                                            }
                                        } while (option != INVALIDATION_VALUE);
                                        option = BACK;
                                    }
                                    break;
                                case 3:
                                    option = INVALIDATION_VALUE;
                                    break;
                                default:
                                    option = validateOption();
                                    check_if_default = 1;
                                    break;
                            }

                        } while (option != INVALIDATION_VALUE);
                        option = BACK;
                        break;
                    case 3: //COMPARAÇÕES
                        //INÍCIO DECLARAÇÕES
                        int[][] comparisons_array;
                        double[] period_1_average, period_2_average, period_1_desvio_padrao, period_2_desvio_padrao;
                        int[] line_of_user_date = new int[NUMBER_OF_LINES_IN_TWO_PERIODS];
                        int period_1, period_2, number_days_to_compare;
                        //FIM DECLARAÇÕES
                        do {
                            if (checkIfFirstTime(check_if_default)) {
                                compareMenu();
                                option = sc.nextInt();
                            } else {
                                check_if_default = 0;
                            }

                            int data_option;
                            switch (option) {
                                case 1:
                                    if (data_accumulated[0][0] == 0) {
                                        System.out.println("E necessário carregar um ficheiro para poder prosseguir!");
                                        option = INVALIDATION_VALUE;
                                    } else {
                                        askPeriodsForDifferences(line_of_user_date, dates_accumulated);
                                        compareAndAdjustPeriods(lines_in_order, line_of_user_date);

                                        period_1 = numberOfDaysBetweenTwoDates(line_of_user_date[1], line_of_user_date[0]);  // dá return ao number_of_days que o período contém
                                        period_2 = numberOfDaysBetweenTwoDates(line_of_user_date[3], line_of_user_date[2]);
                                        number_days_to_compare = numberOfDaysToCompare(period_1, period_2);  // dá return ao
                                        int[][] period_1_differences = calculateDailyNewCases(line_of_user_date[0], (number_days_to_compare + line_of_user_date[0]), data_accumulated);  // dá return á matriz das diferenças do período
                                        int[][] period_2_differences = calculateDailyNewCases(line_of_user_date[2], (number_days_to_compare + line_of_user_date[2]), data_accumulated);
                                        comparisons_array = calculateDifferenceArrays(period_1_differences, period_2_differences, number_days_to_compare);  // dá return ao array das diferenças entre as diferenças dos período
                                        double[] comparisons_array_average = averageForComparison(comparisons_array, number_days_to_compare);
                                        double[] comparisons_array_desvio_padrao = standardDeviationForComparison(comparisons_array, comparisons_array_average, number_days_to_compare);


                                        period_1_average = averageForComparison(period_1_differences, number_days_to_compare);  // calcula a média por tab de um período
                                        period_2_average = averageForComparison(period_2_differences, number_days_to_compare);

                                        period_1_desvio_padrao = standardDeviationForComparison(period_1_differences, period_1_average, number_days_to_compare); // calcula o desvio padrão por tab de um período
                                        period_2_desvio_padrao = standardDeviationForComparison(period_2_differences, period_2_average, number_days_to_compare);

                                        data_option = chooseWhatDataToPrint();
                                        while (5 < data_option || data_option < 1) {
                                            validateOption();
                                            data_option = sc.nextInt();
                                        }

                                        printDifferencesComparisons(comparisons_array, dates_accumulated, line_of_user_date[0], line_of_user_date[2], number_days_to_compare, 0, data_option);
                                        printerAverageAndDesvio(comparisons_array_average, "|Medias|", data_option, "Accumulated");
                                        printerAverageAndDesvio(comparisons_array_desvio_padrao, "|Desvio Padrao|", data_option, "Accumulated");

                                        System.out.println();
                                        System.out.printf("Período 1: %s - %s%n%n", dates_accumulated[line_of_user_date[0]], dates_accumulated[line_of_user_date[0] + number_days_to_compare]);
                                        printerAverageAndDesvio(period_1_average, "|Medias|", data_option, "Accumulated");
                                        printerAverageAndDesvio(period_1_desvio_padrao, "|Desvio Padrao|", data_option, "Accumulated");

                                        System.out.println();
                                        System.out.printf("Período 2: %s - %s%n%n", dates_accumulated[line_of_user_date[2]], dates_accumulated[line_of_user_date[2] + number_days_to_compare]);
                                        printerAverageAndDesvio(period_2_average, "|Medias|", data_option, "Accumulated");
                                        printerAverageAndDesvio(period_2_desvio_padrao, "|Desvio Padrao|", data_option, "Accumulated");


                                        do {
                                            if (checkIfFirstTime(check_if_default)) {
                                                storeInfoMenu();
                                                option = sc.nextInt();
                                            } else {
                                                check_if_default = 0;
                                            }

                                            switch (option) {
                                                case 1: // GUARDAR FICHEIO CSV
                                                    System.out.printf("%nIntroduza o nome do ficheiro:%n");
                                                    String name_file_output = sc.next();
                                                    PrintWriter output = new PrintWriter(name_file_output);
                                                    output.format("Diferenças de Casos entre o período de %s - %s e o período de %s - %s%n", dates_accumulated[line_of_user_date[0]], dates_accumulated[line_of_user_date[0] + number_days_to_compare], dates_accumulated[line_of_user_date[2]], dates_accumulated[line_of_user_date[2] + number_days_to_compare]);
                                                    comparisonsDifferencesWriteToFile(output, "Accumulated", data_option, comparisons_array, dates_accumulated, line_of_user_date[0], line_of_user_date[2]);
                                                    output.format("Diferenças entre períodos%n%n");
                                                    averageAndStandardDesviationWriteToFile(output, "|Media|", "Accumulated", data_option, comparisons_array_average);
                                                    averageAndStandardDesviationWriteToFile(output, "|Desvio Padrao|", "Accumulated", data_option, comparisons_array_desvio_padrao);
                                                    nonInteractiveDashToFile(output);
                                                    output.format("Período 1 - %s - %s%n%n", dates_accumulated[line_of_user_date[0]], dates_accumulated[line_of_user_date[0] + number_days_to_compare]);
                                                    averageAndStandardDesviationWriteToFile(output, "|Media|", "Accumulated", data_option, period_1_average);
                                                    averageAndStandardDesviationWriteToFile(output, "|Desvio Padrao|", "Accumulated", data_option, period_1_average);
                                                    nonInteractiveDashToFile(output);
                                                    output.format("Período 2 - %s - %s%n%n", dates_accumulated[line_of_user_date[2]], dates_accumulated[line_of_user_date[2] + number_days_to_compare]);
                                                    averageAndStandardDesviationWriteToFile(output, "|Media|", "Accumulated", data_option, period_1_average);
                                                    averageAndStandardDesviationWriteToFile(output, "|Desvio Padrao|", "Accumulated", data_option, period_1_average);
                                                    output.close();
                                                    break;

                                                case 2: // BACK
                                                    option = INVALIDATION_VALUE;
                                                    break;

                                                default:
                                                    option = validateOption();
                                                    check_if_default = 1;
                                                    break;
                                            }

                                        } while (option != INVALIDATION_VALUE);
                                        option = BACK;
                                        break;

                                    }
                                    break;
                                case 2:
                                    if (data_totals[0][0] == 0) {
                                        System.out.println("E necessário carregar um ficheiro para poder prosseguir!");
                                        option = INVALIDATION_VALUE;
                                    } else {
                                        askPeriodsForTotals(line_of_user_date, dates_totals);
                                        compareAndAdjustPeriods(lines_in_order, line_of_user_date);

                                        period_1 = numberOfDaysBetweenTwoDates(line_of_user_date[1], line_of_user_date[0]);  // dá return ao number_of_days que o período contém
                                        period_2 = numberOfDaysBetweenTwoDates(line_of_user_date[3], line_of_user_date[2]);

                                        number_days_to_compare = numberOfDaysToCompare(period_1, period_2);  // dá return ao menor período

                                        int[][] period_1_total = defineTotalArray(line_of_user_date[0], (line_of_user_date[0] + number_days_to_compare), data_totals, number_days_to_compare);
                                        int[][] period_2_total = defineTotalArray(line_of_user_date[2], (line_of_user_date[2] + number_days_to_compare), data_totals, number_days_to_compare);

                                        comparisons_array = calculateDifferenceArrays(period_1_total, period_2_total, number_days_to_compare);
                                        double[] comparisons_array_average = averageForComparison(comparisons_array, number_days_to_compare);
                                        double[] comparisons_array_desvio_padrao = standardDeviationForComparison(comparisons_array, comparisons_array_average, number_days_to_compare);

                                        period_1_average = averageForComparison(period_1_total, number_days_to_compare);  // calcula a média por tab de um período
                                        period_2_average = averageForComparison(period_2_total, number_days_to_compare);

                                        period_1_desvio_padrao = standardDeviationForComparison(period_1_total, period_1_average, number_days_to_compare); // calcula o desvio padrão por tab de um período
                                        period_2_desvio_padrao = standardDeviationForComparison(period_2_total, period_2_average, number_days_to_compare);

                                        data_option = chooseWhatDataToPrint();
                                        while (5 < data_option || data_option < 1) {
                                            validateOption();
                                            data_option = sc.nextInt();
                                        }

                                        printDifferencesComparisons(comparisons_array, dates_totals, line_of_user_date[0], line_of_user_date[2], number_days_to_compare, 1, data_option);
                                        printerAverageAndDesvio(comparisons_array_average, "|Medias|", data_option, "Accumulated");
                                        printerAverageAndDesvio(comparisons_array_desvio_padrao, "    |Desvio Padrao|", data_option, "Accumulated");

                                        System.out.printf("%nPeríodo 1: %s - %s%n%n", dates_totals[line_of_user_date[0]], dates_totals[line_of_user_date[0] + number_days_to_compare]);
                                        printerAverageAndDesvio(period_1_average, "|Medias|", data_option, "Total");
                                        printerAverageAndDesvio(period_1_desvio_padrao, "|Desvio Padrao|", data_option, "Total");

                                        System.out.printf("%nPeríodo 2: %s - %s%n%n", dates_totals[line_of_user_date[2]], dates_totals[line_of_user_date[2] + number_days_to_compare]);
                                        printerAverageAndDesvio(period_2_average, "|Medias|", data_option, "Total");
                                        printerAverageAndDesvio(period_2_desvio_padrao, "|Desvio Padrao|", data_option, "Total");

                                        do {
                                            if (checkIfFirstTime(check_if_default)) {
                                                storeInfoMenu();
                                                option = sc.nextInt();
                                            } else {
                                                check_if_default = 0;
                                            }

                                            switch (option) {
                                                case 1: // GUARDAR FICHEIO CSV
                                                    System.out.printf("%nIntroduza o nome do ficheiro:%n");
                                                    String name_file_output = sc.next();
                                                    PrintWriter output = new PrintWriter(name_file_output);
                                                    output.format("Diferenças de Casos entre o período de %s - %s e o período de %s - %s%n", dates_totals[line_of_user_date[0]], dates_totals[line_of_user_date[0] + number_days_to_compare], dates_totals[line_of_user_date[2]], dates_totals[line_of_user_date[2] + number_days_to_compare]);
                                                    comparisonsDifferencesWriteToFile(output, "Totals", data_option, comparisons_array, dates_totals, line_of_user_date[0], line_of_user_date[2]);
                                                    output.format("Diferenças entre períodos%n%n");
                                                    averageAndStandardDesviationWriteToFile(output, "|Media|", "Totals", data_option, comparisons_array_average);
                                                    averageAndStandardDesviationWriteToFile(output, "|Desvio Padrao|", "Totals", data_option, comparisons_array_desvio_padrao);
                                                    nonInteractiveDashToFile(output);
                                                    output.format("Período 1 - %s - %s%n%n", dates_totals[line_of_user_date[0]], dates_totals[line_of_user_date[0] + number_days_to_compare]);
                                                    averageAndStandardDesviationWriteToFile(output, "|Media|", "Totals", data_option, period_1_average);
                                                    averageAndStandardDesviationWriteToFile(output, "|Desvio Padrao|", "Totals", data_option, period_1_average);
                                                    nonInteractiveDashToFile(output);
                                                    output.format("Período 2 - %s - %s%n%n", dates_totals[line_of_user_date[2]], dates_totals[line_of_user_date[2] + number_days_to_compare]);
                                                    averageAndStandardDesviationWriteToFile(output, "|Media|", "Totals", data_option, period_1_average);
                                                    averageAndStandardDesviationWriteToFile(output, "|Desvio Padrao|", "Totals", data_option, period_1_average);
                                                    output.close();
                                                    break;

                                                case 2: // BACK
                                                    option = INVALIDATION_VALUE;
                                                    break;

                                                default:
                                                    option = validateOption();
                                                    check_if_default = 1;
                                                    break;
                                            }

                                        } while (option != INVALIDATION_VALUE);
                                        option = BACK;
                                        break;
                                    }
                                    break;
                                case 3:
                                    //Voltar
                                    option = INVALIDATION_VALUE;
                                    break;
                                default:
                                    option = validateOption();
                                    check_if_default = 1;
                                    break;

                            }
                        } while (option != INVALIDATION_VALUE);
                        option = BACK;
                        break;
                    case 5: //SAIR PROGRAMA
                        System.out.printf("%nPrograma Terminado.");
                        option = INVALIDATION_VALUE;
                        break;
                    case 4: // FAZER UMA PREVISAO
                        do {
                            if (checkIfFirstTime(check_if_default)) {
                                predictMenu();
                                option = sc.nextInt();
                            } else {
                                check_if_default = 0;
                            }

                            switch (option) {
                                case 1:// PREVISAO DE CASOS NUM DIA
                                    if (data_totals[0][0] == 0 && P_matrix[4][4] == 0) {
                                        System.out.println();
                                        System.out.println("E necessário carregar um ficheiro com os registos totais para poder prosseguir!");
                                        System.out.println();
                                        System.out.println("E necessário carregar um ficheiro com uma matriz de transiçoes para poder prosseguir!");
                                        option = INVALIDATION_VALUE;
                                    } else if (data_totals[0][0] == 0) {
                                        System.out.println();
                                        System.out.println("E necessário carregar um ficheiro com os registos totais para poder prosseguir!");
                                        option = INVALIDATION_VALUE;
                                    } else if (P_matrix[4][4] == 0) {
                                        System.out.println();
                                        System.out.println("E necessário carregar um ficheiro com uma matriz de transiçoes para poder prosseguir!");
                                        option = INVALIDATION_VALUE;
                                    } else {
                                        sc.nextLine();
                                        String wanted_day;
                                        System.out.printf("%nIntroduza o dia que pretender prever: ");
                                        wanted_day = sc.nextLine();
                                        System.out.println();
                                        wanted_day = validateDatePredict(wanted_day, dates_totals);

                                        int[] values_of_t_and_k = getPeriodBetweenDatesPredict(wanted_day, dates_totals);
                                        int t = values_of_t_and_k[0]; // t é o dia mais recente em relação ao dia pedido (wanted_day)
                                        int k = values_of_t_and_k[1]; // k é a diferença entre dias

                                        int[] numbers_from_day = getTabsNumbersFromDay(data_totals, t); // vetor que store os números das 5 tabs
                                        double[] predict_numbers = calculateNumberPredict(k, P_matrix, numbers_from_day);

                                        int data_option = chooseWhatDataToPrintPredict();
                                        do {
                                            while (6 < data_option || data_option < 1) {
                                                validateOption();
                                                data_option = sc.nextInt();
                                            }
                                            if (checkIfFirstTime(check_if_default)) {
                                                ShowStoreOrBothMenu();
                                                option = sc.nextInt();
                                                System.out.println();
                                            } else {
                                                check_if_default = 0;
                                            }

                                            switch (option) {
                                                case 1:
                                                    //IMPRIMIR TODOS OS RESULTADOS
                                                    printerPredictNumber(predict_numbers, wanted_day, data_option);
                                                    break;
                                                case 2:
                                                    //GUARDAR EM CSV
                                                    System.out.println("Introduza o nome do ficheiro: ");
                                                    String output_file_name = sc.next();
                                                    predictNumberToFile(data_option, wanted_day, predict_numbers, output_file_name);
                                                    break;
                                                case 3:
                                                    printerPredictNumber(predict_numbers, wanted_day, data_option);
                                                    System.out.println();
                                                    System.out.println("Introduza o nome do ficheiro: ");
                                                    output_file_name = sc.next();
                                                    predictNumberToFile(data_option, wanted_day, predict_numbers, output_file_name);
                                                    //FAZER AMBOS
                                                    break;
                                                case 4:
                                                    option = INVALIDATION_VALUE;
                                                    break;
                                                default:
                                                    option = validateOption();
                                                    check_if_default = 1;
                                                    break;
                                            }
                                        } while (option != INVALIDATION_VALUE);
                                        option = BACK;

                                    }
                                    break;
                                case 2:// DIAS ATE ÓBITO
                                    if (P_matrix[4][4] == 0) {
                                        System.out.println();
                                        System.out.println("E necessário carregar um ficheiro com uma matriz de transiçoes para poder prosseguir!");
                                        option = INVALIDATION_VALUE;
                                    } else {
                                        calculateDaysToDeathArray(P_matrix, days_to_death);
                                        do {
                                            if (checkIfFirstTime(check_if_default)) {
                                                ShowStoreOrBothMenu();
                                                option = sc.nextInt();
                                                System.out.println();
                                            } else {
                                                check_if_default = 0;
                                            }
                                            String[] tabs_days_to_death = {"->Se nao estiver infetado: ", "->Se estiver infetado: ", "->Se estiver hospitalizado: ", "->Se estiver internado nos UCI: "};

                                            switch (option) {
                                                case 1:
                                                    //IMPRIMIR TODOS OS RESULTADOS
                                                    PrintVectorConsole(days_to_death, tabs_days_to_death);
                                                    break;
                                                case 2:
                                                    //GUARDAR EM CSV
                                                    System.out.println("Introduza o nome do ficheiro: ");
                                                    String output_file_name = sc.next();
                                                    PrintVectorFile(days_to_death,tabs_days_to_death,output_file_name);
                                                    break;
                                                case 3:
                                                    System.out.println();
                                                    System.out.println("Introduza o nome do ficheiro: ");
                                                    output_file_name = sc.next();
                                                    PrintVectorConsole(days_to_death, tabs_days_to_death);
                                                    PrintVectorFile(days_to_death,tabs_days_to_death,output_file_name);
                                                    //FAZER AMBOS
                                                    break;
                                                case 4:
                                                    option = INVALIDATION_VALUE;
                                                    break;
                                                default:
                                                    option = validateOption();
                                                    check_if_default = 1;
                                                    break;
                                            }
                                        } while (option != INVALIDATION_VALUE);
                                        option = BACK;





                                    }

                                    break;
                                case 3: //VOLTAR
                                    option = INVALIDATION_VALUE;
                                    break;
                                default:
                                    option = validateOption();
                                    check_if_default = 1;
                                    break;
                            }
                        }
                        while (option != INVALIDATION_VALUE);
                        option = BACK;
                        break;
                    default:
                        option = validateOption();
                        check_if_default = 1;
                        break;
                }
            } while (option != INVALIDATION_VALUE);
        }

    }// MAIN

    public static void nonInteractiveMode(String[] console_inputs, int[][] data_accumulated, int[][] data_totals, String[] dates_totals, String[] dates_accumulated, double[][] P_matrix, double[] days_to_death) throws FileNotFoundException {
        if (console_inputs.length == 5 || console_inputs.length == 16 || console_inputs.length == 20) {
            int[][] daily_new_cases_array;
            int[][] new_cases_array_to_print = new int[NUMBER_OF_DAYS][NUMBER_OF_TABS];  // condição para dar print
            String resolution_time = getParameter(console_inputs, "-r");
            String initial_date = getParameter(console_inputs, "-di");
            String final_date = getParameter(console_inputs, "-df");
            String initial_date_1 = getParameter(console_inputs, "-di1");
            String final_date_1 = getParameter(console_inputs, "-df1");
            String initial_date_2 = getParameter(console_inputs, "-di2");
            String final_date_2 = getParameter(console_inputs, "-df2");
            String day_to_predict = getParameter(console_inputs, "-T");
            String totals_file, accumulated_file, matrix_file, output_file;
            int line_first_date_totals , line_first_date_accumulated = 0, line_last_date_accumulated, period_1_total, period_2_total, number_days_to_compare_total;
            int[][] comparisons_array_totals;
            double[] period_1_average_acc, period_2_average_acc, period_1_desvio_padrao_acc, period_2_desvio_padrao_acc;
            int[][] comparison_array_acc;
            double[] period_1_average_totals, period_2_average_totals, period_1_desvio_padrao_totals, period_2_desvio_padrao_totals;
            int[] values_of_t_and_k;
            double[] predict_numbers;
            double[] comparisons_array_average_acc, comparisons_array_desvio_padrao_acc;

            String[] tabs_acc = {" |Datas|", "|Novos Casos|", "|Novas Hospitalizacoes|", "|Novos Internamentos em UCI|", "|Obitos|"};
            String[] tabs_totals = {" |Datas|", "|Casos Ativos|", "|Hospitalizados|", "|Internados em UCI|", "|Obitos|"};

            if (console_inputs.length == 5) {
                totals_file = console_inputs[2];
                matrix_file = console_inputs[3];
                output_file = console_inputs[4];
                PrintWriter output = new PrintWriter(output_file);
                readDataMarkov(matrix_file, P_matrix);
                readData(totals_file, data_totals, dates_totals);
                values_of_t_and_k = getPeriodBetweenDatesPredict(day_to_predict, dates_totals);
                int t = values_of_t_and_k[0];
                int k = values_of_t_and_k[1];
                predict_numbers = calculateNumberPredict(k, P_matrix, getTabsNumbersFromDay(data_totals, t));
                calculateDaysToDeathArray(P_matrix, days_to_death);

                // PRINTS
                output.format("Números previsto para o dia %s%n%n", day_to_predict);
                nonInteractivePredictsToFile(output, day_to_predict, predict_numbers);
                nonInteractiveDashToFile(output);
                nonInteractiveDaysToDeath(output, days_to_death);

                output.close();
                //ULTIMO PONTO ENUNCIADO - Previsões
            } else if (console_inputs.length == 16) {
                resolutionTimeNonInteractive(resolution_time);
                accumulated_file = console_inputs[14];
                output_file = console_inputs[15];
                PrintWriter output = new PrintWriter(output_file);
                readData(accumulated_file, data_accumulated, dates_accumulated);
                verifyDateFormatAndAdapt(dates_accumulated);
                line_last_date_accumulated = validateDateNonInteractive(findLineOfTheDate(final_date, dates_accumulated));
                line_first_date_accumulated = validateDateNonInteractive(findLineOfTheDate(initial_date, dates_accumulated));
                switch (resolution_time) {
                    case "0":
                        daily_new_cases_array = calculateDailyNewCases(line_first_date_accumulated, line_last_date_accumulated, data_accumulated);
                        new_cases_array_to_print = daily_new_cases_array;
                        break;
                    case "1":
                        int[] week_limits = weeksLimits(line_first_date_accumulated, line_last_date_accumulated, dates_accumulated);
                        validateWeeksLimitsNonInterative(week_limits[0], week_limits[1], dates_accumulated);
                        daily_new_cases_array = calculateDailyNewCases(week_limits[0], week_limits[1], data_accumulated);
                        new_cases_array_to_print = calculateWeeklyNewCases(daily_new_cases_array);
                        break;
                    case "2":
                        validateMonthLimitsNonInteractive(MINIMUM_DAYS_IN_A_MONTH, firstLimitMonth(line_first_date_accumulated, dates_accumulated), secondLimitMonth(line_first_date_accumulated, dates_accumulated, calculateNumberOfDaysInAMonth(line_last_date_accumulated, dates_accumulated)));
                        daily_new_cases_array = calculateDailyNewCases(firstLimitMonth(line_first_date_accumulated, dates_accumulated), secondLimitMonth(line_last_date_accumulated, dates_accumulated, calculateNumberOfDaysInAMonth(line_last_date_accumulated, dates_accumulated)), data_accumulated);
                        new_cases_array_to_print = calculateMonthlyNewCases(daily_new_cases_array, dates_accumulated, MONTH_INCREASE, firstLimitMonth(line_first_date_accumulated, dates_accumulated));
                }

                int period_1_accumulated = numberOfDaysBetweenTwoDates(findLineOfTheDate(initial_date_1, dates_accumulated), findLineOfTheDate(final_date_1, dates_accumulated));
                int period_2_accumulated = numberOfDaysBetweenTwoDates(findLineOfTheDate(initial_date_2, dates_accumulated), findLineOfTheDate(final_date_2, dates_accumulated));
                int number_days_to_compare_accumulated = numberOfDaysToCompare(period_1_accumulated, period_2_accumulated);
                int[][] period_1_differences = calculateDailyNewCases(findLineOfTheDate(initial_date_1, dates_accumulated), (number_days_to_compare_accumulated + findLineOfTheDate(initial_date_1, dates_accumulated)), data_accumulated);  // dá return á matriz das diferenças do período
                int[][] period_2_differences = calculateDailyNewCases(findLineOfTheDate(initial_date_2, dates_accumulated), (number_days_to_compare_accumulated + findLineOfTheDate(initial_date_2, dates_accumulated)), data_accumulated);
                comparison_array_acc = calculateDifferenceArrays(period_1_differences, period_2_differences, number_days_to_compare_accumulated);
                period_1_average_acc = averageForComparison(period_1_differences, number_days_to_compare_accumulated);
                period_2_average_acc = averageForComparison(period_2_differences, number_days_to_compare_accumulated);
                period_1_desvio_padrao_acc = standardDeviationForComparison(period_1_differences, period_1_average_acc, number_days_to_compare_accumulated);
                period_2_desvio_padrao_acc = standardDeviationForComparison(period_2_differences, period_2_average_acc, number_days_to_compare_accumulated);
                comparisons_array_average_acc = averageForComparison(comparison_array_acc, number_days_to_compare_accumulated);
                comparisons_array_desvio_padrao_acc = standardDeviationForComparison(comparison_array_acc, comparisons_array_average_acc, number_days_to_compare_accumulated);
                //PENULTIMO PONTO - Tudo menos previsões e totais
                // PRINTS
                nonInteractivePrinterTwoDatesVariation(output, tabs_acc, dates_accumulated, new_cases_array_to_print, resolution_time, line_first_date_accumulated);
                nonInteractiveDashToFile(output);
                nonInteractivePrinterComparisons(output, tabs_acc, dates_accumulated, comparison_array_acc, initial_date_1, initial_date_2, number_days_to_compare_accumulated);
                nonInteractiveDashToFile(output);
                output.format("Diferença entre Períodos%n");
                nonInteractiveAverageAndDesvioToFile(output, "|Medias|", comparisons_array_average_acc, tabs_acc);
                nonInteractiveAverageAndDesvioToFile(output, "|Desvio Padrao|", comparisons_array_desvio_padrao_acc, tabs_acc);
                nonInteractiveDashToFile(output);
                output.format("Período 1: %s - %s%n%n", dates_accumulated[findLineOfTheDate(initial_date_1, dates_accumulated)], dates_accumulated[(findLineOfTheDate(initial_date_1, dates_accumulated)) + number_days_to_compare_accumulated]);
                nonInteractiveAverageAndDesvioToFile(output, "|Medias|", period_1_average_acc, tabs_acc);
                nonInteractiveAverageAndDesvioToFile(output, "|Desvio Padrao|", period_1_desvio_padrao_acc, tabs_acc);
                nonInteractiveDashToFile(output);
                output.format("Período 2: %s - %s%n%n", dates_accumulated[findLineOfTheDate(initial_date_2, dates_accumulated)], dates_accumulated[(findLineOfTheDate(initial_date_2, dates_accumulated)) + number_days_to_compare_accumulated]);
                nonInteractiveAverageAndDesvioToFile(output, "|Medias|", period_2_average_acc, tabs_acc);
                nonInteractiveAverageAndDesvioToFile(output, "|Desvio Padrao|", period_2_desvio_padrao_acc, tabs_acc);
                output.close();

            } else if (console_inputs.length == 20) {
                resolutionTimeNonInteractive(resolution_time);
                matrix_file = console_inputs[18];
                totals_file = console_inputs[16];
                accumulated_file = console_inputs[17];
                output_file = console_inputs[19];
                PrintWriter output = new PrintWriter(output_file);
                readData(totals_file, data_totals, dates_totals);
                readData(accumulated_file, data_accumulated, dates_accumulated);
                readDataMarkov(matrix_file, P_matrix);
                verifyDateFormatAndAdapt(dates_accumulated);
                line_last_date_accumulated = findLineOfTheDate(final_date, dates_accumulated);
                line_first_date_accumulated = findLineOfTheDate(initial_date, dates_accumulated);
                //TOTAIS
                line_first_date_totals = findLineOfTheDate(initial_date, dates_totals); // PRINT
                switch (resolution_time) {
                    case "0":
                        daily_new_cases_array = calculateDailyNewCases(line_first_date_accumulated, line_last_date_accumulated, data_accumulated);
                        new_cases_array_to_print = daily_new_cases_array;
                        break;
                    case "1":
                        int[] week_limits = weeksLimits(line_first_date_accumulated, line_last_date_accumulated, dates_accumulated);
                        validateWeeksLimitsNonInterative(week_limits[0], week_limits[1], dates_accumulated);
                        daily_new_cases_array = calculateDailyNewCases(week_limits[0], week_limits[1], data_accumulated);
                        new_cases_array_to_print = calculateWeeklyNewCases(daily_new_cases_array);
                        break;
                    case "2":
                        validateMonthLimitsNonInteractive(MINIMUM_DAYS_IN_A_MONTH, firstLimitMonth(line_first_date_accumulated, dates_accumulated), secondLimitMonth(line_first_date_accumulated, dates_accumulated, calculateNumberOfDaysInAMonth(line_last_date_accumulated, dates_accumulated)));
                        daily_new_cases_array = calculateDailyNewCases(firstLimitMonth(line_first_date_accumulated, dates_accumulated), secondLimitMonth(line_first_date_accumulated, dates_accumulated, calculateNumberOfDaysInAMonth(line_last_date_accumulated, dates_accumulated)), data_accumulated);
                        new_cases_array_to_print = calculateMonthlyNewCases(daily_new_cases_array, dates_accumulated, MONTH_INCREASE, firstLimitMonth(line_first_date_accumulated, dates_accumulated));
                }

                // ACUMULADOS
                int period_1_accumulated = numberOfDaysBetweenTwoDates(findLineOfTheDate(initial_date_1, dates_accumulated), findLineOfTheDate(final_date_1, dates_accumulated));
                int period_2_accumulated = numberOfDaysBetweenTwoDates(findLineOfTheDate(initial_date_2, dates_accumulated), findLineOfTheDate(final_date_2, dates_accumulated));
                int number_days_to_compare_accumulated = numberOfDaysToCompare(period_1_accumulated, period_2_accumulated);
                int[][] period_1_differences = calculateDailyNewCases(findLineOfTheDate(initial_date_1, dates_accumulated), (number_days_to_compare_accumulated + findLineOfTheDate(initial_date_1, dates_accumulated)), data_accumulated);  // dá return à matriz das diferenças do período
                int[][] period_2_differences = calculateDailyNewCases(findLineOfTheDate(initial_date_2, dates_accumulated), (number_days_to_compare_accumulated + findLineOfTheDate(initial_date_2, dates_accumulated)), data_accumulated);
                comparison_array_acc = calculateDifferenceArrays(period_1_differences, period_2_differences, number_days_to_compare_accumulated);
                period_1_average_acc = averageForComparison(period_1_differences, number_days_to_compare_accumulated);
                period_2_average_acc = averageForComparison(period_2_differences, number_days_to_compare_accumulated);
                period_1_desvio_padrao_acc = standardDeviationForComparison(period_1_differences, period_1_average_acc, number_days_to_compare_accumulated);
                period_2_desvio_padrao_acc = standardDeviationForComparison(period_2_differences, period_2_average_acc, number_days_to_compare_accumulated);
                comparisons_array_average_acc = averageForComparison(comparison_array_acc, number_days_to_compare_accumulated);
                comparisons_array_desvio_padrao_acc = standardDeviationForComparison(comparison_array_acc, comparisons_array_average_acc, number_days_to_compare_accumulated);
                //TOTAIS
                period_1_total = numberOfDaysBetweenTwoDates(findLineOfTheDate(initial_date_1, dates_totals), findLineOfTheDate(final_date_1, dates_totals));
                period_2_total = numberOfDaysBetweenTwoDates(findLineOfTheDate(initial_date_2, dates_totals), findLineOfTheDate(final_date_2, dates_totals));
                number_days_to_compare_total = numberOfDaysToCompare(period_1_total, period_2_total);
                int[][] period_1_totals = defineTotalArray(findLineOfTheDate(initial_date_1, dates_totals), (number_days_to_compare_total + findLineOfTheDate(initial_date_1, dates_totals)), data_totals, number_days_to_compare_total);
                int[][] period_2_totals = defineTotalArray(findLineOfTheDate(initial_date_2, dates_totals), (number_days_to_compare_total + findLineOfTheDate(initial_date_2, dates_totals)), data_totals, number_days_to_compare_total);
                comparisons_array_totals = calculateDifferenceArrays(period_1_totals, period_2_totals, number_days_to_compare_total);
                period_1_average_totals = averageForComparison(period_1_totals, number_days_to_compare_total);  // calcula a média por tab de um período
                period_2_average_totals = averageForComparison(period_2_totals, number_days_to_compare_total);
                period_1_desvio_padrao_totals = standardDeviationForComparison(period_1_totals, period_1_average_totals, number_days_to_compare_total); // calcula o desvio padrão por tab de um período
                period_2_desvio_padrao_totals = standardDeviationForComparison(period_2_totals, period_2_average_totals, number_days_to_compare_total);
                values_of_t_and_k = getPeriodBetweenDatesPredict(day_to_predict, dates_totals);
                int t = values_of_t_and_k[0];
                int k = values_of_t_and_k[1];
                predict_numbers = calculateNumberPredict(k, P_matrix, getTabsNumbersFromDay(data_totals, t));
                calculateDaysToDeathArray(P_matrix, days_to_death);

                //FAZER TUDO

                // PRINTS
                nonInteractivePrinterTwoDatesVariation(output, tabs_acc, dates_accumulated, new_cases_array_to_print, resolution_time, line_first_date_accumulated);
                nonInteractiveDashToFile(output);
                nonInteractivePrinterToFileTotalsDay(output, dates_totals, line_first_date_totals, data_totals, tabs_totals);
                nonInteractiveDashToFile(output);
                nonInteractivePrinterComparisons(output, tabs_acc, dates_accumulated, comparison_array_acc, initial_date_1, initial_date_2, number_days_to_compare_accumulated);
                nonInteractiveDashToFile(output);

                output.format("Diferença entre Períodos%n");
                nonInteractiveAverageAndDesvioToFile(output, "|Medias|", comparisons_array_average_acc, tabs_acc);
                nonInteractiveAverageAndDesvioToFile(output, "|Desvio Padrao|", comparisons_array_desvio_padrao_acc, tabs_acc);
                nonInteractiveDashToFile(output);
                output.format("Período 1: %s - %s%n%n", dates_accumulated[findLineOfTheDate(initial_date_1, dates_accumulated)], dates_accumulated[(findLineOfTheDate(initial_date_1, dates_accumulated)) + number_days_to_compare_accumulated]);
                nonInteractiveAverageAndDesvioToFile(output, "|Medias|", period_1_average_acc, tabs_acc);
                nonInteractiveAverageAndDesvioToFile(output, "|Desvio Padrao|", period_1_desvio_padrao_acc, tabs_acc);
                nonInteractiveDashToFile(output);
                output.format("Período 2: %s - %s%n%n", dates_accumulated[findLineOfTheDate(initial_date_2, dates_accumulated)], dates_accumulated[(findLineOfTheDate(initial_date_2, dates_accumulated)) + number_days_to_compare_accumulated]);
                nonInteractiveAverageAndDesvioToFile(output, "|Medias|", period_2_average_acc, tabs_acc);
                nonInteractiveAverageAndDesvioToFile(output, "|Desvio Padrao|", period_2_desvio_padrao_acc, tabs_acc);
                nonInteractiveDashToFile(output);

                nonInteractivePrinterComparisons(output, tabs_totals, dates_accumulated, comparisons_array_totals, initial_date_1, initial_date_2, number_days_to_compare_total);
                nonInteractiveDashToFile(output);
                output.format("Diferença entre Períodos%n");
                nonInteractiveAverageAndDesvioToFile(output, "|Medias|", comparisons_array_average_acc, tabs_totals);
                nonInteractiveAverageAndDesvioToFile(output, "|Desvio Padrao|", comparisons_array_desvio_padrao_acc, tabs_totals);
                nonInteractiveDashToFile(output);
                output.format("Período 1: %s - %s%n%n", dates_totals[findLineOfTheDate(initial_date_1, dates_totals)], dates_totals[(findLineOfTheDate(initial_date_1, dates_totals)) + number_days_to_compare_total]);
                nonInteractiveAverageAndDesvioToFile(output, "|Medias|", period_1_average_totals, tabs_totals);
                nonInteractiveAverageAndDesvioToFile(output, "|Desvio Padrao|", period_1_desvio_padrao_totals, tabs_totals);
                nonInteractiveDashToFile(output);
                output.format("Período 2: %s - %s%n%n", dates_totals[findLineOfTheDate(initial_date_2, dates_totals)], dates_totals[(findLineOfTheDate(initial_date_2, dates_totals)) + number_days_to_compare_total]);
                nonInteractiveAverageAndDesvioToFile(output, "|Medias|", period_2_average_totals, tabs_totals);
                nonInteractiveAverageAndDesvioToFile(output, "|Desvio Padrao|", period_2_desvio_padrao_totals, tabs_totals);
                nonInteractiveDashToFile(output);
                output.format("Números previsto para o dia %s%n%n", day_to_predict);
                nonInteractivePredictsToFile(output, day_to_predict, predict_numbers);
                nonInteractiveDashToFile(output);
                nonInteractiveDaysToDeath(output, days_to_death);
                output.close();
            }

        } else {
            System.out.println("INTRODUCAO INVALIDA");
        }

    }

    public static void nonInteractivePrinterComparisons(PrintWriter output, String[] tabs, String[] dates, int[][] comparisons, String di1, String di2, int number_days_to_compare_acc) {
        int line_first_date_1 = findLineOfTheDate(di1, dates);
        int line_first_date_2 = findLineOfTheDate(di2, dates);
        output.format("Diferenças de Casos entre o período de %s - %s e o período de %s - %s%n%n", di1, dates[line_first_date_1 + number_days_to_compare_acc], di2, dates[line_first_date_2 + number_days_to_compare_acc]);
        output.format("%13s %30s %26s %24s %11s%n", tabs[0], tabs[1], tabs[2], tabs[3], tabs[4]);

        for (int line = 0; line < determineArrayLength(comparisons); line++) {
            output.format("Dias %s - %s ", dates[line_first_date_1], dates[line_first_date_2]);
            for (int column = 1; column < comparisons[0].length; column++) {
                output.format("      %-17d ", comparisons[line][column]);
            }
            output.format("%n");
            line_first_date_1++;
            line_first_date_2++;
        }
        output.format("%n");
    }

    public static void nonInteractiveDashToFile(PrintWriter output) {
        for (int i = 0; i < 121; i++) {
            output.format("-");
        }
        output.format("%n");
    }

    public static void nonInteractivePrinterTwoDatesVariation(PrintWriter output, String[] tabs, String[] dates_acc, int[][] new_cases_array_to_print, String resolution_time, int line_first_date_acc)  {

        if (resolution_time.equals("0")) {
            output.format("%-22s%-23s%-24s%-25s%15s%n", tabs[0], tabs[1], tabs[2], tabs[3], tabs[4]);
        } else if (resolution_time.equals("1")) {
            output.format("%-29s%-22s%-24s%-26s%15s%n", tabs[0], tabs[1], tabs[2], tabs[3], tabs[4]);
        } else {
            output.format("%-23s%-23s%-24s%-25s%15s%n", tabs[0], tabs[1], tabs[2], tabs[3], tabs[4]);
        }
        int accumulated_increase, month_number_of_days = 0;
        for (int line = 0; line < determineArrayLength(new_cases_array_to_print); line++) {

            if (resolution_time.equals("1")) {
                output.print("Semana de ");
                accumulated_increase = WEEK_INCREASE * line;
                output.printf("%-20s", (dates_acc[line_first_date_acc + accumulated_increase]));
            } else if (resolution_time.equals("2")) {
                String[] month_names = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
                int month_determiner = line_first_date_acc + DAY_INCREASE + month_number_of_days; //variavel que ajuda a determinar o próximo mês
                output.format("%-24s", month_names[getMonthNumber(dates_acc, month_determiner)]);
                month_number_of_days += calculateNumberOfDaysInAMonth(month_determiner, dates_acc);
            } else if (resolution_time.equals("0")) {
                accumulated_increase = DAY_INCREASE * line;
                output.format("%-24s", (dates_acc[line_first_date_acc + accumulated_increase]));
            }

            for (int column = 1; column < new_cases_array_to_print[0].length; column++) {
                output.format("   %-24d", new_cases_array_to_print[line][column]);
            }
            output.format("%n");
        }
    }

    public static void nonInteractivePrinterToFileTotalsDay(PrintWriter output, String[] dates_total, int line_of_1st_date_totals, int[][] data_totals, String[] tabs) {
        output.format("  %-16s%-20s%-19s%-27s%-19s%n", tabs[0], tabs[1], tabs[2], tabs[3], tabs[4]);
        output.format("  %s          ", dates_total[line_of_1st_date_totals]);
        for (int tab = 1; tab < data_totals[0].length; tab++) {
            output.format("%-22d", data_totals[line_of_1st_date_totals][tab]);
        }
        output.format("%n");
    }

    public static void nonInteractiveAverageAndDesvioToFile(PrintWriter output, String to_print, double[] period, String[] tabs) {
        if (to_print.equals("|Medias|")) {
            output.format("%52s%n%n", to_print);
        } else {
            output.format("%56s%n%n", to_print);
        }

        output.format(" %-25s%-25s%-26s      %-24s%n", tabs[1], tabs[2], tabs[3], tabs[4]);
        for (int value = 0; value < period.length; value++) {
            output.format("   %-24.4f", period[value]);
        }
        output.format("%n");
        output.format("%n");
    }

    public static void nonInteractivePredictsToFile(PrintWriter output, String day_to_predict, double[] predict_numbers) {
        String[] tabs = {"|Data|", "|Nao Infetados|", " |Casos Ativos|", "|Hospitalizacoes Ativas|", " |Internados UCI Ativos|", "    |Obitos|"};
        output.format("  %-17s%-21s%-17s%-24s%-17s%s%n", tabs[0], tabs[1], tabs[2], tabs[3], tabs[4], tabs[5]);
        output.format(day_to_predict);
        for (double number : predict_numbers) {
            output.format(" %20.1f", number);
        }
        output.format("%n%n");
    }

    public static void nonInteractiveDaysToDeath(PrintWriter output, double[] days_to_death) {
        String[] tabs_days_to_death = {"->Se nao estiver infetado: ", "->Se estiver infetado: ", "->Se estiver hospitalizado: ", "->Se estiver internado nos UCI: "};
        output.format("%n");
        output.format("Número de dias, em media, esperados ate a morte: %n%n");
        for (int position = 0; position < days_to_death.length; position++) {
            output.format("%20s%d%n", tabs_days_to_death[position], (int) days_to_death[position]);
        }
    }

    public static int determineArrayLength(int[][] array) {
        int line, sum = 0;
        for (line = 0; line < array.length; line++) {
            for (int column = 0; column < array[0].length; column++) {
                sum += array[line][column];
            }
            if (sum == 0) {
                return line;
            }
            sum = 0;
        }
        return line;
    }

    public static String getParameter(String[] console_inputs, String parameter_to_find) {
        for (int position = 0; position < console_inputs.length; position++) {
            if (console_inputs[position].equals(parameter_to_find)) {
                return console_inputs[position + 1];
            }
        }
        return "null";
    }

    public static String validateDatePredict(String date, String[] dates) {
        while (getPeriodBetweenDatesPredict(date, dates)[1] < 0) {
            System.out.println("Data Invalida. Introduza novamente");
            System.out.print("Nova data: ");
            date = sc.nextLine();
            return date;
        }
        return date;
    }

    public static int[] getPeriodBetweenDatesPredict(String wanted_day, String[] dates) {
        int[] values_of_t_and_k = new int[2];

        if (findLineOfTheDate(wanted_day, dates) == INVALIDATION_VALUE) {
            String[] line2_items = wanted_day.split("-");

            String line1 = getLastLineOfFile(dates);
            String[] line1_items = line1.split("-");

            Date date1 = new Date(Integer.parseInt(line1_items[2]), Integer.parseInt(line1_items[1]) - 1, Integer.parseInt(line1_items[0]));
            Date date2 = new Date(Integer.parseInt(line2_items[2]), Integer.parseInt(line2_items[1]) - 1, Integer.parseInt(line2_items[0]));

            long difference_in_time = (date2.getTime() - date1.getTime());
            difference_in_time = difference_in_time / (1000 * 3600 * 24);
            values_of_t_and_k[0] = findLineOfTheDate(line1, dates);
            values_of_t_and_k[1] = (int) difference_in_time;

        } else {
            int k = findLineOfTheDate(wanted_day, dates);
            String t_date = dates[k - 1];
            int t = findLineOfTheDate(t_date, dates);
            values_of_t_and_k[0] = t;
            values_of_t_and_k[1] = 1;
        }
        return values_of_t_and_k;
    }

    public static int[] getTabsNumbersFromDay(int[][] data, int line) {
        int[] numbers_from_day = new int[NUMBER_OF_TABS];
        for (int column = 0; column < data[0].length; column++) {
            numbers_from_day[column] = data[line][column];
        }
        return numbers_from_day;
    }

    public static double[] calculateNumberPredict(int k, double[][] probability, int[] numbers_from_day) {
        double[] number_predict_tabs = new double[NUMBER_OF_TABS];
        double sum = 0;

        for (int line = 0; line < probability.length; line++) {
            for (int column = 0; column < probability[0].length; column++) {
                sum += Math.pow(probability[line][column], k) * numbers_from_day[line];
            }
            number_predict_tabs[line] = sum;
            sum = 0;
        }
        return number_predict_tabs;
    }

    public static void printerPredictNumber(double[] predict_numbers, String wanted_day, int data_option) {
        String[] tabs = {"|Data|", "|Nao Infetados|", "|Casos Ativos|", "|Total de Hospitalizados|", "|Total de Internados em UCI|", "|Obitos|"};
        switch (data_option) {
            case 1:
                System.out.printf(" %-20s %s%n", tabs[0], tabs[1]);
                System.out.printf("%-20s ", wanted_day);
                System.out.printf("%15.4f%n%n", predict_numbers[0]);
                break;

            case 2:
                System.out.printf(" %-20s %s%n", tabs[0], tabs[2]);
                System.out.printf("%-20s ", wanted_day);
                System.out.printf("%13.4f%n%n", predict_numbers[1]);
                break;

            case 3:
                System.out.printf(" %-20s %s%n", tabs[0], tabs[3]);
                System.out.printf("%-20s ", wanted_day);
                System.out.printf("%17.4f%n%n", predict_numbers[2]);
                break;

            case 4:
                System.out.printf(" %-20s %s%n", tabs[0], tabs[4]);
                System.out.printf("%-20s ", wanted_day);
                System.out.printf("%17.4f%n%n", predict_numbers[3]);
                break;

            case 5:
                System.out.printf(" %-20s %s%n", tabs[0], tabs[5]);
                System.out.printf("%-20s ", wanted_day);
                System.out.printf("%8.4f%n%n", predict_numbers[4]);
                break;

            case 6:
                String[] tabs_spefic = {" |Data|", "|Nao Infetados|", " |Casos Ativos|", "|Total de Hospitalizados|", "|Total de Internados em UCI|", "        |Obitos|"};
                System.out.printf("%-23s%-25s%-21s%-24s%-23s%-24s%n", tabs_spefic[0], tabs_spefic[1], tabs_spefic[2], tabs_spefic[3], tabs_spefic[4], tabs_spefic[5]);
                System.out.print(wanted_day);
                for (double number : predict_numbers) {
                    System.out.printf(" %24.1f", number);
                }
                System.out.println();
        }

    }

    public static void predictNumberToFile(int data_option, String wanted_day, double[] predict_numbers, String output_file_name) throws FileNotFoundException {
        PrintWriter output = new PrintWriter(output_file_name);
        String[] tabs = {"|Data|", "|Nao Infetados|", "|Casos Ativos|", "|Total de Hospitalizados|", "|Total de Internados em UCI|", "|Obitos|"};
        switch (data_option) {
            case 1:
                output.printf(" %-20s %s%n", tabs[0], tabs[1]);
                output.printf("%-20s ", wanted_day);
                output.printf("%15.4f", predict_numbers[0]);
                break;

            case 2:
                output.printf(" %-20s %s%n", tabs[0], tabs[2]);
                output.printf("%-20s ", wanted_day);
                output.printf("%13.4f", predict_numbers[1]);
                break;

            case 3:
                output.printf(" %-20s %s%n", tabs[0], tabs[3]);
                output.printf("%-20s ", wanted_day);
                output.printf("%17.4f", predict_numbers[2]);
                break;

            case 4:
                output.printf(" %-20s %s%n", tabs[0], tabs[4]);
                output.printf("%-20s ", wanted_day);
                output.printf("%17.4f", predict_numbers[3]);
                break;

            case 5:
                output.printf(" %-20s %s%n", tabs[0], tabs[5]);
                output.printf("%-20s ", wanted_day);
                output.printf("%8.4f", predict_numbers[4]);
                break;

            case 6:
                String[] tabs_spefic = {" |Data|", "|Nao Infetados|", " |Casos Ativos|", "|Total de Hospitalizados|", "|Total de Internados em UCI|", "        |Obitos|"};
                output.printf("%-24s%-24s%-21s%-24s%-23s%-24s%n", tabs_spefic[0], tabs_spefic[1], tabs_spefic[2], tabs_spefic[3], tabs_spefic[4], tabs_spefic[5]);
                output.print(wanted_day);
                for (double number : predict_numbers) {
                    output.printf(" %24.1f", number);
                }
                output.println();
        }
        output.close();
    }

    public static String getLastLineOfFile(String[] dates) {
        for (int date = 0; date < dates.length; date++) {
            if (dates[date] == null) {
                return dates[date - 1];
            }
        }
        return "ERRO";
    }

    public static void calculateDaysToDeathArray(double[][] P_matrix, double[] days_to_death) {
        double[][] Q_matrix = new double[LINES_MARKOV - 1][COLUMNS_MARKOV - 1];
        double[][] I_Q_matrix;
        double[][] L_matrix = new double[LINES_MARKOV - 1][COLUMNS_MARKOV - 1];
        double[][] U_matrix = new double[LINES_MARKOV - 1][COLUMNS_MARKOV - 1];
        double[][] inverted_U_matrix = new double[LINES_MARKOV - 1][COLUMNS_MARKOV - 1];
        double[][] inverted_L_matrix = new double[LINES_MARKOV - 1][COLUMNS_MARKOV - 1];
        double[][] N_matrix = new double[LINES_MARKOV - 1][COLUMNS_MARKOV - 1];
        double[] line_vector_1 = {1, 1, 1, 1};

        transform5x5MatrixInto4x4(P_matrix, Q_matrix);
        I_Q_matrix = subtractIdentityByMatrix(Q_matrix);
        LUDecomposition(I_Q_matrix, L_matrix, U_matrix);
        invertMatrixL(L_matrix, inverted_L_matrix);
        invertMatrixU(U_matrix, inverted_U_matrix);
        if (checkIfMultiplicationIsPossible(inverted_U_matrix, inverted_L_matrix)) {
            multiplicasteMatrices(inverted_U_matrix, inverted_L_matrix, N_matrix);
            if (line_vector_1.length == N_matrix[0].length) {
                multiplicasteVectorByMatrix(line_vector_1, N_matrix, days_to_death);
                System.out.println();
            }
        } else {
            System.out.println("A MATRIZ INTRODUZIDA INICIALMENTE E INVALIDA!");
        }

    }

    public static void multiplicasteVectorByMatrix(double[] vector, double[][] matrix, double[] resultant_vector) {
        for (int column = 0; column < matrix.length; column++) {
            double sum = 0;
            for (int line = 0; line < matrix[0].length; line++) {
                sum += vector[column] *matrix[line][column];
            }
            resultant_vector[column] = sum;
        }
    }

    public static void multiplicasteMatrices(double[][] first_matrix, double[][] second_matrix, double[][] resultant_matrix) {
        for (int line = 0; line < first_matrix[0].length; line++) {
            for (int column = 0; column < second_matrix.length; column++) {
                for (int helper = 0; helper < second_matrix[0].length; helper++)
                    resultant_matrix[line][column] += first_matrix[line][helper] * second_matrix[helper][column];
            }
        }
    }

    public static boolean checkIfMultiplicationIsPossible(double[][] inverted_U, double[][] inverted_L) {
        return inverted_U.length == inverted_L[0].length;
    }

    public static void LUDecomposition(double[][] I_Q_matrix, double[][] L_matrix, double[][] U_matrix) {
        //COLUNA 0
        L_matrix[0][0] = I_Q_matrix[0][0];
        L_matrix[1][0] = I_Q_matrix[1][0];
        L_matrix[2][0] = I_Q_matrix[2][0];
        L_matrix[3][0] = I_Q_matrix[3][0];
        U_matrix[0][0] = 1;
        U_matrix[1][0] = 0;
        U_matrix[2][0] = 0;
        U_matrix[3][0] = 0;//CERTO
        //LINHA 0
        U_matrix[0][1] = I_Q_matrix[0][1] / I_Q_matrix[0][0];
        U_matrix[0][2] = I_Q_matrix[0][2] / I_Q_matrix[0][0];
        U_matrix[0][3] = I_Q_matrix[0][3] / I_Q_matrix[0][0];
        L_matrix[0][1] = 0;
        L_matrix[0][2] = 0;
        L_matrix[0][3] = 0;//CERTO
        //COLUNA 1
        L_matrix[1][1] = I_Q_matrix[1][1] - (L_matrix[1][0] * U_matrix[0][1]);
        L_matrix[2][1] = I_Q_matrix[2][1] - (L_matrix[2][0] * U_matrix[0][1]);
        L_matrix[3][1] = I_Q_matrix[3][1] - (L_matrix[3][0] * U_matrix[0][1]);
        U_matrix[1][1] = 1;
        U_matrix[2][1] = 0;
        U_matrix[3][1] = 0;//CERTO
        //COLUNA 2
        U_matrix[1][2] = (I_Q_matrix[1][2] - (L_matrix[1][0] * U_matrix[0][2])) / L_matrix[1][1];
        U_matrix[2][2] = 1;
        U_matrix[3][2] = 0;
        L_matrix[1][2] = 0;
        L_matrix[2][2] = I_Q_matrix[2][2] - (L_matrix[2][0] * U_matrix[0][2]) - (L_matrix[2][1] * U_matrix[1][2]);
        L_matrix[3][2] = I_Q_matrix[3][2] - (L_matrix[3][0] * U_matrix[0][2]) - (L_matrix[3][1] * U_matrix[1][2]);//CERTO
        //COLUNA 3
        L_matrix[1][3] = 0;
        L_matrix[2][3] = 0;
        U_matrix[1][3] = (I_Q_matrix[1][3] - (L_matrix[1][0] * U_matrix[0][3])) / L_matrix[1][1];
        U_matrix[2][3] = (I_Q_matrix[2][3] - ((L_matrix[2][0] * U_matrix[0][3]) + (L_matrix[2][1] * U_matrix[1][3]))) / L_matrix[2][2];
        L_matrix[3][3] = I_Q_matrix[3][3] - ((L_matrix[3][0] * U_matrix[0][3]) + (L_matrix[3][1] * U_matrix[1][3]) + (L_matrix[3][2] * U_matrix[2][3]));

        U_matrix[3][3] = 1;//CERTO

    } //LU DECOMPOSITION

    public static void invertMatrixL(double[][] L_matrix, double[][] inverted_matrix) {
        //DIAGONAL PRINCIPAL
        inverted_matrix[0][0] = 1 / (L_matrix[0][0]);//
        inverted_matrix[1][1] = 1 / (L_matrix[1][1]);//
        inverted_matrix[2][2] = 1 / (L_matrix[2][2]);//
        inverted_matrix[3][3] = 1 / (L_matrix[3][3]);//
        //COLUNA 0
        inverted_matrix[1][0] = (-(L_matrix[1][0] * inverted_matrix[0][0])) / L_matrix[1][1]; // CERTO

        inverted_matrix[2][0] = -((L_matrix[2][0] * inverted_matrix[0][0]) + (L_matrix[2][1] * inverted_matrix[1][0])) / L_matrix[2][2];//
        inverted_matrix[3][0] = -((L_matrix[3][0] * inverted_matrix[0][0]) + (L_matrix[3][1] * inverted_matrix[1][0]) + (L_matrix[3][2] * inverted_matrix[2][0])) / L_matrix[3][3];//ver
        //COLUNA 1
        inverted_matrix[2][1] = (-(L_matrix[2][1] * inverted_matrix[1][1])) / L_matrix[2][2];//
        inverted_matrix[3][1] = -((L_matrix[3][1] * inverted_matrix[1][1]) + (L_matrix[3][2] * inverted_matrix[2][1])) / L_matrix[3][3];
        //COLUNA 2
        inverted_matrix[3][2] = (-(L_matrix[3][2] * inverted_matrix[2][2])) / L_matrix[3][3];
    }

    public static void invertMatrixU(double[][] U_matrix, double[][] inverted_matrix_U) {
        //DIAGONAL DIRETAMENTE ACIMA DA DIAGONAL PRINCIPAL
        inverted_matrix_U[0][1] = -U_matrix[0][1]; // CERTO
        inverted_matrix_U[1][2] = -U_matrix[1][2]; // CERTO
        inverted_matrix_U[2][3] = -U_matrix[2][3]; // CERTO
        //PRIMEIRA LINHA
        inverted_matrix_U[0][2] = -((U_matrix[0][1] * inverted_matrix_U[1][2]) + U_matrix[0][2]);  // CERTO

        inverted_matrix_U[1][3] = -((U_matrix[1][2] * inverted_matrix_U[2][3]) + U_matrix[1][3]);  // CERTO

        inverted_matrix_U[0][3] = -((U_matrix[0][1] * inverted_matrix_U[1][3]) + (U_matrix[0][2] * inverted_matrix_U[2][3] + U_matrix[0][3]));  // CERTO
        //SEGUNDA LINHA

        //
        inverted_matrix_U[0][0] = 1;
        inverted_matrix_U[1][1] = 1;
        inverted_matrix_U[2][2] = 1;
        inverted_matrix_U[3][3] = 1;
    }

    public static void PrintVectorFile(double[] vector, String[] tabs, String output_file) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(output_file);
        out.println("Numero de dias, em media, esperados ate a morte: ");
        for (int position = 0; position < vector.length; position++) {
        out.printf("%20s%d%n", tabs[position], (int) vector[position]);
        }
        out.close();
    }
    public static void PrintVectorConsole(double[] vector, String[] tabs) {
        System.out.println();
        System.out.println("Numero de dias, em media, esperados ate a morte: ");
        for (int position = 0; position < vector.length; position++) {
            System.out.printf("%20s%d%n", tabs[position], (int) vector[position]);
        }
    }

    public static double[][] createIdentity(int order) {
        double[][] matrix = new double[order][order];
        for (int position = 0; position < order; position++) {
            matrix[position][position] = 1;
        }
        return matrix;
    }

    public static double[][] subtractIdentityByMatrix(double[][] Q_matrix) {
        double[][] identity_matrix = createIdentity(Q_matrix.length);
        for (int line = 0; line < Q_matrix.length; line++) {
            for (int column = 0; column < Q_matrix.length; column++) {
                Q_matrix[line][column] = identity_matrix[line][column] - Q_matrix[line][column];
            }
        }
        return Q_matrix;
    }

    public static void transform5x5MatrixInto4x4(double[][] P_matrix, double[][] Q_matrix) {
        for (int line = 0; line < Q_matrix.length; line++) {
            for (int column = 0; column < Q_matrix.length; column++) {
                Q_matrix[line][column] = P_matrix[line][column];
            }
        }
    }

    public static void verifyAndReadFile(String file_name_input, int[][] data, String[] dates) throws FileNotFoundException {
        if (checkIfThereIsFile(file_name_input)) {
            readData(file_name_input, data, dates);
            verifyDateFormatAndAdapt(dates);
        } else {
            System.out.printf("Ficheiro nao encontrado!%n%nPretende tentar de novo?%n%n1 - Sim.%n2 - Nao.%n%nOpçao: ");
            int option = sc.nextInt();
            if (option == 1) {
                file_name_input = chooseNameFileMenu();
                verifyAndReadFile(file_name_input, data, dates);
            }
        }
    }

    public static void verifyAndReadFileForMarkov(String file_name_input, double[][] probability_array) throws FileNotFoundException {
        if (checkIfThereIsFile(file_name_input)) {
            readDataMarkov(file_name_input, probability_array);
        } else {
            System.out.printf("Ficheiro nao encontrado!%n%nPretende tentar de novo?%n%n1 - Sim.%n2 - Nao.%n%nOpçao: ");
            int option = sc.nextInt();
            if (option == 1) {
                file_name_input = chooseNameFileMenu();
                verifyAndReadFileForMarkov(file_name_input, probability_array);
            }
        }

    }

    public static boolean checkIfThereIsFile(String file_name) {
        File file = new File(file_name);
        return file.isFile();
    }

    public static void predictMenu() {
        MainMenu();
        System.out.printf("%nPretende obter:%n1 - Uma estimativa dos dados num determinado dia.%n2 - Uma estimativa da media de dias ate chegar ao estado de óbito.%n3 - Voltar%n%nOpçao: ");
    }

    public static void readDataMarkov(String file_input_name, double[][] probability) throws FileNotFoundException {
        Scanner input = new Scanner(new File(file_input_name));
        String line;
        int line_array = 0;
        int column_array = 0;
        while (input.hasNextLine()) {
            line = input.nextLine();
            if (!line.isEmpty()) {
                String[] items = line.split("=");
                probability[line_array][column_array] = Double.parseDouble(items[1]);
                column_array++;
            } else {
                line_array++;
                column_array = 0;
            }
        }
        input.close();
    }

    public static void chooseFileTypeMenu() {
        MainMenu();
        System.out.printf("%nPretende carregar um ficheiro com:%n1 - O registo de dados acumulados.%n2 - O registo do total de casos.%n3 - Uma matriz de transiçoes.%n4 - Voltar%n%nOpçao: ");
    }

    public static void askPeriodsForDifferences(int[] line_of_user_date, String[] dates_accumulated) {
        System.out.println();
        System.out.println("Para o primeiro período introduza as datas: ");
        line_of_user_date[0] = validateDateForDifferences(findLineOfTheDate((askDate()), dates_accumulated), dates_accumulated);
        line_of_user_date[1] = validateDateForDifferences(findLineOfTheDate((askDate()), dates_accumulated), dates_accumulated);

        System.out.println();
        System.out.println("Para o segundo período introduza as datas: ");
        line_of_user_date[2] = validateDateForDifferences(findLineOfTheDate((askDate()), dates_accumulated), dates_accumulated);
        line_of_user_date[3] = validateDateForDifferences(findLineOfTheDate((askDate()), dates_accumulated), dates_accumulated);
    }//Pergunta ao user dates e atualiza o vetor line_of_user_date para diferenças

    public static void askPeriodsForTotals(int[] line_of_user_date, String[] dates_accumulated) {
        System.out.println();
        System.out.println("Para o primeiro período introduza as datas: ");
        line_of_user_date[0] = validateDate(findLineOfTheDate((askDate()), dates_accumulated), dates_accumulated);
        line_of_user_date[1] = validateDate(findLineOfTheDate((askDate()), dates_accumulated), dates_accumulated);
        System.out.println();
        System.out.println("Para o segundo período introduza as datas: ");
        line_of_user_date[2] = validateDate(findLineOfTheDate((askDate()), dates_accumulated), dates_accumulated);
        line_of_user_date[3] = validateDate(findLineOfTheDate((askDate()), dates_accumulated), dates_accumulated);
    }//Pergunta ao user dates e atualiza o vetor line_of_user_date para totais

    public static void validateWeeksLimitsNonInterative(int line1, int line2, String[] dates) {
        int[] limits = weeksLimits(line1, line2, dates);
        if (limits[1] - limits[0] < WEEK_INCREASE - DAY_INCREASE) {
            System.out.println("ERROR - INVALID DATES  - NOT A FULL WEEK");
            System.exit(0);
        }
    }//Validar os dados para as semanas

    public static int validateDateNonInteractive(int line) {
        if (line == INVALIDATION_VALUE) {
            System.out.println("ERROR - INVALID DATES ");
            System.exit(0);
        }
        return line;
    }

    public static void validateMonthLimitsNonInteractive(int MINIMUM_DAYS_IN_A_MONTH, int line1, int line2) {
        if ((line2 - line1) < MINIMUM_DAYS_IN_A_MONTH) {
            System.out.println("ERROR - INVALID DATES  - NOT A FULL MONTH");
            System.exit(0);
        }
    } // Validar os dados para os meses

    public static void resolutionTimeNonInteractive(String resolution_time) {
        if (resolution_time.equals("0") || resolution_time.equals("1") || resolution_time.equals("2")) {
            int counter = 0;
        } else {
            System.out.println("ERROR - RESOLUTION TIME - NOT VALID");
            System.exit(0);
        }
    }

    public static boolean checkIfFirstTime(int check_if_default) {
        return check_if_default != 1;
    } // Fazer os defaults funcionar a 100%

    public static void MainMenu() {
        String m = "Menu";
        System.out.println();
        Dash();
        System.out.printf("%31s%n", m);
        Dash();
        System.out.println();
    } // Apresentar o início do Menu

    public static void Dash() {
        for (int i = 0; i < 58; i++) {
            System.out.print("-");
        }
        System.out.println();
    }

    public static void readData(String file, int[][] data, String[] dates) throws FileNotFoundException {
        Scanner input = new Scanner(new File(file));
        String line; //
        input.nextLine();
        int day = 0;
        while (input.hasNextLine()) {
            line = input.nextLine();
            String[] items = line.split(",");
            dates[day] = items[0];
            for (int tab = 0, item = 1; tab < data[0].length; tab++, item++) {
                data[day][tab] = Integer.parseInt(items[item]);
            }
            day++;
        }
        input.close();
    } // Ler toda a informação necessária do ficheiro

    public static String askDate() {
        System.out.printf("%nFormato DD-MM-YYYY: ");
        return sc.next();
    }// Perguntar a data

    public static void verifyDateFormatAndAdapt(String[] dates) {
        String date = dates[0];
        String[] separate_date = date.split("-");
        int date_int_value = Integer.parseInt(separate_date[0]);
        if (date_int_value > 99) {
            for (int line = 0; dates[line] != null; line++) {
                date = dates[line];
                dates[line] = reverseDateFormat(date);
            }
        }
    }  // Verificar se a datas se encontram no formato YYYY-MM-DD e se sim inverter

    public static String reverseDateFormat(String date) {

        String[] separate_date = date.split("-");
        String[] reversed_format_date = new String[separate_date.length];
        for (int item = 0; item < separate_date.length; item++) {
            reversed_format_date[item] = separate_date[separate_date.length - 1 - item];
        }
        return String.join("-", reversed_format_date[0], reversed_format_date[1], reversed_format_date[2]);

    } // Passar de DD-MM-YYYY para YYYY-MM-DD e vice versa

    public static int validateDate(int line, String[] dates) {
        while (line == INVALIDATION_VALUE) {
            System.out.printf("Nao ha dados correspondentes ao dia introduzido.%nPor favor introduzir dados novamente.%n");
            line = findLineOfTheDate((askDate()), dates);
        }
        return line;
    }// Para verificar que o dia se encontra na lista de dados

    public static int findLineOfTheDate(String date_to_compare, String[] dates) {
        for (int day_test = 0; day_test < dates.length; day_test++) {
            if (dates[day_test] != null && dates[day_test].equals(date_to_compare)) {
                return day_test;
            }
        }
        return INVALIDATION_VALUE;
    }// encontrar em que linha da matriz com as datas se encontra determinada data

    public static int getDayOfTheWeek(int line, String[] dates) {
        String[] date_temp = dates[line].split("-");
        int year = Integer.parseInt(date_temp[2]);
        int month = Integer.parseInt(date_temp[1]);
        int day = Integer.parseInt(date_temp[0]);
        calendar.set(year, month - 1, day - 1); // Janeiro = 0
        int week_day;
        week_day = calendar.get(calendar.DAY_OF_WEEK); // Segunda = 1, Terça = 2 e assim sucessivamente
        if (week_day == 7) {
            week_day = 0;  // Domingo = 0
        }
        return week_day;
    } // ir buscar o dia da semana

    public static int[] weeksLimits(int day1_line, int day2_line, String[] dates) {

        int first_date = getDayOfTheWeek(day1_line, dates);
        int final_date = getDayOfTheWeek(day2_line, dates);
        if (first_date != 1) {
            int days_to_go_forward = 8 - first_date;
            day1_line += days_to_go_forward;
        }
        if (final_date != 0) {
            day2_line -= final_date;
        }
        int[] days_lines = new int[2];
        days_lines[0] = day1_line;
        days_lines[1] = day2_line;
        return days_lines;
    } // Ir buscar a segunda feira e domingo correspondente

    public static int[] validateWeeksLimits(int line1, int line2, String[] dates) {
        int[] limits = weeksLimits(line1, line2, dates);
        //retifica as datas
        while (limits[1] - limits[0] < WEEK_INCREASE - DAY_INCREASE) {
            System.out.printf("Nao ha dados suficientes para uma semana inteira.%nPor favor introduza novamente os dados.%n");
            System.out.printf("%nPara a primeira data introduza os dados: %n");
            line1 = validateDateForDifferences(findLineOfTheDate((askDate()), dates), dates);
            System.out.printf("%nPara a segunda data introduza os dados: %n");
            line2 = validateDateForDifferences(findLineOfTheDate((askDate()), dates), dates);
            if (line2 < line1) {
                int temp = line2;
                line2 = line1;
                line1 = temp;
            }
            limits = weeksLimits(line1, line2, dates);
        }
        return limits;
    }//Validar os dados para as semanas

    public static int firstLimitMonth(int line, String[] dates) {
        String[] date_temp = dates[line].split("-");
        int day = Integer.parseInt(date_temp[0]);
        while (day != 1 && line < dates.length) {
            line++;
            date_temp = dates[line].split("-");
            day = Integer.parseInt(date_temp[0]);
        }
        return line;
    }// calcular o primeiro limite para os meses

    public static int secondLimitMonth(int line, String[] dates, int number_of_days_month) {
        String[] date_temp = dates[line].split("-");
        int day = Integer.parseInt(date_temp[0]);

        while (day != number_of_days_month && line > 0) {
            line--;
            date_temp = dates[line].split("-");
            day = Integer.parseInt(date_temp[0]);
            number_of_days_month = calculateNumberOfDaysInAMonth(line, dates);

        }
        return line;
    } // calcular o segundo / limite final para os meses

    public static int getMonthNumber(String[] dates, int line) {
        String[] date_temp = dates[line].split("-");
        int year = Integer.parseInt(date_temp[2]);
        int month = (Integer.parseInt(date_temp[1]) - 1);
        int day = Integer.parseInt(date_temp[0]);
        calendar.set(year, month, day);
        return calendar.get(Calendar.MONTH);
    }

    public static int calculateNumberOfDaysInAMonth(int line, String[] dates) {

        String[] date_temp = dates[line].split("-");
        int year = Integer.parseInt(date_temp[2]);
        int month = (Integer.parseInt(date_temp[1]) - 1);
        int day = Integer.parseInt(date_temp[0]);
        calendar.set(year, month, day);

        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }// Calcular o numero de dias num mês

    public static void validateMonthLimits(String[] dates, int MINUMUN_DAYS_IN_A_MONTH, int[] month_limits, int[] lines_in_order) {

        while ((month_limits[1] - month_limits[0]) < MINUMUN_DAYS_IN_A_MONTH) {
            int line1, line2;
            System.out.printf("Nao ha dados suficientes para um mês inteiro.%nPor favor introduza novamente os dados.%n");
            System.out.println("Para a primeira data introduza os dados: ");
            line1 = validateDateForDifferences(findLineOfTheDate((askDate()), dates), dates);
            System.out.println("Para a segunda data introduza os dados: ");
            line2 = validateDateForDifferences(findLineOfTheDate((askDate()), dates), dates);
            orderDates(line1, line2, lines_in_order);
            month_limits[0] = firstLimitMonth(line1, dates);
            month_limits[1] = secondLimitMonth(line2, dates, calculateNumberOfDaysInAMonth(line2, dates));
        }
    } // Validar os dados para os meses

    public static int validateDateForDifferences(int line, String[] dates) {
        line = validateDate(line, dates);
        while (line == 0) {
            System.out.printf("Nao ha dados suficientes para o dia introduzido.%nO mesmo vai ser descartado.%n%n");
            line += 1;
            validateDate(line, dates);
        }
        return line;
    }

    public static int[][] calculateDailyNewCases(int line_of_1st_date, int line_of_2nd_date, int[][] data) {
        line_of_1st_date -= 1;
        int total_lines = line_of_2nd_date - line_of_1st_date;
        int line = 0; // corresponde ao número total de diferenças

        int[][] difference_Matrix = new int[total_lines][data[0].length];
        while (line_of_1st_date < line_of_2nd_date) {
            for (int column = 0; column < data[0].length; column++) {
                difference_Matrix[line][column] = (data[line_of_1st_date + DAY_INCREASE][column] - data[line_of_1st_date][column]);
            }
            line++;
            line_of_1st_date += DAY_INCREASE;
        }
        return difference_Matrix;
    } // Calcular as diferenças diarias entre duas datas

    public static int[][] calculateWeeklyNewCases(int[][] difference_matrix) {
        double number_of_weeks_support = Math.round(difference_matrix.length / (double) WEEK_INCREASE);
        int number_of_weeks = (int) number_of_weeks_support, increase = 0;
        int[][] weekTotals = new int[number_of_weeks][difference_matrix[0].length];

        for (int week = 0; week < number_of_weeks; week++) {
            for (int tab = 0; tab < difference_matrix[0].length; tab++) {
                for (int day = increase; day < WEEK_INCREASE + increase; day++) {
                    weekTotals[week][tab] += difference_matrix[day][tab];
                }
            }
            increase += WEEK_INCREASE;

        }
        return weekTotals;
    }// Calcular as diferenças entre semanas

    public static int[][] calculateMonthlyNewCases(int[][] difference_matrix, String[] dates, int month_increase, int line_1st_date) {
        double number_of_months_support = Math.round(difference_matrix.length / (double) month_increase);
        int number_of_months = (int) number_of_months_support;
        int[][] month_totals = new int[number_of_months][difference_matrix[0].length];

        int line = line_1st_date, increase = 0;
        int next_month = calculateNumberOfDaysInAMonth(line, dates);

        for (int month = 0; month < number_of_months; month++) {

            for (int tab = 0; tab < difference_matrix[0].length; tab++) {
                for (int day = increase; day < next_month; day++) {
                    month_totals[month][tab] += difference_matrix[day][tab];
                    line = day;
                }
            }
            if (month < number_of_months - 1) {
                line++;
                increase += calculateNumberOfDaysInAMonth(line, dates);
                line++;
                next_month += calculateNumberOfDaysInAMonth(line, dates); // Fica no dia anterior ao que devia mas adicionar 1 causa OutOfIndex
            }
        }
        return month_totals;
    }// Calcular os totais das diferenças para meses

    public static void chooseWhatToDoWithTheData(int line_1st_date, String[] dates, int[][] differences, int increase) throws FileNotFoundException {
        int option = 0, check_if_default = 0, data_option;
        String name_file_output;
        data_option = chooseWhatDataToPrint();


        do {
            while (5 < data_option || data_option < 1) {
                validateOption();
                data_option = sc.nextInt();
            }

            if (checkIfFirstTime(check_if_default)) {
                ShowStoreOrBothMenu();
                option = sc.nextInt();
            } else {
                check_if_default = 0;
            }

            switch (option) {
                case 1:
                    //IMPRIMIR TODOS OS RESULTADOS
                    System.out.println();
                    printDifferencesInfo(differences, dates, line_1st_date, increase, data_option);
                    break;
                case 2:
                    //GUARDAR EM CSV
                    System.out.println();
                    System.out.println("Em que ficheiro quer guardar os seus dados?");
                    name_file_output = sc.next();
                    printToFileDifferences(line_1st_date, dates, differences, increase, name_file_output, data_option);
                    break;
                case 3:
                    System.out.println();
                    System.out.println("Em que ficheiro quer guardar os seus dados?");
                    name_file_output = sc.next();
                    printToFileDifferences(line_1st_date, dates, differences, increase, name_file_output, data_option);
                    printDifferencesInfo(differences, dates, line_1st_date, increase, data_option);
                    //FAZER AMBOS
                    break;
                case 4:
                    option = INVALIDATION_VALUE;
                    break;
                default:
                    option = validateOption();
                    check_if_default = 1;
                    break;
            }
        } while (option != INVALIDATION_VALUE);
    }// escolher o que fazer com a informação

    public static void firstMenu() {
        MainMenu();
        System.out.printf("                        Bem vindo!%n%nEscolha a opçao que pretende:%n%n1 - Carregar um ficheiro.%n2 - Fazer uma compreensao dos dados.%n3 - Comparar os dados em diferentes intervalos de tempo.%n4 - Fazer uma previsao da evoluçao da pandemia.%n5 - Sair%n%nOpçao: ");

    } // Onde escolhe a funcionalidade

    public static void TypeOfAnalysisMenu() {
        MainMenu();
        System.out.printf("Que tipo de análise deseja?%n1 - Diária.%n2 - Semanal.%n3 - Mensal.%n4 - Voltar.%n%nOpçao: ");

    } //  Menu, onde o user escolhe o período

    public static void ShowStoreOrBothMenu() {
        MainMenu();
        System.out.printf("Deseja:%n1 - Visualizar os dados.%n2 - Guardar os dados num ficheiro csv.%n3 - Ambas as opçoes.%n4 - Voltar.%n%nOpçao: ");
    } // Menu, onde escolhe o que fazer com os dados

    public static void printerInfo(String[] dates, int[][] data, int line_of_1st_date, int data_option) {
        System.out.println();
        switch (data_option) {  // FALTA DATA
            case 1:
                System.out.println(" |Datas|               |Casos Ativos|");
                System.out.printf("%s          ", dates[line_of_1st_date]);
                System.out.printf("%11d%n", data[line_of_1st_date][1]);
                break;
            case 2:
                System.out.println(" |Datas|                |Hospitalizados|");
                System.out.printf("%s          ", dates[line_of_1st_date]);
                System.out.printf("%12d%n", data[line_of_1st_date][2]);
                break;
            case 3:
                System.out.println(" |Datas|             |Internados em UCI|");
                System.out.printf("%s          ", dates[line_of_1st_date]);
                System.out.printf("%12d%n", data[line_of_1st_date][3]);
                break;
            case 4:
                System.out.println(" |Datas|                   |Obitos| ");
                System.out.printf("%s          ", dates[line_of_1st_date]);
                System.out.printf("%10d%n", data[line_of_1st_date][4]);
                break;
            case 5:
                System.out.printf("  |Datas|      |Casos Ativos|  |Hospitalizados| |Internados em UCI|   |Obitos|  %n");
                System.out.printf("%s          ", dates[line_of_1st_date]);
                for (int tab = 1; tab < data[0].length; tab++) {
                    System.out.printf("%-18d", data[line_of_1st_date][tab]);
                }
        }
    }

    public static void printerToFileTotals(String[] dates, int[][] data, int line_of_1st_date, int data_option) throws FileNotFoundException {
        System.out.printf("%nEm que ficheiro quer guardar os seus dados?%n");
        sc.nextLine();
        String file_name = sc.nextLine();
        PrintWriter output = new PrintWriter(file_name);

        switch (data_option) {
            case 1:
                output.printf(" |Datas|            |Casos Ativos|%n");
                output.printf("%s %17d", dates[line_of_1st_date], data[line_of_1st_date][1]);
                break;
            case 2:
                output.printf(" |Datas|            |Hospitalizados|%n");
                output.printf("%s %17d", dates[line_of_1st_date], data[line_of_1st_date][2]);
                break;
            case 3:
                output.printf(" |Datas|             |Internados em UCI|%n");
                output.printf("%s %17d", dates[line_of_1st_date], data[line_of_1st_date][3]);
                break;
            case 4:
                output.printf(" |Datas|                |Obitos| %n");
                output.printf("%s %17d", dates[line_of_1st_date], data[line_of_1st_date][4]);
                break;
            case 5:
                output.printf(" |Datas|        |Casos Ativos|    |Hospitalizados| |Internados em UCI|     |Obitos|  %n");
                output.printf("%s          ", dates[line_of_1st_date]);
                for (int tab = 1; tab < data[0].length; tab++) {
                    output.printf("%-23d", data[line_of_1st_date][tab]);
                }
        }
        output.close();
    }// Escrever num ficheiro informação relativa aos totais num dia

    public static void totalOrNewMenu() {
        MainMenu();
        System.out.printf("Escolha a opçao que pretende:%n1 - Dados totais num dia.%n2 - Variaçao dos dados num intervalo de tempo.%n3 - Voltar.%n%nOpçao: ");
    }// Menu onde se escolhe se pretende dados totais de um dia ou variação dos dados (p.e. Casos ativos ou Novos Casos)

    public static String chooseNameFileMenu() {
        sc.nextLine();
        System.out.println();
        System.out.printf("Introduza o caminho para o ficheiro com os dados: %n");
        return sc.nextLine();
    } // Menu para escolher o ficheiro do qual são lidos os dados

    public static void compareMenu() {
        MainMenu();
        System.out.printf("Pretende:%n1 - Comparar o número de novos casos em dois períodos.%n2 - Comparar número total de casos em dois períodos.%n3 - Voltar%n%nOpçao: ");
    } // Menu para escolher o que comparar

    public static int validateOption() {
        System.out.println("Opçao invalida. Introduza novamente a opçao pretendida.");
        System.out.print("Opçao: ");
        return sc.nextInt();

    } // Validar a opçao do utilizador

    public static int chooseWhatDataToPrint() {
        MainMenu();
        System.out.printf("Pretende obter dados referentes a:%n1 - Número de infetados.%n2 - Número de hospitalizaçoes.%n3 - Número de Internados em UCI.%n4 - Óbitos.%n5 - Todos os valores.%n%nOpçao: ");
        return sc.nextInt();
    }

    public static int chooseWhatDataToPrintPredict() {
        MainMenu();
        System.out.printf("Pretende obter dados referentes a:%n1 - Número de nao infetados.%n2 - Número de infetados.%n3 - Número de hospitalizaçoes em UCI.%n4 - Internados.%n5 - Óbitos.%n6 - Todos os valores.%n%nOpçao: ");
        return sc.nextInt();
    }

    public static void printToFileDifferences(int line_1st_date, String[] dates, int[][] differences, int increase, String fileNameOutput, int data_option) throws FileNotFoundException {
        PrintWriter output = new PrintWriter(fileNameOutput);

        switch (data_option) {
            case 1:
                output.println(" |Datas|            |Novos Casos|");
                break;
            case 2:
                output.println(" |Datas|           |Novas Hospitalizacoes|");
                break;
            case 3:
                output.println(" |Datas|           |Novos Internamentos UCI|");
                break;
            case 4:
                output.println(" |Datas|                   |Obitos| ");
                break;
            case 5:
                if (increase == 1) {
                    String[] tabs = {"|Dates|", "|Novos Casos|", "|Novas Hopitalizacoes|", "|Novos Internamentos UCI|", "         |Obitos|"};
                    for (String word : tabs) {
                        output.printf("%-24s", word);
                    }
                    output.println();
                } else if (increase == 7) {
                    String[] tabs = {"|Dates|", "  |Novos Casos|", "|Novas Hopitalizacoes|", "|Novos Internamentos UCI|", "       |Obitos|"};
                    for (String word : tabs) {
                        output.printf("%-26s", word);
                    }
                    output.println();
                } else {
                    String[] tabs = {"|Dates|", "|Novos Casos|", "|Novas Hopitalizacoes|", "|Novos Internamentos UCI|", "         |Obitos|"};
                    for (String word : tabs) {
                        output.printf("%-24s", word);
                    }
                    output.println();
                }
                break;
        }
        int accumulated_increase, month_number_of_days = 0;
        if (data_option == 5) {


            for (int line = 0; line < differences.length; line++) {
                if (increase == 7) {
                    output.print("Semana de ");
                    accumulated_increase = increase * line;
                    output.printf("%-20s", (dates[line_1st_date + accumulated_increase]));
                } else if (increase > 7) {
                    String[] month_names = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
                    int month_determiner = line_1st_date + DAY_INCREASE + month_number_of_days; //variavel que ajuda a determinar o próximo mês
                    output.printf("%-24s", month_names[getMonthNumber(dates, month_determiner)]);
                    month_number_of_days += calculateNumberOfDaysInAMonth(month_determiner, dates);
                } else if (increase == 1) {
                    accumulated_increase = increase * line;
                    output.printf("%-24s", (dates[line_1st_date + accumulated_increase]));
                }

                for (int column = 1; column < differences[0].length; column++) {
                    output.printf("   %-24d", differences[line][column]);
                }
                output.println();
            }

        } else {
            if (increase == 7) {
                for (int line = 0; line < differences.length; line++) {
                    output.print("Semana de ");
                    accumulated_increase = increase * line;
                    output.printf("%s", (dates[line_1st_date + accumulated_increase]));
                    output.printf("%8d%n", differences[line][data_option]);
                }
            } else if (increase == 1) {
                for (int line = 0; line < differences.length; line++) {
                    output.printf("%s%18d%n", dates[line + 1], differences[line][data_option]);
                }
            } else {
                for (int line = 0; line < differences.length; line++) {
                    String[] month_names = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
                    int month_determiner = line_1st_date + DAY_INCREASE + month_number_of_days; //variavel que ajuda a determinar o próximo mês
                    output.printf("%-24s", month_names[getMonthNumber(dates, month_determiner)]);
                    month_number_of_days += calculateNumberOfDaysInAMonth(month_determiner, dates);
                    output.printf("%8d%n", differences[line][data_option]);
                }
            }
        }


        output.close();
    }// Escrever num ficheiro a informação relativa às diferenças

    public static void printDifferencesInfo(int[][] differences, String[] dates, int line_1st_date, int increase, int data_option) {
        System.out.println();
        switch (data_option) {
            case 1:
                System.out.println(" |Datas|            |Novos Casos|");
                break;
            case 2:
                System.out.println(" |Datas|       |Novas Hospitalizacoes|");
                break;
            case 3:
                System.out.println(" |Datas|        |Novos Internamentos UCI|");
                break;
            case 4:
                System.out.println(" |Datas|               |Obitos| ");
                break;
            case 5:
                if (increase == 1) {
                    String[] tabs = {"|Dates|", "|Novos Casos|", "|Novas Hopitalizacoes|", "|Novos Internamentos UCI|", "         |Obitos|"};
                    for (String word : tabs) {
                        System.out.printf("%-24s", word);
                    }
                    System.out.println();
                } else if (increase == 7) {
                    String[] tabs = {"|Dates|", "  |Novos Casos|", "|Novas Hopitalizacoes|", "|Novos Internamentos UCI|", "      |Obitos|"};
                    for (String word : tabs) {
                        System.out.printf("%-26s", word);
                    }
                    System.out.println();
                } else {
                    String[] tabs = {"|Dates|", "|Novos Casos|", "|Novas Hopitalizacoes|", "|Novos Internamentos UCI|", "         |Obitos|"};
                    for (String word : tabs) {
                        System.out.printf("%-24s", word);
                    }
                    System.out.println();
                }
                break;
        }
        int accumulated_increase, month_number_of_days = 0;
        if (data_option == 5) {


            for (int line = 0; line < differences.length; line++) {
                if (increase == 7) {
                    System.out.print("Semana de ");
                    accumulated_increase = increase * line;
                    System.out.printf("%-20s", (dates[line_1st_date + accumulated_increase]));
                } else if (increase > 7) {
                    String[] month_names = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
                    int month_determiner = line_1st_date + DAY_INCREASE + month_number_of_days; //variavel que ajuda a determinar o próximo mês
                    System.out.printf("%-24s", month_names[getMonthNumber(dates, month_determiner)]);
                    month_number_of_days += calculateNumberOfDaysInAMonth(month_determiner, dates);
                } else if (increase == 1) {
                    accumulated_increase = increase * line;
                    System.out.printf("%-24s", (dates[line_1st_date + accumulated_increase]));
                }

                for (int column = 1; column < differences[0].length; column++) {
                    System.out.printf("   %-24d", differences[line][column]);
                }
                System.out.println();
            }

        } else {
            if (increase == 7) {
                for (int line = 0; line < differences.length; line++) {
                    System.out.print("Semana de ");
                    accumulated_increase = increase * line;
                    System.out.printf("%s", (dates[line_1st_date + accumulated_increase]));
                    System.out.printf("%8d%n", differences[line][data_option]);
                }
            } else if (increase == 1) {
                for (int line = 0; line < differences.length; line++) {
                    System.out.printf("%s%18d%n", dates[line + 1], differences[line][data_option]);
                }
            } else {
                for (int line = 0; line < differences.length; line++) {
                    String[] month_names = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
                    int month_determiner = line_1st_date + DAY_INCREASE + month_number_of_days; //variavel que ajuda a determinar o próximo mês
                    System.out.printf("%-24s", month_names[getMonthNumber(dates, month_determiner)]);
                    month_number_of_days += calculateNumberOfDaysInAMonth(month_determiner, dates);
                    System.out.printf("%8d%n", differences[line][data_option]);
                }
            }
        }
    }// Escrever na consola as informações relativas às diferenças

    public static int numberOfDaysBetweenTwoDates(int line_of_1st_date, int line_of_2nd_date) {
        return Math.abs(line_of_2nd_date - line_of_1st_date);
    }

    public static int numberOfDaysToCompare(int time_period_1, int time_period_2) {
        return Math.min(time_period_1, time_period_2);
    } //Método que calcula o número de dias para as diferenças diárias.

    public static int[] orderDates(int line1, int line2, int[] lines_in_order) {
        lines_in_order[0] = Math.min(line1, line2);
        lines_in_order[1] = Math.max(line1, line2);
        return lines_in_order;
    } //Método que retifica a ordem das datas.

    public static int[][] calculateDifferenceArrays(int[][] periodTime1, int[][] periodTime2, int number_of_days_compare) {
        int[][] results = new int[number_of_days_compare + DAY_INCREASE][NUMBER_OF_TABS];

        for (int line = 0; line < results.length; line++) {
            for (int column = 0; column < results[0].length; column++) {
                results[line][column] = periodTime1[line][column] - periodTime2[line][column];
            }
        }
        return results;
    }

    public static int[][] defineTotalArray(int line1, int line2, int[][] data, int number_of_days_to_compare) {
        int[][] period_of_total_values = new int[number_of_days_to_compare + 1][NUMBER_OF_TABS];
        int position = 0;

        while (line1 <= line2) {
            for (int column = 1; column < period_of_total_values[0].length; column++) {
                period_of_total_values[position][column] = data[line1][column];
            }
            line1++;
            position++;
        }

        return period_of_total_values;
    }

    public static void printDifferencesComparisons(int[][] comparison_matrix, String[] dates, int line_1st_date, int line_3rd_date, int number_of_days, int option_new_total, int data_option) {
        System.out.printf("%nDiferenças de Casos entre o período de %s - %s e o período de %s - %s%n", dates[line_1st_date], dates[line_1st_date + number_of_days], dates[line_3rd_date], dates[line_3rd_date + number_of_days]);
        String[] tabs_accumalated = {"|Datas|", "|Novos Casos|", "|Novas Hospitalizacoes|", "|Novos Internamentos UCI|", "|Obitos|"};
        String[] tabs_totals = {"|Datas|", "|Casos Ativos|", "|Hospitalizados|", "|Internados em UCI|", "|Obitos|"};

        if (option_new_total == 0) {
            switchPrinterDifferencesComparisons(data_option, tabs_accumalated, comparison_matrix, dates, line_1st_date, line_3rd_date);
        } else {
            switchPrinterDifferencesComparisons(data_option, tabs_totals, comparison_matrix, dates, line_1st_date, line_3rd_date);
        }

    }

    public static void switchPrinterDifferencesComparisons(int data_option, String[] tabs, int[][] comparison_matrix, String[] dates, int line_1st_date, int line_3rd_date) {
        System.out.println();
        switch (data_option) {
            case 1:
                System.out.printf("%18s    %22s%n", tabs[0], tabs[1]);

                for (int column = 1; column < 2; column++) {
                    for (int line = 0; line < comparison_matrix.length; line++) {
                        System.out.printf("Dias %s - %s ", dates[line_1st_date], dates[line_3rd_date]);
                        System.out.printf("      %-15d %n", comparison_matrix[line][column]);
                        line_1st_date++;
                        line_3rd_date++;
                    }
                }
                break;

            case 2:
                System.out.printf("%18s    %22s%n", tabs[0], tabs[2]);

                for (int column = 2; column < 3; column++) {
                    for (int line = 0; line < comparison_matrix.length; line++) {
                        System.out.printf("Dias %s - %s ", dates[line_1st_date], dates[line_3rd_date]);
                        System.out.printf("      %-15d %n", comparison_matrix[line][column]);
                        line_1st_date++;
                        line_3rd_date++;
                    }
                }
                break;

            case 3:
                System.out.printf("%18s    %22s%n", tabs[0], tabs[3]);

                for (int column = 3; column < 4; column++) {
                    for (int line = 0; line < comparison_matrix.length; line++) {
                        System.out.printf("Dias %s - %s ", dates[line_1st_date], dates[line_3rd_date]);
                        System.out.printf("      %-15d %n", comparison_matrix[line][column]);
                        line_1st_date++;
                        line_3rd_date++;
                    }
                }
                break;

            case 4:
                System.out.printf("%18s    %22s%n", tabs[0], tabs[4]);

                for (int column = 4; column < 5; column++) {
                    for (int line = 0; line < comparison_matrix.length; line++) {
                        System.out.printf("Dias %s - %s ", dates[line_1st_date], dates[line_3rd_date]);
                        System.out.printf("      %-15d %n", comparison_matrix[line][column]);
                        line_1st_date++;
                        line_3rd_date++;
                    }
                }
                break;

            case 5:
                System.out.println("");
                System.out.printf("%18s %25s %24s %24s %10s%n", tabs[0], tabs[1], tabs[2], tabs[3], tabs[4]);
                for (int[] line : comparison_matrix) {
                    System.out.printf("Dias %s - %s ", dates[line_1st_date], dates[line_3rd_date]);
                    for (int column = 1; column < comparison_matrix[0].length; column++) {
                        System.out.printf("      %-15d ", line[column]);
                    }
                    System.out.println();
                    line_1st_date++;
                    line_3rd_date++;
                }
                System.out.println();
                break;
        }
        System.out.println();
    }

    public static void storeInfoMenu() { // aka menuzito
        MainMenu();
        System.out.printf("%n%nEscolha a opçao que pretende:%n1 - Guardar os dados num ficheiro csv.%n2 - Voltar%nOpçao: ");
    }

    public static void compareAndAdjustPeriods(int[] lines_in_order, int[] line_of_user_date) {
        orderDates(line_of_user_date[0], line_of_user_date[1], lines_in_order);
        line_of_user_date[0] = lines_in_order[0];
        line_of_user_date[1] = lines_in_order[1];
        orderDates(line_of_user_date[2], line_of_user_date[3], lines_in_order);
        line_of_user_date[2] = lines_in_order[0];
        line_of_user_date[3] = lines_in_order[1];
    }

    public static double[] averageForComparison(int[][] period_user_differences, int number_of_days_to_compare) {
        double[] average = new double[NUMBER_OF_TABS - DAY_INCREASE];
        double sum = 0;

        for (int column = 1; column < period_user_differences[0].length; column++) {
            for (int line = 0; line < period_user_differences.length; line++) {
                sum += period_user_differences[line][column];
            }
            average[column - 1] = sum / (number_of_days_to_compare + 1);
            sum = 0;
        }
        return average;
    }

    public static double[] standardDeviationForComparison(int[][] period_user_differences, double[] average, int number_of_days_to_compare) {
        double sum = 0;
        double[] desvio_padrao_array = new double[NUMBER_OF_TABS - DAY_INCREASE];

        for (int column = 1; column < period_user_differences[0].length; column++) {
            for (int line = 0; line < period_user_differences.length; line++) {
                sum += Math.pow((period_user_differences[line][column] - average[column - 1]), 2);
            }
            desvio_padrao_array[column - 1] = Math.sqrt(sum / number_of_days_to_compare);
            sum = 0;
        }
        return desvio_padrao_array;
    }

    public static void printerAverageAndDesvio(double[] period, String to_print, int data_option, String type_of_file) {
        String[] tabs_accumalated = {"|Novos Casos|", "|Novas Hospitalizacoes|", "|Novos Internamentos UCI|", "|Obitos|"};
        String[] tabs_totals = {"|Casos Ativos|", "|Hospitalizados|", "|Internados em UCI|", "|Obitos|"};

        if (to_print.equals("|Medias|")) {
            if (type_of_file.equals("Accumulated")) {
                switchPrinterAverageAndDesvio(data_option, to_print, tabs_accumalated, period);
            } else {
                switchPrinterAverageAndDesvio(data_option, to_print, tabs_totals, period);
            }
        } else {
            if (type_of_file.equals("Accumulated")) {
                switchPrinterAverageAndDesvio(data_option, to_print, tabs_accumalated, period);
            } else {
                switchPrinterAverageAndDesvio(data_option, to_print, tabs_totals, period);
            }
        }
    }

    public static void switchPrinterAverageAndDesvio(int data_option, String to_print, String[] tabs, double[] period) {

        switch (data_option) {
            case 1:
                System.out.printf("%s%n", to_print);
                System.out.printf("%s - ", tabs[0]);
                System.out.printf("%.2f%n%n", period[0]);
                break;
            case 2:
                System.out.printf("%s%n", to_print);
                System.out.printf("%s - ", tabs[1]);
                System.out.printf("%.2f%n%n", period[1]);
                break;
            case 3:
                System.out.printf("%s%n", to_print);
                System.out.printf("%s - ", tabs[2]);
                System.out.printf("%.2f%n%n", period[2]);
                break;
            case 4:
                System.out.printf("%s%n", to_print);
                System.out.printf("%s - ", tabs[3]);
                System.out.printf("%.2f%n%n", period[3]);
                break;
            case 5:
                System.out.printf("%45s%n", to_print);
                System.out.printf("  %-24s%-24s%-24s          %-24s%n", tabs[0], tabs[1], tabs[2], tabs[3]);
                for (int value = 0; value < period.length; value++) {
                    System.out.printf("   %-24.4f", period[value]);
                }
                System.out.println();
                System.out.println();
        }
    }

    public static void switchComparisonsDifferencesWriteToFile(PrintWriter output, int data_option, String[] tabs, int[][] comparison_array, String[] dates, int line_1st_date, int line_3rd_date) {
        switch (data_option) {
            case 1:
                output.format("%18s    %22s%n", tabs[0], tabs[1]);

                for (int column = 1; column < 2; column++) {
                    for (int line = 0; line < comparison_array.length; line++) {
                        output.format("Dias %s - %s ", dates[line_1st_date], dates[line_3rd_date]);
                        output.format("      %-15d %n", comparison_array[line][column]);
                        line_1st_date++;
                        line_3rd_date++;
                    }
                }
                break;

            case 2:
                output.format("%18s    %22s%n", tabs[0], tabs[2]);

                for (int column = 2; column < 3; column++) {
                    for (int line = 0; line < comparison_array.length; line++) {
                        output.format("Dias %s - %s ", dates[line_1st_date], dates[line_3rd_date]);
                        output.format("      %-15d %n", comparison_array[line][column]);
                        line_1st_date++;
                        line_3rd_date++;
                    }
                }
                break;

            case 3:
                output.format("%18s    %22s%n", tabs[0], tabs[3]);

                for (int column = 3; column < 4; column++) {
                    for (int line = 0; line < comparison_array.length; line++) {
                        output.format("Dias %s - %s ", dates[line_1st_date], dates[line_3rd_date]);
                        output.format("      %-15d %n", comparison_array[line][column]);
                        line_1st_date++;
                        line_3rd_date++;
                    }
                }
                break;

            case 4:
                output.format("%18s    %22s%n", tabs[0], tabs[4]);

                for (int column = 4; column < 5; column++) {
                    for (int line = 0; line < comparison_array.length; line++) {
                        output.format("Dias %s - %s ", dates[line_1st_date], dates[line_3rd_date]);
                        output.format("      %-15d %n", comparison_array[line][column]);
                        line_1st_date++;
                        line_3rd_date++;
                    }
                }
                break;

            case 5:
                output.format("%n%18s %25s %24s %24s %10s%n", tabs[0], tabs[1], tabs[2], tabs[3], tabs[4]);
                for (int[] line : comparison_array) {
                    output.format("Dias %s - %s ", dates[line_1st_date], dates[line_3rd_date]);
                    for (int column = 1; column < comparison_array[0].length; column++) {
                        System.out.printf("      %-15d ", line[column]);
                    }
                    output.format("%n%n");
                    line_1st_date++;
                    line_3rd_date++;
                }
                System.out.println();
                break;
        }
    }

    public static void comparisonsDifferencesWriteToFile(PrintWriter output, String type_of_file, int data_option, int[][] comparisons_array, String[] dates, int line1, int line3) {
        String[] tabs_accumalated = {"|Novos Casos|", "|Novas Hospitalizacoes|", "|Novos Internamentos UCI|", "|Obitos|"};
        String[] tabs_totals = {"|Casos Ativos|", "|Hospitalizados|", "|Internados em UCI|", "|Obitos|"};

        if (type_of_file.equals("Accumulated")) {
            switchComparisonsDifferencesWriteToFile(output, data_option, tabs_accumalated, comparisons_array, dates, line1, line3);
        } else {
            switchComparisonsDifferencesWriteToFile(output, data_option, tabs_totals, comparisons_array, dates, line1, line3);
        }
    }

    public static void switchWriteToFileComparisons(PrintWriter output, int data_option, String to_print, double[] period, String[] tabs) {
        switch (data_option) {
            case 1:
                output.format("%s%n", to_print);
                output.format("%s - ", tabs[0]);
                output.format("%.2f%n%n", period[0]);
                break;
            case 2:
                output.format("%s%n", to_print);
                output.format("%s - ", tabs[1]);
                output.format("%.2f%n%n", period[1]);
                break;
            case 3:
                output.format("%s%n", to_print);
                output.format("%s - ", tabs[2]);
                output.format("%.2f%n%n", period[2]);
                break;
            case 4:
                output.format("%s%n", to_print);
                output.format("%s - ", tabs[3]);
                output.format("%.2f%n%n", period[3]);
                break;
            case 5:
                output.format("%45s%n", to_print);
                output.format("  %-24s%-24s%-24s          %-24s%n", tabs[0], tabs[1], tabs[2], tabs[3]);
                for (int value = 0; value < period.length; value++) {
                    output.format("   %-24.4f", period[value]);
                }
                output.format("%n%n");
        }
    }

    public static void averageAndStandardDesviationWriteToFile(PrintWriter output, String to_print, String type_of_file, int data_option, double[] period) {
        String[] tabs_accumalated = {"|Novos Casos|", "|Novas Hospitalizacoes|", "|Novos Internamentos UCI|", "|Obitos|"};
        String[] tabs_totals = {"|Casos Ativos|", "|Hospitalizados|", "|Internados em UCI|", "|Obitos|"};

        if (to_print.equals("|Medias|")) {
            if (type_of_file.equals("Accumulated")) {
                switchWriteToFileComparisons(output, data_option, to_print, period, tabs_accumalated);
            } else {
                switchWriteToFileComparisons(output, data_option, to_print, period, tabs_totals);
            }
        } else {
            if (type_of_file.equals("Accumulated")) {
                switchWriteToFileComparisons(output, data_option, to_print, period, tabs_accumalated);
            } else {
                switchWriteToFileComparisons(output, data_option, to_print, period, tabs_totals);
            }
        }
    }
}

