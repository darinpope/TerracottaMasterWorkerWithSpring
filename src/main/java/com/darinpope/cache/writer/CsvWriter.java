package com.darinpope.cache.writer;

import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.CacheWriter;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Collection;

public class CsvWriter implements CacheWriter{
  private static final Logger LOGGER = Logger.getLogger(CsvWriter.class);
  private final File file;
  private       volatile OutputStream out;
  private       volatile boolean      write = true;


  public CsvWriter(final File file) {
    this.file = file;
  }

  public CacheWriter clone(final Ehcache ehcache) throws CloneNotSupportedException {
    throw new CloneNotSupportedException("CsvWriter cannot be cloned!");
  }

  public void init() {
    try {
      if(file.createNewFile()) {
        out = new BufferedOutputStream(new FileOutputStream(file));
      } else {
        out = new BufferedOutputStream(new FileOutputStream(file, true));
      }
    } catch (IOException e) {
      throw new CacheException("Couldn't create file " + file, e);
    }
  }

  public void dispose() throws CacheException {
    try {
      if (out != null) {
        out.close();
      }
    } catch (IOException e) {
      throw new CacheException("Couldn't close file " + file, e);
    }
  }

  public synchronized void write(final Element element) throws CacheException {

    if(!write) {
      return;
    }

    Object value = element.getObjectValue();

    for (Field field : value.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      String fieldValue = null;
      try {
        fieldValue = (String) field.get(value);
        if(fieldValue != null) {
            fieldValue = fieldValue.toString().replace('"', '\'');
        }
        out.write(("\"" + fieldValue + "\",").getBytes("UTF-8"));
      } catch (IOException e) {
        throw new CacheException("Couldn't write value " + fieldValue + " from field " + field + " to file", e);
      } catch (IllegalAccessException e) {
        throw new CacheException("Couldn't read value of field " + field + " on instance " + value, e);
      }
    }
    try {
      out.write("\n".getBytes());
      out.flush();
    } catch (IOException e) {
      throw new CacheException("Couldn't flush to file " + file);
    }
  }

  public synchronized void writeAll(final Collection<Element> elements) throws CacheException {
    for (Element element : elements) {
      write(element);
    }
  }

  public synchronized void delete(final CacheEntry cacheEntry) throws CacheException {
    // noop
  }

  public synchronized void deleteAll(final Collection<CacheEntry> cacheEntries) throws CacheException {
    // noop
  }
}
