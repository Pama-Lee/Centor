package cn.devspace.centro.entity;

import cn.devspace.nucleus.Plugin.DataEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "centro_poll")
@TableName("centro_poll")
public class Poll extends DataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
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

    @Column(name = "allow_guest", columnDefinition = "INT DEFAULT 0")
    private Integer allow_guest;
    @Column(name = "createTime", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private Timestamp createTime;
    @Column(name = "times")
    private String times;

    @Column(name = "status", columnDefinition = "INT DEFAULT 1")
    private Integer status;
}
