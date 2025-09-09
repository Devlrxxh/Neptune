package dev.lrxh.api.config;

import java.util.List;

public interface IDataAccessor {
    String getString();
    List<String> getStringList();
    int getInt();
    boolean getBoolean();
    String getHeader();
    String getPath();
    String getComment();
}
