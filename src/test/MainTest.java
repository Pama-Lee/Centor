package test;

import cn.devspace.centro.entity.DeadLineTime;
import cn.devspace.centro.entity.PollTime;
import cn.devspace.centro.units.pollUnit;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import cn.devspace.nucleus.App.MailLobby.unit.sendMail;
public class MainTest {
    public static void main(String[] args) {
      sendMail sendMail = new sendMail();
      if(sendMail.sendSimpleEmail("pama@pamalee.cn", "test", "test")){
            System.out.println("success");
      }else {
            System.out.println("fail");
      }
    }

    public static Object parseTimeToMap(String time){
        // time: [{date:[2023,04,05],startTime:[00,00],stopTime:[00,01]},{date:[2023,04,05],startTime:[00,00],stopTime:[00,06]}]  将json字符串转换为List
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

    }

    public static List<PollTime> parseTime(String time){
        // time: [{date:[2023,04,05],startTime:[00,00],stopTime:[00,01]},{date:[2023,04,05],startTime:[00,00],stopTime:[00,06]}]  将json字符串转换为List
        List<Map> timeList = (List<Map>) parseTimeToMap(time);
        List<PollTime> pollTimeList = new ArrayList<>();
        for (Map map : timeList) {
            PollTime pollTime = new PollTime();
            pollTime.year =  ((List) map.get("date")).get(0).toString();
            pollTime.month = (String) ((List) map.get("date")).get(1);
            pollTime.day = (String) ((List) map.get("date")).get(2);
            pollTime.start_time_hour = (String) ((List) map.get("startTime")).get(0);
            pollTime.start_time_minute = (String) ((List) map.get("startTime")).get(1);

            pollTime.end_time_hour = (String) ((List) map.get("stopTime")).get(0);
            pollTime.end_time_minute = (String) ((List) map.get("stopTime")).get(1);

            pollTimeList.add(pollTime);
        }
        return pollTimeList;
    }
}
