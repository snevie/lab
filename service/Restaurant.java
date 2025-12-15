package service;

import util.*;
import model.*;
import exception.RestaurantException;
import java.util.concurrent.*;
import java.util.*;

//класс ресторана, управляющий всеми потоками
public class Restaurant {
    private final Logger logger;
    private final BlockingQueue<Order> orderQueue;     //новые заказы
    private final BlockingQueue<Order> readyQueue;     //готовые заказы
    
    //потоки официантов
    private final List<Waiter> waiters;
    private final List<Thread> waiterThreads;
    
    //пул поваров (не final, инициализируется в методе open)
    private ExecutorService kitchen;
    private final List<Chef> chefs;
    
    //флаг для работы ресторана
    private volatile boolean isOpen;
    
    public Restaurant() {
        this.logger = Logger.getInstance();
        
        //инициализация очередей
        this.orderQueue = new LinkedBlockingQueue<>(Config.ORDER_QUEUE_SIZE);
        this.readyQueue = new LinkedBlockingQueue<>();
        
        //инициализация списков
        this.waiters = new ArrayList<>();
        this.waiterThreads = new ArrayList<>();
        this.chefs = new ArrayList<>();
        
        this.isOpen = false;
        this.kitchen = null;
    }
    
    //запуск потоков
    public void open() {
        if (isOpen) {
            throw new RestaurantException("Ресторан уже открыт");
        }
        
        logger.logSystemStart();
        isOpen = true;
        
        try {
            //создание поваров
            kitchen = Executors.newFixedThreadPool(Config.CHEFS_COUNT);
            for (int i = 1; i <= Config.CHEFS_COUNT; i++) {
                Chef chef = new Chef(i, orderQueue, readyQueue);
                chefs.add(chef);
                kitchen.execute(chef);
            }
            
            //создание официантов
            for (int i = 1; i <= Config.WAITERS_COUNT; i++) {
                Waiter waiter = new Waiter(i, orderQueue, readyQueue);
                Thread waiterThread = new Thread(waiter);
                
                waiters.add(waiter);
                waiterThreads.add(waiterThread);
                
                waiterThread.start();
            }
            
            logger.info("Ресторан открыт, работают " + 
                       Config.WAITERS_COUNT + " официант(а) и " + 
                       Config.CHEFS_COUNT + " повар(а)");
            
        } catch (Exception e) {
            //возникла ошибка => останавливаем работу кухни
            if (kitchen != null) {
                kitchen.shutdownNow();
            }
            throw new RestaurantException("Ошибка при открытии ресторана", e);
        }
    }
    
    //остановка всех потоков
    public void close() {
        if (!isOpen) {
            throw new RestaurantException("Ресторан уже закрыт");
        }
        
        logger.info("Ресторан готовится к закрытию");
        isOpen = false;
        
        try {
            //остановка официантов
            for (Waiter waiter : waiters) {
                waiter.stopWorking();
            }
            
            for (Thread waiterThread : waiterThreads) {
                waiterThread.join(5000);
                if (waiterThread.isAlive()) {
                    waiterThread.interrupt();
                }
            }
            
            //остановка поваров
            for (Chef chef : chefs) {
                chef.stopCooking();
            }
            
            //завершение работы кухни
            if (kitchen != null) {
                kitchen.shutdown();
                if (!kitchen.awaitTermination(10, TimeUnit.SECONDS)) {
                    kitchen.shutdownNow();
                }
            }
            
            printStatistics();
            logger.logSystemShutdown();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RestaurantException("Прервано закрытие ресторана", e);
        } finally {
            //принудительная остановка потоков
            if (kitchen != null) {
                kitchen.shutdownNow();
            }
            for (Thread waiterThread : waiterThreads) {
                if (waiterThread.isAlive()) {
                    waiterThread.interrupt();
                }
            }
        }
    }
    
    public boolean isOpen() {
        return isOpen;
    }
    
    //получаем текущую статистику очередей
    public String getQueueStatus() {
        return String.format("Очередь на кухню: %d/%d | Готовые заказы: %d",
                orderQueue.size(), Config.ORDER_QUEUE_SIZE, readyQueue.size());
    }
    
    //статистика работы ресторана
    private void printStatistics() {
        logger.info("-".repeat(60));
        logger.info("СТАТИСТИКА РАБОТЫ РЕСТОРАНА");
        logger.info("-".repeat(60));
        logger.info("Очередь на кухню: " + orderQueue.size() + " заказ(ов)");
        logger.info("Готовые заказы: " + readyQueue.size() + " заказ(ов)");
        logger.info("Официантов работало: " + waiters.size());
        logger.info("Поваров работало: " + chefs.size());
        logger.info("-".repeat(60));
    }
    
    //получение списка текущих заказов в очереди
    public List<Order> getPendingOrders() {
        return new ArrayList<>(orderQueue);
    }
    
    //получение списка готовых заказов
    public List<Order> getReadyOrders() {
        return new ArrayList<>(readyQueue);
    }
}