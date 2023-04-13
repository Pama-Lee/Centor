package cn.devspace.centro.mail;

import java.util.Date;
import java.util.List;

public class sendThread extends Thread{

    private List<MailEntity> mailEntityList;

    // 传入邮件池
    public sendThread(List<MailEntity> mailEntityList) {
        this.mailEntityList = mailEntityList;
    }

    @Override
    public void run() {
        // 遍历邮件池
        for (MailEntity mailEntity : mailEntityList) {
            // 时间
            Date now = new Date();
            String footer = "<br><br>Centrosome Team<br>Ministry of CODE<br>"+ now;
            // 发送邮件
            boolean status = MailBase.getInstance().sendSingleMail(mailEntity.getTo(), mailEntity.getSubject(), mailEntity.getContent() + footer);
            // 更新邮件状态
            mailEntity.setStatus(status);
        }
        // 结束线程
        this.interrupt();
    }



}
