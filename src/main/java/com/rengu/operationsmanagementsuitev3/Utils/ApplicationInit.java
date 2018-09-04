package com.rengu.operationsmanagementsuitev3.Utils;

import com.rengu.operationsmanagementsuitev3.Entity.RoleEntity;
import com.rengu.operationsmanagementsuitev3.Entity.UserEntity;
import com.rengu.operationsmanagementsuitev3.Service.RoleService;
import com.rengu.operationsmanagementsuitev3.Service.UserService;
import com.rengu.operationsmanagementsuitev3.Thread.TCPReceiveThread;
import com.rengu.operationsmanagementsuitev3.Thread.UDPReceiveThread;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * @program: OperationsManagementSuiteV3
 * @author: hanchangming
 * @create: 2018-08-22 17:09
 **/

@Order(value = -1)
@Component
public class ApplicationInit implements ApplicationRunner {

    private final RoleService roleService;
    private final UserService userService;
    private final TCPReceiveThread tcpReceiveThread;
    private final UDPReceiveThread udpReceiveThread;

    @Autowired
    public ApplicationInit(RoleService roleService, UserService userService, TCPReceiveThread tcpReceiveThread, UDPReceiveThread udpReceiveThread) {
        this.roleService = roleService;
        this.userService = userService;
        this.tcpReceiveThread = tcpReceiveThread;
        this.udpReceiveThread = udpReceiveThread;
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        // 初始化文件保存路径
        File file = new File(ApplicationConfig.FILES_SAVE_PATH);
        if (!file.exists()) {
            FileUtils.forceMkdir(file);
        }
        // 启动TCP消息接受线程
        tcpReceiveThread.TCPMessageReceiver();
        // 启动UDP消息接受线程
        udpReceiveThread.UDPMessageReceiver();
        // 初始化默认管理员角色
        if (!roleService.hasRoleByName(ApplicationConfig.DEFAULT_ADMIN_ROLE_NAME)) {
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName(ApplicationConfig.DEFAULT_ADMIN_ROLE_NAME);
            roleEntity.setDescription("系统默认管理员角色");
            roleService.saveRole(roleEntity);
        }
        //初始化默认用户角色
        if (!roleService.hasRoleByName(ApplicationConfig.DEFAULT_USER_ROLE_NAME)) {
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName(ApplicationConfig.DEFAULT_USER_ROLE_NAME);
            roleEntity.setDescription("系统默认用户角色");
            roleService.saveRole(roleEntity);
        }
        // 初始化管理员用户
        if (!userService.hasUserByUsername(ApplicationConfig.DEFAULT_ADMIN_USERNAME)) {
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(ApplicationConfig.DEFAULT_ADMIN_USERNAME);
            userEntity.setPassword(ApplicationConfig.DEFAULT_ADMIN_PASSWORD);
            userService.saveAdminUser(userEntity);
        }
    }
}
