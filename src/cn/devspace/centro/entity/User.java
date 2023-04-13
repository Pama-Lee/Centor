package cn.devspace.centro.entity;

import cn.devspace.nucleus.Plugin.DataEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Table(name = "centro_user")
@Entity
@Data
@TableName("centro_user")
public class User extends DataEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(name = "uid", nullable = false)
    private Long uid;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "openID", nullable = false)
    private String openID;

    @CreationTimestamp
    @Column(name = "createTime", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createTime;

}
