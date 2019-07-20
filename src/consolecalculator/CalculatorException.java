package consolecalculator;

/**
 * Создан для выявления некорректных данных не подходящих 
 * под требования программы
 * @author Akira
 */
public class CalculatorException extends RuntimeException {
    
    public CalculatorException(String errMsg) {
        super(errMsg);
    }
}
