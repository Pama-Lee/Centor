package cn.devspace.centro.mapper;

import cn.devspace.centro.GuardThread;
import cn.devspace.centro.Main;
import cn.devspace.centro.database.MapperManager;
import cn.devspace.centro.entity.*;
import cn.devspace.centro.mail.MailBase;
import cn.devspace.centro.mail.MailEntity;
import cn.devspace.centro.units.pollUnit;
import cn.devspace.centro.units.userUnit;
import cn.devspace.nucleus.Manager.Annotation.Router;
import cn.devspace.nucleus.Manager.RouteManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.*;

@Mapper
public class poll extends RouteManager {

    /**
     * 创建新的Poll
     * @param args 自动传入post值
     * @return 返回结果
     */
    @Router("poll/createNewPoll")
    public String createNewPoll(Map<String,String> args){
        //限定传入的参数
        String[] params = {"token","title","location","if_deadline","times"};
        if(checkParams(args,params)){
            //验证token
            LoginToken checkToken = userUnit.verifyLoginToken(args.get("token"));
            if (checkToken == null){
                return ResponseString(403,-1,"Invalid Token");
            }
            Poll poll = new Poll();
            poll.setTitle(args.get("title"));
            if (args.get("des") != null) poll.setDes(args.get("des"));
            poll.setLocation(args.get("location"));
            Long UID = checkToken.getUid();
            poll.setUid(UID);
            poll.setIf_deadline(Integer.valueOf(args.get("if_deadline")));

            if (args.get("deadline") != null && args.get("if_deadline").equals("1")){
                DeadLineTime deadline = pollUnit.addDeadLineTime(args.get("deadline"));
                if (deadline == null){
                    return ResponseString(101,-1,"Invalid deadline");
                }else{
                    poll.setDeadline(String.valueOf(deadline.tid));
                }
            }
            poll.setAllow_guest(Integer.valueOf(args.get("allow_guest")));

            String times = pollUnit.getDatabaseFormatTime(args.get("times"));
            if (times == null){
                return ResponseString(101,-1,"Invalid times");
            }
            // 处理时间
            poll.setTimes(times);
            MapperManager.getInstance().pollMapper.insert(poll);
            MailBase.getInstance().sendSingleMail(MapperManager.getInstance().userMapper.selectById(UID).getEmail(),"Centrosome Notice","You have created a new poll.<br> Title: "+args.get("title")+"<br> Location: "+args.get("location"));
            return ResponseString(200,1,"Create new poll success");
        }else {
            return ResponseString(101,-1,"Invalid params");
        }
    }

    /**
     * 停止投票
     * @param args 传入参数
     * @return 返回结果
     */
    @Router("poll/stopPoll")
    public String stopPoll(Map<String,String> args){
        String[] params = {"token","pid"};
        if (checkParams(args,params)){
            // 验证token
            LoginToken checkToken = userUnit.verifyLoginToken(args.get("token"));
            if (checkToken == null){
                return ResponseString(403,-1,"Invalid token");
            }
            // 验证pid
            Poll poll = MapperManager.getInstance().pollMapper.selectById(args.get("pid"));
            if (poll == null){
                return ResponseString(101,-1,"Invalid pid");
            }
            // 验证权限
            if (!poll.getUid().equals(checkToken.getUid())){
                return ResponseString(403,0,"Permission denied");
            }
            poll.setStatus(0);
            MapperManager.getInstance().pollMapper.updateById(poll);

            GuardThread.stop(poll);

            return ResponseString(200,1,"Stop poll success");
        }else {
            return ResponseString(101,-1,"Invalid params");
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
            // 验证token
            LoginToken checkToken = userUnit.verifyLoginToken(args.get("ltoken"));
            if (checkToken == null){
                return ResponseString(403,0,"Invalid token");
            }

            Page<Poll> page = new Page<>(Long.valueOf(args.get("page")),10);
            QueryWrapper<Poll> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uid",checkToken.getUid()).last("ORDER BY pid DESC");
            // 不等于2
            queryWrapper.ne("status",2);
            Page<Poll> polls = MapperManager.getInstance().pollMapper.selectPage(page,queryWrapper);

            for (Poll poll : polls.getRecords()) {
                if (poll.getIf_deadline() == 1){
                    DeadLineTime deadLineTime = MapperManager.getInstance().deadLineTimeBaseMapper.selectById(poll.getDeadline());
                    poll.setDeadline(deadLineTime.getFullTime());
                }
            }

            return ResponseObject(200,1,"success",polls);
        }
    }

