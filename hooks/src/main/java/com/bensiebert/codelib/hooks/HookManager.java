package com.bensiebert.codelib.hooks;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class HookManager {

    private static final Map<String, List<MethodInvoker>> registry = new ConcurrentHashMap<>();

    private HookManager() { /* no instances */ }

    public static void scan(String... basePackages) {
        Set<Class<?>> hookAnnoTypes = null;

        for(String basePackage : basePackages) {
            Set<Class<?>> annotations = scanForAnnotations(basePackage);
            if(hookAnnoTypes == null) {
                hookAnnoTypes = annotations;
            } else {
                hookAnnoTypes.addAll(annotations);
            }
        }

        Set<Method> methods = null;

        for(String basePackage : basePackages) {
            Set<Method> scannedMethods = scanForMethods(basePackage, hookAnnoTypes.toArray(new Class[0]));
            if(methods == null) {
                methods = scannedMethods;
            } else {
                methods.addAll(scannedMethods);
            }
        }

        for(Class<?> hookType : hookAnnoTypes) {
            for(Method method : methods) {
                if(method.isAnnotationPresent((Class<? extends Annotation>) hookType)) {
                    String category = method.getAnnotation((Class<? extends Annotation>) hookType).annotationType().getAnnotation(Hook.class).value();
                    register(category, method);
                }
            }
        }

    }

    public static Set<Class<?>> scanForAnnotations(String basePackage) {
        Reflections refl = new Reflections(new ConfigurationBuilder()
                .forPackage(basePackage)
                .addScanners(Scanners.TypesAnnotated)
        );

        return refl.getTypesAnnotatedWith(Hook.class);
    }

    public static Set<Method> scanForMethods(String basePackage, Class<? extends Annotation>... annotationClasses) {
        Reflections refl = new Reflections(new ConfigurationBuilder()
                .forPackage(basePackage)
                .addScanners(Scanners.MethodsAnnotated)
        );

        Set<Method> methods = new HashSet<>();
        for(Class<? extends Annotation> annoClass : annotationClasses) {
            methods.addAll(refl.getMethodsAnnotatedWith(annoClass));
        }

        return methods;
    }


    private static void register(String category, Method method) {
        System.out.println("Registering hook: " + category + " -> " + method);
        method.setAccessible(true);
        Object bean;
        try {
            // instantiate the declaring class (must have no-arg ctor)
            bean = method.getDeclaringClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to instantiate hook-handling class "
                            + method.getDeclaringClass(), e);
        }
        registry
                .computeIfAbsent(category, k -> new ArrayList<>())
                .add(new MethodInvoker(bean, method));
    }

    /**
     * Fire all hooks registered under the given category, passing args.
     */
    public static void fire(String category, Object... args) {
        List<MethodInvoker> invokers = registry.getOrDefault(category, Collections.emptyList());
        for (MethodInvoker inv : invokers) {
            inv.invoke(args);
        }
    }

    // simple holder for bean + method
    private static class MethodInvoker {
        final Object bean;
        final Method method;

        MethodInvoker(Object bean, Method method) {
            this.bean = bean;
            this.method = method;
        }

        void invoke(Object... args) {
            try {
                method.invoke(bean, args);
            } catch (Exception e) {
                throw new RuntimeException("Hook invocation failed: " + method, e);
            }
        }
    }
}
