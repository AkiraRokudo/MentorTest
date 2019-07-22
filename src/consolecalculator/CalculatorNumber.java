package consolecalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Akira
 */
public class CalculatorNumber {

    private Boolean isRomanNumber;

    private int value;

    public CalculatorNumber(String str) {
        str = str.trim();
        isRomanNumber = isNonDigit(str);
        if (isRomanNumber) {
            value = RomanNumberTransfer.toInteger(str);
        } else {
            value = Integer.valueOf(str);
        }

    }
    static Character specialSymb = '\u0305';

    public Boolean getIsRomanNumber() {
        return isRomanNumber;
    }

    public int getValue() {
        return value;
    }

    private boolean isNonDigit(String str) {
        return str.chars().anyMatch(c -> !Character.isDigit(c));

    }

    /**
     * Вспомогательный класс для преобразования из одного состояния в другое
     */
    public static class RomanNumberTransfer {

        public static HashMap<Character, Integer> interpretationDigitsMap;
        private static List<Long> allowedPrevLessCurList = Stream.of(4L, //IV Остальные нам не подойдут.
                9L, //IX
                40L, //XL 
                90L, //XC
                400L,//CD
                900L //CM
        ).collect(Collectors.toList());
        
        private static List<String> prohibitiedList = Stream.of("IIII", //Такие использовать нельзя
                "VV", 
                "XXXX", 
                "LL", 
                "CCCC", 
                "DD", 
                "MMMM"
        ).collect(Collectors.toList());
        
        
        
        public static HashMap<Character, Integer> interpretationHalfDigitsMap; //для проверки
        //TODO; Реализовать возможность более легкого ввода 4000+

        static {
            
            interpretationDigitsMap = new HashMap();
            interpretationHalfDigitsMap = new HashMap();
            interpretationDigitsMap.put('I', 1);
            interpretationHalfDigitsMap.put('V', 5); //-
            interpretationDigitsMap.put('X', 10);
            interpretationHalfDigitsMap.put('L', 50); //-
            interpretationDigitsMap.put('C', 100);
            interpretationHalfDigitsMap.put('D', 500); //-
            interpretationDigitsMap.put('M', 1000);
            interpretationDigitsMap.putAll(interpretationHalfDigitsMap);
        }

