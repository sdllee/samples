package me.leon.samples.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class ZkClient {

    private String zkHosts = "127.0.0.1:2181";
    private CuratorFramework client;

    public ZkClient() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(zkHosts, retryPolicy);
        client.start();
    }

    public ZkClient(String zkHosts) {
        if (zkHosts != null) {
            this.zkHosts = zkHosts;
        }
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(this.zkHosts, retryPolicy);
        client.start();
    }

    public void setZkHosts(String zkHosts) {
        this.zkHosts = zkHosts;
    }

    /**
     * 获取client，先这么写，后续弄懂了再优化
     *
     * @return
     */
    public CuratorFramework getClient() {
        return client;
    }

    public CuratorFramework getNewClient() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkHosts, retryPolicy);
        client.start();
        return client;
    }

    /**
     * 关闭连接
     *
     * @param client
     */
    public void close(CuratorFramework client) {
        if (client == this.client) {
            //nothing
        } else {
            client.close();
        }
    }


    /**
     * 创建节点
     *
     * @param path       路径
     * @param createMode 节点类型
     * @param data       节点数据
     * @return 是否创建成功
     */
    public boolean crateNode(CuratorFramework client, String path, CreateMode createMode, String data) {
        try {
            client.create().withMode(createMode).forPath(path, data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 创建节点
     *
     * @param path       路径
     * @param createMode 节点类型
     * @param data       节点数据
     * @return 是否创建成功
     */
    public boolean crateNode(String path, CreateMode createMode, String data) {
        return this.crateNode(client, path, createMode, data);
    }

    /**
     * 删除节点
     *
     * @param path 路径
     * @return 删除结果
     */
    public boolean deleteNode(CuratorFramework client, String path) {
        try {
            client.delete().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    /**
     * 删除节点
     *
     * @param path 路径
     * @return 删除结果
     */
    public boolean deleteNode(String path) {
        return this.deleteNode(client, path);
    }

    /**
     * 删除一个节点，并且递归删除其所有的子节点
     *
     * @param path 路径
     * @return 删除结果
     */
    public boolean deleteChildrenIfNeededNode(CuratorFramework client, String path) {
        try {
            client.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    /**
     * 删除一个节点，并且递归删除其所有的子节点
     *
     * @param path 路径
     * @return 删除结果
     */
    public boolean deleteChildrenIfNeededNode(String path) {
        return this.deleteChildrenIfNeededNode(client, path);
    }

    /**
     * 判断节点是否存在
     *
     * @param path 路径
     * @return true-存在  false-不存在
     */
    public boolean isExistNode(CuratorFramework client, String path) {
        try {
            Stat stat = client.checkExists().forPath(path);

            return stat != null ? true : false;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * 判断节点是否存在
     *
     * @param path 路径
     * @return true-存在  false-不存在
     */
    public boolean isExistNode(String path) {
        return this.isExistNode(client, path);
    }

    /**
     * 判断节点是否是持久化节点
     *
     * @param path 路径
     * @return 2-节点不存在  | 1-是持久化 | 0-临时节点
     */
    public int isPersistentNode(String path) {
        try {
            Stat stat = client.checkExists().forPath(path);

            if (stat == null) {
                return 2;
            }

            if (stat.getEphemeralOwner() > 0) {
                return 1;
            }

            return 0;
        } catch (Exception e) {
            e.printStackTrace();

            return 2;
        }
    }

    /**
     * 获取节点数据
     *
     * @param path 路径
     * @return 节点数据，如果出现异常，返回null
     */
    public String getNodeData(CuratorFramework client, String path) {

        try {
            byte[] bytes = client.getData().forPath(path);
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setNodeData(CuratorFramework client, String path, String data) {
        try {
            this.client.setData().forPath(path, data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册节点数据变化事件
     *
     * @param path              节点路径
     * @param nodeCacheListener 监听事件
     * @return 注册结果
     */
    public boolean registerWatcherNodeChanged(CuratorFramework client, String path, NodeCacheListener nodeCacheListener) {
        NodeCache nodeCache = new NodeCache(client, path, false);
        try {
            nodeCache.getListenable().addListener(nodeCacheListener);

            nodeCache.start(true);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * 更新节点数据
     *
     * @param path     路径
     * @param newValue 新的值
     * @return 更新结果
     */
    public boolean updateNodeData(CuratorFramework client, String path, String newValue) {
        //判断节点是否存在
        if (!isExistNode(path)) {
            return false;
        }

        try {
            client.setData().forPath(path, newValue.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
