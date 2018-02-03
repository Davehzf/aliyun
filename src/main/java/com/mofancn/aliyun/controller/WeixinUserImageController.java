package com.mofancn.aliyun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mofancn.aliyun.service.WeixinUserImageService;
import com.mofancn.common.pojo.jedisClient;
import com.mofancn.common.utils.HttpClientUtil;
import com.mofancn.common.utils.MofancnResult;

@Controller
@RequestMapping("/weixin")
public class WeixinUserImageController {

	@Autowired
	private WeixinUserImageService weixinUserImageService;
	@Autowired
	private jedisClient jedisClient;
	@Value("${WEIXIN_ACCESSTOKEN_KEY}")
	private String WEIXIN_ACCESSTOKEN_KEY;
	
	@RequestMapping("/WeixinImageUpload")
	@ResponseBody
	public MofancnResult  WeixinImageUpload(String mediaId){
		
		String accessToken = jedisClient.get(WEIXIN_ACCESSTOKEN_KEY);
		if (accessToken != null) {
			
			return weixinUserImageService.uploadUserImageToAliOss(accessToken, mediaId);
		}
		
		
		String url = "http://weixin.mofancn.com/weixin/getaccesstoken";
		String doGet = HttpClientUtil.doGet(url);
		if (doGet.length() <= 0) {
			return MofancnResult.build(500, "获取accesstoken失败");
		}
		MofancnResult mofancnResult = MofancnResult.formatToPojo(doGet, MofancnResult.class);
		if (mofancnResult.getStatus() == 200) {
			String accessToken2 = mofancnResult.getData().toString();
			return weixinUserImageService.uploadUserImageToAliOss(accessToken2, mediaId);
		}
		
		return MofancnResult.build(500, "未知错误");
		
	}
}
