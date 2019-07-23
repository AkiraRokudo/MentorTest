package consolecalculator;

import java.util.Scanner;

/**
 * Программа для калькуляции римских и арабских чисел в пределе Integer
 * @author Akira
 */
public class ConsoleCalculator {
    
    //TODO: навести порядок в последовательности описания методов, сгрупировать их.
    
    public static void main(String[] args) {
        startApp();

    }
    
    /**
     * Точка запуска(мейн не в счет) калькулятора.
     */
    private static void startApp() {
        //and it's work, while user input symbols
        Scanner in = new Scanner(System.in);        
    
        printFirstMessages();
        boolean inWork = true;
        
        while (inWork) { //работаем, пока не упадем в ошибку, или пользователь не введет команду на выход
            final String inputSymbols = in.next();
            switch (inputSymbols) {
                case "exit":
                    inWork = false;
                    break;
                case "h":
                    System.out.println(getHelperMessage());
                    break;
                case "r":
                    System.out.println(getRulesMessage());
                    break;
                case "em"://активируем более понятный вывод спецсимвола
                    System.out.println("Активирован более простой режим ввода и вывода больших римских чисел");
                    CalculatorNumber.setSpecialSymb('_');
                    break;
                //TODO: дописать кейзы на простую работу парсера туда и обратно(без калькуляции. Подумать на тему нужности кейза с выводом возможных ошибок
                default:
                    CalculatorParser resultParse = CalculatorParser.parse(inputSymbols);
                    try {
                        System.out.println("Результат расчетов: " + calculate(resultParse));
                    } catch (NumberFormatException e) {
                        throw new CalculatorException("Ошибка при преобразовании итогового результата в число");
                    }
            }
        }
        in.close();
        System.out.println("Программа завершила свою работу");
    }
    
    /**
     * Сообщение, демонстрируемое при старте программы.
     * Вынесено в отдельный метод для удобства редактирования
     */
    private static void printFirstMessages() {
        System.out.println("Калькулятор 0.9");
        System.out.println("Добро пожаловать в калькулятор. Для вызова справки введите 'h'");
        System.out.println("Для выхода введите слово 'exit'");
        System.out.println("Введите выражение для расчетов в ОДНУ строку");
    }

    /**
     *
     * @return сообщение подсказка, для удобства
     */
    private static String getHelperMessage() {
        return "'exit' - завершить работу калькулятора \n"
                + "'h' - вызов справки \n"
                + "'r' - требования к арифметическому выражению и формату записи римских чисел \n"
                + "'em' - easyMode более простой способ записи сложных римских цифр \n"
                + "Любой другой набор символов будет воспринят как набор арифметических символов";
    }
    
    /**
     * 
     * @return Правила, по которым работает калькулятор 
     */
    private static String getRulesMessage() {
        return "Требования на основании исходного ТЗ: \n"
                + "1. Калькулятор умеет выполнять операции сложения, вычитания, умножения и деления с двумя числами: a + b, a - b, a * b, a / b. \n"
                + "Данные передаются в одну строку (смотрите пример)! Решения, в которых каждое число и арифмитеческая операция \n"
                + "передаются с новой строки считаются неверными.\n"
                + "2. Калькулятор умеет работать как с арабскими (1,2,3,4,5…), так и с римскими (I,II,III,IV,V…) цифрами.\n"
                + "3. Калькулятор умеет работать с цифрами от 0 до 9 включительно. Числа могут быть любые в диапазоне типа int.\n"
                + "4. Калькулятор умеет работать только с целыми числами.\n"
                + "5. Калькулятор умеет работать только с арабскими или римскими цифрами одновременно, при вводе пользователем строки \n"
                + "вроде 3 + II калькулятор должен выбросить исключение и прекратить свою работу.\n"
                + "6. При вводе пользователем неподходящих чисел приложение выбрасывает исключение и завершает свою работу.\n"
                + "7. При вводе пользователем строки не соответствующей одной из вышеописанных арифметических операций приложение \n"
                + "выбрасывает исключение и завершает свою работу.\n"
                + "Пример работы программы:\n"
                + "Input:\n"
                + "1 + 2\n"
                + "\n"
                + "Output:\n"
                + "3\n"
                + "\n"
                + "Input:\n"
                + "VI / III\n"
                + "\n"
                + "Output:\n"
                + "II\n"
                + "\n"
                + "Требования обусловленные особенностями написания цифр римскими символами.\n"
                + "В случае несоответствия требованиям, программа выдаст соответствующую ошибку.\n"
                + "1. Если указывается не арабское число, то допустимы (за исключением символа арифметической операции)\n"
                + "только следующие символы 'I', 'V', 'X', 'L', 'C', 'D', 'M' и спецсимвол для обозначения чисел свыше 3999.\n"
                + "2. Спецсимволом считается знак 'I\u0305', выглядящий как надчеркивание над числовым символом, или, если режим работы упрощенный,\n"
                + "как символ подчеркивания. Употребляется после числового символа, увеличивает его значение на тысячу раз.\n"                
                + "Например: 'V' = 5. 'V_' = 5000 'V__' = 5000000.\n"
                + "3. Первым символом не может быть Спецсимвол\n"
                + "4. Половинными символами считаются 'V', 'L', 'D'. Остальные числовые символы считаются единичными\n"
                + "5. Недопустимо указание 3+ единичных или 2+ вторичных одинаковых символов подряд.\n"
                + "6. В соответствии с п. 3 требований ТЗ, недопустимо указание более 2 (более 3, если символ 'I') спецсимволов подряд.\n"
                + "7. Недопустимо наличие 2 одинаковых символов если левее расположенный символ меньше(IXX).\n"
                + "8. Недопустимо наличие символа равного или большего чем предпредыдущее, если предыдущее больше предпредыдущего(ICX или ICI).\n"
                + "9. Недопустимо наличие символа больше предыдущего, если предпредыдущий меньше предыдущего (ICM)\n"
                + "10. Допустимые варианты сочетания меньшей цифры слева и большей справа: 'IV','IX','XL','XC','CD','CM'.\n"
                + "Допустимы аналогичные сочетания для больших чисел( Например MV_)\n"
                + "11. Недопустимо наличие 2 одинаковых символов с последующим бОльшим символом - к примеру 'IIX'\n"
                + "12. Недопустимо наличие половинного символа большего чем предыдущий и равного предпредыдущему (VIV)\n"
                + "13. Недопустимо наличие большего числа чем предыдущее, если предыдущее меньше предпредыдущего(VIX)\n"
                + "\n"
                ;
    }

