package cn.jants.plugin.mongo;

import cn.jants.common.bean.Log;
import cn.jants.core.ext.Plugin;
import cn.jants.core.module.ServiceManager;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * @author MrShun
 * @version 1.0
 */
public class MongoDbPlugin implements Plugin {

    private String uri;

    private MongoClient mongoClient;

    private MongoDbTpl mongoDbTpl;

    public MongoDbPlugin(String uri) {
        this.uri = uri;
    }

    @Override
    public boolean start() throws Exception {
        try {
            MongoClientURI mongoClientURI = new MongoClientURI(uri);
            mongoClient = new MongoClient(mongoClientURI);
            String database = mongoClientURI.getDatabase();
            DB db = mongoClient.getDB(database);

            db.collectionExists(database);
            Log.debug("MongoDb 连接成功... ");

            //初始化MongoDbTpl
            mongoDbTpl = new MongoDbTpl(db);
            ServiceManager.setService("plugin_mongo_MongoDbTpl", mongoDbTpl);
            return true;
        } catch (Exception e) {
            Log.error("MongoDb连接失败, 请认真检查配置 ... ", e.getMessage());
            throw new Exception(e);
        }
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDbTpl getMongoDbTpl() {
        return mongoDbTpl;
    }

    @Override
    public boolean destroy() {
        mongoClient.close();
        return true;
    }
}
