package com.mofancn.aliyun.service;

import com.mofancn.common.utils.MofancnResult;

public interface WeixinUserImageService {

	public MofancnResult uploadUserImageToAliOss(String token,String mediaId);
}
