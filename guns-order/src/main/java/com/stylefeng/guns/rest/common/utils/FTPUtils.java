package com.stylefeng.guns.rest.common.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "ftp")
public class FTPUtils {
    private String hostName;
    private Integer port;
    private String userName;
    private String password;

    private FTPClient ftpClient;

    void initFTP(){
        try {
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("utf-8");
            ftpClient.connect(hostName, port);
            ftpClient.login(userName, password);
        } catch (IOException e) {
            log.error("FTP初始化失败"+e.getMessage());
        }
    }

    // 输入一个路径，然后将路径里的json文件按转换成字符串返回给我
    public String getFileStrByAddress(String fileAddress){
        initFTP();
        BufferedReader reader =null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(ftpClient.retrieveFileStream(fileAddress))
            );
            StringBuilder sb = new StringBuilder();
            while(true){
                String line = reader.readLine();
                if(StringUtils.isEmpty(line)){
                    break;
                }
                sb.append(line);
            }
            ftpClient.logout();
            return sb.toString();

        } catch (IOException e) {
            log.error("FTP获取文件失败"+e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Test
    public void testFtp(){
        System.out.println(getFileStrByAddress("/cgs.json"));
    }
}
