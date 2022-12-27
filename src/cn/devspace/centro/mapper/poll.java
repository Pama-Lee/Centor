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

    /**
     * 创建新的Poll
     * @param args 自动传入post值
     * @return 返回结果
     */
    @Router("poll/createNewPoll")
    public String test(Map<String,String> args){
        //限定传入的参数
        String[] params = {"token","title","des","location","if_deadline","deadline","if_hide","times"};
        if(checkParams(args,params)){
            Session session = Main.getPollSession();
            Poll poll = new Poll();
            poll.setTitle(args.get("title"));
            poll.setDes(args.get("des"));
            poll.setLocation(args.get("location"));
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

    @Router("poll/getPolls")
    public String getPolls(Map<String,String> args){
        String[] params = {"ltoken","page","t"};
        if (!checkParams(args,params)){
            return ResponseString(101,0,"非法参数");
        }else {
            if (!tokenUnit.checkLoginToken(args.get("ltoken"))){
                return ResponseString(102,0,"登陆失效");
            }else {
                Session session = Main.getPollSession();
                String UID = tokenUnit.getUIDbyLoginToken(args.get("ltoken"));
               // Log.sendLog("UID:"+UID);
                Criteria criteria = session.createCriteria(Poll.class);
                criteria.add(Restrictions.eq("uid",Long.valueOf(UID)));
                List<Poll> pollList = criteria.list();
                if (pollList != null){
                    Map<String,String > result = new HashMap<>(20);
                    for (Poll poll:pollList){
                        result.put(String.valueOf(poll.getPid()),poll.getTitle());
                    }
                    return Map2Json(result);
                }else {
                    return null;
                }
            }
        }
    }
}
