package me.leon.samples.zookeeper;

import me.leon.samples.utils.DateUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class EventNumZkSupplier {

    @Autowired
    private ZkClient zkClient;

    public void initData() {
        String ymd = DateUtils.formatToYmd(System.currentTimeMillis());
        String ymdPath = "/samples/"  + ymd;
        if (zkClient.isExistNode(ymdPath)) {
            zkClient.deleteNode(ymdPath);
        }
    }

    public String getEventNum(String clientName) {
        String ymd = DateUtils.formatToYmd(System.currentTimeMillis());
        String lockPath = "/samples/lock";
        String ymdPath = "/samples/"  + ymd;
        CuratorFramework client = zkClient.getClient();
        try {
            InterProcessMutex lock = new InterProcessMutex(client, lockPath);
            String eventNum = null;
            if (lock.acquire(3, TimeUnit.SECONDS)) {
                if (!zkClient.isExistNode(client, ymdPath)) {
                    zkClient.crateNode(client, ymdPath, CreateMode.PERSISTENT, "1");
                }
                try {
                    String data = zkClient.getNodeData(client, ymdPath);
                    int num = Integer.parseInt(data);
                    eventNum = ymd + (100000 + num + "").substring(1);
                    zkClient.setNodeData(client, ymdPath, (num + 1) + "");
                } finally {
                    //System.out.println(clientName + " releasing the lock");
                    lock.release(); // always release the lock in a finally block
                }
            }
            return eventNum;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            zkClient.close(client);
        }
        return null;
    }
}
