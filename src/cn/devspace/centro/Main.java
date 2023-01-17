package cn.devspace.centro;

import cn.devspace.centro.entity.Announcement;
import cn.devspace.centro.entity.Poll;
import cn.devspace.centro.entity.User;
import cn.devspace.centro.mapper.announcement;
import cn.devspace.centro.mapper.poll;
import cn.devspace.nucleus.Manager.DataBase.DataBase;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.PluginBase;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.Map;

public class Main extends PluginBase {

    public static String applicationName = "Centrosome";
    public static String version = "v0.0.1";

    public static String PluginKey = null;

    private DataBase dataBase;

    private static Session pollSession;
    private static Session announcementSession;

    /**
     * 当插件加载时事件
     */
    @Override
    public void onLoad() {
        PluginKey = super.getKey();
        sendLog(translateMessage("Loading",applicationName,version));
        // ==========初始化数据库
        sendLog(translateMessage("initDatabase"));
        dataBase = new DataBase(this.getClass(),new User());
        pollSession = dataBase.newSession("poll",this.getClass(), new Poll());
        announcementSession = dataBase.newSession("announcement",this.getClass(),new Announcement());


        // ==========初始化路由
        sendLog(translateMessage("initRouter"));
        initRoute(poll.class);
        initRoute(announcement.class);
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


}
