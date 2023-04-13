package cn.devspace.centro.mail;

import cn.devspace.nucleus.App.MailLobby.unit.sendMail;
import lombok.Data;

import java.util.Date;
import java.util.List;

public class MailBase {
    // 单例模式
    private static MailBase mailBase = null;

    private final sendMail sendMail = new sendMail();

    public static MailBase getInstance(){
        if (mailBase == null) {
            mailBase = new MailBase();
        }
        return mailBase;
    }

    public boolean sendSingleMail(String to, String subject, String content) {
        // 时间
        Date now = new Date();
        String footer = "<br><br>Centrosome Team<br>Ministry of CODE<br>"+ now;
        return sendMail.sendSimpleEmail(to, subject, content + footer);
    }

    public boolean sendMailList(List<MailEntity> mailEntityList) {
        try{
            sendThread sendThread = new sendThread(mailEntityList);
            sendThread.start();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
