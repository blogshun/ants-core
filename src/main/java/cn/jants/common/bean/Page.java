package cn.jants.common.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 */
public class Page<T> extends ArrayList{

    /**
     * 当前第几页
     */
    private int index = 10;

    /**
     * 每页大小
     */
    private int size;

    /**
     * 数据对象
     */
    private T data;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 总页数
     */
    private int pages;

    public Page(List<T> list) {
        if (list instanceof Page) {
            Page page = (Page) list;
            this.index = page.getIndex();
            this.size = page.getSize();
            this.total = page.getTotal();
            this.data = (T) page.getData();
            this.pages = page.getPages();
        }
    }

    public Page(int index, int size) {
        this.index = index;
        this.size = size;
    }

    public Page(int index, int size, T data, long total, int pages) {
        this.index = index;
        this.size = size;
        this.data = data;
        this.total = total;
        this.pages = pages;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
