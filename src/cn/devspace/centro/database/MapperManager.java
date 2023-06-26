package cn.devspace.centro.database;

import cn.devspace.centro.entity.*;
import cn.devspace.nucleus.Manager.Annotation.DataMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@DataMapper
public class MapperManager {

    /**
     * 单例模式
     */
    private static MapperManager mapperManager = null;

    @PostConstruct
    public void init(){
        mapperManager = this;
    }

    public static MapperManager getInstance(){
        return mapperManager;
    }

    @Resource
    public BaseMapper<Poll> pollMapper;

    @Resource
    public BaseMapper<Announcement> announcementMapper;

    @Resource
    public BaseMapper<User> userMapper;

    @Resource
    public BaseMapper<LoginToken> loginTokenMapper;

    @Resource
    public BaseMapper<Contact> contactMapper;

    @Resource
    public BaseMapper<PollTime> pollTimeBaseMapper;

    @Resource
    public BaseMapper<DeadLineTime> deadLineTimeBaseMapper;

    @Resource
    public BaseMapper<PollUser> pollUserBaseMapper;

    @Resource
    public BaseMapper<PollToken> pollTokenBaseMapper;





}
