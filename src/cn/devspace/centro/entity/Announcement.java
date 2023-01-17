package cn.devspace.centro.entity;

import cn.devspace.nucleus.Plugin.DataEntity;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "centro-announcement")
public class Announcement extends DataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aid", nullable = false)
    private Long aid;

    @Column(name = "content",nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "createTime")
    private Timestamp createTime;
}
