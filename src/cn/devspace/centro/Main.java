package cn.devspace.centro;

import cn.devspace.centro.entity.Poll;
import cn.devspace.centro.entity.User;
import cn.devspace.centro.mapper.poll;
import cn.devspace.nucleus.Manager.DataBase.DataBase;
import cn.devspace.nucleus.Plugin.PluginBase;
import org.hibernate.Session;

public class Main extends PluginBase {

    public static String applicationName = "Centrosome";
    public static String version = "v0.0.1";

    private DataBase dataBase;

    private static Session pollSession;

    @Override
    public void onLoad() {
        sendLog(translateMessage("Loading",applicationName,version));
        // ==========初始化数据库
        sendLog(translateMessage("initDatabase"));
        dataBase = new DataBase(this.getClass(),new User());
        pollSession = dataBase.newSession("poll",this.getClass(), new Poll());

        // ==========初始化路由
        sendLog(translateMessage("initRouter"));
        initRoute(poll.class);
    }

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
}
