package cn.devspace.centro.entity;

import cn.devspace.nucleus.Plugin.DataEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "centro_login_token")
@TableName("centro_login_token")
public class LoginToken extends DataEntity {
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long tid;

    @Column(name = "uid", nullable = false)
    private Long uid;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "sourceToken", nullable = false)
    private String sourceToken;

    @Column(name = "expireTime", nullable = false)
    private Long expireTime;

    @Column(name = "createTime", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createTime;


}
