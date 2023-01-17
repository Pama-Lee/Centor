package cn.devspace.centro.mapper;

import cn.devspace.centro.Main;
import cn.devspace.centro.entity.Announcement;
import cn.devspace.nucleus.Manager.Annotation.Router;
import cn.devspace.nucleus.Manager.RouteManager;
import cn.devspace.nucleus.Message.Log;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.List;
import java.util.Map;

public class announcement extends RouteManager {

    @Router("overall/announcement")
    public Object getAnnouncement(Map<String,String> args){
        Session announcementSession = Main.getAnnouncementSession();
        Criteria criteria = announcementSession.createCriteria(Announcement.class);
        List<Announcement> list = criteria.list();
        if (list != null){
            return list.get(0);
        }else {
            return ResponseString(101,0,"不存在公告");
        }
    }

    /**
     * 创建新的公告
     * @param args 传入的参数，需要包含ltoken和message
     * @return 返回创建结果
     */
    @Router("overall/createNewAnnouncement")
    public Object createNewAnnouncement(Map<String ,String > args){
        String[] params = {"ltoken","message"};
        if (!checkParams(args,params)){
            return ResponseString(-1,-1,"非法参数");
        }
        Session announcementSession = Main.getAnnouncementSession();
        Announcement announcement = new Announcement();
        announcement.setContent(args.get("message"));
        announcementSession.save(announcement);
        try{
            announcementSession.getTransaction().commit();
            return ResponseString(200,1,"发布成功");
        }catch (Exception e){
            Log.sendLog(e.getMessage());
            return ResponseString(101,-1,"发布失败");
        }
    }
}
