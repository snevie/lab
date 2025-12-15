package service;

import model.*;
import util.Logger;
import util.Config;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

//класс официанта
public class Waiter implements Runnable {
    private final int id;                          //id официанта
    private final BlockingQueue<Order> orderQueue; //очередь заказов на кухню
    private final BlockingQueue<Order> readyQueue; //очередь готовых заказов
    private final Logger logger;                   //логгер для записи событий
    private volatile boolean isWorking = true;     //флаг для работы официанта
    
    public Waiter(int id, BlockingQueue<Order> orderQueue, BlockingQueue<Order> readyQueue) {
        this.id = id;
        this.orderQueue = orderQueue;
        this.readyQueue = readyQueue;
        this.logger = Logger.getInstance();
        
        Thread.currentThread().setName("Официант #" + id);
        logger.info("Официант #" + id + " начал работу");
    }
    
    @Override
    public void run() {
        while (isWorking || !orderQueue.isEmpty() || !readyQueue.isEmpty()) {
            try {
                //попытка взять готовый заказ из очереди
                Order readyOrder = readyQueue.poll(500, TimeUnit.MILLISECONDS);
                
                if (readyOrder != null) {
                    deliverOrder(readyOrder);
                } else if (isWorking) {
                    //если нет готовых заказов, имитируем прием заказа от гостя
                    Order order = takeOrderFromCustomer();
                    sendOrderToKitchen(order);
                }
                
            } catch (InterruptedException e) {
                logger.warning("Официант #" + id + " был прерван");
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        logger.info("Официант #" + id + " завершил работу");
    }
    
    //принятие заказа
    private Order takeOrderFromCustomer() {
        //создание нового гостя и заказа
        Customer customer = new Customer();
        Dish dish = Dish.getRandomDish();
        
        Order order = new Order(customer, dish);
        order.setStatus(Order.OrderStatus.ACCEPTED);
        
        logger.info("Официант #" + id + " принял заказ #" + order.getId() + 
                   " от " + customer + ": " + dish.getName());
        
        //имитация времени на оформление заказа
        try {
            Thread.sleep(Config.WAITER_PROCESSING_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return order;
    }
    
    //передача заказа на кухню
    private void sendOrderToKitchen(Order order) throws InterruptedException {
        order.setStatus(Order.OrderStatus.COOKING);
        
        //попытка положить заказ в очередь на кухню
        boolean added = orderQueue.offer(order, 2, TimeUnit.SECONDS);
        
        if (added) {
            logger.info("Официант #" + id + " передал заказ #" + order.getId() + 
                       " на кухню (" + orderQueue.size() + " в очереди)");
        } else {
            logger.warning("Официант #" + id + ": очередь на кухню переполнена, заказ #" + 
                          order.getId() + " отложен");
            Thread.sleep(1000);
            sendOrderToKitchen(order);
        }
    }
    
    //передача готового заказа гостю
    private void deliverOrder(Order order) {
        order.setStatus(Order.OrderStatus.DELIVERED);
        
        //имитация времени отдачи
        try {
            Thread.sleep(Config.DELIVERY_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        //гость получает заказ
        order.getCustomer().receiveOrder(order);
        logger.info("Официант #" + id + " отдал заказ #" + order.getId() + 
                   " гостю " + order.getCustomer());
    }
    
    //остановка работы официанта
    public void stopWorking() {
        this.isWorking = false;
        logger.info("Официант #" + id + " завершает работу");
    }
}