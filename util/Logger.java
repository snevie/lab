package util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

//класс для логирования событий системы ресторана
public class Logger {
    private static Logger instance;
    private static final ReentrantLock lock = new ReentrantLock();
    
    private final File logFile;
    private final SimpleDateFormat dateFormat;
    
    //конструктор для Singleton
    private Logger() {
        this.dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        
        //создание директории для логов, если ее еще нет
        File logsDir = new File("logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }
        
        //создание файла лога с timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new Date());
        this.logFile = new File(logsDir, "restaurant_" + timestamp + ".log");
        
        try {
            if (logFile.createNewFile()) {
                System.out.println("Файл лога создан: " + logFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Ошибка создания файла лога: " + e.getMessage());
        }
    }
    
    //получение экземпляра логгера (Singleton)
    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) {
                    instance = new Logger();
                }
            }
        }
        return instance;
    }
    
    //логирование сообщений разных уровней
    public void info(String message) {
        log("INFO", message);
    }
    
    public void warning(String message) {
        log("WARNING", message);
    }
    
    public void error(String message, Throwable throwable) {
        log("ERROR", message + " | Ошибка: " + throwable.getMessage());
    }
    
    //основной метод логирования
    private void log(String level, String message) {
        lock.lock();
        try {
            String timestamp = dateFormat.format(new Date());
            String threadName = Thread.currentThread().getName();
            String logMessage = String.format("[%s] [%s] [%s] %s",
                    timestamp, level, threadName, message);
            
            System.out.println(logMessage);
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
                writer.println(logMessage);
            } catch (IOException e) {
                System.err.println("Ошибка записи в файл лога: " + e.getMessage());
            }
        } finally {
            lock.unlock();
        }
    }
    
    //логирование начала работы системы
    public void logSystemStart() {
        info("-".repeat(60));
        info("РЕСТОРАН ЗАПУЩЕН");
        info("Официантов: " + Config.WAITERS_COUNT);
        info("Поваров: " + Config.CHEFS_COUNT);
        info("Размер очереди заказов: " + Config.ORDER_QUEUE_SIZE);
        info("Время работы: " + Config.RESTAURANT_WORK_TIME + " мс");
        info("-".repeat(60));
    }
    
    //логирование завершения работы системы
    public void logSystemShutdown() {
        info("-".repeat(60));
        info("РЕСТОРАН ОСТАНОВЛЕН");
        info("-".repeat(60));
    }
}