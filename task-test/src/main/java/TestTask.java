import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.chener.zp.task.core.entity.TaskData;
import xyz.chener.zp.task.core.target.JarTaskExecute;
import xyz.chener.zp.task.core.target.TaskHandler;
import xyz.chener.zp.task.core.target.TaskLogger;

import java.lang.reflect.Method;

public class TestTask implements TaskHandler {

    @Override
    public String handle(@NotNull TaskData param, long batch) {

        TaskLogger.logError("123");
        TaskLogger.logInfo("456");

        return null;
    }

    @Override
    public long getTaskBatchSize(@NotNull TaskData param) {
        return TaskHandler.DefaultImpls.getTaskBatchSize(this, param);
    }


    public static void main(String[] args) {
// -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=12345 -Dcom.sun.management.jmxremote.rmi.port=12345 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=localhost

        JarTaskExecute.addShunDownHook(()->{
            System.out.println("End....");
        });

        JarTaskExecute.waitTaskFinish(new TestTask());
    }

}
