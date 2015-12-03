package com.airppt.airppt.entry;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by user on 2015/8/24.
 */
public class PhotoFilter {
    private GPUImageFilter filter;
    private String name;

    public PhotoFilter(GPUImageFilter filter, String name) {
        this.filter = filter;
        this.name = name;
    }

    public GPUImageFilter getFilter() {
        return filter;
    }

    public void setFilter(GPUImageFilter filter) {
        this.filter = filter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
