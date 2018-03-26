package com.youedata.daas.rest.batch.resource;

import org.springframework.core.io.InputStreamResource;

import java.io.InputStream;

/**
 * Created by cdyoue on 2018/1/19.
 */
public class DaasResource extends InputStreamResource {
    private String filename;
    public DaasResource(InputStream inputStream) {
        super(inputStream);
    }

    public DaasResource(InputStream inputStream, String description) {
        super(inputStream, description);
    }
    public DaasResource(InputStream inputStream, String description,String filename) {
        super(inputStream, description);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return filename;
    }
}
