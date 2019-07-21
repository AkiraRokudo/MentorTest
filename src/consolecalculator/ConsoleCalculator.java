package consolecalculator;

import java.util.Scanner;

import java.util.Scanner;

/**
 *
 * @author Akira
 */
public class ConsoleCalculator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        startApp();
        
        //создать класс Number
        //содержит поле значение, интегер
        //содержит поле булинь римское ли
        
        //создать класс. преобразователь.
        
        
        //мы получили строку.
        //отдали её преобразователю.
        //преобразователь в итоге возвращает карту - 2 числа и делитель.
        //либо ошибку.
        
        //если ошибку - выводим, и успокаиваемся.
        //сперва создадим как считаем нужным. Затем переключим на другую ветку и создадим 
        //еще один вариант,без исключений
    }

    private static void startApp() {
        //and it's work, while user input symbols
        Scanner in = new Scanner(System.in);
        String inputString = "valueForStart";
        Character specialSymb = '\u0305';
    //    System.out.println("I" + specialSymb);
    
    
        //тестовый соут
        System.out.println(CalculatorNumber.RomanNumberTransfer.
                toRomanNumber(
              //  2573146357L
                    //   1289354246
                        99
                //toInteger( 
//"d"+specialSymb+specialSymb+specialSymb
            //    "MDXLxVIII"
        ));
        
        
   //     System.out.println(Integer.MAX_VALUE);
        printFirstMessages();        
        while (true) {
            if("exit".equals(inputString = in.next())) {
                break;
            }
            CalculatorParser resultParse = CalculatorParser.parse(inputString);
            System.out.println("Результат расчетов: " + calculate(resultParse));
        }
        in.close();
        System.out.println("Программа завершила свою работу");
    }
    
    private static void printFirstMessages() {
        System.out.println("Калькулятор 0.1");
        System.out.println("ВВедите данные. Для завершения работы, введите слово 'exit'");
        //TODO: дописать хелпер.
    }
    
    private static String calculate(CalculatorParser resultParse) {
        check(resultParse);
        long result = 0L;
        switch(resultParse.getArithmeticOperationSymbol()) {
            case"+":
                result = resultParse.getFirstNumber().getValue()+ resultParse.getSecondNumber().getValue();
                break;
            case"*":
                break;
            case"/":
                break;
            case"-":
                break;
        }
        return null;
    }
    
    /**
     * Проверим результат разбора на соответствие
     * @param resultParse 
     */
    private static void check(CalculatorParser resultParse) {
        if(!resultParse.getFirstNumber().getIsRomanNumber().equals
            (resultParse.getSecondNumber().getIsRomanNumber())) {
            throw new CalculatorException("Запрещено производить арифметические операции над числами в разных системах исчисления");
        }
        
    }
}
