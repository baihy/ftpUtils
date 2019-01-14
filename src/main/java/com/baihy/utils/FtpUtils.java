package com.baihy.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

/**
 * @projectName: ftpUtils
 * @packageName: com.baihy.utils
 * @description:
 * @author: huayang.bai
 * @date: 2019/01/14 15:25
 */
public class FtpUtils {

    private final Logger LOGGER = LoggerFactory.getLogger(FtpUtils.class);

    private final String FTP_CLIENT_ENCODING = "UTF-8";

    /**
     * ftp服务器的ip地址
     */
    private String ftpIp;
    /**
     * ftp服务器的端口号
     */
    private Integer ftpPort;
    /**
     * ftp服务器的用户名
     */
    private String ftpUsername;
    /**
     * ftp服务器的密码
     */
    private String ftpPassword;

    private FTPClient ftpClient;

    public FtpUtils(String ftpIp, Integer ftpPort, String ftpUsername, String ftpPassword) {
        this.ftpIp = ftpIp;
        this.ftpPort = ftpPort;
        this.ftpUsername = ftpUsername;
        this.ftpPassword = ftpPassword;
        ftpClient = new FTPClient();
    }

    /**
     * ftp服务器登录
     *
     * @return
     */
    private boolean login() {
        boolean result = false;
        try {
            ftpClient.connect(this.ftpIp, this.ftpPort);
            result = ftpClient.login(this.ftpUsername, this.ftpPassword);
            initFtpClient();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("ftp服务器登录失败：", e);
        }
        return result;
    }


    private void initFtpClient() {
        try {
            // 设置以二进制流的方式传输
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setControlEncoding(FTP_CLIENT_ENCODING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean upload(String remote, String local) throws Exception {
        boolean result;
        if (StringUtils.isEmpty(remote)) {
            throw new IllegalArgumentException("this param remote is not empty!!!");
        }
        if (StringUtils.isEmpty(local)) {
            throw new IllegalArgumentException("this param local is not empty!!!");
        }
        File localFile = new File(local);
        if (!localFile.exists() || localFile.isDirectory()) {
            throw new IllegalArgumentException(local + " is not exists or is directory");
        }
        if (login()) {
            LOGGER.info("ftp服务器登录成功！！！");
        }
        File parentFile = new File(remote);
        String parent = parentFile.getParent();
        this.makeDirectory(parent);
        InputStream is = new FileInputStream(local);
        result = ftpClient.storeFile(new String(remote.getBytes("UTF-8"), "ISO8859-1"), is);
        is.close();
        if (logout()) {
            LOGGER.info("ftp服务器退出登录成功！！！");
        }
        return result;
    }

    /**
     * 创建ftp远程路径
     *
     * @param remotePath
     * @throws IOException
     */
    private void makeDirectory(String remotePath) throws IOException {
        Stack<String> pathStack = new Stack<>();
        handlePath(remotePath, pathStack);
        while (!pathStack.isEmpty()) {
            ftpClient.makeDirectory(new String(pathStack.pop().getBytes("UTF-8"), "ISO8859-1"));
        }
    }

    /**
     * 递归把路径存放到栈中
     *
     * @param remotePath
     * @param pathStack
     * @throws IOException
     */
    private void handlePath(String remotePath, Stack<String> pathStack) throws IOException {
        File remote = new File(remotePath);
        if (remote.exists()) {
            return;
        } else {
            pathStack.push(remotePath);
            handlePath(remote.getParentFile().getPath(), pathStack);
        }
    }

    private boolean logout() {
        boolean result = false;
        try {
            result = ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("ftp服务器退出登录失败：", e);
        }
        return result;
    }

}
