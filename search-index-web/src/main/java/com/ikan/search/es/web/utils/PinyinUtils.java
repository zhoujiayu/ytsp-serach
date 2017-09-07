package com.ikan.search.es.web.utils;

import java.util.HashMap;
import java.util.Map;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinUtils
{
  private static PinyinUtils instance = new PinyinUtils();
  private static HanyuPinyinOutputFormat format;
  public static Map<String, String> dictionary = new HashMap<String, String>();

  public static PinyinUtils getInstance() {
    return instance;
  }

  public void init() {
    format = new HanyuPinyinOutputFormat();
    format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
    format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    format.setVCharType(HanyuPinyinVCharType.WITH_V);
  }

  public String getPinYinHeadCharByDictionary(String str)
  {
    if (("".equals(str)) || (str == null)) {
      return null;
    }
    char[] chs = str.toCharArray();

    StringBuilder result = new StringBuilder();
    try {
      for (int i = 0; i < chs.length; i++)
      {
        String[] arr = chineseToPinYin(chs[i]);
        if (arr == null) {
          result.append("");
        } else if (arr.length == 0) {
          result.append(chs[i]);
        } else if (arr.length == 1) {
          result.append(arr[0].charAt(0));
        } else if (arr[0].equals(arr[1])) {
          result.append(arr[0].charAt(0));
        }
        else {
          String prim = str.substring(i, i + 1);

          String lst = null; String rst = null;

          if (i <= str.length() - 2) {
            rst = str.substring(i, i + 2);
          }
          if ((i >= 1) && (i + 1 <= str.length())) {
            lst = str.substring(i - 1, i + 1);
          }

          String answer = null;
          for (String py : arr)
          {
            if (("".equals(py)) || (py == null))
              continue;
            if (((lst != null) && (py.equals(dictionary.get(lst)))) || (
              (rst != null) && (py.equals(dictionary.get(rst))))) {
              answer = py;
              break;
            }
            if (py.equals(dictionary.get(prim))) {
              answer = py;
            }
          }
          if (answer != null)
            result.append(answer.charAt(0));
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return result.toString();
  }

  public String getHanyupinyinByDictionary(String hanyu)
  {
    if (("".equals(hanyu)) || (hanyu == null)) {
      return null;
    }

    if (format == null) {
      init();
    }
    StringBuffer result = new StringBuffer();
    char[] chs = hanyu.toCharArray();
    try
    {
      for (int i = 0; i < chs.length; i++) {
        String[] arr = chineseToPinYin(chs[i]);
        if (arr == null) {
          result.append("");
        } else if (arr.length == 0) {
          result.append(chs[i]);
        } else if (arr.length == 1) {
          result.append(arr[0]);
        } else if (arr[0].equals(arr[1])) {
          result.append(arr[0]);
        }
        else {
          String prim = hanyu.substring(i, i + 1);
          String lst = null; String rst = null;
          if (i <= hanyu.length() - 2) {
            rst = hanyu.substring(i, i + 2);
          }
          if ((i >= 1) && (i + 1 <= hanyu.length())) {
            lst = hanyu.substring(i - 1, i + 1);
          }
          String answer = null;
          for (String py : arr) {
            if (("".equals(py)) || (py == null))
              continue;
            if (((lst != null) && (py.equals(dictionary.get(lst)))) || (
              (rst != null) && (py.equals(dictionary.get(rst))))) {
              answer = py;
              break;
            }

            if (py.equals(dictionary.get(prim))) {
              answer = py;
            }
          }
          if (answer != null) {
            result.append(answer);
          }
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return result.toString();
  }

  public static String[] chineseToPinYin(char c)
  {
    String[] pinyArr = new String[0];
    if ((c < 40869) && (c >= '一')) {
      try {
        pinyArr = PinyinHelper.toHanyuPinyinStringArray(c, format);
      }
      catch (BadHanyuPinyinOutputFormatCombination e) {
        e.printStackTrace();
      }
    }
    return pinyArr;
  }

  public String getHanyupinyin(String hanyu)
  {
    if (format == null) {
      init();
    }
    StringBuffer sb = new StringBuffer();
    try {
      char[] hanziArr = hanyu.toCharArray();
      for (char c : hanziArr)
        if ((c < 40869) && (c >= '一')) {
          String[] pinyArr = PinyinHelper.toHanyuPinyinStringArray(c, format);
          if (pinyArr != null)
            sb.append(pinyArr[0]);
        } else {
          sb.append(c);
        }
    }
    catch (Exception localException)
    {
    }
    return sb.toString();
  }

  public String getPinYinHeadChar(String str)
  {
    StringBuilder convert = new StringBuilder();
    for (int j = 0; j < str.length(); j++) {
      char word = str.charAt(j);

      String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
      if (pinyinArray != null)
        convert.append(pinyinArray[0].charAt(0));
      else {
        convert.append(word);
      }
    }
    return convert.toString();
  }

  private static final boolean isChinese(char c) {
    Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

    return (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) || 
      (ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS) || 
      (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A);
  }

  public static final boolean isChinese(String strName)
  {
    char[] ch = strName.toCharArray();
    for (int i = 0; i < ch.length; i++) {
      char c = ch[i];
      if (isChinese(c)) {
        return true;
      }
    }
    return false;
  }
}