package cn.devspace.centro;

import cn.devspace.centro.database.MapperManager;
import cn.devspace.centro.entity.*;
import cn.devspace.centro.mail.MailBase;
import cn.devspace.centro.mail.MailEntity;
import cn.devspace.centro.units.pollUnit;
import cn.devspace.nucleus.Message.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 每10分钟检查一次Poll的deadline
 */
public class GuardThread extends TimerTask {
    @Override
    public void run() {
        Log.sendLog("运行GuardThread");

        QueryWrapper<Poll> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        queryWrapper.eq("if_deadline", 1);
        List<Poll> pollList = MapperManager.getInstance().pollMapper.selectList(queryWrapper);
        for (Poll poll : pollList) {
            // 获取deadline
            DeadLineTime deadLineTime = MapperManager.getInstance().deadLineTimeBaseMapper.selectById(poll.getDeadline());
            LocalDateTime deadLine = getDeadLineTime(deadLineTime);
            // 获取当前时间
            LocalDateTime now = LocalDateTime.now();
            Log.sendLog(poll.getTitle()+" deadline: "+deadLine.toString());

            // 比较
            if (now.isAfter(deadLine)) {
                Log.sendLog(poll.getTitle()+" deadline has passed");
                stop(poll);
            }

        }
    }

    public static void stop(Poll poll){
        // deadline已过
        poll.setStatus(0);
        MapperManager.getInstance().pollMapper.updateById(poll);
        // 发送邮件
        List<PollUser> pollUserList = MapperManager.getInstance().pollUserBaseMapper.selectList(new QueryWrapper<PollUser>().eq("pid", poll.getPid()));
        List<MailEntity> emailList = new ArrayList<>();
        pollUserList.forEach(pollUser -> {
            MailEntity mailEntity = new MailEntity();
            mailEntity.setTo(pollUser.getEmail());
            mailEntity.setSubject("Poll Passed");

            // 结果
            Map<String, Integer> result = getResult(poll.getPid());

            // 格式化字符串
            String resultStr = "";
            for (String key : result.keySet()) {
                resultStr += key + ">>>-----------> " + result.get(key) + "\n";
            }

            mailEntity.setContent("Poll "+poll.getTitle()+" deadline has passed. Result:\n "+resultStr);

            emailList.add(mailEntity);
        });
        MailBase.getInstance().sendMailList(emailList);
    }

    // 获取结果
    private static Map<String, Integer> getResult(Long pid) {
        Map<String, Integer> result = new HashMap<>();
        // 获取这个poll的所有用户
        List<PollUser> pollUserList = MapperManager.getInstance().pollUserBaseMapper.selectList(new QueryWrapper<PollUser>().eq("pid", pid));
        // 统计每个选项的人数
        Map<Long,Integer> countMap = new HashMap<>();
        for (PollUser pollUser : pollUserList){
            if (pollUser.getOptions() == null){
                continue;
            }
            String options = pollUser.getOptions();
            List<Long> optionList = pollUnit.parseTimeToIdList(options);
            for (Long option : optionList){
                PollTime pollTime = MapperManager.getInstance().pollTimeBaseMapper.selectById(option);
                if (countMap.containsKey(option)){
                    countMap.put(option,countMap.get(option)+1);
                    result.put(pollUnit.getTimeFormatString(pollTime),countMap.get(option));
                }else {
                    countMap.put(option,1);
                    result.put(pollUnit.getTimeFormatString(pollTime) ,1);
                }
            }
        }

        return result;

    }



    private LocalDateTime getDeadLineTime(DeadLineTime deadline) {
        int year = Integer.parseInt(deadline.year);
        int month = Integer.parseInt(deadline.month);
        int day = Integer.parseInt(deadline.day);
        int hour = Integer.parseInt(deadline.hour);
        int minute = Integer.parseInt(deadline.minute);
        return LocalDateTime.of(year, month, day, hour, minute);
    }

}
