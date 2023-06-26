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
@Table(name = "centro_dead_line_time")
@TableName("centro_dead_line_time")
public class DeadLineTime extends DataEntity {
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    public Long tid;
    public String year;
    public String month;
    public String day;
    public String hour;
    public String minute;


    public String getFullTime() {
        return year + "-" + month + "-" + day + " " + hour + ":" + minute;
    }

}
