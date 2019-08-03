package com.stylefeng.guns.api.user;

import com.stylefeng.guns.api.user.vo.UserInfoModel;
import com.stylefeng.guns.api.user.vo.UserModel;

public interface UserAPI {

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 0：登录失败，uuid：登录成功
     */
    int login(String username, String password);

    /**
     * 用户注册
     * @param userModel 用户对象
     * @return true: 注册成功 false: 注册失败
     */
    boolean register(UserModel userModel);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return true: 不存在  false: 存在
     */
    boolean checkUsername(String username);

    /**
     * 获取用户信息
     * @param uuid 用户id
     * @return 用户信息
     */
    UserInfoModel getUserInfo(int uuid);

    /**
     * 更新用户信息
     * @param userInfoModel 用户信息
     * @return 更新后的用户信息（不成功则返回null）
     */
    UserInfoModel updateUserInfo(UserInfoModel userInfoModel);

}
