package cn.devspace.centro.entity;

import cn.devspace.centro.database.MapperManager;
import lombok.Data;

@Data
public class PollUserDTO {

    private String email;
    private Long id;


    public PollUserDTO(PollUser pollUser) {
        this.id = pollUser.getId();
        if (pollUser.getUid() != null) {
            this.email = MapperManager.getInstance().userMapper.selectById(pollUser.getUid()).getEmail();
        } else {
            this.email = pollUser.getEmail();
        }
    }

}
