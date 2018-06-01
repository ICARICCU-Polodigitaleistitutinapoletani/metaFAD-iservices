package com.gruppometa.mets2mag;


import java.util.List;

/**
 * Created by ingo on 07/03/17.
 */
public interface FileDescriptionRepository {
    public List<Integer> getDimensionOfImage(String path);
    public String getJsonInfo(String baseDir,String path);
}
