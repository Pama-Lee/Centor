package cn.devspace.centro.entity;

import cn.devspace.centro.database.MapperManager;
import lombok.Data;

/**
 * 单向联系人, 不需要对方同意
 */
@Data
public class ContactDTO {
    // 联系人id
    private Long tid;
    // 联系人姓名
    private String name;


    public String getName() {
        // 通过tid获取邮箱
        return MapperManager.getInstance().userMapper.selectById(tid).getEmail();
    }

}
