package com.housekeeping.common.utils;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Author su
 * @Date 2021/1/25 12:13
 */
public class SortListUtil<E> {
    /***
     * 转换为String获取HashCode排序
     * @param list
     * @param method
     * @param order   desc降序  null升序
     */
    public void Sort(List<E> list, final String method, final String order) {
        Collections.sort(list, new Comparator<E>() {
            @Override
            public int compare(E o1, E o2) {
                int ret = 0;
                try {
                    Method method1 = o1.getClass().getMethod(method, null);
                    Method method2 = o2.getClass().getMethod(method, null);
                    //倒序
                    if (StringUtils.isNotEmpty(order) && "desc".equalsIgnoreCase(order)) {
                        ret = method2.invoke(o2, null).toString().compareTo(method1.invoke(o1, null)
                                .toString());
                    }
                    else {
                        ret = method1.invoke(o1, null).toString()
                                .compareTo(method2.invoke(o2, null).toString());
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return ret;
            }
        });
    }

    /***
     * Double值排序
     * @param list
     * @param method
     * @param order   desc降序  null升序
     */
    public void SortByDouble(List<E> list, final String method, final String order) {
        Collections.sort(list, new Comparator<E>() {
            @Override
            public int compare(E o1, E o2) {
                int ret = 0;
                try {
                    Method method1 = o1.getClass().getMethod(method, null);
                    Method method2 = o2.getClass().getMethod(method, null);
                    //倒序
                    if (StringUtils.isNotEmpty(order) && "desc".equalsIgnoreCase(order)) {
                        ret = ((Double)method2.invoke(o2, null)).compareTo(((Double)method1.invoke(o1, null)));
                    }
                    else {
                        ret = ((Double)method1.invoke(o1, null)).compareTo(((Double)method2.invoke(o2, null)));
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return ret;
            }
        });
    }

}
