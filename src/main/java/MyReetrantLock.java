import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyReetrantLock {
    /**
     * 用于标记那个线程进行执行
     * 1:主线程
     * 2:线程2
     * 3:线程3
     */
    private static int HASOUT = 1;
    public static void go() throws InterruptedException
    {
        final Core core = new Core();
        //子线程2
        new Thread(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int i = 1; i <= 2; i++)
                        {
                            core.SubMethod2(i);
                        }
                    }
                }
        ).start();

        //子线程3
        new Thread(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int i = 1; i <= 2; i++)
                        {
                            core.SubMethod3(i);
                        }
                    }
                }
        ).start();

        //主线程
        for (int i = 1; i <= 2; i++)
        {
            core.MainMethod(i);
        }
    }

    /**
     * 创建一个静态的类
     * 将核心的业务逻辑的方法放在这里
     * 体现了高内聚的特点
     */
    static class Core{
        //创建一个Lock锁对象
        public Lock lock = new ReentrantLock();
        //创建三个Condition对象 分别用于控制三个线程
        public Condition condition1 = lock.newCondition();
        public Condition condition2 = lock.newCondition();
        public Condition condition3 = lock.newCondition();
        //线程2   循环输出10次
        public void SubMethod2(int j){
            lock.lock();//开启Lock锁
            try{
                //true 执行
                //false 等待
                while(HASOUT!=2){
                    try
                    {
                        condition2.await();//线程2等待
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                //线程2执行
                for (int i = 1; i <= 2; i++)
                {
                    System.out.println("this is sub2 thread..."+i+"......."+j);
                }
                HASOUT = 3;
                condition3.signal();//唤醒线程3
            }finally{
                lock.unlock();//释放锁
            }
        }

        //线程3   循环输出20次
        public void SubMethod3(int j){
            lock.lock();//开启Lock锁
            try{
                //true 执行
                //false 等待
                while(HASOUT!=3){
                    try
                    {
                        condition3.await();//线程3等待
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                //线程3执行
                for (int i = 1; i <= 2; i++)
                {
                    System.out.println("this is sub3 thread..."+i+"......."+j);
                }
                HASOUT = 1;
                condition1.signal();//唤醒线程1(主线程)
            }finally{
                lock.unlock();//释放锁
            }
        }

        //主线程调用循环输出一百次
        public void MainMethod(int j){
            lock.lock();
            try{
                //false 执行
                //true 等待
                while(HASOUT!=1){
                    try
                    {
                        condition1.await();//主线程等待
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                //执行主线程
                for (int i = 1; i <= 2; i++)
                {
                    System.out.println("this is main thread..."+i+"......"+j);
                }
                HASOUT = 2;
                condition2.signal();//唤醒线程2
            }finally{
                lock.unlock();
            }
        }
    }
}
