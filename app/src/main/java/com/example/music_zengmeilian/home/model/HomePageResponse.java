package com.example.music_zengmelian.model;

import java.util.List;

/**
 * 首页数据响应模型
 * 对应API返回的JSON数据结构
 */
public class HomePageResponse {
    private int code;       // 响应状态码
    private String msg;     // 响应消息
    private PageData<HomePageInfo> data; // 分页数据

    // Getter方法
    public int getCode() { return code; }
    public String getMsg() { return msg; }
    public PageData<HomePageInfo> getData() { return data; }

    /**
     * 分页数据包装类
     * @param <T> 数据类型
     */
    public static class PageData<T> {
        private List<T> records; // 当前页数据列表
        private int total;       // 总数据量
        private int size;        // 每页大小
        private int current;     // 当前页码
        private int pages;       // 总页数

        public List<T> getRecords() { return records; }
        public int getTotal() { return total; }
        public int getSize() { return size; }
        public int getCurrent() { return current; }
        public int getPages() { return pages; }
    }

    /**
     * 首页模块信息
     */
    public static class HomePageInfo {
        private int moduleConfigId;  // 模块配置ID
        private String moduleName;   // 模块名称
        private int style;           // 样式类型(1:banner,2:横滑大卡,3:一行一列,4:一行两列)
        private List<com.example.music_zengmelian.model.MusicInfo> musicInfoList; // 音乐列表

        public int getModuleConfigId() { return moduleConfigId; }
        public String getModuleName() { return moduleName; }
        public int getStyle() { return style; }
        public List<com.example.music_zengmelian.model.MusicInfo> getMusicInfoList() { return musicInfoList; }
    }
}