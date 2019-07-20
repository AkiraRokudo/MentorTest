package consolecalculator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Класс преобразует переданную ему строку, либо выкидывает ошибку
 * @author Akira
 */
public class CalculatorParser {
    
    private CalculatorNumber firstNumber;
    private CalculatorNumber secondNumber;
    private String arithmeticOperationSymbol;
    
    private static List<String> allowedArithmeticOperationSymbols = Stream.of("+","-","*","/").collect(Collectors.toList());

    public CalculatorNumber getFirstNumber() {
        return firstNumber;
    }

    public CalculatorNumber getSecondNumber() {
        return secondNumber;
    }

    public String getArithmeticOperationSymbol() {
        return arithmeticOperationSymbol;
    }

    public CalculatorParser(CalculatorNumber firstNumber, CalculatorNumber secondNumber, String arithmeticOperationSymbol) {
        this.firstNumber = firstNumber;
        this.secondNumber = secondNumber;
        this.arithmeticOperationSymbol = arithmeticOperationSymbol;
    } 
    
    public static CalculatorParser parse(String inputString) {
        if(!allowedArithmeticOperationSymbols.stream().anyMatch(aos -> inputString.contains(aos))) {
            throw new CalculatorException("В введенной строке отсутствует символ арифметической операции");
        }
        //else В этой версии не ставим, т.к. дальше тупо не идем.
        String arithmeticOperationSymbolTemp = checkAndGetArithmeticOperationSymbol(inputString);
        String [] potentialNumbersString = inputString.split(arithmeticOperationSymbolTemp);
        return new CalculatorParser(
                new CalculatorNumber(potentialNumbersString[0]), 
                new CalculatorNumber(potentialNumbersString[1]), 
                arithmeticOperationSymbolTemp);
    }
    
    /**
     * 
     * @param inputString 
     * @return символ арифметической операции
     */
    private static String checkAndGetArithmeticOperationSymbol(String inputString) {
        String arithmeticOperationSymbolTemp = null;
        for (String arithmeticOperationSymbol : allowedArithmeticOperationSymbols) {
            //пробуем засплитить. 
            //если 1 - значит нет там символа
            //если 3+ - кидаем ошибку.
            //если 2 - чекаем на налл.
            if(inputString.split(arithmeticOperationSymbol).length > 2) {
                throw new CalculatorException("В введенной строке присутствует " + 
                        (inputString.split(arithmeticOperationSymbol).length-1) + " '" + arithmeticOperationSymbol + "' символов. Допустим только 1");
            }
            if(inputString.split(arithmeticOperationSymbol).length == 2) { //рабочий случай
                if(arithmeticOperationSymbolTemp != null) {
                    throw new CalculatorException("В введеной строке присутствует как минимум 2 разных арифметических символа:" + 
                            arithmeticOperationSymbolTemp + " и " + arithmeticOperationSymbol + ".Допустим только 1 арифметический символ");
                }
                arithmeticOperationSymbolTemp = arithmeticOperationSymbol;
            }          
        }
        if(arithmeticOperationSymbolTemp == null) {
            throw new CalculatorException("В введенной строке отсутствует символ арифметической операции");
        }
        return arithmeticOperationSymbolTemp;
    }
}
