package com.example.music_zengmeilian.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HomePageResponse {
    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private PageData<HomePageInfo> data;

    public static class PageData<T> {
        @SerializedName("records")
        private List<T> records;
        @SerializedName("total")
        private int total;
        @SerializedName("size")
        private int size;
        @SerializedName("current")
        private int current;
        @SerializedName("pages")
        private int pages;

        public List<T> getRecords() {
            return records;
        }

        public int getTotal() {
            return total;
        }

        public int getSize() {
            return size;
        }

        public int getCurrent() {
            return current;
        }

        public int getPages() {
            return pages;
        }
    }

    public static class HomePageInfo {
        @SerializedName("moduleConfigId")
        private int moduleConfigId;
        @SerializedName("moduleName")
        private String moduleName;
        @SerializedName("style")
        private int style;
        @SerializedName("musicInfoList")
        private List<MusicInfo> musicInfoList;

        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }

        public int getModuleConfigId() {
            return moduleConfigId;
        }

        public String getModuleName() {
            return moduleName;
        }

        public int getStyle() {
            return style;
        }

        public List<MusicInfo> getMusicInfoList() {
            return musicInfoList;
        }
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public PageData<HomePageInfo> getData() {
        return data;
    }
}