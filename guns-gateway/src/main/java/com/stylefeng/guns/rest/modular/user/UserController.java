package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import com.sun.javafx.collections.MappingChange;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/user/")
@RestController
public class UserController {

    @Reference(interfaceClass = UserAPI.class,check = false)
    private UserAPI userAPI;


    @RequestMapping(value="register",method = RequestMethod.POST)
    public ResponseVO register(UserModel userModel){
        // 在Controller层进行非法数据的判断
        String username = userModel.getUsername();
        String password = userModel.getPassword();

        if(username==null || username.trim().length()==0){
            return ResponseVO.serviceFail("用户名不能为空！");
        }else if(password==null || password.trim().length()==0){
            return ResponseVO.serviceFail("密码不能为空！");
        }
        if (!userAPI.checkUsername(username)){
            return ResponseVO.serviceFail("用户名已存在");
        }
        boolean registerSucceed = userAPI.register(userModel);
        if (registerSucceed){
            return ResponseVO.success("恭喜注册成功");
        } else {
            return ResponseVO.serviceFail("用户注册失败");
        }
    }

    @RequestMapping(value="check",method = RequestMethod.POST)
    public ResponseVO check(String username){
        if(username!=null && username.trim().length()>0){
            // 当返回true的时候，表示用户名可用
            boolean notExists = userAPI.checkUsername(username);
            if (notExists){
                return ResponseVO.success("用户名不存在");
            }else{
                return ResponseVO.serviceFail("用户名已存在");
            }

        }else{
            return ResponseVO.serviceFail("用户名不能为空");
        }
    }

    @RequestMapping(value="logout",method = RequestMethod.GET)
    public ResponseVO logout(){
        /*
            应用：
                1、前端存储JWT 【七天】 ： JWT的刷新
                2、服务器端会存储活动用户信息【30分钟】  —— 30分钟后，即使你持有jwt也无法登陆（除非设置自动登录）
                3、JWT里的userId为key，查找活跃用户
                注：有时修改过密码，前端token未刷新，这是无法通过验证的。这种情况要考虑到。
            退出：
                1、前端删除掉JWT
                2、后端服务器删除活跃用户缓存
            现状：
                1、前端删除掉JWT，就一定没法通过验证了（正式环境下别这么做）
            Harson:
                后期记得用redis实现以下
         */


        return ResponseVO.success("用户退出成功");
    }


    @RequestMapping(value="getUserInfo",method = RequestMethod.GET)
    public ResponseVO getUserInfo(){
        // 获取当前登陆用户
        String userId = CurrentUser.getCurrentUser();
        if(userId != null && userId.trim().length()>0){
            // 将用户ID传入后端进行查询
            int uuid = Integer.parseInt(userId);
            UserInfoModel userInfo = userAPI.getUserInfo(uuid);
            if(userInfo!=null){
                return ResponseVO.success(userInfo);
            }else{
                return ResponseVO.appFail("用户信息查询失败");
            }
        }else{
            return ResponseVO.serviceFail("用户未登陆");
        }
    }

    @RequestMapping(value="updateUserInfo",method = RequestMethod.POST)
    public ResponseVO updateUserInfo(UserInfoModel userInfoModel){
        // 获取当前登陆用户
        String userId = CurrentUser.getCurrentUser();
        if(userId != null && userId.trim().length()>0){
            // 将用户ID传入后端进行查询
            int uuid = Integer.parseInt(userId);
            // 判断当前登陆人员的ID与修改的结果ID是否一致
            if(uuid != userInfoModel.getUuid()){
                return ResponseVO.serviceFail("请修改您个人的信息");
            }

            UserInfoModel userInfo = userAPI.updateUserInfo(userInfoModel);
            if(userInfo!=null){
                return ResponseVO.success(userInfo);
            }else{
                return ResponseVO.appFail("用户信息修改失败");
            }
        }else{
            return ResponseVO.serviceFail("用户未登陆");
        }
    }

}
