package cn.devspace.centro.units;

import cn.devspace.nucleus.Message.Log;

import java.util.HashMap;
import java.util.Map;

public class pollUnit {
    /**
     * 处理前端返回的时间数据
     * @param text 时间字符串 example:[[1673715415545,1673715415545]]
     * @return 返回处理完成的MAP
     */
    public Map<Integer, Map<String,String>> parse(String text){
        int leng = text.length();
        Map<Integer, Map<String,String>> map = new HashMap<>(20);
        if (text.charAt(0) == '[' && text.charAt(1) == '['){
            String[] st = text.split("],\\[");
            for (int min=0;min<st.length;min++) {
                st[min] = st[min].replace("[","").replace("]","");
                String[] res = st[min].split(",");
                Map<String, String> times = new HashMap<>(4);
                times.put(res[0],res[1]);
                map.put(min,times);
            }
            Log.sendLog(map.toString());
            return map;
        }else{
            return null;
        }
    }
}
