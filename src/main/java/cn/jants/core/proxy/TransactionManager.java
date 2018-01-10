package cn.jants.core.proxy;


import cn.jants.common.annotation.service.Tx;
import cn.jants.common.enums.DataSourceType;
import cn.jants.restful.bind.utils.ReflectionUtils;
import cn.jants.common.annotation.service.Source;
import cn.jants.common.enums.TxLevel;
import cn.jants.plugin.db.Db;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 */
public class TransactionManager {

    private List<Db> dbs;

    public TransactionManager(Object target, Method method) {
        //=================== 处理事物 ======================
        Tx methodTx = method.getAnnotation(Tx.class);
        Tx classTx = target.getClass().getAnnotation(Tx.class);

        if (methodTx != null || classTx != null) {
            Field[] fields = target.getClass().getDeclaredFields();
            dbs = new ArrayList<>();
            for (Field field : fields) {
                if (field.getType() == Db.class) {
                    Source source = field.getDeclaredAnnotation(Source.class);
                    TxLevel level = TxLevel.REPEATED_READ;
                    if (methodTx != null) {
                        level = methodTx.value();
                    } else if (classTx != null) {
                        level = classTx.value();
                    }
                    DataSource dataSource = DataSourceType.getDataSource(source);
                    try {
                        ReflectionUtils.makeAccessible(field);
                        Db db = (Db) field.get(target);
                        //开启事物
                        String sourceName = source.value();
                        String sourceType = "[".concat(String.valueOf(source.type())).concat("] 数据源名称：");
                        String tips = "".equals(sourceName) ? sourceType.concat("{默认} ") : sourceType.concat("{" + sourceName + "} ");
                        db.startTx(dataSource, tips, level);
                        dbs.add(db);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void commit() {
        if (dbs != null && dbs.size() != 0) {
            for (Db db : dbs) {
                try {
                    db.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void rollback() {
        if (dbs != null && dbs.size() != 0) {
            for (Db db : dbs) {
                db.rollback();
            }
        }
    }
}
