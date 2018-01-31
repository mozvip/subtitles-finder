package com.github.mozvip.subtitles;

import java.lang.reflect.Constructor;
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

    private SubTitleFinderFactory() {}

    public static <T> T createInstance(Class<? extends T> finderClass) throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {

        Class<?>[] internalClasses = finderClass.getClasses();
        for (Class<?> internal : internalClasses) {
            if (Modifier.isStatic(internal.getModifiers())) {
                Constructor<?> defaultConstructor = internal.getDeclaredConstructor();
                if (defaultConstructor == null) {
                    continue;
                }
                Object builder = defaultConstructor.newInstance();

                Map<String, Method> builderMethods = new HashMap<>();
                Method buildMethod = null;
                Method[] methods = internal.getMethods();
                for (Method method: methods) {
                    if (method.getReturnType().equals(internal) && method.getParameterCount() == 1) {
                        // this method returns the builder class
                        builderMethods.put(method.getName(), method);
                    } else if (method.getReturnType().equals(finderClass)) {
                        buildMethod = method;
                    }
                }
                if (buildMethod == null) {
                    continue;
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
