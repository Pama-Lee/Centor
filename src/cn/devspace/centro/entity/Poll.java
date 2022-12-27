package cn.devspace.centro.entity;

import cn.devspace.nucleus.Plugin.DataEntity;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "centro_poll")
public class Poll extends DataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pid", nullable = false)
    private Long pid;
    @Column(name = "uid", nullable = false)
    private Long uid;
    @Column(name = "title")
    private String title;
    @Column(name = "des")
    private String des;
    @Column(name = "location")
    private String location;
    @Column(name = "if_deadline")
    private Integer if_deadline;
    @Column(name = "deadline")
    private String deadline;
    @Column(name = "if_hide")
    private Integer if_hide;
    @Column(name = "createTime")
    @CreationTimestamp
    private Timestamp createTime;
    @Column(name = "times")
    private String times;
}
