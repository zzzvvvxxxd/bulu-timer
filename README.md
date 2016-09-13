## Bulu-Timer

> 基于DelayQueue的简单的定时任务队列

#### Quick Start
```Java
class Main {
    public static void main(String[] args) {
        // 初始化任务队列
        JobScheduler scheduler = new JobScheduler("default");
        // 向队列中提交任务,每1s打印一行文本
        scheduler.addJob("print-hello-world-every-10s", 1000L, new JobMethod() {
            @Override
            public void execute() throws Exception {
                System.out.println(System.currentTimeMillis());
            }
        }, true);
        // 开始轮转
        scheduler.start();
    }
}
```

提交的任务分为两种:
* OneTimeJob 仅在队列中被执行一次
* CommonJob 

```
public Job addJob(String name, long intervalMillis, JobMethod method, boolean isOneTimeJob)
// 默认为CommonJob
public Job addJob(String name, long intervalMillis, JobMethod method)
public Job addOneTimeJob(String name, long intervalMillis, JobMethod method)
```