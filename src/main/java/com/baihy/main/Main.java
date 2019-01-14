package com.baihy.main;

import com.baihy.utils.FtpUtils;

/**
 * @projectName: ftpUtils
 * @packageName: com.baihy.main
 * @description:
 * @author: huayang.bai
 * @date: 2019/01/14 15:35
 */
public class Main {

    public static void main(String[] args) {
        FtpUtils ftpUtils = new FtpUtils("192.168.0.101", 2222, "ftp", "ftp");
        try {
            ftpUtils.upload("/阿飞岁的/阿萨德发送到/大幅杀跌.log", "D:\\采集报送工具\\数据报送工具V3.0_2019-01-14\\logs\\debug.log");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
