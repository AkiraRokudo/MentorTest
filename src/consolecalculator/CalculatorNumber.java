package consolecalculator;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Класс-адаптер. Позволяет отображать число в римской и арабской системе
 * исчисления
 *
 * @author Akira
 */
public class CalculatorNumber {

    private boolean isRomanNumber;

    private int value;
    //можно было бы запилить и стринг для романского представления,
    //но мы в любом случае работаем с числами(арабскими), и вывод исходных чисел 
    //в романском варианте нам не нужен.

    //спецсимвол для больших римских чисел
    private static Character specialSymb = '\u0305'; // выглядит как символ надчеркивания

    public CalculatorNumber(String str) {
        str = str.trim();
        isRomanNumber = isNonDigit(str);
        try {
            if (isRomanNumber) {
                value = RomanNumberTransfer.toInteger(str);
            } else {
                value = Integer.valueOf(str);
            }
        } catch (java.lang.NumberFormatException e) { //это от переизбытка
            throw new CalculatorException("Ошибка при преобразовании строки в числа");
        }

    }

    /**
     *
     * @return представлено ли было число (изначально) в римской форме записи
     */
    public boolean getIsRomanNumber() {
        return isRomanNumber;
    }

    /**
     * Сеттер для более удобного способа отображения чисел 4000+
     *
     * @param specialSymb
     */
    public static void setSpecialSymb(Character specialSymb) {
        CalculatorNumber.specialSymb = specialSymb;
    }

    /**
     *
     * @return число в арабской системе представления.
     */
    public int getValue() {
        return value;
    }

    /**
     *
     * @param str строка для проверки
     * @return являются ли хоть один из символов не числом
     */
    public boolean isNonDigit(String str) {
        return str.chars().anyMatch(c -> !Character.isDigit(c));
    }

    /**
     * Вспомогательный класс для преобразования из одного состояния в другое
     */
    public static class RomanNumberTransfer {

        private static List<Long> allowedPrevLessCurList = Stream.of(4L, //IV все возможные сочетания 2 символов, левый из которых меньше правого
                9L, //IX
                40L, //XL 
                90L, //XC
                400L,//CD
                900L //CM
        ).collect(Collectors.toList());

        private static List<String> prohibitiedList = Stream.of("IIII", //Такие сочетания использовать нельзя
                "VV",
                "XXXX",
                "LL",
                "CCCC",
                "DD",
                "MMMM"
        ).collect(Collectors.toList());

        private static HashMap<Character, Integer> interpretationDigitsMap; //хранит все символьные римские представления.
        public static HashMap<Character, Integer> interpretationHalfDigitsMap; //половинные(не кратные 10 символы. в связи с особенностями отображения выделены отдельно

