package edu.ecnu.ica.scheduler;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;

/**
 * Created by dianping on 16/9/13.
 */
public class JobScheduler extends Thread{
    private static Logger logger = Logger.getLogger(JobScheduler.class);

    private String schedulerName;
    private volatile boolean isRunning = false;
    private int runningRounds = 0;
    private ConcurrentLinkedQueue<Job> jobs = new ConcurrentLinkedQueue<>();
    private DelayQueue<Job>      delayQueue = new DelayQueue<>();
    private final String           birthday = new SimpleDateFormat().format(new Date());

    public JobScheduler(String name) {
        this.schedulerName = name;
    }

    public Job addJob(String name, long intervalMillis, JobMethod method, boolean isOneTimeJob) throws RuntimeException {
        if(isRunning) {
            logger.error("Illegal time point for addJob(), scheduler {" + this.schedulerName + "} is running.");
            throw new RuntimeException("Scheduler: " + this.schedulerName + " is running, can't add a new job.");
        }
        Job newJob = new Job(this, name, intervalMillis, method);
        if(isOneTimeJob) {
            newJob.setOneTimeJob(true);
            newJob.setNextTimestampInMillis();
        }
        this.jobs.add(newJob);
        this.delayQueue.offer(newJob);
        return newJob;
    }

    public Job addJob(String name, long intervalMillis, JobMethod method) throws RuntimeException {
        return addJob(name, intervalMillis, method, false);
    }

    public Job addOneTimeJob(String name, long intervalMillis, JobMethod method) throws RuntimeException {
        return addJob(name, intervalMillis, method, true);
    }

    @Override
    public void run() {
        this.isRunning = true;
        while(true) {
            Job job = null;
            boolean runAgain = true;
            try {
                job = delayQueue.take();
                if (job.isCanceled()) {
                    runAgain = false;
                    logger.info(job.getName() + " is cancelled");
                } else {
                    job.execute();
                    logger.info("after");
                }
                runAgain = !job.isOneTimeJob() && runAgain;
            } catch (Throwable t) {
                logger.error(String.format("%s Unexpected Exception when running jobs. Ignore this time. ", this.getName()), t);
            } finally {
                if (runAgain) {
                    assert job != null;
                    job.setNextTimestampInMillis();
                    delayQueue.offer(job);
                } else {
                    jobs.remove(job);
                }
            }
        }
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void increaseCount() {
        this.runningRounds ++;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public static void main(String[] args) {
        JobScheduler scheduler = new JobScheduler("default");
        scheduler.addJob("print-hello-world-every-10s", 1000L, new JobMethod() {
            @Override
            public void execute() throws Exception {
                System.out.println(System.currentTimeMillis());
            }
        }, true);
        scheduler.start();
    }
}
