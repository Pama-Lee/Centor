package cn.devspace.centro.mapper;

import cn.devspace.centro.database.MapperManager;
import cn.devspace.centro.entity.Contact;
import cn.devspace.centro.entity.ContactDTO;
import cn.devspace.centro.entity.LoginToken;
import cn.devspace.centro.entity.User;
import cn.devspace.centro.units.userUnit;
import cn.devspace.nucleus.Manager.Annotation.Router;
import cn.devspace.nucleus.Manager.RouteManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class contact extends RouteManager {
    @Router("contact/list")
    public Object getContactList(Map<String,String> args){
        if (args.get("token") == null){
            return ResponseString(101,0,"token Not Found");
        }
        // 验证token
        LoginToken loginToken = userUnit.verifyLoginToken(args.get("token"));
        if (loginToken == null){
            return ResponseString(101,0,"token Invalid");
        }

        // 默认最多显示100条
        int size = 100;
        Page<Contact> contactPage = getContactListByPage(1,size,loginToken.getUid());

        List<Contact> contactList = contactPage.getRecords();
        List<ContactDTO> contactDTOList = new ArrayList<>();
        if (contactList.size() == 0){
            return ResponseObject(200,1,"Success",contactDTOList);
        }
        // 将数据转为ContactDTO
        for (Contact contact : contactList){
            ContactDTO contactDTO = new ContactDTO();
            contactDTO.setTid(contact.getCid());
            String email = MapperManager.getInstance().userMapper.selectById(contact.getToUid()).getEmail();
            contactDTO.setEmail(email);
            contactDTO.setName(contact.getName());
            contactDTOList.add(contactDTO);
        }

        return ResponseObject(200,1,"Success",contactDTOList);
    }

    @Router("contact/add")
    public Object addContact(Map<String,String> args){
        if (args.get("token") == null || args.get("email") == null){
            return ResponseString(103,0,"Parameter Not Found");
        }
        // 验证token
        LoginToken loginToken = userUnit.verifyLoginToken(args.get("token"));
        if (loginToken == null){
            return ResponseString(103,0,"token Invalid");
        }

        // 检查是否是自己
        if (args.get("email").equals(MapperManager.getInstance().userMapper.selectById(loginToken.getUid()).getEmail())){
            return ResponseString(101,0,"Can't Add Yourself");
        }

        // 检查是否存在该用户
        QueryWrapper<User> queryWrapperEmail = new QueryWrapper<>();
        queryWrapperEmail.eq("email",args.get("email"));
        List<User> list = MapperManager.getInstance().userMapper.selectList(queryWrapperEmail);
        if (list.size() == 0){
            return ResponseString(101,0,"User Not Found");
        }
        // 检查是否已经存在
        QueryWrapper<Contact> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fromUid",loginToken.getUid());
        queryWrapper.eq("toUid",list.get(0).getUid());
        List<Contact> ContactList = MapperManager.getInstance().contactMapper.selectList(queryWrapper);
        if (ContactList.size() > 0){
            // 之前存在，但是被删除了
            if (ContactList.get(0).getStatus() == 0){
                Contact contact = ContactList.get(0);
                contact.setStatus(1);
                MapperManager.getInstance().contactMapper.updateById(contact);
                return ResponseString(200,1,"Success");
            }
            return ResponseString(101,0,"Contact Already Exists");
        }



        // 创建新联系人
        Contact contact = new Contact();
        contact.setFromUid(loginToken.getUid());
        contact.setToUid(list.get(0).getUid());
        // 如果存在姓名
        if (args.get("name") != null){
            if (args.get("name").length() > 30){
                return ResponseString(101,0,"Name Too Long");
            }

            contact.setName(args.get("name"));
        }
        MapperManager.getInstance().contactMapper.insert(contact);
        return ResponseString(200,1,"Success");
    }

    @Router("contact/delete")
    public Object deleteContact(Map<String,String> args){
        if (args.get("token") == null || args.get("tid") == null){
            return ResponseString(103,0,"Parameter Not Found");
        }
        // 验证token
        LoginToken loginToken = userUnit.verifyLoginToken(args.get("token"));
        if (loginToken == null){
            return ResponseString(103,0,"token Invalid");
        }

        // 检查tid是否存在
        QueryWrapper<Contact> queryWrapperEmail = new QueryWrapper<>();
        queryWrapperEmail.eq("cid",args.get("tid"));
        List<Contact> list = MapperManager.getInstance().contactMapper.selectList(queryWrapperEmail);
        if (list.size() == 0){
            return ResponseString(104,0,"Contact Not Found");
        }

        // 检查这个联系人是否属于当前用户
        if (!Objects.equals(list.get(0).getFromUid(), loginToken.getUid())){
            return ResponseString(105,0,"Contact Not Found");
        }

        Contact contact = list.get(0);
        contact.setStatus(0);

        // 删除联系人
        MapperManager.getInstance().contactMapper.updateById(contact);
        return ResponseString(200,1,"Success");
    }

    private Page<Contact> getContactListByPage(int page, int size, Long UID){
        Page<Contact> contactPage = new Page<>(page,size);
        QueryWrapper<Contact> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fromUid",UID);
        queryWrapper.eq("status",1);
        return MapperManager.getInstance().contactMapper.selectPage(contactPage,queryWrapper);
    }

}
