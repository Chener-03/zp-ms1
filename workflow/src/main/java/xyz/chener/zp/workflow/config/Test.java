package xyz.chener.zp.workflow.config;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;

/**
 * @Author: chenzp
 * @Date: 2023/06/19/10:36
 * @Email: chen@chener.xyz
 */


@Component
public class Test implements CommandLineRunner {

    @Autowired
    ProcessEngine processEngine;

    @Override
    public void run(String... args) throws Exception {
/*        InputStream is = this.getClass().getResourceAsStream("/test.bpmn20.xml");
        Deployment test = processEngine.getRepositoryService().createDeployment()
                .addInputStream("test.bpmn", is)
                .name("test")
                .deploy();*/

        HashMap<Object, Object> m = new HashMap<>();
        m.put("var","AA");


        System.out.println();
    }
}
