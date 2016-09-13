package edu.ecnu.ica.scheduler;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by dianping on 16/9/13.
 */
public class Job implements Delayed{
    protected long nextTimestampInMillis = System.currentTimeMillis();
    private final JobScheduler scheduler;
    private String name;
    private long intervalInMillis;
    private JobMethod jobMethod;
    private volatile boolean canceled = false;
    private volatile boolean isOneTimeJob = false;

    public Job(JobScheduler jobScheduler, String name, long interval, JobMethod jobMethod) {
        this.scheduler = jobScheduler;
        this.name      = name;
        this.intervalInMillis = interval;
        this.jobMethod = jobMethod;
    }

    public void execute() {
        try {
            jobMethod.execute();
            scheduler.increaseCount();
        } catch (Exception e) {
        }
    }

    public void cancel() {
        this.canceled = true;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setNextTimestampInMillis() {
        this.nextTimestampInMillis = System.currentTimeMillis() + this.intervalInMillis;
    }

    public boolean isOneTimeJob() {
        return isOneTimeJob;
    }

    public void setOneTimeJob(boolean oneTimeJob) {
        isOneTimeJob = oneTimeJob;
    }

    public long getIntervalInMillis() {
        return intervalInMillis;
    }

    public void setIntervalInMillis(long intervalInMillis) {
        this.intervalInMillis = intervalInMillis;
    }

    public String getName() {
        return name;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(nextTimestampInMillis - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
    }
}
