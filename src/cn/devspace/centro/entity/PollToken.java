package cn.devspace.centro.entity;

import cn.devspace.nucleus.Plugin.DataEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "centro_poll_token")
@TableName("centro_poll_token")
public class PollToken extends DataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private Long pid;

    private String token;

    private String email;

    @Column(columnDefinition = "int default 1")
    private Integer status;
}
