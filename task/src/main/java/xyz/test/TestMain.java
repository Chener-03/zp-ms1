package xyz.test;


import xyz.chener.zp.task.core.TaskHandler;

public class TestMain {
    public static void main(String[] args) {
    }

    public static class AAAA implements TaskHandler {

        @Override
        public Object handle( Object task) {
            System.out.println("6666");
            return null;
        }
    }

}
