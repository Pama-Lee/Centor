package cn.devspace.centro.units;

import cn.devspace.centro.database.MapperManager;
import cn.devspace.centro.entity.DeadLineTime;
import cn.devspace.centro.entity.PollTime;
import cn.devspace.nucleus.Message.Log;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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


    public static Object parseTimeToMap(String time){
        // time: [{date:[2023,04,05],startTime:[00,00],stopTime:[00,01]},{date:[2023,04,05],startTime:[00,00],stopTime:[00,06]}]  将json字符串转换为List
        try {
            List<Object> timeList = (List<Object>) new Gson().fromJson(time, List.class);
            // 将其中所有的'.0'去掉
            for (Object o : timeList) {
                Map<String, Object> timeMap = (Map<String, Object>) o;
                for (String key : timeMap.keySet()) {
                    if (timeMap.get(key) instanceof List) {
                        List<Object> timeList1 = (List<Object>) timeMap.get(key);
                        for (int j = 0; j < timeList1.size(); j++) {
                            if (timeList1.get(j) instanceof Double) {
                                timeList1.set(j, ((Double) timeList1.get(j)).intValue());
                            }
                        }
                    }
                }
            }
            return timeList;
        }catch (Exception e){
            return null;
        }
    }

    public static List<PollTime> parseTime(String time){
        // time: [{date:[2023,04,05],startTime:[00,00],stopTime:[00,01]},{date:[2023,04,05],startTime:[00,00],stopTime:[00,06]}]  将json字符串转换为List
        List<Map> timeList = (List<Map>) parseTimeToMap(time);
        List<PollTime> pollTimeList = new ArrayList<>();
        for (Map map : timeList) {
            PollTime pollTime = new PollTime();
            pollTime.year =  ((List) map.get("date")).get(0).toString();
            pollTime.month =  String.valueOf(((List) map.get("date")).get(1));
            pollTime.day = String.valueOf(((List) map.get("date")).get(2));
            pollTime.start_time_hour = String.valueOf(((List) map.get("startTime")).get(0));
            pollTime.start_time_minute =  String.valueOf(((List) map.get("startTime")).get(1));

            pollTime.end_time_hour =  String.valueOf(((List) map.get("stopTime")).get(0));
            pollTime.end_time_minute =  String.valueOf(((List) map.get("stopTime")).get(1));

            pollTimeList.add(pollTime);
        }
        return pollTimeList;
    }

    public static List<Long> addPollTime(List<PollTime> pollTimeList){
        List<Long> idList = new ArrayList<>();
        for (PollTime pollTime : pollTimeList) {
            MapperManager.getInstance().pollTimeBaseMapper.insert(pollTime);
            idList.add(pollTime.getTid());
        }
        return idList;
    }

    public static String timeFormat(List<Long> idList){
        String time = "";
        for (Long id : idList) {
            // 如果是最后一个
            if (idList.indexOf(id) == idList.size()-1){
                time += id;
                break;
            }
            time += id + ",";
        }
        return time;
    }

    public static List<Long> parseTimeToIdList(String time){
        List<Long> idList = new ArrayList<>();
        for (String s : time.split(",")) {
            idList.add(Long.valueOf(s));
        }
        return idList;
    }

    public static List<PollTime> getTimeList(String time){
        List<Long> idList = new ArrayList<>();
        for (String s : time.split(",")) {
            idList.add(Long.valueOf(s));
        }
        List<PollTime> pollTimeList = new ArrayList<>();
        for (Long id : idList) {
            pollTimeList.add(MapperManager.getInstance().pollTimeBaseMapper.selectById(id));
        }
        return pollTimeList;
    }

    public static String getDatabaseFormatTime(String time){
        try{
            return timeFormat(addPollTime(parseTime(time)));
        } catch (Exception e){
            Log.sendWarn(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // 处理deadline
    public static DeadLineTime parseDeadLineTime(String time){
        // time: [{date:[2023,04,05],startTime:[00,00],stopTime:[00,01]},{date:[2023,04,05],startTime:[00,00],stopTime:[00,06]}]  将json字符串转换为List
            Map<String, Object> map = (Map<String, Object>) new Gson().fromJson(time, Map.class);
            DeadLineTime deadLineTime = new DeadLineTime();
            deadLineTime.year =  ((List) map.get("date")).get(0).toString();
            deadLineTime.month = (String) ((List) map.get("date")).get(1);
            deadLineTime.day = (String) ((List) map.get("date")).get(2);
            deadLineTime.hour = (String) ((List) map.get("time")).get(0);
            deadLineTime.minute = (String) ((List) map.get("time")).get(1);
        return deadLineTime;
    }

    public static DeadLineTime addDeadLineTime(String time){
        try {
            DeadLineTime deadLineTime = parseDeadLineTime(time);
            MapperManager.getInstance().deadLineTimeBaseMapper.insert(deadLineTime);
            return deadLineTime;
        }catch (Exception e){
            return null;
        }
    }



    // 获取Time的格式化字符串
    public static String getTimeFormatString(PollTime pollTime){
        return pollTime.year + "-" + pollTime.month + "-" + pollTime.day + " " + pollTime.start_time_hour + ":" + pollTime.start_time_minute + " - " + pollTime.end_time_hour + ":" + pollTime.end_time_minute;
    }


}



