package me.leon.samples.redis;


import me.leon.samples.utils.DateUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class EventNumSupplier {


    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;

    /**
     * 获取案件编号，格式 yyyy-MM-dd5位流水号
     * @return
     */
    public String getEventNum() {
        String ymd = DateUtils.formatToYmd(System.currentTimeMillis());
        long num = redisTemplate.opsForValue().increment("eventNum:" + ymd, 1);
        return ymd + (100000 + num + "").substring(1);
    }

}