    /**
     *
     * @param resultParse объект хранящий 2 числа и знак арифметической операции
     * @return строковое представление результатов расчета
     * @throws NumberFormatException при выходе за пределы Integer
     */
    private static String calculate(CalculatorParser resultParse) throws NumberFormatException {
        check(resultParse);
        long result = 0L;
        switch (resultParse.getArithmeticOperationSymbol()) {
            case "+":
                result = resultParse.getFirstNumber().getValue() + resultParse.getSecondNumber().getValue();
                break;
            case "*":
                //не боимся переполнения- максимум там интегер.max
                result = resultParse.getFirstNumber().getValue() * resultParse.getSecondNumber().getValue();
                break;
            case "/":
                result = resultParse.getFirstNumber().getValue() / resultParse.getSecondNumber().getValue();
                break;
            case "-":
                result = resultParse.getFirstNumber().getValue() - resultParse.getSecondNumber().getValue();
                break;
        }
        int numResult = Integer.valueOf(String.valueOf(result)); //финт ушами дабы чекнуть выход за пределы
        return resultParse.getFirstNumber().getIsRomanNumber()
                ? CalculatorNumber.RomanNumberTransfer.toRomanNumber(numResult)
                : String.valueOf(numResult);
    }

    /**
     * Проверим результат разбора на соответствие
     *
     * @param resultParse
     */
    private static void check(CalculatorParser resultParse) {
        if (!resultParse.getFirstNumber().getIsRomanNumber() == resultParse.getSecondNumber().getIsRomanNumber()) {
            throw new CalculatorException("Запрещено производить арифметические операции над числами в разных системах исчисления");
        }
        //проверка на размер проводится раньше        
    }
}

/**
 * ******************************************
 *              ТЕСТЫ 
 ******************************************* 
 * 
 * TODO: продумать тесты на рандомные ПРАВИЛЬНЫЕ сочетания
 * 
 *          Тест на все двоичные сочетания 
 * 
 * for (Character object : CalculatorNumber.RomanNumberTransfer.interpretationDigitsMap.keySet()) {
            for (Character character
                    : CalculatorNumber.RomanNumberTransfer.interpretationDigitsMap.keySet()) {
                System.out.print(object + "" + character+" ");  
                try {
                    System.out.println(CalculatorNumber.RomanNumberTransfer.toInteger(object + ""
                            + character));
                } catch (CalculatorException e) {
                    System.out.println(e.getMessage());
                }
            }
    }
 */

//Удачные плюс тесты
//  "iii"   
//  "vii"  
//  "xii"
//  "xxv"
//  "xxi"
//  "xix"
//  "lvi"
//  "xiv"
//  "xvi"                        
//  "dxv"                        
//  "dlv"                        
//  "dxl"                        
//  "dxc"                        
//  "xci"                        
//  "xli"                      
//  "xlv"    
//  "xcv"         
//17+         

//Удачные минус тесты               
//  "ixx"                          
//  "icc"                          
//  "vxx"                          
//  "iic"                          
//  "iiv"                          
//  "iil"                          
//  "ixi"                          
//  "ici"                          
//  "vxv"                          
//  "vcv"                          
//  "ivi"                          
//  "ili"                          
//  "vlv"                          
//  "vdv"                          
//  "viv"                          
//  "lil"                          
//  "xvx"                          
//  "lvl"                          
//  "cvl"                          
//  "cil"                         
//  "ldv"       
//  "ldi"       
//  "ldx"       
//  "lcv"       
//  "lci"       
//  "xmi"                           
//  "ivx"       
//  "ivl"       
//  "vld"       
//  "vlc"                               
//  "ixv"       
//  "vcl"       
//  "icx"       
//  "ilx"       
//  "ilv"       
//  "vlx"       
//  "vix"                       