        /**
         * Преобразовывает Римское написание числа в обычное
         *
         * @param romanNumber
         * @return
         */
        public static int toInteger(final String romanNumber) {
            //TODO: проанализировать на вопрос кэша до 100 к примеру.
            //проверка что это вообще число нам подходит
            final String testRomanNumber = romanNumber.toUpperCase();
            System.out.print(testRomanNumber+" ");
            // romanNumber.chars().forEach(c-> System.out.println(c));
            if (testRomanNumber.chars().anyMatch(c -> (!interpretationDigitsMap.containsKey((char) c) && (char) c != specialSymb))) {
                throw new CalculatorException("Ошибка при интерпретации строки '" + romanNumber + "' - присутствуют неподходящие символы");
            }
            if (prohibitiedList.stream().anyMatch(ps-> testRomanNumber.contains(ps))) {
                throw new CalculatorException("Ошибка при интерпретации строки '" + romanNumber + "' - недопустимо использование более 3 для единичных и 2 для половинных символов одинаковых символов подряд");
            }
            
            //проверка на отсутствие IIII VV и т.д.
            //IVII нельзя
            //IXX тоже нельзя.
            boolean prevLess = false; // TODO: провести анализ на предмет нужности
            long prevprevSymb = 0;
            long value = 0; //итоговое число
            int multiplierCount = 0; //множитель числа на 10^3
            int equalsSymbolCount = 1; //количество одинаковых символов подряд
            if (romanNumber.charAt(0) == specialSymb) {
                throw new CalculatorException("Ошибка при интерпретации числа '" + romanNumber + "' - Первым символом не может быть символ надчеркивания");
            }
            long prevSymb = 0;
            for (Character charSymb : romanNumber.toCharArray()) {
                //если это черта - переходим к следующему.

                if (charSymb.equals(specialSymb)) {
                    multiplierCount++;
                    prevSymb *= 1000;
                    if (multiplierCount > 2 && (charSymb != 'I')) {
                        throw new CalculatorException("Ошибка при интерпретации числа '" + romanNumber + "' - не может быть более 2 символов подчеркивания подряд,если следующий символ не 'I'");
                    }
                } else {
                    value += prevSymb;
                    multiplierCount = 0; //обнулим множитель

                    long curSymb = interpretationDigitsMap.get(charSymb);

                    if (value == 0) { //блок только для 1 захода
                        prevSymb = curSymb;
                        continue;
                    }

                    if (prevSymb == curSymb) { //значит это II XX CC или MM
                        prevSymb = curSymb;
                        equalsSymbolCount++;
                        if (equalsSymbolCount > 3) {
                            throw new CalculatorException("ошибка много подряд одинаковых");
                        }
                        if (prevLess) {
                            throw new CalculatorException("Нельзя числа IXX");
                        }
                        prevLess = false;
                    } else { //значит либо больше либо меньше
                        if (prevSymb > curSymb) { // если не IXI то ок.
                            if (prevLess) { //ICX или ICI на 2 сверке
                                if (prevprevSymb <= curSymb) 
                                    throw new CalculatorException("Нельзя числа ICI или ICX");
                                    prevLess = false; //очень даже reachable statement, хоть и не выглядит таковым)))
                            }
                        } else { //prevSymb < curSymb
                            if (prevLess) {
                                throw new CalculatorException("Нельзя числа ICM");
                            }
                            if (!isPosibbleValue(curSymb, prevSymb, multiplierCount)) {// блочим варианты 1000 50000
                                throw new CalculatorException("Недопустимый вариант сочетания меньшей цифры слева и большей справа");
                            }
                            if (equalsSymbolCount > 1) { //iix iiv отбрасываем
                                throw new CalculatorException("Недопустимо наличие 2 одинаковых символов с последующим большим символом - 'IIX' к примеру");
                            }
                            if (prevprevSymb != 0) { //если не второе сравнение
                                if (prevprevSymb == curSymb) { //viv или xix
                                    if (interpretationHalfDigitsMap.containsKey(charSymb)) {
                                        //значит это v,l,или d
                                        throw new CalculatorException("Нельзя числа VIV ");
                                    }
                                } //значит prevprevSymb <> curSymb.
                                //рабочими вариантами может быть только prevprevSymb<>prevSymb(долгая доказывать)
                                //обработаем этот факт.
                                else {
                                    if (prevprevSymb < curSymb) { //vix   //xci
                                        throw new CalculatorException("Нельзя числа VIX ");
                                    }
                                }
                            }
                            //если они одинаковы(xix) то должны быть разрешены (нельзя viv)

                            value -= 2 * (prevSymb);//ICX = -1 
                            prevLess = true;
                        }
                        prevprevSymb = prevSymb; //ixi   
                        prevSymb = curSymb;
                        equalsSymbolCount = 1;

                    }
                }

            }
            
            //вышли из цикла
            if (multiplierCount > 2 && (prevSymb != 'I')) { //проверка последнего числа
                throw new CalculatorException("Ошибка при интерпретации числа '" + romanNumber + "' - не может быть более 2 символов подчеркивания подряд,если следующий символ не 'I'");
            }
            value += prevSymb;

            int ivalue = (int) value;
            return ivalue;
        }

