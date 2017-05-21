import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTest {

	public static void main(String[] args) {

		ExecutorService pool = Executors.newFixedThreadPool(10);
		
		//newCachedThreadPool
//		new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
//				new SynchronousQueue<Runnable>());
		
		LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(5);
		//newFiexThreadPool
		ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10,
                0L, TimeUnit.MILLISECONDS,
                queue);
		for(int i = 0;i<15;i++){
			final int j = i;
			System.out.println(queue.size());
			executor.execute(new Runnable() {
				@Override
				public void run() {
					System.out.println("i="+j+",queue="+queue.size());
					try {
						Thread.sleep(1000000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		System.out.println("-------------------");
		executor.execute(new SleepThread(15,queue));
		System.out.println(queue.size());
		executor.execute(new SleepThread(16,queue));
		System.out.println(queue.size());
		executor.execute(new SleepThread(17,queue));
		System.out.println(queue.size());
		executor.execute(new SleepThread(18,queue));
		System.out.println(queue.size());
		executor.execute(new SleepThread(19,queue));
		System.out.println(queue.size());
	}
	
	static class SleepThread implements Runnable{
		private String threadName;
		BlockingQueue<Runnable> queue;
		
		public SleepThread(int count,BlockingQueue<Runnable> queue) {
			threadName = "thread "+count;
			this.queue = queue;
		}
		
		@Override
		public void run() {
			System.out.println(threadName+" sleep....."+",queue="+queue.size());
			try {
				Thread.sleep(1000000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
