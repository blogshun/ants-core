package cn.jants.core.handler;

import cn.jants.common.bean.Log;
import cn.jants.common.enums.ResponseCode;
import cn.jants.common.utils.StrUtil;
import cn.jants.core.ext.Handler;
import cn.jants.restful.render.Json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 屏蔽快速刷新
 *
 * @author MrShun
 * @version 1.0
 */
public class LimitRefreshHandler implements Handler {

    protected ConcurrentMap<String, ClientRequest> tokens = new ConcurrentHashMap<>();

    /**
     * 需要拦截的路径
     */
    private String[] apis = null;

    /**
     * 快速刷新时间设置
     */
    private long time = 1000;

    /**
     * 设置多长时间清空一次缓存, 默认2小时
     */
    private long clearTime = 1000 * 60 * 60 * 2;

    /**
     * 设置判断大于多少次为恶意刷新
     */
    private long count = 50;

    /**
     * 初始化定时任务
     */
    private ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

    public LimitRefreshHandler(String... apis) {
        init(0, 0, 0, apis);
    }

    public LimitRefreshHandler(long time, String... apis) {
        init(0, time, 0, apis);
    }

    public LimitRefreshHandler(long clearTime, long time, String... apis) {
        init(clearTime, time, 0, apis);
    }

    public LimitRefreshHandler(long clearTime, long time, long count, String... apis) {
        init(clearTime, time, count, apis);
    }

    private void init(long cTime, long time, long count, String... apis) {
        try {
            this.apis = apis;
            if (time != 0) {
                this.time = time;
            }
            if (count != 0) {
                this.count = count;
            }
            if (cTime != 0) {
                this.clearTime = cTime;
            }
            //设置2小时清空一次
            exec.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    Log.debug("limit refresh map is clear!");
                }
            }, 0, clearTime, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException("防止快速刷新配有误!" + e.getMessage());
        }
    }


    @Override
    public boolean preHandler(String target, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (apis != null) {
            String sessionId = request.getSession().getId();
            ClientRequest client = tokens.get(sessionId);
            if (client != null && !client.getCheck()) {
                writeBlackList(response);
                return false;
            }
            for (String api : apis) {
                if (target.startsWith(api)) {
                    if (tokens.containsKey(sessionId)) {
                        if (client.getUrl().equals(target)) {
                            long lastTime = client.getLastTime();
                            long currentTime = System.currentTimeMillis() - lastTime;
                            if (client.getCount() > count) {
                                //大于多少次记录到IP黑名单
                                client.setCheck(false);
                                tokens.put(sessionId, client);
                                writeBlackList(response);
                                return false;
                            } else if (currentTime > time) {
                                client.setLastTime(System.currentTimeMillis());
                                tokens.put(sessionId, client);
                                return true;
                            } else {
                                client.setLastTime(System.currentTimeMillis());
                                client.setCount(client.getCount() + 1);
                                tokens.put(sessionId, client);
                                //可能会出现一个页面出现几次相同接口,添加_refresh则可以绕过校验
                                String queryStr = request.getQueryString();
                                if (StrUtil.notBlank(queryStr) && queryStr.indexOf("_refresh") != -1) {
                                    return true;
                                } else {
                                    try {
                                        response.setContentType("application/json;charset=utf-8");
                                        PrintWriter w = response.getWriter();
                                        w.print(Json.exception(ResponseCode.REFRESH_INFO));
                                        w.close();
                                        return false;
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else {
                            client.setCheck(client.getCheck());
                            client.setLastTime(System.currentTimeMillis());
                            client.setUrl(target);
                            client.setCount(0L);
                            tokens.put(sessionId, client);
                            return true;
                        }

                    } else {
                        tokens.put(sessionId, new ClientRequest(request.getRemoteAddr(), target, System.currentTimeMillis(), 0L, true));
                        return true;
                    }
                    break;
                }
            }
            return true;
        } else {
            return true;
        }
    }


    /**
     * 设置黑名单
     *
     * @param response
     * @throws IOException
     */
    private void writeBlackList(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter w = response.getWriter();
        w.print("已经被拉黑到了黑名单!");
        w.close();
    }

    /**
     * 客户端信息
     */
    private class ClientRequest {

        private String ip;

        private String url;

        private Long lastTime;

        private Long count;

        private Boolean isCheck;

        public ClientRequest(String ip, String url, Long lastTime, Long count, Boolean isCheck) {
            this.ip = ip;
            this.url = url;
            this.lastTime = lastTime;
            this.count = count;
            this.isCheck = isCheck;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Long getLastTime() {
            return lastTime;
        }

        public void setLastTime(Long lastTime) {
            this.lastTime = lastTime;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }

        public Boolean getCheck() {
            return isCheck;
        }

        public void setCheck(Boolean check) {
            isCheck = check;
        }
    }
}
