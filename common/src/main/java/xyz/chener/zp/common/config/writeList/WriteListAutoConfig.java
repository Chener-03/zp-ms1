package xyz.chener.zp.common.config.writeList;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.common.entity.WriteList;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;


@Configuration
public class WriteListAutoConfig implements EnvironmentAware, BeanDefinitionRegistryPostProcessor {

    private String contextPath = null;

    public static List<String> writeList = Collections.EMPTY_LIST;

    @Override
    public void setEnvironment(Environment environment) {
        contextPath = environment.getProperty("server.context-path");
    }



    public void setWriteList(List<Class<?>> classes) {
        ArrayList<String> write = new ArrayList<>();
        classes.forEach(e->{
            WriteList ann = e.getAnnotation(WriteList.class);
            if (ann != null) {
                for (Method method : e.getMethods()) {
                    write.addAll(Arrays.asList(getMethodRestPath(method, e)));
                }
            }else
            {
                for (Method method : e.getMethods()) {
                    WriteList an = method.getAnnotation(WriteList.class);
                    if (an != null) {
                        write.addAll(Arrays.asList(getMethodRestPath(method, e)));
                    }
                }
            }
        });
        writeList = Collections.unmodifiableList(write);
    }

    private String[] getMethodRestPath(Method method,Class<?> clazz){
        RequestMapping ann = clazz.getAnnotation(RequestMapping.class);
        String parentPath = null;
        if (ann != null) {
            parentPath = ann.value()[0];
        }
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        PatchMapping patchMapping = method.getAnnotation(PatchMapping.class);
        if (requestMapping != null) {
            return getPaths(parentPath, requestMapping.value());
        }else if (getMapping != null) {
            return getPaths(parentPath, getMapping.value());
        }else if (postMapping != null) {
            return getPaths(parentPath, postMapping.value());
        }else if (putMapping != null) {
            return getPaths(parentPath, putMapping.value());
        }else if (deleteMapping != null) {
            return getPaths(parentPath, deleteMapping.value());
        }else if (patchMapping != null) {
            return getPaths(parentPath, patchMapping.value());
        }
        return new String[0];
    }

    private String[] getPaths(String parentPath, String[] values) {
        String[] paths = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            paths[i] = (contextPath==null?"":contextPath )+ (parentPath ==null?"": parentPath)+ values[i];
        }
        return paths;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        List<Class<?>> classes = Arrays.stream(registry.getBeanDefinitionNames()).filter(e -> {
            try {
                Class<?> clazz = Class.forName(registry.getBeanDefinition(e).getBeanClassName());
                if (clazz.getAnnotation(RestController.class) != null || clazz.getAnnotation(Controller.class) != null) {
                    return true;
                }
            } catch (Exception ignored) {
            }
            return false;
        }).map((Function<String, Class<?>>) s -> {
            try {
                return Class.forName(registry.getBeanDefinition(s).getBeanClassName());
            } catch (ClassNotFoundException ignored) {
            }
            return null;
        }).filter(Objects::nonNull).toList();
        setWriteList(classes);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
