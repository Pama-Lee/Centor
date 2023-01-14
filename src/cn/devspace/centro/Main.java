package cn.devspace.centro;

import cn.devspace.centro.entity.Poll;
import cn.devspace.centro.entity.User;
import cn.devspace.centro.mapper.poll;
import cn.devspace.nucleus.Manager.DataBase.DataBase;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.PluginBase;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends PluginBase {

    public static String applicationName = "Centrosome";
    public static String version = "v0.0.1";

    public static String PluginKey = null;

    private DataBase dataBase;

    private static Session pollSession;

    @Override
    public void onLoad() {
        PluginKey = super.getKey();
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
    public Map<Integer, Map<String,String>> parse(){
        String test = "[[1673715415545,1673715415545]]";
        int leng = test.length();
        Map<Integer, Map<String,String>> map = new HashMap<>(20);

        if (test.charAt(0) == '[' && test.charAt(1) == '['){
            String[] st = test.split("],\\[");
            for (int min=0;min<st.length;min++) {
                st[min] = st[min].replace("[","").replace("]","");
                String[] res = st[min].split(",");
                Map<String, String> times = new HashMap<>(4);
                times.put(res[0],res[1]);
                map.put(min,times);
            }
            Log.sendLog(map.toString());
            return map;
        }else{
            return null;
        }


    }








}
