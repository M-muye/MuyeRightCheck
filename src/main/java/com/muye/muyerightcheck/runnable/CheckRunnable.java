package com.muye.muyerightcheck.runnable;

import com.muye.muyerightcheck.MuyeRightCheck;
import org.bukkit.scheduler.BukkitRunnable;

public class CheckRunnable extends BukkitRunnable {
    private String playerName;
    public CheckRunnable(String playerName){
        this.playerName = playerName;
    }
    private int cooldownTime = MuyeRightCheck.cooldown;
    @Override
    public void run() {
        cooldownTime--;
        //当倒计时小于等于0，也就是冷却倒计时结束
        if (cooldownTime<=0){
            //设置玩家查看权限为TRUE
            MuyeRightCheck.getPlayerCheckPerm().put(playerName,Boolean.TRUE);
            //取消该Runnable任务
            MuyeRightCheck.getRunnableManager().stopRunnable(playerName);
            MuyeRightCheck.getPlayerPokeMap().remove(playerName);
        }
    }
    public int getCooldownTime(){
        return this.cooldownTime;
    }
}
