package com.ikan.search.es.listener;

import com.ikan.search.es.web.utils.PinyinUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DictionaryListener
  implements ServletContextListener
{
  public void contextInitialized(ServletContextEvent sce)
  {
    BufferedReader br = null;
    try {
      File file = new File(DictionaryListener.class.getResource("/").getFile() + File.separator + "duoyinzi_pinyin.txt");
      br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

      String line = null;
      while ((line = br.readLine()) != null)
      {
        String[] arr = line.split("#");

        if ((arr[1] != null) && (!"".equals(arr[1]))) {
          String[] sems = arr[1].split(" ");
          for (String sem : sems)
          {
            if ((sem != null) && (!"".equals(sem)))
              PinyinUtils.dictionary.put(sem, arr[0]);
          }
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void contextDestroyed(ServletContextEvent sce)
  {
  }
}