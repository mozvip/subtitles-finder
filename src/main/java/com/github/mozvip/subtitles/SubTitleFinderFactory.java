package com.github.mozvip.subtitles;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class SubTitleFinderFactory {

    private static Map<String, String> env = System.getenv();

    public static void setEnv(Map<String, String> env) {
        SubTitleFinderFactory.env = env;
    }

    public static <T> T createInstance(Class<? extends T> finderClass) throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {

        Class<?>[] internalClasses = finderClass.getClasses();
        for (Class<?> class1 : internalClasses) {
            if (class1.getSimpleName().equals("Builder") && Modifier.isStatic(class1.getModifiers())) {

                Object builder = class1.getDeclaredConstructor().newInstance();

                Map<String, Method> builderMethods = new HashMap<>();
                Method buildMethod = null;
                Method[] methods = class1.getMethods();
                for (Method method: methods) {
                    if (method.getReturnType().equals(class1) && method.getParameterCount() == 1) {
                        // this method returns the builder class
                        builderMethods.put(method.getName(), method);
                    } else if (method.getReturnType().equals(finderClass)) {
                        buildMethod = method;
                    }
                }
                for (Map.Entry<String, Method> entry:builderMethods.entrySet()) {
                    String variableName = String.format("%s_%s", finderClass.getSimpleName(), entry.getKey());
                    if (env.containsKey(variableName)) {
                        entry.getValue().invoke(builder, env.get(variableName));
                    }
                }
                return (T) buildMethod.invoke(builder, (Object[]) null);
            }
        }
        return finderClass.getDeclaredConstructor().newInstance();
    }

}
