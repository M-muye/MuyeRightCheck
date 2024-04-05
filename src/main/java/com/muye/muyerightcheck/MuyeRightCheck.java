package com.muye.muyerightcheck;

import com.muye.muyerightcheck.manager.BukkitRunnableManager;
import com.muye.muyerightcheck.runnable.CheckRunnable;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.EVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.IVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class MuyeRightCheck extends JavaPlugin implements Listener, CommandExecutor {

    public static MuyeRightCheck main;
    public static BukkitRunnableManager runnableManager;
    private static HashMap<String,Pokemon> playerPokeMap;
    public static int cooldown;
    private static HashMap<String, Boolean> playerCheckPerm;
    private String StillCooling;

    private String reload;

    private String Permission;

    private List<String> vip;

    private List<String> defaultM;

    private List<String> worldlist;

    public static HashMap<String, Boolean> getPlayerCheckPerm() {
        return playerCheckPerm;
    }

    public static BukkitRunnableManager getRunnableManager() {
        return runnableManager;
    }

    //获取右键的实体宝可梦
    public static net.minecraft.entity.Entity bkToNmsEntity(Entity entity) {
        return ((CraftEntity) entity).getHandle();
    }

    public static List<String> ReplaceList(List<String> stringList, Pokemon pokemon) {
        List<String> list = new ArrayList<>(stringList);
        IVStore ivStore = pokemon.getIVs();
        EVStore evStore = pokemon.getEVs();
        Moveset moveset = pokemon.getMoveset();
        StringBuilder moves = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            if (moveset.get(i) != null){
                moves.append(moveset.get(i).getMove().getLocalizedName());
            } else {
                moves.append("空");
            }
            if (i != 3){
                moves.append("-");
            }
        }
        list.replaceAll(s-> s.replace("&", "§")
                .replace("%pokemon%", pokemon.getLocalizedName())
                .replace("%level%", String.valueOf(pokemon.getLevel()))
                .replace("%owner%", pokemon.getOwnerPlayer()!=null ? pokemon.getOwnerName(): "野生")
                .replace("%shiny%", pokemon.isShiny() ? "闪光" : "非闪光")
                .replace("%ivstotal%", ivStore.getPercentageString(1) + "%")
                .replace("%evstotal%", (new DecimalFormat("#0.0%")).format(evStore.getTotal() / 510))
                .replace("%ivHp%", String.valueOf(ivStore.getStat(StatsType.HP)))
                .replace("%ivAttack%", String.valueOf(ivStore.getStat(StatsType.Attack)))
                .replace("%ivSpecialAttack%", String.valueOf(ivStore.getStat(StatsType.SpecialAttack)))
                .replace("%ivDefence%", String.valueOf(ivStore.getStat(StatsType.Defence)))
                .replace("%ivSpecialDefence%", String.valueOf(ivStore.getStat(StatsType.SpecialDefence)))
                .replace("%ivSpeed%", String.valueOf(ivStore.getStat(StatsType.Speed)))
                .replace("%evHp%", String.valueOf(evStore.getStat(StatsType.HP)))
                .replace("%evAttack%", String.valueOf(evStore.getStat(StatsType.Attack)))
                .replace("%evSpecialAttack%", String.valueOf(evStore.getStat(StatsType.SpecialAttack)))
                .replace("%evDefence%", String.valueOf(evStore.getStat(StatsType.Defence)))
                .replace("%evSpecialDefence%", String.valueOf(evStore.getStat(StatsType.SpecialDefence)))
                .replace("%evSpeed%", String.valueOf(evStore.getStat(StatsType.Speed)))
                .replace("%nature%", pokemon.getNature().getLocalizedName())
                .replace("%gender%", pokemon.getGender().getLocalizedName())
                .replace("%growth%", pokemon.getGrowth().getLocalizedName())
                .replace("%ability%", pokemon.getAbility().getLocalizedName())
                .replace("%moves%", moves));
        return list;
    }

    public static HashMap<String, Pokemon> getPlayerPokeMap() {
        return playerPokeMap;
    }

    public static String Replace(String string) {
        return string.replace("&", "§");
    }

    @Override
    public void onEnable() {
        getLogger().info("§6-----------------");
        getLogger().info("§6| §f作者: §b§l沐夜");
        getLogger().info("§6| §f联系方式: Q2103074851");
        getLogger().info("§6| §f接TrV3，技术，小插件定制");
        getLogger().info("§6-----------------");
        getLogger().info("§6| §b" + this.getDescription().getName() + " §fis §aStarting!");
        getLogger().info("§6| §bVersion§f: " + this.getDescription().getVersion());
        main = this;
        Reload();
        runnableManager = new BukkitRunnableManager(this);
        playerPokeMap = new HashMap<>();
        playerCheckPerm = new HashMap<>();
        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getPluginCommand("MuyeRightCheck").setExecutor(this);
        Bukkit.getPluginCommand("mrc").setExecutor(this);
        getLogger().info("§6-----------------");
    }

    private void Reload() {
        saveDefaultConfig();
        reloadConfig();
        cooldown = getConfig().getInt("Cooldown");
        reload = Replace(getConfig().getString("Messages.Reload"));
        StillCooling = Replace(getConfig().getString("Messages.StillCooling"));
        vip = getConfig().getStringList("Groups.vip");
        defaultM = getConfig().getStringList("Groups.default");
        Permission = getConfig().getString("VipPermission");
        worldlist = getConfig().getStringList("WorldList");
    }

    @EventHandler
    public void rightPokemon(PlayerInteractEntityEvent event) {
        if (event.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }
        Player player = event.getPlayer();
        if (!worldlist.contains(player.getWorld().getName())) {
            return;
        }
        Entity entity = event.getRightClicked();
        if (!(bkToNmsEntity(entity) instanceof EntityPixelmon)) {
            return;
        }
        EntityPixelmon entityPixelmon = (EntityPixelmon) bkToNmsEntity(entity);
        playerPokeMap.put(player.getName(),entityPixelmon.getPokemonData());
        if (playerCheckPerm.getOrDefault(player.getName(), Boolean.TRUE)) {
            if (playerPokeMap.get(player.getName())==null){
                playerPokeMap.put(player.getName(),entityPixelmon.getPokemonData());
            }
            //TODO  输出精灵信息
            if (entityPixelmon.hasOwner()){
                ReplaceList(vip, playerPokeMap.get(player.getName())).forEach(player::sendMessage);
            } else {
                if (player.hasPermission(Permission)) {
                    ReplaceList(vip, playerPokeMap.get(player.getName())).forEach(player::sendMessage);
                } else {
                    ReplaceList(defaultM, playerPokeMap.get(player.getName())).forEach(player::sendMessage);
                }
            }
            //设置玩家查看权限为FALSE
            playerCheckPerm.put(player.getName(), Boolean.FALSE);
            //进行冷却计算
            //在BukkitRunnable管理器中添加以玩家名为键的runnable
            runnableManager.put(player.getName(), new CheckRunnable(player.getName()));
            //循环执行对应的CheckRunnable，20tick一循环，也就是1秒
            runnableManager.startRunnable(player.getName(), 0L, 20);
        } else {
            player.sendMessage(StillCooling.replace("%time%", String.valueOf(runnableManager.getRunnable(player.getName()).getCooldownTime())));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp() && args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            Reload();
            sender.sendMessage(reload);
            return true;
        }
        if (args[0].equalsIgnoreCase("check")) {
            int cooldownTime;
            if (runnableManager.getRunnable(sender.getName()) != null) {
                cooldownTime = runnableManager.getRunnable(sender.getName()).getCooldownTime();
                sender.sendMessage("冷却还剩" + cooldownTime + "秒");
            } else {
                sender.sendMessage("你还未查看过一次宝可梦,还没记录你的冷却");
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        getLogger().info("§6-----------------");
        getLogger().info("§6| §b" + this.getDescription().getName() + " §fis §cClosing!");
        getLogger().info("§6-----------------");
        //清空Map缓存
        playerCheckPerm.clear();
    }
}