        /**
         * Преобразовывает число в Римское написание числа
         *
         * @param number
         * @return
         */
        public static String toRomanNumber(int number) {
            if (number < 0) {
                throw new CalculatorException("Ошибка при выводе числа '" + number + "' - в римскую систему исчисления можно интерпретировать только положительное число");
            }
            //i v 1-9
            //x-l 10-99
            //c-d 100-999
            //m-v_ 1000-9999
            String strNumb = String.valueOf(number);
            int len = String.valueOf(number).length();
            int iteration = len % 3 == 0 ? len / 3 : (len / 3) + 1;
            int romanaPair = len % 3; //TODO:Переделать на инт.
            StringBuilder sb = new StringBuilder();
            for (; iteration > 0;) {
                char symb = strNumb.charAt(0);
                strNumb = strNumb.substring(1);
                switch (Character.toString(symb)) {
                    //TODO:переделать на кейз под 5 позиций. и с 3 брейками
                    case "3": { //III
                        sb.append(getPair(romanaPair, false));
                        sb.append(getUpperScore((iteration) - 1 - (1 == romanaPair ? 1 : 0)));
                    }
                    case "2": {//II
                        sb.append(getPair(romanaPair, false));
                        sb.append(getUpperScore((iteration) - 1 - (1 == romanaPair ? 1 : 0)));
                    }
                    case "1": {//I
                        sb.append(getPair(romanaPair, false));
                        sb.append(getUpperScore((iteration) - 1 - (1 == romanaPair ? 1 : 0)));
                        break;
                    }
                    case "4": {//IV
                        sb.append(getPair(romanaPair, false));
                        sb.append(getUpperScore((iteration) - 1));
                    }
                    case "5": {//V
                        sb.append(getPair(romanaPair, true));
                        sb.append(getUpperScore((iteration) - 1));
                        break;
                    }
                    case "6": {//VI
                        sb.append(getPair(romanaPair, true));
                        sb.append(getUpperScore((iteration) - 1));
                        sb.append(getPair(romanaPair, false));
                        sb.append(getUpperScore((iteration) - 1));
                        break;
                    }
                    case "7": {//VII
                        sb.append(getPair(romanaPair, true));
                        sb.append(getUpperScore((iteration) - 1));
                        sb.append(getPair(romanaPair, false));
                        sb.append(getUpperScore((iteration) - 1));
                        sb.append(getPair(romanaPair, false));
                        sb.append(getUpperScore((iteration) - 1));
                        break;
                    }
                    case "8": {//VIII 
                        sb.append(getPair(romanaPair, true));
                        sb.append(getUpperScore((iteration) - 1));
                        sb.append(getPair(romanaPair, false));
                        sb.append(getUpperScore((iteration) - 1));
                        sb.append(getPair(romanaPair, false));
                        sb.append(getUpperScore((iteration) - 1));
                        sb.append(getPair(romanaPair, false));
                        sb.append(getUpperScore((iteration) - 1));
                        break;
                    }
                    case "9": {//IX
                        sb.append(getPair(romanaPair, false));
                        sb.append(getUpperScore((iteration) - 1 - (1 == romanaPair ? 1 : 0)));
                        sb.append(getPair((romanaPair + 1), false));
                        sb.append(getUpperScore((iteration) - 1));
                        break;
                    }
                }
                //2-1-0-2-1-0...
                switch (romanaPair) {
                    case 0: {
                        romanaPair = 2;
                        break;
                    }
                    case 1: {
                        romanaPair = 0;
                        iteration--;
                        break;
                    }
                    case 2: {
                        romanaPair = 1;
                        break;
                    }
                }
            }
            //TODO:исправить последниее единичные разряды если есть на правильные.
            String s = sb.toString();
            if (s.length() > 3) {
                String sub = s.substring(s.length() - 3);
                //   sub;
                s = s.substring(0, s.length() - 3).concat(sub.replaceAll("M", "I"));
            } else {
                s = s.replaceAll("M", "I");
            }
            return s;
        }

        private static Boolean isPosibbleValue(Long curSymb, Long prevSymb, Integer multiplierCount) {
            return allowedPrevLessCurList.contains((curSymb - prevSymb) / (multiplierCount == 0 ? 1 : multiplierCount * 1000));
        }
    }

    /**
     * Проверка на вхождение в необходимый интервал
     */
    public void isPossibleValue() {
        int intTempValue = (int) value;
        long longTempValue = intTempValue;
        if (value != longTempValue) {
            throw new CalculatorException("Превышено допустимое значение: от " + Integer.MIN_VALUE + " до " + Integer.MAX_VALUE);
        }
    }

    @Override
    public String toString() {
        return isRomanNumber
                ? RomanNumberTransfer.toRomanNumber(value)
                : value + "";
    }

    //перенести в утилсы
    private static String getPair(int m, boolean isHalf) {

        switch (m) {
            case 2:
                return isHalf ? "L" : "X";
            case 1:
                return isHalf ? "V" : "M";

            case 3: //хак - 99  = xcix 
            case 0:
                return isHalf ? "D" : "C";
            default:
                throw new CalculatorException("неизвестная пара символов");
        }
    }

    private static String getUpperScore(Integer scoreCount) {
        StringBuilder scoreBuilder = new StringBuilder();
        for (int i = 0; i < scoreCount; i++) {
            scoreBuilder.append(specialSymb);
        }
        return scoreBuilder.toString();
    }

}
