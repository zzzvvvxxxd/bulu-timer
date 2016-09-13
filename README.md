## Bulu-Timer

> 基于DelayQueue的简单的定时任务队列

## Quick Start
```Java
// 初始化任务队列
JobScheduler scheduler = new JobScheduler("default");
// 向队列中添加任务,每1s打印一行文本
scheduler.addJob("print-hello-world-every-10s", 1000L, new JobMethod() {
    @Override
    public void execute() throws Exception {
        System.out.println(System.currentTimeMillis());
    }
}, true);
// 开始轮转
scheduler.start();
```