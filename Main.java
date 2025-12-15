import exception.RestaurantException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import service.Restaurant;
import util.*;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getInstance();
        Restaurant restaurant = null;
        Scanner scanner = new Scanner(System.in);
        
        try {
            //меню выбора настроек
            boolean configSelected = false;
            while (!configSelected) {
                System.out.println("\nВыберите вариант:");
                System.out.println("1. Использовать настройки по умолчанию");
                System.out.println("2. Настроить параметры вручную");
                System.out.print("Ваш выбор: ");
                
                String choice = scanner.nextLine().trim();
                
                switch (choice) {
                    case "1":
                        Config.useDefaultConfiguration();
                        configSelected = true;
                        break;
                    case "2":
                        Config.readConfigurationFromConsole();
                        configSelected = true;
                        break;
                    default:
                        System.out.println("Неверный выбор. Попробуйте снова.");
                }
            }
            
            restaurant = new Restaurant();
            restaurant.open();
            
            logger.info("Ресторан будет работать " + 
                       Config.RESTAURANT_WORK_TIME/1000 + " секунд");
            
            long startTime = System.currentTimeMillis();
            long elapsedTime = 0;
            
            //основной цикл работы ресторана
            while (elapsedTime < Config.RESTAURANT_WORK_TIME && restaurant.isOpen()) {
                //периодически выводим статус очередей
                if (elapsedTime % 5000 == 0) {
                    logger.info("Статус: " + restaurant.getQueueStatus());
                }
                
                TimeUnit.SECONDS.sleep(1);
                elapsedTime = System.currentTimeMillis() - startTime;
            }
            
            if (restaurant != null && restaurant.isOpen()) {
                restaurant.close();
            }
            
            logger.info("Программа завершена.");
            
        } catch (RestaurantException e) {
            logger.error("Ошибка в работе ресторана", e);
        } catch (InterruptedException e) {
            logger.error("Программа была прервана", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("Неожиданная ошибка", e);
        } finally {
            //гарантия закрытия ресторана при любом исходе
            if (restaurant != null && restaurant.isOpen()) {
                try {
                    restaurant.close();
                } catch (Exception e) {
                    logger.error("Ошибка при неожиданном закрытии ресторана", e);
                }
            }
            
            if (scanner != null) {
                scanner.close();
            }
        }
    }
}