package cn.devspace.centro.entity;

import cn.devspace.nucleus.Plugin.DataEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@TableName("centro_poll_time")
@Table(name = "centro_poll_time")
public class PollTime extends DataEntity {
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    public Long tid;
    public String year;
    public String month;
    public String day;

    public String start_time_hour;
    public String start_time_minute;

    public String end_time_hour;
    public String end_time_minute;
}
