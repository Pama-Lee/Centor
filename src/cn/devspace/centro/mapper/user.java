package cn.devspace.centro.mapper;

import cn.devspace.centro.database.MapperManager;
import cn.devspace.centro.entity.LoginToken;
import cn.devspace.centro.entity.User;
import cn.devspace.centro.entity.UserInfoDTO;
import cn.devspace.centro.mail.MailBase;
import cn.devspace.centro.units.testUnit;
import cn.devspace.centro.units.userUnit;
import cn.devspace.nucleus.Manager.Annotation.Router;
import cn.devspace.nucleus.Manager.RouteManager;
import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Plugin.manager.PluginDataTransfer;
import cn.devspace.nucleus.Plugin.manager.PluginManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class user extends RouteManager {

    /**
     * 接入RootJam
     * @param args
     * @return
     */
    @Router("login/token")
    public Object login(Map<String, String> args){

        if (args.containsKey("token")) {
            String openId = requestToken(args.get("token"));
            if (openId == null) {
                return ResponseString(101,0,"token Invalid");
            }else {
                // 查询数据库，如果存在则返回用户信息，如果不存在则创建新用户
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("openID",openId);
                List<User> list = MapperManager.getInstance().userMapper.selectList(queryWrapper);
                if (list.size() > 1){
                    return ResponseString(101,0,"User Info Error!");
                }
                if (list.size() == 0) {
                    // 创建新用户
                    User user = new User();
                    user.setOpenID(openId);
                    MapperManager.getInstance().userMapper.insert(user);
                    LoginToken loginToken = createLoginToken(user, args.get("token"));
                    if (loginToken == null) {
                        return ResponseString(101,0,"Create User Error");
                    }
                    User user1 = requestUserInfo(openId);
                    if (user1 == null) {
                        return ResponseString(101,0,"Create User Error");
                    }
                    MapperManager.getInstance().userMapper.updateById(user1);

                    // 发送欢迎邮件
                    MailBase.getInstance().sendSingleMail(user1.getEmail(),"Welcome to Centrosome","Welcome to Centrosome, your account is "+user1.getEmail());

                    return ResponseObject(200,1,"Create User Success",loginToken);
                }
                LoginToken loginToken = createLoginToken(list.get(0), args.get("token"));
                if (loginToken == null) {
                    return ResponseString(101,0,"Create User Error");
                }
                return ResponseObject(200,1,"Login Success",loginToken);
            }
        }
        return ResponseString(101,0,"token Not Found");
    }

    @Router("user/info")
    public Object getInfo(Map<String, String> args){
        if (args.get("token") == null) {
            return ResponseString(101,0,"token Not Found");
        }
        LoginToken loginToken = userUnit.verifyLoginToken(args.get("token"));
        if (loginToken == null) {
            return ResponseString(101,0,"token Invalid");
        }
        User user = MapperManager.getInstance().userMapper.selectById(loginToken.getUid());
        if (user == null) {
            return ResponseString(101,0,"User Not Found");
        }

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setName(user.getName());
        userInfoDTO.setEmail(user.getEmail());

        return ResponseObject(200,1,"Get User Info Success",userInfoDTO);

    }

    private String requestToken(String token){
        PluginManager pluginManager = new PluginManager();
        Map<String, Object> args = new HashMap<>(4);
        args.put("token",token);
        PluginDataTransfer pluginDataTransfer =  pluginManager.invoke("cn.pamalee.rootjam","Api","requestToken",args);
        if (pluginDataTransfer.isSuccessful) {
            if (pluginDataTransfer.getResult().get("openId") == null) {
                return null;
            }else {
                return pluginDataTransfer.getResult().get("openId").toString();
            }
        }else {
            testUnit.printAllFields(pluginDataTransfer);
            // 请求出错
            return null;
        }

    }

    private User requestUserInfo(String openId){
        PluginManager pluginManager = new PluginManager();
        Map<String, Object> args = new HashMap<>(4);
        args.put("openid",openId);
        PluginDataTransfer pluginDataTransfer =  pluginManager.invoke("cn.pamalee.rootjam","Api","requestUserInfoMap",args);
        testUnit.printAllFields(pluginDataTransfer);
        if (pluginDataTransfer.isSuccessful) {
            Log.sendLog(String.valueOf(pluginDataTransfer.getResult()));
            if (pluginDataTransfer.getResult().get("email") == null) {
                return null;
            }else {
                User user = MapperManager.getInstance().userMapper.selectList(new QueryWrapper<User>().eq("openID",openId)).get(0);
                if (user == null) {
                    return null;
                }
                user.setEmail(pluginDataTransfer.getResult().get("email").toString());
                user.setName(pluginDataTransfer.getResult().get("user").toString());
                return user;
            }
        }else {
            Log.sendWarn(String.valueOf(pluginDataTransfer.getResult()));
            Log.sendWarn(String.valueOf(pluginDataTransfer.message));
            // 请求出错
            return null;
        }

    }

    private LoginToken createLoginToken(User user, String sourceToken){
        if (user.getOpenID() == null) {
            return null;
        }
        // 查询是否存在登录记录
        QueryWrapper<LoginToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sourceToken",sourceToken);
        List<LoginToken> list = MapperManager.getInstance().loginTokenMapper.selectList(queryWrapper);
        if (list.size() >= 1){
            // 检查是否过期
            if (list.get(0).getExpireTime() > System.currentTimeMillis()) {
                // 未过期
                return list.get(0);
            }
        }

        LoginToken loginToken = new LoginToken();
        loginToken.setUid(user.getUid());
        // MD5 加密
        String token = DigestUtils.md5DigestAsHex((user.getUid() + user.getOpenID() + System.currentTimeMillis()).getBytes());
        loginToken.setToken(token);
        loginToken.setSourceToken(sourceToken);
        // 登录时间14天
        loginToken.setExpireTime(System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000);
        MapperManager.getInstance().loginTokenMapper.insert(loginToken);
        return loginToken;
    }

}