        static { //инициализируется при первом обращении к вложенному классу.
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
            //TODO: Реализовать возможность более легкого ввода 4000+
            
            //проверка что это вообще число нам подходит
            final String testRomanNumber = romanNumber.toUpperCase();
            if (testRomanNumber.chars().anyMatch(c -> (!interpretationDigitsMap.containsKey((char) c) && (char) c != specialSymb))) {
                throw new CalculatorException("Ошибка при интерпретации строки '" + romanNumber + "' - присутствуют неподходящие символы");
            }
            if (prohibitiedList.stream().anyMatch(ps -> testRomanNumber.contains(ps))) {
                throw new CalculatorException("Ошибка при интерпретации строки '" + romanNumber + "' - недопустимо использование более 3 для единичных и 2 для половинных символов, одинаковых символов подряд");
            }

            boolean prevLess = false; // TODO: провести анализ на предмет нужности. Указывает на то, что предыдущий символ был меньше предпредыдущего
            long prevprevSymb = 0; //предпредыдущее число-символ
            long value = 0; //итоговое число. лонг, дабы отловить переизбыток.
            int multiplierCount = 0; //множитель числа на 10^3 для проверки на переизбыток, фиксирования количества * числа на 1000 и краткости проверки чисел из allowedPrevLessCurList
            int equalsSymbolCount = 1; //количество одинаковых символов подряд
            if (testRomanNumber.charAt(0) == specialSymb) {
                throw new CalculatorException("Ошибка при интерпретации числа '" + romanNumber + "' - первым символом не может быть символ надчеркивания");
            }
            long prevSymb = 0; //предыдущее число-символ
            
            //непосредственно сам разбор
            for (Character charSymb : testRomanNumber.toCharArray()) {
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
                        prevSymb = curSymb; //по идее надо бы в конце блока, но они и так одинаковы
                        equalsSymbolCount++;
                        if (equalsSymbolCount > 3) { //CHECK IT:перестраховка, по идее избыточно. Кандидат на удаление после тестов. 
                            throw new CalculatorException("Ошибка - слишком много подряд одинаковых символов");
                        }
                        if (prevLess) {
                            throw new CalculatorException("Ошибка - недопустимо наличие 2 одинаковых символов если левее расположенный символ меньше(IXX)");
                        }
                        prevLess = false;
                    } else { //значит либо больше либо меньше
                        if (prevSymb > curSymb) { 
                            if (prevLess) { //ICX или ICI на 2 сверке  xci
                                if (prevprevSymb <= curSymb) {
                                    throw new CalculatorException("Ошибка - недопустимо наличие символа равного или большего чем предпредыдущее, если предыдущее больше предпредыдущего(ICX или ICI)");
                                }
                                prevLess = false; //очень даже reachable statement, хоть и не выглядит таковым)))
                            }
                        } else { //prevSymb < curSymb
                            if (prevLess) {
                                throw new CalculatorException("Ошибка - недопустимо наличие символа больше предыдущего, если предпредыдущий меньше предыдущего (ICM)");
                            }
                            if (!isPosibbleValue(curSymb, prevSymb, multiplierCount)) {// блочим варианты 1000 50000
                                throw new CalculatorException("Недопустимый вариант сочетания меньшей цифры слева и большей справа");
                            }
                            if (equalsSymbolCount > 1) { //iix iiv отбрасываем
                                throw new CalculatorException("Недопустимо наличие 2 одинаковых символов с последующим большим символом - к примеру 'IIX'");
                            }
                            if (prevprevSymb != 0) { //если не первое сравнение. Если первое и не подходящее упадем вот тут isPosibbleValue
                                if (prevprevSymb == curSymb) { //viv или xix
                                    if (interpretationHalfDigitsMap.containsKey(charSymb)) {
                                        //значит это v,l или d
                                        throw new CalculatorException("Ошибка - недопустимо наличие половинного(не кратного 10, не считая I) символа большего чем предыдущий и равного предпредыдущему (VIV)");
                                    }
                                } 
                                //значит prevprevSymb <> curSymb.
                                //рабочими вариантами может быть только 
                                //prevprevSymb<>prevSymb (долго доказывать)
                                //обработаем этот факт.
                                else {
                                    if (prevprevSymb < curSymb) { //vix - нельзя  //xci - а это можно
                                        throw new CalculatorException("Ошибка - недопустимо наличие большего числа чем предыдущее, если предыдущее меньше предпредыдущего(VIX)");
                                    }
                                }
                            }
                            value -= 2 * (prevSymb);// дважды отнимаем, т.к. на прошлой итерации добавили
                            prevLess = true;
                        }
                        prevprevSymb = prevSymb; 
                        prevSymb = curSymb;
                        equalsSymbolCount = 1;
                    }
                }
            }
            //вышли из цикла
            if (multiplierCount > 2 && (prevSymb != 'I')) { //проверка последнего числа
                throw new CalculatorException("Ошибка при интерпретации числа '" + romanNumber + "' - не может быть более 2 символов надчеркивания подряд, если символ слева не 'I'");
            }
            value += prevSymb;
            return Integer.valueOf(String.valueOf(value)); //проверка на перебор
        }

        /**
         * Преобразовывает число в Римское написание числа
         *
         * @param number число для преобразования
         * @return стркоу с римским представление числа
         */
        public static String toRomanNumber(int number) {
            if (number <= 0) {
                throw new CalculatorException("Ошибка при выводе числа '" + number + "' - в римскую систему исчисления можно интерпретировать только положительное число");
            }
            //i v 1-9
            //x-l 10-99
            //c-d 100-999
            //m-v_ 1000-9999
            String strNumb = String.valueOf(number);
            int len = String.valueOf(number).length(); //количество разрядов числа. Трим не нужен ибо строка из числа получена
            int iteration = len % 3 == 0 ? len / 3 : (len / 3) + 1; //по троечное разделение разрядов. Например 34043 даст 2 разряда. 1 целый и один начатый
            int romanaPair = len % 3; //Представляет собой один из 3 числовых разрядов в очередной тройке разрядов. Необходим для определения соответствуюшего римского символа    Переделать на инт. it's done
            StringBuilder sb = new StringBuilder();
            
            //непосредственно сам цикл преобразования
            for (;iteration > 0;) { //так короче хах)
                char symb = strNumb.charAt(0);//возьмем очередной символ и подрежем строку
                strNumb = strNumb.substring(1);
                switch (Character.toString(symb)) { //определяем какую последовательность символов надо добавить
                    //TODO:переделать на кейз под 5 позиций. и с 3 брейками
                    case "3": { //III
                        sb.append(getPair(romanaPair, false)); //добавляем число и его черточки
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
                switch (romanaPair) { //скачем между сотнями десятками и единицами
                    case 0: {
                        romanaPair = 2;
                        break;
                    }
                    case 1: {
                        romanaPair = 0;
                        iteration--; //закончили с очередной тройкой разрядов
                        break;
                    }
                    case 2: {
                        romanaPair = 1;
                        break;
                    }
                }
            }
            //исправим последние единичные разряды (если есть) на правильные. it's done
            String s = sb.toString();
            if (s.length() > 3) { //XMX он тоже подправит
                String sub = s.substring(s.length() - 3);
                s = s.substring(0, s.length() - 3).concat(sub.replaceAll("M", "I"));
            } else {
                s = s.replaceAll("M", "I");
            }
            return s;
        }

        /**
         * Проверяет пару символов, при том что правый больше левого на
         * допустимость сочетания
         *
         * @param curSymb правый символ
         * @param prevSymb левый символ
         * @param multiplierCount разрядность чисел, которую надо убрать для
         * проверки
         * @return допустимость подобной пары.
         */
        private static Boolean isPosibbleValue(Long curSymb, Long prevSymb, Integer multiplierCount) {
            return allowedPrevLessCurList.contains((curSymb - prevSymb) / (multiplierCount == 0 ? 1 : multiplierCount * 1000));
        }
    }

    /**
     * Проверка на вхождение в необходимый интервал. Закоменчено ибо придуман
     * другой способ
     *
     * public void isPossibleValue() { int intTempValue = (int) value; long
     * longTempValue = intTempValue; if (value != longTempValue) { throw new
     * CalculatorException("Превышено допустимое значение: от " +
     * Integer.MIN_VALUE + " до " + Integer.MAX_VALUE); } }
     */
    
    
    /**
     * Он конечно не особо нужен, но для красоты..
     *
     * @return число в арабской или романской системе исчисления.
     */
    @Override
    public String toString() {
        return isRomanNumber
                ? RomanNumberTransfer.toRomanNumber(value)
                : value + "";
    }

    /**
     * Вспомогательный метод для преобразования цифры в римские символы
     *
     * @param m разряд(единицы, десятки или сотни) числа
     * @param isHalf половинное(V,L,D) или нет число
     * @return соответствующий символ
     */
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

    /**
     *
     * @param scoreCount длина строки
     * @return строку состоящую из спец символа римских "тысяч"
     */
    private static String getUpperScore(Integer scoreCount) {
        StringBuilder scoreBuilder = new StringBuilder();
        for (int i = 0; i < scoreCount; i++) {
            scoreBuilder.append(specialSymb);
        }
        return scoreBuilder.toString();
    }
}
