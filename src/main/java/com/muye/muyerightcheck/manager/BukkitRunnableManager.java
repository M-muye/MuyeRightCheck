package com.muye.muyerightcheck.manager;

import org.bukkit.plugin.java.JavaPlugin;
import com.muye.muyerightcheck.runnable.CheckRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BukkitRunnableManager {

    //创建一个HashMap对象，用来存储String和CheckRunnable的对应关系
    private final ConcurrentHashMap<String, CheckRunnable> runnables;

    //创建一个JavaPlugin对象，用来获取插件实例
    private JavaPlugin plugin;
    private HashSet<String> runnings;
    //创建一个构造方法，传入插件实例
    public BukkitRunnableManager(JavaPlugin plugin) {
        //初始化HashMap对象
        runnables = new ConcurrentHashMap<>();
        runnings = new HashSet<>();
        //赋值插件实例
        this.plugin = plugin;
    }

    //创建一个方法，用来添加一个String和CheckRunnable的对应关系
    public void put(String key, CheckRunnable runnable) {
        runnables.put(key, runnable);
    }

    public ConcurrentHashMap<String, CheckRunnable> getRunnables() {
        return runnables;
    }

    //创建一个方法，用来移除一个String和CheckRunnable的对应关系
    public void remove(String key) {
        //从HashMap中移除name对应的runnable
        runnables.remove(key);
    }

    public void removePlayerAll() {
        getPlayerAllRunnableID().forEach(n -> {
            runnables.remove(n);
        });
    }

    //创建一个方法，用来开启指定的CheckRunnable
    public void startRunnable(String key, long delay, long period) {
        //从HashMap中获取name对应的runnable
        CheckRunnable runnable = runnables.get(key);
        //判断runnable是否存在
        if (runnable != null) {
            //如果存在，调用runTaskTimer方法，传入插件实例，延迟时间和周期时间
            runnable.runTaskTimerAsynchronously(plugin, delay, period);
            runnings.add(key);
        }
    }

    public CheckRunnable getRunnable(String key) {
        return runnables.get(key);
    }

    //创建一个方法，用来关闭指定的CheckRunnable
    public void stopRunnable(String key) {
        //从HashMap中获取name对应的runnable
        CheckRunnable runnable = runnables.get(key);
        //判断runnable是否存在
        if (runnable != null) {
            //如果存在，调用cancel方法，取消任务
            runnable.cancel();
            runnings.remove(key);
            runnables.remove(key);
        }
    }

    public HashSet<String> getRunnings() {
        return runnings;
    }

    public void setRunnings(HashSet<String> runnings) {
        this.runnings = runnings;
    }

    public Set<String> getPlayerAllRunnableID() {
        return runnables.keySet();
    }

}
