package consolecalculator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Класс преобразует переданную ему строку в свой объект, либо выкидывает ошибку
 *
 * @author Akira
 */
public class CalculatorParser {

    private CalculatorNumber firstNumber;
    private CalculatorNumber secondNumber;
    private String arithmeticOperationSymbol; //Стринг, для удобства юзания в свитче
    
    //допустимые арифметические операции
    private static List<String> allowedArithmeticOperationSymbols = Stream.of("+", "-", "*", "/").collect(Collectors.toList());
    
    /**
     * 
     * @return первое число
     */
    public CalculatorNumber getFirstNumber() {
        return firstNumber;
    }
    
    /**
     * 
     * @return второе число
     */
    public CalculatorNumber getSecondNumber() {
        return secondNumber;
    }
    
    /**
     * 
     * @return символ арифметической операции
     */
    public String getArithmeticOperationSymbol() {
        return arithmeticOperationSymbol;
    }
    
    public CalculatorParser(CalculatorNumber firstNumber, CalculatorNumber secondNumber, String arithmeticOperationSymbol) {
        this.firstNumber = firstNumber;
        this.secondNumber = secondNumber;
        this.arithmeticOperationSymbol = arithmeticOperationSymbol;
    }
    
    /**
     * Разделяет строку на 3 части: 2 числа и 
     * символ арифметической операции между ними.
     * @param inputString строка для разделения
     * @return 
     * @throws CalculatorException Если что пойдет не так.
     */
    public static CalculatorParser parse(String inputString) throws CalculatorException {
        if (!allowedArithmeticOperationSymbols.stream().anyMatch(aos -> inputString.contains(aos))) {
            throw new CalculatorException("В введенной строке отсутствует символ арифметической операции");
        }
        //else не ставим, т.к. дальше тупо не идем.
        String arithmeticOperationSymbolTemp = checkAndGetArithmeticOperationSymbol(inputString);
        String[] potentialNumbersString = inputString.split("\\" + arithmeticOperationSymbolTemp); //см. схожее экранирование в методе пред. строки.
        return new CalculatorParser(
                new CalculatorNumber(potentialNumbersString[0]),
                new CalculatorNumber(potentialNumbersString[1]),
                arithmeticOperationSymbolTemp);
    }

    /**
     * Выявляет в строке арифметический символ
     * @param inputString исходная строка
     * @return арифметический символ
     * @throws CalculatorException Если 2 одинаковых или разных символа,или их нет вовсе
     */
    private static String checkAndGetArithmeticOperationSymbol(String inputString) throws CalculatorException {
        String arithmeticOperationSymbolTemp = null;
        //пробуем засплитить. 
        //если 1 - значит нет там символа
        //если 2 - чекаем на налл.
        //если 3+ - кидаем ошибку.
        for (String arithmeticOperationSymbol : allowedArithmeticOperationSymbols) {
            if (inputString.split("\\" + arithmeticOperationSymbol).length > 2) { //экранируем символ, иначе может счестся регуляркой
                throw new CalculatorException("В введенной строке присутствует "
                        + (inputString.split(arithmeticOperationSymbol).length - 1) + " '" + arithmeticOperationSymbol + "' символов. Допустим только 1");
            }
            if (inputString.split("\\" + arithmeticOperationSymbol).length == 2) { //рабочий случай
                if (arithmeticOperationSymbolTemp != null) {
                    throw new CalculatorException("В введеной строке присутствует как минимум 2 разных арифметических символа:"
                            + arithmeticOperationSymbolTemp + " и " + arithmeticOperationSymbol + ".Допустим только 1 арифметический символ");
                }
                arithmeticOperationSymbolTemp = arithmeticOperationSymbol;
            }
        }
        if (arithmeticOperationSymbolTemp == null) { //CHECK IT:перестраховка, по идее избыточно. Кандидат на удаление после тестов. 
            throw new CalculatorException("В введенной строке отсутствует символ арифметической операции");
        }
        return arithmeticOperationSymbolTemp;
    }
}
