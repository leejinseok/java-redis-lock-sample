package coml.example.redislock;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BaseConfig {

    public static ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(32, 200, 0L, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>());
    }

}
