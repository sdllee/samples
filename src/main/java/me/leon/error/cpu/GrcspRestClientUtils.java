/**
 * 
 */
package me.leon.error.cpu;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpClientConfig.Builder;
import com.ning.http.client.providers.netty.NettyAsyncHttpProvider;
import org.apache.commons.lang.StringUtils;
import org.apache.wink.client.AsyncHttpClientConfiguration;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.httpclient.ApacheHttpClientConfig;

import javax.ws.rs.core.MediaType;

/**
 * 将流程引擎wink 客户端调用抽出来，方便进行适配
 * 
 * @see com.smartdot.indiplatform.processengine.services.client.ServiceFactory
 *      主要在该类调用
 * @author huangxz
 *
 */
public class GrcspRestClientUtils {

  private static RestClient client = null;

  private static boolean isAsyncRestClient = true;

  private static Builder clientBuilder = new AsyncHttpClientConfig.Builder();

  private static final int maxConnection = 10000;
  private static final int connectionTimeoutInMs = 60000;
  private static final int requestTimeoutInMs = 60000;
  private static final int readTimeout = 60000;

  static {
    clientBuilder.setMaximumConnectionsTotal(maxConnection);
    clientBuilder.setConnectionTimeoutInMs(connectionTimeoutInMs);
    clientBuilder.setRequestTimeoutInMs(requestTimeoutInMs);
  }

  /**
   * 统一入口,默认的是异步wink Rest Client
   * 
   * @return
   */
  public static RestClient getRestClient(String appKey) {

    if (client != null) {
      return client;
    }

    if (isAsyncRestClient) {
      return getAsyncHttpRestClient(appKey);
    } else {
      return getHttpRestClient();
    }

  }

  public static RestClient getRestClient() {

//    if (client != null) {
//      return client;
//    }

    if (isAsyncRestClient) {
      return getAsyncHttpRestClient();
    } else {
      return getHttpRestClient();
    }

  }

  /**
   * 20160122以前版本
   * 
   * @return
   */
  private static RestClient getHttpRestClient() {

    ApacheHttpClientConfig config = new ApacheHttpClientConfig();
    config.connectTimeout(connectionTimeoutInMs);
    config.readTimeout(requestTimeoutInMs);
    config.setMaxPooledConnections(maxConnection);
    config.setChunked(false);

    client = new RestClient(config);
    return client;
  }

  /**
   * 20160122以后版本增加 wink 异步调用模式
   * 
   * @return
   */
  private static RestClient getAsyncHttpRestClient() {

    NettyAsyncHttpProvider nettyAsyncHttpProvider = new NettyAsyncHttpProvider(clientBuilder.build());
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient(nettyAsyncHttpProvider);

    AsyncHttpClientConfiguration config = new AsyncHttpClientConfiguration(asyncHttpClient);
    config.setBypassHostnameVerification(false);
    config.connectTimeout(connectionTimeoutInMs);
    config.readTimeout(readTimeout);

    client = new RestClient(config);

    return client;
  }

  private static RestClient getAsyncHttpRestClient(String appKey) {

    NettyAsyncHttpProvider nettyAsyncHttpProvider = new NettyAsyncHttpProvider(clientBuilder.build());
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient(nettyAsyncHttpProvider);

    AsyncHttpClientConfiguration config = new AsyncHttpClientConfiguration(asyncHttpClient);
    config.setBypassHostnameVerification(true);
    config.connectTimeout(connectionTimeoutInMs);
    config.readTimeout(readTimeout);

    if (StringUtils.isNotBlank(appKey) && !StringUtils.equals(appKey, "default")) {
      //config.handlers(new RestClientHandler("", "", appKey));
    }

    client = new RestClient(config);

    return client;
  }

  public static void main(String[] args) {
      //-Xms2G -Xmx2G -Xmn1G -XX:SurvivorRatio=8 -verbose:gc -XX:+PrintGCTimeStamps -Xloggc:/tmp/gc.log -XX:+PrintGCDetails
      //分配不同的jvm参数，观察
      //分配8G内存时，报错无法创建更多的native thread
      //分配2G内存时，报错java.lang.OutOfMemoryError: Direct buffer memory
      while (true) {
          RestClient restClient = GrcspRestClientUtils.getRestClient();
          Resource resource = restClient.resource("http://ptsjapp1.oa.com.cn:18080/oa-webapp/rest/userResource/user/2117267756196843515")
                  .accept(MediaType.APPLICATION_JSON);
          String content = resource.get(String.class);
          //System.out.println(content);
      }
  }
}