package xyz.chener.zp.common.config.mbp;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/02/08/16:58
 * @Email: chen@chener.xyz
 */

@Configuration
@ConditionalOnBean(SqlSessionFactory.class)
@AutoConfigureBefore(PageHelperAutoConfiguration.class)
public class MybatisInterceptorsConfig implements InitializingBean {

    private final List<SqlSessionFactory> sqlSessionFactoryList;

    public MybatisInterceptorsConfig(List<SqlSessionFactory> sqlSessionFactoryList) {
        this.sqlSessionFactoryList = sqlSessionFactoryList;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CustomFieldsQueryInterceptor interceptor = new CustomFieldsQueryInterceptor();
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
            if (!containsInterceptor(configuration, interceptor)) {
                configuration.addInterceptor(interceptor);
            }
        }
    }

    private boolean containsInterceptor(org.apache.ibatis.session.Configuration configuration, Interceptor interceptor) {
        try {
            return configuration.getInterceptors().stream().anyMatch(config->interceptor.getClass().isAssignableFrom(config.getClass()));
        } catch (Exception e) {
            return false;
        }
    }

}
