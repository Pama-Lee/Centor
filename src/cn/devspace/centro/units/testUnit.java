package cn.devspace.centro.units;

import java.lang.reflect.Field;

public class testUnit {
    // 将一个类的全部变量都打印出来
    public static void printAllFields(Object object) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                System.out.println(field.getName() + " = " + field.get(object));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
