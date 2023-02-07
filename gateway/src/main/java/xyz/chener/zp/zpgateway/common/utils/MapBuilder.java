package xyz.chener.zp.zpgateway.common.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: chenzp
 * @Date: 2023/01/13/11:12
 * @Email: chen@chener.xyz
 */
public class MapBuilder <K,V>{

    public static <K,V> Mb<K,V> getInstance()
    {
        return MapBuilder.<K,V>getInstance(false);
    }

    public static <K,V> Mb<K,V> getInstance(boolean isHash)
    {
        if (isHash)
            return new Mb<>(new HashMap<>());
        else return new Mb<>(new LinkedHashMap<>());
    }

    public static class Mb <K,V>{
        private final Map<K,V> map;

        public Mb(Map<K, V> map) {
            this.map = map;
        }

        public Mb <K,V> add(K k,V v)
        {
            map.put(k,v);
            return this;
        }

        public Map<K,V> build()
        {
            return map;
        }
    }
}
