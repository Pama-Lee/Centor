package cn.devspace.centro;

import cn.devspace.centro.mapper.announcement;
import cn.devspace.centro.mapper.contact;
import cn.devspace.centro.mapper.poll;
import cn.devspace.centro.mapper.user;
import cn.devspace.nucleus.Manager.DataBase.DataBase;
import cn.devspace.nucleus.Plugin.Config.ConfigBase;
import cn.devspace.nucleus.Plugin.PluginBase;
import org.hibernate.Session;

import java.util.Timer;

public class Main extends PluginBase {

    public static String applicationName = "Centrosome";
    public static String version = "v0.0.1";

    public static String PluginKey = null;

    private DataBase dataBase;

    private static Session pollSession;
    private static Session announcementSession;

    public static ConfigBase configBase;


    /**
     * 当插件加载时事件
     */
    @Override
    public void onLoad() {
        PluginKey = super.getKey();
        sendLog(translateMessage("Loading",applicationName,version));

        // ==========初始化路由
        sendLog(translateMessage("initRouter"));
        initRoute(poll.class);
        initRoute(announcement.class);
        initRoute(user.class);
        initRoute(contact.class);

        initConfig();
    }

    private void initConfig(){
        configBase = new ConfigBase();
        configBase.load(this.getPluginDataPath(), "config.yml");

        if (configBase.get("host") == null) {
            configBase.set("host", "https://domain.com");
            configBase.save();
        }
    }

    /**
     * 当插件完成加载时事件
     */
    @Override
    public void onEnable() {
        sendLog(translateMessage("Enable",applicationName));
    }

    public static Session getPollSession(){
        if (!pollSession.getTransaction().isActive()) {
            pollSession.getTransaction().begin();
        }
        return pollSession;
    }

    public static Session getAnnouncementSession(){
        if (!announcementSession.getTransaction().isActive()){
            announcementSession.getTransaction().begin();
        }
        return announcementSession;
    }

    @Override
    public void onEnabled() {

        // 开启守护线程
        Timer timer = new Timer();
        // 每3分钟检查一次
        timer.schedule(new GuardThread(), 0, 1000 * 60 * 3);
    }
}