    @Router("poll/getPollInfo")
    public Object getPollInfo(Map<String,String> args){
        String[] params = {"token","pid"};
        if (!checkParams(args,params)){
            return ResponseString(101,0,"Invalid params");
        }else {
            // 验证token
            LoginToken checkToken = userUnit.verifyLoginToken(args.get("token"));
            if (checkToken == null){
                return ResponseString(403,0,"Invalid token");
            }

            Poll poll = MapperManager.getInstance().pollMapper.selectById(args.get("pid"));
            List<PollUser> pollUsers = MapperManager.getInstance().pollUserBaseMapper.selectList(new QueryWrapper<PollUser>().eq("pid",args.get("pid")));
            List<PollUserDTO>  pollUserDTOS = new ArrayList<>();
            for (PollUser pollUser : pollUsers) {
                PollUserDTO pollUserDTO = new PollUserDTO(pollUser);
                pollUserDTOS.add(pollUserDTO);
            }
            Map<String,Object> res = new HashMap<>();
            res.put("poll",poll);
            res.put("users",pollUserDTOS);
            if (poll == null){
                return ResponseString(404,0,"Poll not found");
            } else {
                return ResponseObject(200,1,"success",res);
            }
        }
    }

    @Router("poll/addPollUser")
    public Object addPollUser(Map<String,String> args){
        String[] params = {"token","pid"};
        if (!checkParams(args,params)){
            return ResponseString(101,0,"Invalid params");
        }else {
            // 如果既不存在uid, 也不存在email, 则返回错误
            if (args.get("uid") == null && args.get("email") == null){
                return ResponseString(101,0,"Invalid params");
            }
            // 验证token
            LoginToken checkToken = userUnit.verifyLoginToken(args.get("token"));
            if (checkToken == null){
                return ResponseString(403,0,"Invalid token");
            }

            // 检查pid是否属于当前用户
            Poll poll = MapperManager.getInstance().pollMapper.selectById(args.get("pid"));
            if (poll == null){
                return ResponseString(404,0,"Poll not found");
            }
            if (!poll.getUid().equals(checkToken.getUid())){
                return ResponseString(403,0,"Permission denied");
            }

            // 如果这个poll已经结束了, 则返回错误
            if (poll.getStatus() == 0){
                return ResponseString(403,0,"Poll has been stopped");
            }

            // 如果存在uid, 根据|分割
            if (args.get("uid") != null){
                // 成功的个数
                int success = 0;
                // 失败的个数
                int fail = 0;
                String[] uids = args.get("uid").split("\\|");
                for (String uid : uids) {
                    // 如果uid不是数字, 则跳过
                    if (!uid.matches("[0-9]+")){
                        fail++;
                        continue;
                    }
                    PollUser pollUser = new PollUser();
                    pollUser.setPid(Long.valueOf(args.get("pid")));
                    // 查询uid
                    User user = MapperManager.getInstance().userMapper.selectById(uid);
                    if (user == null){
                        fail++;
                        continue;
                    } else {
                        success++;
                    }
                    pollUser.setUid(Long.valueOf(uid));
                    MapperManager.getInstance().pollUserBaseMapper.insert(pollUser);
                }
                Map<String,Integer> res = new HashMap<>();
                res.put("success",success);
                res.put("fail",fail);
                return ResponseObject(200,1,"success",res);
            }
            if (args.get("email") != null){
                // 成功的个数
                int success = 0;
                // 失败的个数
                int fail = 0;
                String[] emails = args.get("email").split("\\|");
                List<MailEntity> mailEntities = new ArrayList<>();
                // 获取host
                String host = Main.configBase.get("host").toString();

                for (String email : emails){
                    // 如果email不是邮箱格式, 则跳过
                    if (!email.matches("[a-zA-Z0-9_]+@[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)+")){
                        fail++;
                        continue;
                    }
                    PollUser pollUser = new PollUser();
                    pollUser.setPid(Long.valueOf(args.get("pid")));
                    // 根据email查询uid
                    User user = MapperManager.getInstance().userMapper.selectOne(new QueryWrapper<User>().eq("email",email));
                    if (user != null) {
                        pollUser.setUid(user.getUid());
                        MapperManager.getInstance().pollUserBaseMapper.insert(pollUser);
                        MailEntity mailEntity = new MailEntity();
                        mailEntity.setSubject("Centrosome - You have been invited to a poll");
                        mailEntity.setTo(email);
                        mailEntity.setContent("You have been invited to a poll, please click the link below to view the poll: <br> 你被邀请到一个投票中, 请点击下面的链接查看投票: <br> <a href=\""+host+"/pollParticipate?p=" + args.get("pid") + "\">"+host+"/pollParticipate</a>");
                        mailEntities.add(mailEntity);
                        success++;
                    }else {
                        pollUser.setEmail(email);

                        // 检查是否已经邀请过了
                        PollUser checkPollUser = MapperManager.getInstance().pollUserBaseMapper.selectOne(new QueryWrapper<PollUser>().eq("pid",args.get("pid")).eq("email",email));
                        if (checkPollUser != null){
                            fail++;
                            continue;
                        }
                        MapperManager.getInstance().pollUserBaseMapper.insert(pollUser);

                        // 新的token
                        String token = UUID.randomUUID().toString();
                        PollToken pollToken = new PollToken();
                        pollToken.setPid(Long.valueOf(args.get("pid")));
                        pollToken.setToken(token);
                        pollToken.setEmail(email);
                        MapperManager.getInstance().pollTokenBaseMapper.insert(pollToken);

                        MailEntity mailEntity = new MailEntity();
                        mailEntity.setSubject("Centrosome - You have been invited to a poll");
                        mailEntity.setTo(email);
                        mailEntity.setContent("You have been invited to a poll, please click the link below to view the poll: <br> 你被邀请到一个投票中, 请点击下面的链接查看投票: <br> <a href=\""+host+"/pollParticipate?p=" + args.get("pid") + "&token=" + token + "&email=" + email + "\">"+host+"/pollParticipate</a>");
                        mailEntities.add(mailEntity);
                        success++;
                    }
                }
                // 发送邮件
                MailBase.getInstance().sendMailList(mailEntities);
                Map<String,Integer> res = new HashMap<>();
                res.put("success",success);
                res.put("fail",fail);
                return ResponseObject(200,1,"success",res);

            }
            return ResponseString(200,1,"success");
        }
    }

