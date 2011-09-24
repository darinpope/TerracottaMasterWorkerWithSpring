package com.darinpope.cache.writer;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.CacheWriterFactory;

import java.io.File;
import java.util.Properties;

public class CsvWriterFactory extends CacheWriterFactory{
    private static final String PATH = System.getProperty("user.home");

    @Override
    public CacheWriter createCacheWriter(Ehcache cache, Properties properties) {
        return new CsvWriter(new File(PATH + "/" + cache.getName() + ".csv"));
    }
}
