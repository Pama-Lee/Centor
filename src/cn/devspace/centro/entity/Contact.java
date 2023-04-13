package cn.devspace.centro.entity;

import cn.devspace.nucleus.Plugin.DataEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 单向联系人, 不需要对方同意
 */
@Data
@Entity
@Table(name = "centro_contact")
@TableName("centro_contact")
public class Contact extends DataEntity {
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long cid;

    private Long fromUid;
    private Long toUid;

    // 0: 删除 1: 正常
    @Column(columnDefinition = "int default 1")
    private Integer status;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createTime;
}