    @Router("poll/deletePollUser")
    public Object deletePollUser(Map<String,String> args){
        String[] params = {"token","pid","email"};
        if (!checkParams(args,params)){
            return ResponseString(101,0,"Invalid params");
        }else {
            // 验证token
            LoginToken checkToken = userUnit.verifyLoginToken(args.get("token"));
            if (checkToken == null){
                return ResponseString(403,0,"Invalid token");
            }

            // 检查pid是否属于当前用户
            Poll poll = MapperManager.getInstance().pollMapper.selectById(args.get("pid"));
            if (poll == null){
                return ResponseString(404,0,"Poll not found");
            }
            if (!poll.getUid().equals(checkToken.getUid())){
                return ResponseString(403,0,"Permission denied");
            }

            // 如果这个poll已经结束了, 则返回错误
            if (poll.getStatus() == 0){
                return ResponseString(403,0,"Poll has been stopped");
            }

            // 根据email查询uid
            User user = MapperManager.getInstance().userMapper.selectOne(new QueryWrapper<User>().eq("email",args.get("email")));
            if (user == null){
                return ResponseString(404,0,"User not found");
            }
            // 删除
            MapperManager.getInstance().pollUserBaseMapper.delete(new QueryWrapper<PollUser>().eq("pid",args.get("pid")).eq("uid",user.getUid()));
            return ResponseString(200,1,"success");
        }
    }

    // 删除poll
    @Router("poll/deletePoll")
    public Object deletePoll(Map<String,String> args){
        String[] params = {"token","pid"};
        if (!checkParams(args,params)){
            return ResponseString(101,0,"Invalid params");
        }else {
            // 验证token
            LoginToken checkToken = userUnit.verifyLoginToken(args.get("token"));
            if (checkToken == null){
                return ResponseString(403,0,"Invalid token");
            }

            // 检查pid是否属于当前用户
            Poll poll = MapperManager.getInstance().pollMapper.selectById(args.get("pid"));
            if (poll == null){
                return ResponseString(404,0,"Poll not found");
            }
            if (!poll.getUid().equals(checkToken.getUid())){
                return ResponseString(403,0,"Permission denied");
            }

            // 删除
            poll.setStatus(2);
            MapperManager.getInstance().pollMapper.updateById(poll);
            return ResponseString(200,1,"success");
        }
    }



