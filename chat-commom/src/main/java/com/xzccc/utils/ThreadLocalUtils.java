package com.xzccc.utils;

import org.springframework.stereotype.Service;

@Service
public class ThreadLocalUtils {
  private static ThreadLocal<Long> threadLocal = new ThreadLocal();

  public void set(Long key) {
    threadLocal.set(key);
  }

  public Long get() {
    return threadLocal.get();
  }

  public void remove() {
    threadLocal.remove();
  }
}
