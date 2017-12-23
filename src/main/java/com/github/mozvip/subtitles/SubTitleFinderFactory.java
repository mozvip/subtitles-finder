package com.github.mozvip.subtitles;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class SubTitleFinderFactory {

    public static <T> T createInstance(Class<? extends T> finderClass) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Class<?>[] internalClasses = finderClass.getClasses();
        for (Class<?> class1 : internalClasses) {
            if (class1.getSimpleName().equals("Builder") && Modifier.isStatic(class1.getModifiers())) {

                Object builder = class1.newInstance();

                Map<String, Method> builderMethods = new HashMap<>();
                Method buildMethod = null;
                Method[] methods = class1.getMethods();
                for (Method method: methods) {
                    if (method.getReturnType().equals(class1)) {
                        // this method returns the builder class
                        builderMethods.put(method.getName(), method);
                    } else if (method.getReturnType().equals(finderClass)) {
                        buildMethod = method;
                    }
                }
                for (Map.Entry<String, Method> entry:builderMethods.entrySet()) {
                    String variableName = String.format("%s_%s", finderClass.getSimpleName(), entry.getKey());
                    String env = System.getenv().get(variableName);
                    if (env != null) {
                        entry.getValue().invoke(builder, env);
                    }
                }
                return (T) buildMethod.invoke(builder, null);
            }
        }
        return finderClass.newInstance();
    }

}
