package consolecalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Akira
 */
public class CalculatorNumber {

    private Boolean isRomanNumber;

    private int value;

    public CalculatorNumber(String str) {
        str = str.trim();
        isRomanNumber = isDigit(str);
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

    private boolean isDigit(String str) {
        return str.chars().anyMatch(c -> !Character.isDigit(c));

    }

    /**
     * Вспомогательный класс для преобразования из одного состояния в другое
     */
    public static class RomanNumberTransfer {

        static HashMap<Character, Integer> interpretationDigitsMap;

        //TODO; Реализовать возможность более легкого ввода 4000+
        static {
            interpretationDigitsMap = new HashMap();
            interpretationDigitsMap.put('I', 1);
            interpretationDigitsMap.put('V', 5); //-
            interpretationDigitsMap.put('X', 10);
            interpretationDigitsMap.put('L', 50); //-
            interpretationDigitsMap.put('C', 100);
            interpretationDigitsMap.put('D', 500); //-
            interpretationDigitsMap.put('M', 1000);
        }

        /**
         * Преобразовывает Римское написание числа в обычное
         *
         * @param romanNumber
         * @return
         */
        public static int toInteger(String romanNumber) {
            //проверка что это вообще число нам подходит
            romanNumber = romanNumber.toUpperCase();
            // romanNumber.chars().forEach(c-> System.out.println(c));
            if (romanNumber.chars().anyMatch(c -> (!interpretationDigitsMap.containsKey((char) c) && (char) c != specialSymb))) {
                throw new CalculatorException("Ошибка при интерпретации числа '" + romanNumber + "' - присутствуют неподходящие символы");
            }

            //1. Каждые 2 числа связаны.
            //2. если лев > прав значит все ок плюсуем
            //3 если лев = прав. Значит ок если это единичные.значит все ок плюсуем
            //4 если лев = прав и при этом не больше 3!
            //5 если лев < прав значит надо от прав отнять лев.
            //6 из 5 пункта есть важный фактор - нельзя после него
            //добавить к лев прав.
            //значит нужна проверка, что если предыдущее сравнение было лев < прав
            //то не должно быть лев > прав сейчас
            //IVII нельзя
            //IXX тоже нельзя.
            long value = 0; //итоговое число
            int multiplierCount = 0; //множитель числа на 10^3
            int equalsSymbolCount = 1; //количество одинаковых символов подряд
            if (romanNumber.charAt(0) == specialSymb) {
                throw new CalculatorException("Ошибка при интерпретации числа '" + romanNumber + "' - Первым символом не может быть символ надчеркивания");
            }
            long prevSymb = 0; //однозначно интерпретируется, иначе бы мы упали раньше.
            for (Character charSymb : romanNumber.toCharArray()) {
                if (multiplierCount > 2 && (charSymb != 'I')) {
                    throw new CalculatorException("Ошибка при интерпретации числа '" + romanNumber + "' - не может быть более 2 символов подчеркивания подряд,если следующий символ не 'I'");
                }
                if (charSymb.equals(specialSymb)) {
                    multiplierCount++;
                    prevSymb *= 1000;
                    if (multiplierCount > 3) {//1.000.000.000
                        throw new CalculatorException("Ошибка при интерпретации числа '" + romanNumber + "' - не может быть более 3 символов подчеркивания подряд");
                    }
                } else {
                    value += prevSymb;

                    long curSymb = interpretationDigitsMap.get(charSymb);
                    multiplierCount = 0; //обнулим множитель

                    //значит это не спецсимвол. Следовательно - он должен быть либо больше, либо меньше предыдущего.
                    if (prevSymb == curSymb) { //Это возможно только в 1 случае - если мы имеем дело с 'I'
                        if ((charSymb == 'D' || charSymb == 'L' || charSymb == 'V') && value != 0) { //кратные 5 нельзя использовать дважды.
                            throw new CalculatorException("Ошибка при интерпретации числа '" + romanNumber + "'. Символ '" + charSymb + "' не может быть указан дважды подряд");
                        }
                        //если символ указан дважды подряд - значит он только в плюс идти может.
                        equalsSymbolCount++;
                        if (equalsSymbolCount > 3) {
                            throw new CalculatorException("Ошибка при интерпретации числа '" + romanNumber + "'. Символ '" + charSymb + "' не может быть указан более 3 раз подряд");
                        }
                        prevSymb = curSymb;
                        continue; //можно конечно и оформить через иф елс, или доп проверки ниже, но так быстрее
                    }
                    if (prevSymb > curSymb) {
                        //continue;
                        //просто добавим
                    } else { //prevSymb < curSymb
                        value -= prevSymb * 2; //ибо в прошлую итерацию мы её прибавили
                    }
                    //список тестов
                    //нет арифметической операции(ао)
                    //несколько ао
                    //несколько разных ао
                    //не римские символы, хотя это не число.

                    //символы не одинаковы - сбросим счетчик 
                    equalsSymbolCount = 1;

                    prevSymb = curSymb;
                    //    value+=interpretationDigitsMap.get(charSymb)*multiplier;

                }

                //идем слева направо.
                //допустимо 3 множителя подряд(i___) = 2kkk
                //считаем мы в лонгах
                //если 3 множителя подряд и следующий символ не i - ругаемся
                //
            }
            if (multiplierCount > 2 && (prevSymb != 'I')) { //проверка последнего числа
                throw new CalculatorException("Ошибка при интерпретации числа '" + romanNumber + "' - не может быть более 2 символов подчеркивания подряд,если следующий символ не 'I'");
            }
            value += prevSymb;
            
            int ivalue = (int)value;
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
                        sb.append(getUpperScore((iteration) - 1 -(1 == romanaPair ? 1:0)));
                    }
                    case "2": {//II
                        sb.append(getPair(romanaPair, false));
                        sb.append(getUpperScore((iteration) - 1-(1 == romanaPair ? 1:0)));
                    }
                    case "1": {//I
                        sb.append(getPair(romanaPair, false));
                        sb.append(getUpperScore((iteration) - 1-(1 == romanaPair ? 1:0)));
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
                        sb.append(getUpperScore((iteration) - 1 - (1 == romanaPair ? 1:0)));
                        sb.append(getPair((romanaPair + 1), false));
                        sb.append(getUpperScore((iteration) - 1));
                        break;
                    }
                }                
                //2-1-0-2-1-0...
                switch(romanaPair) {
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
            if(s.length() > 3) {
                String sub = s.substring(s.length()-3);
             //   sub;
                s = s.substring(0,s.length()-3).concat(sub.replaceAll("M", "I"));
            } else {
                s.replaceAll("M", "I");
            }
            return s;
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
        }
        return null;
    }

    private static String getUpperScore(Integer scoreCount) {
        StringBuilder scoreBuilder = new StringBuilder();
        for (int i = 0; i < scoreCount; i++) {
            scoreBuilder.append(specialSymb);
        }
        return scoreBuilder.toString();
    }
}
