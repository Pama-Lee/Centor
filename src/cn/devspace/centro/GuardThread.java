package cn.devspace.centro;

import cn.devspace.centro.database.MapperManager;
import cn.devspace.centro.entity.DeadLineTime;
import cn.devspace.centro.entity.Poll;
import cn.devspace.centro.entity.PollUser;
import cn.devspace.centro.mail.MailBase;
import cn.devspace.centro.mail.MailEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

/**
 * 每10分钟检查一次Poll的deadline
 */
public class GuardThread extends TimerTask {
    @Override
    public void run() {
        QueryWrapper<Poll> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        queryWrapper.eq("if_deadline", 1);
        List<Poll> pollList = MapperManager.getInstance().pollMapper.selectList(queryWrapper);
        for (Poll poll : pollList) {
            // 获取deadline
            DeadLineTime deadLineTime = MapperManager.getInstance().deadLineTimeBaseMapper.selectById(poll.getDeadline());
            Date deadLine = getDeadLineTime(deadLineTime);
            // 获取当前时间
            Date now = new Date();
            // 比较
            if (now.after(deadLine)) {
                // deadline已过
                poll.setStatus(0);
                MapperManager.getInstance().pollMapper.updateById(poll);
                // 发送邮件
                List<PollUser> pollUserList = MapperManager.getInstance().pollUserBaseMapper.selectList(new QueryWrapper<PollUser>().eq("pid", poll.getPid()));
                List<MailEntity> emailList = new ArrayList<>();
                pollUserList.forEach(pollUser -> {
                    MailEntity mailEntity = new MailEntity();
                    mailEntity.setTo(pollUser.getEmail());
                    mailEntity.setSubject("Poll Deadline");
                    mailEntity.setContent("Poll "+poll.getTitle()+" deadline has passed.");
                    emailList.add(mailEntity);
                });
                MailBase.getInstance().sendMailList(emailList);
            }

            // 如果小于半小时
            if (now.after(new Date(deadLine.getTime() - 30 * 60 * 1000))) {
                // 发送邮件
                List<PollUser> pollUserList = MapperManager.getInstance().pollUserBaseMapper.selectList(new QueryWrapper<PollUser>().eq("pid", poll.getPid()));
                List<MailEntity> emailList = new ArrayList<>();
                pollUserList.forEach(pollUser -> {
                    MailEntity mailEntity = new MailEntity();
                    mailEntity.setTo(pollUser.getEmail());
                    mailEntity.setSubject("Poll Deadline");
                    mailEntity.setContent("Poll "+poll.getTitle()+" deadline will pass in "+deadLine.toString());
                    emailList.add(mailEntity);
                });
                MailBase.getInstance().sendMailList(emailList);
            }

        }
    }

    private Date getDeadLineTime(DeadLineTime deadline) {
       Integer year = Integer.valueOf(deadline.year);
         Integer month = Integer.valueOf(deadline.month);
            Integer day = Integer.valueOf(deadline.day);
            Integer hour = Integer.valueOf(deadline.hour);
            Integer minute = Integer.valueOf(deadline.minute);
            Date deadLine = new Date(year, month, day, hour, minute);
            return deadLine;
    }

}
