/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
