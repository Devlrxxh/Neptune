package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class PluginLogger  extends Logger {

    public PluginLogger(Neptune plugin) {
        super(plugin.getName(), null);
        setParent(plugin.getPlugin().getLogger());
        setLevel(Level.ALL);
    }

    @Override
    public void log(LogRecord logRecord) {
        logRecord.setMessage(logRecord.getMessage());
        super.log(logRecord);
    }
}