    @Router("poll/getPollData")
    public Object getPollData(Map<String, String> args){
        String[] params = {"token","pid"};
        if (!checkParams(args,params)){
            return ResponseString(101,0,"Invalid params");
        }else {
            // 验证token
            LoginToken checkToken = userUnit.verifyLoginToken(args.get("token"));
            if (checkToken == null){
                return ResponseString(403,0,"Invalid token");
            }

            // 检查pid是否属于当前用户
            Poll poll = MapperManager.getInstance().pollMapper.selectById(args.get("pid"));
            if (poll == null){
                return ResponseString(404,0,"Poll not found");
            }
            if (!poll.getUid().equals(checkToken.getUid())){
                return ResponseString(403,0,"Permission denied");
            }

            // 获取这个poll的所有可选时间
            String time = poll.getTimes();
            List<Long> idList = pollUnit.parseTimeToIdList(time);
            List<PollTime> pollTimeList = new ArrayList<>();
            for (Long id : idList){
                PollTime pollTime = MapperManager.getInstance().pollTimeBaseMapper.selectById(id);
                pollTimeList.add(pollTime);
            }

            // 获取这个poll的所有用户
            List<PollUser> pollUserList = MapperManager.getInstance().pollUserBaseMapper.selectList(new QueryWrapper<PollUser>().eq("pid",args.get("pid")));
            // 统计每个选项的人数
            Map<Long,Integer> countMap = new HashMap<>();
            for (PollUser pollUser : pollUserList){
                if (pollUser.getOptions() == null){
                    continue;
                }
                String options = pollUser.getOptions();
                List<Long> optionList = pollUnit.parseTimeToIdList(options);
                for (Long option : optionList){
                    if (countMap.containsKey(option)){
                        countMap.put(option,countMap.get(option)+1);
                    }else {
                        countMap.put(option,1);
                    }
                }
            }

            // 每个用户的选项
            List<Map<String,Object>> userOptions = new ArrayList<>();
            for (PollUser pollUser : pollUserList){
                Map<String,Object> userOption = new HashMap<>();
                if (pollUser.getOptions() != null){
                    userOption.put("options",pollUnit.parseTimeToIdList(pollUser.getOptions()));
                }
                
                userOption.put("email",pollUser.getEmail());
                userOptions.add(userOption);
            }

            // 返回结果
            Map<String,Object> res = new HashMap<>();
            res.put("pollTimeList",pollTimeList);
            res.put("countMap",countMap);
            res.put("userOptions",userOptions);
            return ResponseObject(200,1,"success",res);

        }
    }

    @Router("poll/getPollInfoByQuest")
    public Object getPollInfoByQuest(Map<String, String> args){
        String[] params = {"pid"};
        if (!checkParams(args,params)){
            return ResponseString(101,0,"Invalid params");
        }else {
            // 检查pid是否存在
            Poll poll = MapperManager.getInstance().pollMapper.selectById(args.get("pid"));
            if (poll == null){
                return ResponseString(404,0,"Poll not found");
            }

            if (poll.getStatus() == 0){
                return ResponseString(403,0,"Poll has been stopped");
            }

            // 发起人
            User user = MapperManager.getInstance().userMapper.selectById(poll.getUid());

            // 获取这个poll的所有可选时间
            String time = poll.getTimes();
            List<Long> idList = pollUnit.parseTimeToIdList(time);
            List<PollTime> pollTimeList = new ArrayList<>();
            for (Long id : idList){
                PollTime pollTime = MapperManager.getInstance().pollTimeBaseMapper.selectById(id);
                pollTimeList.add(pollTime);
            }

            Map<String,Object> res = new HashMap<>();
            res.put("title",poll.getTitle());
            res.put("location",poll.getLocation());
            res.put("founder",user.getEmail());
            res.put("time",pollTimeList);
            if (poll.getIf_deadline() != null && poll.getIf_deadline() == 1){
                res.put("deadline",MapperManager.getInstance().deadLineTimeBaseMapper.selectById(poll.getDeadline()));
            }

            // 返回结果
            return ResponseObject(200,1,"success",res);
        }
    }

