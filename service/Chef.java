package service;

import model.*;
import util.Logger;
import java.util.concurrent.BlockingQueue;

//класс повара
public class Chef implements Runnable {
    private final int id;                          //id повара
    private final BlockingQueue<Order> orderQueue; //очередь заказов на приготовление
    private final BlockingQueue<Order> readyQueue; //очередь готовых заказов
    private final Logger logger;                   //логгер для записи событий
    private volatile boolean isCooking = true;     //флаг для работы повара
    
    public Chef(int id, BlockingQueue<Order> orderQueue, BlockingQueue<Order> readyQueue) {
        this.id = id;
        this.orderQueue = orderQueue;
        this.readyQueue = readyQueue;
        this.logger = Logger.getInstance();
        
        Thread.currentThread().setName("Повар #" + id);
        logger.info("Повар #" + id + " начал работу");
    }
    
    @Override
    public void run() {
        while (isCooking || !orderQueue.isEmpty()) {
            try {
                //берем заказ из очереди (блокирующий вызов)
                Order order = orderQueue.take();
                
                if (order.getStatus() == Order.OrderStatus.COOKING) {
                    cookOrder(order);
                }
                
            } catch (InterruptedException e) {
                logger.warning("Повар #" + id + " был прерван");
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        logger.info("Повар #" + id + " завершил работу");
    }
    
    //приготовление заказа
    private void cookOrder(Order order) throws InterruptedException {
        Dish dish = order.getDish();
        
        logger.info("Повар #" + id + " начал готовить заказ #" + order.getId() + 
                   ": " + dish.getName() + " (время: " + dish.getPreparationTime() + " мс)");
        
        //имитация времени приготовления блюда
        Thread.sleep(dish.getPreparationTime());
        
        order.setStatus(Order.OrderStatus.READY);
        readyQueue.put(order);
        
        logger.info("Повар #" + id + " приготовил заказ #" + order.getId() + 
                   ": " + dish.getName() + " готов к подаче");
    }
    
    public void stopCooking() {
        this.isCooking = false;
        logger.info("Повар #" + id + " завершает работу");
    }
}