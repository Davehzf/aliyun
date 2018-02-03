package com.mofancn.aliyun.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.aliyun.oss.OSSClient;
import com.mofancn.aliyun.service.WeixinUserImageService;
import com.mofancn.common.utils.MofancnResult;

@Service
public class WeixinUserImageImpl implements WeixinUserImageService {

	@Override
	public MofancnResult uploadUserImageToAliOss(String accessToken, String mediaId) {

		// endpoint以杭州为例，其它region请按实际情况填写
		String endpoint = "http://oss-cn-shenzhen.aliyuncs.com";
		// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录
		// https://ram.console.aliyun.com 创建
		String accessKeyId = "LTAI7dPVbcu3tgWb";
		String accessKeySecret = "weugflsFzQmdVzxAaosZqd45hn9ocO";
		// 创建OSSClient实例
		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
		String BucketName = "mofancn";
		String imagepath = new DateTime().toString("/yyyy/MM/dd");

		InputStream inputStream = null;
		String url = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=" + accessToken + "&media_id="
				+ mediaId;
		String fileExt = "";
		try {
			URL urlGet = new URL(url);
			HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
			http.setRequestMethod("GET"); // 必须是get方式请求
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			fileExt = getFileexpandedName(http.getHeaderField("Content-Type"));
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
			System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
			http.connect();
			System.out.println(fileExt);
			// 获取文件转化为byte流
			inputStream = http.getInputStream();

		} catch (Exception e) {
			e.printStackTrace();
		}
		// 上传
		final String keySuffixWithSlash = "sso" + imagepath +"/";
		ossClient.putObject(BucketName, keySuffixWithSlash, new ByteArrayInputStream(new byte[0]));
		String keys = keySuffixWithSlash + UUID.randomUUID().toString() + fileExt;
		ossClient.putObject(BucketName, keys, inputStream);
		// 关闭client
		ossClient.shutdown();
		return MofancnResult.ok("http://mofancn.oss-cn-shenzhen.aliyuncs.com/" + keys);
	}

	/**
	 * 识别从微信下载的文件类型
	 * 
	 * @param contentType
	 * @return
	 */
	public static String getFileexpandedName(String contentType) {
		String fileEndWitsh = "";
		if ("image/jpeg".equals(contentType))
			fileEndWitsh = ".jpg";
		else if ("audio/mpeg".equals(contentType))
			fileEndWitsh = ".mp3";
		else if ("audio/amr".equals(contentType))
			fileEndWitsh = ".amr";
		else if ("video/mp4".equals(contentType))
			fileEndWitsh = ".mp4";
		else if ("video/mpeg4".equals(contentType))
			fileEndWitsh = ".mp4";
		return fileEndWitsh;
	}

}
