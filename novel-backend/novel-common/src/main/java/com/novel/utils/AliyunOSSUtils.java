package com.novel.utils;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.comm.SignVersion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class AliyunOSSUtils {
	
	
	// 阿里云OSS中的Bucket对应的域名
	@Value("${novel.oss.endpoint}")
	private String endpoint = "https://oss-cn-beijing.aliyuncs.com";
	// Bucket名称
	@Value("${novel.oss.bucket-name}")
	private String bucketName = "java-web-lws";
	// Bucket所属区域
	@Value("${novel.oss.region}")
	private String region = "cn-beijing";
	
	/**
	 * 上传文件
	 * @param content   文件的二进制字节数组
	 * @param originalFilename  原文件名
	 * @return String 该文件的访问路径
	 * @throws Exception
	 */
	public String upload(byte[] content, String originalFilename) throws Exception {
		// 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
		EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
		
		// 填写Object完整路径，例如2024/06/1.png。Object完整路径中不能包含Bucket名称。
		//生成一个新的不重复的文件名
		String newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
		String objectName = "novel" + "/" + newFileName;
		
		// 创建OSSClient实例。
		ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
		clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
		OSS ossClient = OSSClientBuilder.create()
				                .endpoint(endpoint)
				                .credentialsProvider(credentialsProvider)
				                .clientConfiguration(clientBuilderConfiguration)
				                .region(region)
				                .build();
		
		try {
			ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content));
		} finally {
			ossClient.shutdown();
		}
        return "https://" +
		               bucketName +
		               "." +
		               endpoint.split("//")[1] +
		               "/" +
		               objectName;
		// return endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + objectName;
	}
	
}