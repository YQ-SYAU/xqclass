package com.yq.utils;

import java.text.DecimalFormat;

/**
 * 计算文件大小 转成k或m
 */
public class FileSizeUtil {
    private static DecimalFormat df = new DecimalFormat("#.0");
    public static String getSize(Long fileSize){
        if(fileSize>0&&fileSize<1024){
            //单位为b
            return df.format(fileSize)+"B";
        }else if(fileSize>=1024&&fileSize<1024*1024){
            Double size= fileSize/(1024.0);
            return df.format(size)+"KB";
        }else if(fileSize>=1024*1024&&fileSize<1024*1024*1024){
            Double size=fileSize/(1024.0*1024.0);
            return df.format(size)+"MB";
        }else{
            Double size=fileSize/(1024.0*1024.0*1024.0);
            return df.format(size)+"GB";
        }
    }
}
