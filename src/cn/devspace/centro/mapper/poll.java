package cn.devspace.centro.mapper;

import cn.devspace.nucleus.Manager.Annotation.Router;
import cn.devspace.nucleus.Manager.RouteManager;

import java.util.Map;

public class poll extends RouteManager {

    /**
     * 创建新的Poll
     * @param args 自动传入post值
     * @return 返回结果
     */
    @Router("createNewPoll")
    public String test(Map<String,String> args){
        String[] params = {"token","title","des"};
        if(checkParams(args,params)){
            return ResponseString(200,1,args.get("token"));
        }else {
            return ResponseString(101,-1,"参数不合法");
        }
    }
}
