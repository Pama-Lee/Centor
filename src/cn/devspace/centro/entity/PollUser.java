package cn.devspace.centro.entity;

import cn.devspace.centro.database.MapperManager;
import cn.devspace.nucleus.Plugin.DataEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "centro_poll_user")
@TableName("centro_poll_user")
public class PollUser extends DataEntity {
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long id;

    private Long pid;
    private Long uid;

    private String email;

    // 投票选项, 使用|分割
    private String options;


    @Column(columnDefinition = "int default 0")
    private Integer sendEmail;

    @Column(columnDefinition = "int default 0")
    private Integer status;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createTime;


    public String getEmail() {
        if (uid != null) {
            return MapperManager.getInstance().userMapper.selectById(uid).getEmail();
        } else {
            return email;
        }
    }

}
