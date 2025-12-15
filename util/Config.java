package util;
import java.util.Scanner;

//класс содержит все настраиваемые параметры системы
public class Config {
    //настраиваемые параметры
    public static int WAITERS_COUNT = 3;
    public static int CHEFS_COUNT = 2;
    public static int ORDER_QUEUE_SIZE = 10;
    public static int RESTAURANT_WORK_TIME = 30000;
    public static int WAITER_PROCESSING_TIME = 1000;
    public static int DELIVERY_TIME = 800;
    
    //минимальные и максимальные значения для валидации
    private static final int MIN_WAITERS = 1;
    private static final int MAX_WAITERS = 10;
    private static final int MIN_CHEFS = 1;
    private static final int MAX_CHEFS = 10;
    private static final int MIN_QUEUE_SIZE = 1;
    private static final int MAX_QUEUE_SIZE = 100;
    private static final int MIN_WORK_TIME = 5000; //5 сек минимум
    private static final int MAX_WORK_TIME = 300000; //5 мин максимум
    private static final int MIN_PROCESSING_TIME = 100;
    private static final int MAX_PROCESSING_TIME = 5000;
    private static final int MIN_DELIVERY_TIME = 100;
    private static final int MAX_DELIVERY_TIME = 5000;
    
    public static void readConfigurationFromConsole() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Настройка параметров ресторана");
        System.out.println("-".repeat(40));
        
        WAITERS_COUNT = readInt(scanner, "Количество официантов", 
                               MIN_WAITERS, MAX_WAITERS, WAITERS_COUNT);
        
        CHEFS_COUNT = readInt(scanner, "Количество поваров", 
                             MIN_CHEFS, MAX_CHEFS, CHEFS_COUNT);
        
        ORDER_QUEUE_SIZE = readInt(scanner, "Размер очереди заказов", 
                                  MIN_QUEUE_SIZE, MAX_QUEUE_SIZE, ORDER_QUEUE_SIZE);
        
        RESTAURANT_WORK_TIME = readInt(scanner, 
                                      "Время работы ресторана (мс)", 
                                      MIN_WORK_TIME, MAX_WORK_TIME, RESTAURANT_WORK_TIME);
        
        WAITER_PROCESSING_TIME = readInt(scanner, 
                                        "Время обработки заказа официантом (мс)", 
                                        MIN_PROCESSING_TIME, MAX_PROCESSING_TIME, WAITER_PROCESSING_TIME);
        
        DELIVERY_TIME = readInt(scanner, 
                               "Время передачи блюда (мс)", 
                               MIN_DELIVERY_TIME, MAX_DELIVERY_TIME, DELIVERY_TIME);
        
        scanner.close();
    }
    
    private static int readInt(Scanner scanner, String prompt, int min, int max, int defaultValue) {
        while (true) {
            try {
                System.out.printf("\n%s [%d-%d] (по умолчанию: %d): ", 
                                 prompt, min, max, defaultValue);
                String input = scanner.nextLine().trim();
                
                //если ввод пустой, используем значение по умолчанию
                if (input.isEmpty()) {
                    return defaultValue;
                }
                
                int value = Integer.parseInt(input);
                
                if (value < min || value > max) {
                    System.out.printf("Ошибка: значение должно быть от %d до %d.", 
                                     min, max);
                    continue;
                }
                
                return value;
                
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }
    
    public static void printConfiguration() {
        System.out.println("\nТекущие настройки ресторана:");
        System.out.println("-".repeat(40));
        System.out.println("Количество официантов: " + WAITERS_COUNT);
        System.out.println("Количество поваров: " + CHEFS_COUNT);
        System.out.println("Размер очереди заказов: " + ORDER_QUEUE_SIZE);
        System.out.println("Время работы ресторана: " + RESTAURANT_WORK_TIME + " мс");
        System.out.println("Время обработки заказа официантом: " + WAITER_PROCESSING_TIME + " мс");
        System.out.println("Время доставки блюда: " + DELIVERY_TIME + " мс");
        System.out.println("-".repeat(40));
    }
    
    public static void useDefaultConfiguration() {
        System.out.println("Используется конфигурация по умолчанию");
        printConfiguration();
    }
}