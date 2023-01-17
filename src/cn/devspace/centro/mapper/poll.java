package cn.devspace.centro.mapper;

import cn.devspace.centro.Main;
import cn.devspace.centro.entity.Poll;
import cn.devspace.nucleus.App.Login.units.tokenUnit;
import cn.devspace.nucleus.Manager.Annotation.Router;
import cn.devspace.nucleus.Manager.RouteManager;
import cn.devspace.nucleus.Message.Log;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class poll extends RouteManager {

    // Token TYPE
    private final String NEW_POLL_PERMISSION_TYPE = "createNewPoll";

    /**
     * 创建新的Poll
     * @param args 自动传入post值
     * @return 返回结果
     */
    @Router("poll/createNewPoll")
    public String test(Map<String,String> args){
        //限定传入的参数
        String[] params = {"token","title","des","location","if_deadline","deadline","if_hide","times"};
       Log.sendLog("create内部:"+Main.PluginKey);
        if(checkParams(args,params)){
            Long time = System.currentTimeMillis();
            String appToken =tokenUnit.newAppToken(Main.PluginKey, String.valueOf(time));
            if (!tokenUnit.VerifyPermissionToken(args.get("token"),NEW_POLL_PERMISSION_TYPE,Main.applicationName,appToken,String.valueOf(time))){
                return ResponseString(1403,-1,"token无效");
            }
            Session session = Main.getPollSession();
            Poll poll = new Poll();
            poll.setTitle(args.get("title"));
            poll.setDes(args.get("des"));
            poll.setLocation(args.get("location"));
            Long UID = tokenUnit.getUIDbyToken(args.get("token"));
            if (UID == 0L){
                return ResponseString(403,-1,"token失效");
            }
            poll.setUid(UID);
            poll.setIf_deadline(Integer.valueOf(args.get("if_deadline")));
            poll.setDeadline(args.get("deadline"));
            poll.setIf_hide(Integer.valueOf(args.get("if_hide")));
            poll.setTimes(args.get("times"));
            session.save(poll);
            session.getTransaction().commit();
            return ResponseString(200,1,"创建成功!");
        }else {
            return ResponseString(101,-1,"参数不合法");
        }
    }

    /**
     * 获取前端所需的Poll数据
     * @param args 传入数据
     * @return 返回数据
     */
    @Router("poll/getPolls")
    public Object getPolls(Map<String,String> args){
        String[] params = {"ltoken","page","t"};
        if (!checkParams(args,params)){
            return ResponseString(101,0,"非法参数");
        }else {
            if (!tokenUnit.checkLoginToken(args.get("ltoken"))){
                return ResponseString(102,0,"登陆失效");
            }else {
                Session session = Main.getPollSession();
                Long UID = tokenUnit.getUIDbyLoginToken(args.get("ltoken"));
                if (UID == null){
                    return ResponseString(101,0,"登陆失效");
                }
                if (System.currentTimeMillis() - Long.parseLong(args.get("t")) >= 10000 || Long.parseLong(args.get("t")) > System.currentTimeMillis()){
                    return ResponseString(102,0,"时间不匹配");
                }
               // Log.sendLog("UID:"+UID);
                Criteria criteria = session.createCriteria(Poll.class);
                criteria.add(Restrictions.eq("uid",UID));
                List<Poll> pollList = criteria.list();
                if (pollList != null){
                    Map<String,Map<String ,String >> result = new HashMap<>(20);
                    int i = 0;
                    for (Poll poll:pollList){
                        Map<String,String> res = new HashMap<>(5);
                        res.put("title",poll.getTitle());
                        res.put("des",poll.getDes());
                        res.put("pid",String.valueOf(poll.getPid()));
                        result.put(String.valueOf(i),res);
                        i++;
                    }
                    return result;
                }else {
                    return null;
                }
            }
        }
    }
}
