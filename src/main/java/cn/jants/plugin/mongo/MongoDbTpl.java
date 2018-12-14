package cn.jants.plugin.mongo;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 */
public class MongoDbTpl {

    private DB db;



    public DB getDB() {
        return this.db;
    }

    public MongoDbTpl(DB db) {
        this.db = db;
    }

    /**
     * 查询多条MongoDb记录
     *
     * @param conditions     条件对象
     * @param collectionName 集合名称
     */
    public List<DBObject> findList(DBObject conditions, String collectionName) {
        DBCollection table = db.getCollection(collectionName);
        DBCursor cursor = table.find(conditions);
        List<DBObject> result = new ArrayList<>(cursor.size());
        while (cursor.hasNext()) {
            result.add(cursor.next());
        }
        return result;
    }


    /**
     * 查询一条MongoDb记录
     *
     * @param conditions     条件对象
     * @param collectionName 集合名称
     */
    public DBObject findOne(DBObject conditions, String collectionName) {
        DBCollection table = db.getCollection(collectionName);
        return table.findOne(conditions);
    }

    /**
     * 查询删除MongoDb记录
     *
     * @param conditions     条件对象
     * @param collectionName 集合名称
     */
    public void remove(DBObject conditions, String collectionName) {
        DBCollection table = db.getCollection(collectionName);
        table.remove(conditions);
    }

    /**
     * 保存多条MongoDb记录
     *
     * @param dbObject       Json对象集合
     * @param collectionName 集合名称
     */
    public void save(List<DBObject> dbObject, String collectionName) {
        DBCollection table = db.getCollection(collectionName);
        table.insert(dbObject);
    }

    /**
     * 保存一条MongoDb记录
     *
     * @param dbObject       Json对象集合
     * @param collectionName 集合名称
     */
    public void save(DBObject dbObject, String collectionName) {
        DBCollection table = db.getCollection(collectionName);
        table.insert(dbObject);
    }

    /**
     * 更新MongoDb记录
     *
     * @param conditions     条件
     * @param dbObject       更新数据
     * @param collectionName 集合名称
     */
    public void update(DBObject conditions, DBObject dbObject, String collectionName) {
        DBCollection table = db.getCollection(collectionName);
        table.update(conditions, dbObject);
    }
}
