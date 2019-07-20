package consolecalculator;

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
        printFirstMessages();        
        while (true) {
            if("exit".equals(inputString = in.next())) {
                break;
            }
            System.out.println("Введенное выражение:" + inputString);
        }
        in.close();
        System.out.println("Программа завершила свою работу");
    }
    
    private static void printFirstMessages() {
        System.out.println("Калькулятор 0.1");
        System.out.println("ВВедите данные. Для завершения работы, введите слово 'exit'");
        //TODO: дописать хелпер.
    }
}
