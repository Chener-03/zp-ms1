package xyz.chener.zp.datasharing.connect;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
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
public class DBConnectorManager implements CommandLineRunner, DisposableBean {

    public static final ConcurrentHashMap<Integer, DataSourceStruce> dbConnectorMap = new ConcurrentHashMap<>();

    public static final ReentrantLock lock = new ReentrantLock();

    private final DsDatasourceService dsDatasourceService;

    public DBConnectorManager(DsDatasourceService dsDatasourceService) {
        this.dsDatasourceService = dsDatasourceService;
    }

    public void flushDataSource(){
        initDataSource();
    }

    public void flushDataSource(Integer id){
        lock.lock();
        try {
            if (dbConnectorMap.get(id)!=null) {
                closeDataSource(id);
            }
            DsDatasource e = dsDatasourceService.getById(id);
            if (e != null){
                try {
                    DBConnector connector = DBConnector.chooseConnector(e.getType());
                    if (connector == null){
                        log.error("不支持的数据源类型:{},{}",e.getId(),e.getType());
                        return;
                    }
                    DataSourceStruce dataSource = connector.getDataSource(e.getHost(), e.getPort(), e.getParamUrl(), e.getDatabaseName(), e.getUsername(), e.getPassword());
                    dbConnectorMap.put(e.getId(),dataSource);
                }catch (Exception exception){
                    log.error("初始化数据源失败,数据源ID{},ERROR:{}",e.getId(),exception.getMessage());
                }
            }
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
                try {
                    DBConnector connector = DBConnector.chooseConnector(e.getType());
                    if (connector == null){
                        log.error("不支持的数据源类型:{},{}",e.getId(),e.getType());
                        return;
                    }
                    DataSourceStruce dataSource = connector.getDataSource(e.getHost(), e.getPort(), e.getParamUrl(), e.getDatabaseName(), e.getUsername(), e.getPassword());
                    dbConnectorMap.put(e.getId(),dataSource);
                }catch (Exception exception){
                    log.error("初始化数据源失败,数据源ID{},ERROR:{}",e.getId(),exception.getMessage());
                }
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
            } catch (Exception ignored) { }
        }
    }

    public DataSourceStruce getDataSource(Integer key){
        DataSourceStruce dataSourceStruce = dbConnectorMap.get(key);
        if (dataSourceStruce == null){
            DsDatasource dsd = dsDatasourceService.lambdaQuery().eq(DsDatasource::getId, key).one();
            lock.lock();
            try {
                if (dbConnectorMap.get(key)==null){
                    if (dsd == null){
                        return null;
                    }
                    DBConnector connector = DBConnector.chooseConnector(dsd.getType());
                    if (connector == null){
                        return null;
                    }
                    dataSourceStruce = connector.getDataSource(dsd.getHost(), dsd.getPort(), dsd.getParamUrl(), dsd.getDatabaseName(), dsd.getUsername(), dsd.getPassword());
                    dbConnectorMap.put(key,dataSourceStruce);
                }else {
                    dataSourceStruce = dbConnectorMap.get(key);
                }
            }finally {
                lock.unlock();
            }
        }
        return dataSourceStruce;
    }

    public Boolean getConnectHealthy(Integer id){
        DataSourceStruce dataSourceStruce = dbConnectorMap.get(id);
        if (dataSourceStruce == null){
            return false;
        }
        DataSource ds = dataSourceStruce.getDataSource();
        if (ds instanceof HikariDataSource ds0){
            return ds0.isRunning();
        }
        return false;
    }

    @Override
    public void run(String... args) throws Exception {
        initDataSource();
    }

    @Override
    public void destroy() throws Exception {
        dbConnectorMap.forEach((k,v)->{
            closeDataSource(k);
        });
    }
}
