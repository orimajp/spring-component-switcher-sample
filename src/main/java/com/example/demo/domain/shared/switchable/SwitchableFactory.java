package com.example.demo.domain.shared.switchable;

import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SwitchableFactory<T, E extends Switchable<T>> {

    private final Map<T, E> componentMap = new HashMap<>();

    private E defaultComponent;

    public void register(@NonNull List<E> switchables) {
        switchables.forEach(this::registerComponentMap);

        if (componentMap.isEmpty()) {
            throw new RuntimeException("No components");
        }

        defaultComponent = getDefaultComponent();
    }

    public E getComponent() {
        if (defaultComponent == null) {
            throw new RuntimeException("Not initialized.");
        }

        final E component = selectComponent();

        if (component == null) {
            return defaultComponent;
        }

        return component;
    }

    protected abstract T getComponentKey();

    protected boolean isAllowNullKeys() {
        return false;
    }

    protected E selectComponent() {
        final T componentKey = getComponentKey();

        if (componentKey == null) {
            if (isAllowNullKeys()){
                return defaultComponent;
            }

            throw new RuntimeException("Component Key is null.");
        }

        return componentMap.get(componentKey);
    }

    private void registerComponentMap(@NonNull E switchable) {
        final E result = componentMap.putIfAbsent(switchable.getComponentKey(), switchable);

        if (result != null) {
            throw new RuntimeException("Duplicate key found.");
        }
    }

    private E getDefaultComponent() {
        final List<E> defaultComponents = componentMap.values().stream()
                .filter(Switchable::isDefault)
                .collect(Collectors.toList());

        if (defaultComponents.isEmpty()) {
            throw new RuntimeException("No default implements");
        }

        if (defaultComponents.size() > 1) {
            throw new RuntimeException("Multiple default implements");
        }

        return defaultComponents.get(0);
    }

}
