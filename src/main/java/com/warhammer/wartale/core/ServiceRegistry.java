package com.warhammer.wartale.core;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceRegistry {
    private static final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    private ServiceRegistry() {
    }

    public static <T> void register(Class<T> serviceClass, T instance) {
        services.put(serviceClass, instance);
    }

    public static <T> void unregister(Class<T> serviceClass) {
        services.remove(serviceClass);
    }

    public static <T> T get(Class<T> serviceClass) {
        Object service = services.get(serviceClass);
        if (service == null) {
            throw new IllegalStateException("Service not registered: " + serviceClass.getName());
        } else {
            return serviceClass.cast(service);
        }
    }

    public static <T> Optional<T> getOptional(Class<T> serviceClass) {
        return Optional.ofNullable(services.get(serviceClass)).map(serviceClass::cast);
    }

    public static boolean isRegistered(Class<?> serviceClass) {
        return services.containsKey(serviceClass);
    }

    public static void clear() {
        services.clear();
    }
}
