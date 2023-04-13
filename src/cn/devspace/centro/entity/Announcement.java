package cn.devspace.centro.entity;

import cn.devspace.nucleus.Plugin.DataEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@TableName("centro_announcement")
@Table(name = "centro_announcement")
public class Announcement extends DataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long aid;

    private String content;

    @Column(columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private Timestamp createTime;
}
