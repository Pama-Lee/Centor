package cn.devspace.centro;

import cn.devspace.centro.entity.User;
import cn.devspace.centro.mapper.poll;
import cn.devspace.nucleus.Manager.DataBase.DataBase;
import cn.devspace.nucleus.Plugin.PluginBase;

public class main extends PluginBase {

    public static String applicationName = "Centrosome";
    public static String version = "v0.0.1";

    private DataBase dataBase;

    @Override
    public void onLoad() {
        sendLog(translateMessage("Loading",applicationName,version));
        // ==========初始化数据库
        sendLog(translateMessage("initDatabase"));
        dataBase = new DataBase(this.getClass(),new User());
        // ==========初始化路由
        sendLog(translateMessage("initRouter"));
        initRoute(poll.class);
    }

    @Override
    public void onEnable() {
        sendLog(translateMessage("Enable",applicationName));
    }
}
