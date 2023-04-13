package cn.devspace.centro.units;

import cn.devspace.centro.database.MapperManager;
import cn.devspace.centro.entity.LoginToken;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.List;

public class userUnit {

    // 验证登录token
    public static LoginToken verifyLoginToken(String token){
        QueryWrapper<LoginToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("token",token);
        List<LoginToken> list = MapperManager.getInstance().loginTokenMapper.selectList(queryWrapper);
        if (list.size() == 0) {
            return null;
        }
        LoginToken loginToken = list.get(0);
        if (loginToken.getExpireTime() < System.currentTimeMillis()) {
            return null;
        }
        return loginToken;
    }

}
