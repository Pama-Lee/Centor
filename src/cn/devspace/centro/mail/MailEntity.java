package cn.devspace.centro.mail;

import lombok.Data;

@Data
public class MailEntity {
    private String to;
    private String subject;
    private String content;
    private boolean status;
}
