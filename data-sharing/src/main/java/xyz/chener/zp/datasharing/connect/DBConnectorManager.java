package xyz.chener.zp.datasharing.connect;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import xyz.chener.zp.datasharing.connect.entity.DataSourceStruce;
import xyz.chener.zp.datasharing.entity.DsDatasource;
import xyz.chener.zp.datasharing.service.DsDatasourceService;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class DBConnectorManager implements CommandLineRunner {

    public static final ConcurrentHashMap<Integer, DataSourceStruce> dbConnectorMap = new ConcurrentHashMap<>();

    public static final ReentrantLock lock = new ReentrantLock();

    private final DsDatasourceService dsDatasourceService;

    public DBConnectorManager(DsDatasourceService dsDatasourceService) {
        this.dsDatasourceService = dsDatasourceService;
    }

    public void flushDataSource(){
        List<DsDatasource> list = dsDatasourceService.list();
        lock.lock();
        try {
            dbConnectorMap.keySet().stream().filter(e->
                    list.stream().noneMatch(e1->e1.getId().equals(e)))
                    .forEach(this::closeDataSource);
            list.stream().filter(e-> !dbConnectorMap.containsKey(e.getId())).forEach(e->{
                DBConnector connector = DBConnector.chooseConnector(e.getType());
                if (connector == null){
                    log.error("不支持的数据源类型:{},{}",e.getId(),e.getType());
                    return;
                }
                DataSourceStruce dataSource = connector.getDataSource(e.getHost(), e.getPort(), e.getParamUrl(), e.getDatabaseName(), e.getUsername(), e.getPassword());
                dbConnectorMap.put(e.getId(),dataSource);
            });
        }
        finally {
            lock.unlock();
        }
    }

    public void initDataSource(){
        lock.lock();
        try {
            dbConnectorMap.forEach((k,v)->{
                closeDataSource(k);
            });
            List<DsDatasource> dsList = dsDatasourceService.list();
            dsList.forEach(e->{
                DBConnector connector = DBConnector.chooseConnector(e.getType());
                if (connector == null){
                    log.error("不支持的数据源类型:{},{}",e.getId(),e.getType());
                    return;
                }
                DataSourceStruce dataSource = connector.getDataSource(e.getHost(), e.getPort(), e.getParamUrl(), e.getDatabaseName(), e.getUsername(), e.getPassword());
                dbConnectorMap.put(e.getId(),dataSource);
            });
        }
        finally {
            lock.unlock();
        }

    }

    public void closeDataSource(Integer key){
        DataSource ds = dbConnectorMap.remove(key).getDataSource();
        if (ds instanceof Closeable ds0){
            try {
                ds0.close();
            } catch (IOException ignored) { }
        }
    }

    public DataSourceStruce getDataSource(Integer key){
        DataSourceStruce dataSourceStruce = dbConnectorMap.get(key);
        if (dataSourceStruce == null){
            DsDatasource dsd = dsDatasourceService.lambdaQuery().eq(DsDatasource::getId, key).one();
            lock.lock();
            try {
                if (dsd == null){
                    return null;
                }
                DBConnector connector = DBConnector.chooseConnector(dsd.getType());
                if (connector == null){
                    return null;
                }
                dataSourceStruce = connector.getDataSource(dsd.getHost(), dsd.getPort(), dsd.getParamUrl(), dsd.getDatabaseName(), dsd.getUsername(), dsd.getPassword());
                dbConnectorMap.put(key,dataSourceStruce);
            }finally {
                lock.unlock();
            }
        }
        return dataSourceStruce;
    }

    @Override
    public void run(String... args) throws Exception {
        initDataSource();
    }
}
