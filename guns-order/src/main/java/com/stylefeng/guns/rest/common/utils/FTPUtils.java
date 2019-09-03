package com.stylefeng.guns.rest.common.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
@Data
@Primary
@Configuration
@ConfigurationProperties(prefix = "ftp")
public class FTPUtils {
    private String hostName;
    private Integer port;
    private String userName;
    private String password;
    private String seatPath;

    private FTPClient ftpClient;

    public void initFTP(){
        try {
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("utf-8");
            ftpClient.connect(hostName, port);
            ftpClient.login(userName, password);
            ftpClient.enterLocalPassiveMode();
        } catch (IOException e) {
            log.error("FTP初始化失败"+e.getMessage());
        }
    }

    // 输入一个路径，然后将路径里的json文件按转换成字符串返回给我
    public String getFileStrByAddress(String fileName){
        initFTP();
        log.info("返回状态码：{}", ftpClient.getReplyCode());
        BufferedReader reader =null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(ftpClient.retrieveFileStream(seatPath+fileName))
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        FTPClient f = new FTPClient();
        f.connect("127.0.0.1",2101);
        f.login("ftp", "ftp");
        //这一句很重要！！！下面进行解释
        f.enterLocalPassiveMode();

        //返回登录结果状态
        int reply = f.getReplyCode();
        System.out.println(reply);
        FTPFile[] files = f.listFiles("/");
        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i].getName());
        }

        InputStreamReader stream = new InputStreamReader(f.retrieveFileStream("/seats/123214.json"));
        BufferedReader reader = new BufferedReader(stream);
        StringBuilder sb = new StringBuilder();
        while(true){
            String line = reader.readLine();
            if(StringUtils.isEmpty(line)){
                break;
            }
            sb.append(line);
        }
        System.out.println(sb.toString());

        if (!FTPReply.isPositiveCompletion(reply)) {

            System.out.println(f.list());
            f.disconnect();
            return ;
        }
    }
}