    @Router("poll/participate")
    public Object participate(Map<String, String> args){
        String[] params = {"token","pid","options"};
        if (!checkParams(args,params)){
            return ResponseString(101,0,"Invalid params");
        }else {
            // 验证token
            LoginToken checkToken = userUnit.verifyLoginToken(args.get("token"));
            if (checkToken == null){
                if (args.get("email") != null) {
                   PollToken pl =  MapperManager.getInstance().pollTokenBaseMapper.selectOne(new QueryWrapper<PollToken>().eq("email", args.get("email")).eq("token", args.get("token")).eq("status", "1"));
                    if (pl == null) {
                        return ResponseString(403, 0, "Invalid token");
                    }
                    String pid = String.valueOf(pl.getPid());
                    if (!pid.equals(args.get("pid"))) {
                        return ResponseString(403, 0, "Invalid token");
                    }
                    checkToken = new LoginToken();
                    checkToken.setUid(0L);
                } else {
                    return ResponseString(403,0,"Invalid token");
                }
            }

            // 检查pid是否存在
            Poll poll = MapperManager.getInstance().pollMapper.selectById(args.get("pid"));
            if (poll == null){
                return ResponseString(404,0,"Poll not found");
            }

            if (poll.getStatus() == 0){
                return ResponseString(403,0,"Poll has been stopped");
            }

            // 检查是否过了deadline
            if (poll.getIf_deadline() != null && poll.getIf_deadline() == 1){
                DeadLineTime deadLineTime = MapperManager.getInstance().deadLineTimeBaseMapper.selectById(poll.getDeadline());
                // 通过日期类判断
                Date date = new Date();
                Integer year = Integer.valueOf(deadLineTime.year);
                Integer month = Integer.valueOf(deadLineTime.month);
                Integer day = Integer.valueOf(deadLineTime.day);
                Integer hour = Integer.valueOf(deadLineTime.hour);
                Integer minute = Integer.valueOf(deadLineTime.minute);
                Date deadline = new Date(year,month,day,hour,minute);
                if (date.after(deadline)){
                    return ResponseString(403,0,"Deadline has passed");
                }
            }
            // 检查options是否合法
            List<Long> optionList = pollUnit.parseTimeToIdList(args.get("options"));
            for (Long option : optionList){
                if (MapperManager.getInstance().pollTimeBaseMapper.selectById(option) == null){
                    return ResponseString(404,0,"Option not found");
                }
            }

           QueryWrapper<PollUser> p =  new QueryWrapper<PollUser>().eq("pid",args.get("pid"));
            if (args.get("email") != null) {
                p.eq("email", args.get("email"));
            } else {
                p.eq("uid", checkToken.getUid());
            }

            // 检查是否已经参与过
            PollUser pollUser = MapperManager.getInstance().pollUserBaseMapper.selectOne(p.last("limit 1"));
            if (pollUser == null){
                // TODO: 是否支持匿名参与
                if (poll.getAllow_guest() == 1){
                    PollUser newPollUser = new PollUser();
                    newPollUser.setPid(Long.valueOf(args.get("pid")));
                    newPollUser.setUid(checkToken.getUid());
                    newPollUser.setOptions(args.get("options"));
                    MapperManager.getInstance().pollUserBaseMapper.insert(newPollUser);
                    return ResponseString(200,1,"success");
                }else{
                    return ResponseString(403,0,"You have not permission");
                }
            }
            if (pollUser != null && pollUser.getOptions() != null){
                return ResponseString(403,0,"You have participated");
            }
            // 更新
            pollUser.setOptions(args.get("options"));
            MapperManager.getInstance().pollUserBaseMapper.updateById(pollUser);

            if (args.get("email") != null) {
                PollToken pl =  MapperManager.getInstance().pollTokenBaseMapper.selectOne(new QueryWrapper<PollToken>().eq("email", args.get("email")).eq("token", args.get("token")).eq("status", "1"));
                if (pl != null) {
                    pl.setStatus(0);
                    MapperManager.getInstance().pollTokenBaseMapper.updateById(pl);
                }
            }

            return ResponseString(200,1,"success");
        }
    }


}
