package com.translator.view.uml.factory;

import org.jhotdraw.xml.DOMStorable;
import org.jhotdraw.xml.JavaPrimitivesDOMFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CodactorUmlBuilderDefaultDOMFactory extends JavaPrimitivesDOMFactory {
    private static final HashMap<Class<?>, String> classToNameMap = new HashMap();
    private static final HashMap<String, Object> nameToPrototypeMap = new HashMap();
    private static final HashMap<Class<?>, String> enumClassToNameMap = new HashMap();
    private static final HashMap<String, Class<?>> nameToEnumClassMap = new HashMap();
    private static final HashMap<Enum, String> enumToValueMap = new HashMap();
    private static final HashMap<String, Set<Enum>> valueToEnumMap = new HashMap();

    public CodactorUmlBuilderDefaultDOMFactory() {
    }

    public void addStorableClass(String name, Class<?> c) {
        nameToPrototypeMap.put(name, c);
        classToNameMap.put(c, name);
    }

    public void addStorable(String name, DOMStorable prototype) {
        nameToPrototypeMap.put(name, prototype);
        classToNameMap.put(prototype.getClass(), name);
    }

    public void addEnumClass(String name, Class<?> c) {
        enumClassToNameMap.put(c, name);
        nameToEnumClassMap.put(name, c);
    }

    public <T extends Enum<T>> void addEnum(String value, Enum<T> e) {
        enumToValueMap.put(e, value);
        Set<Enum> enums;
        if (valueToEnumMap.containsKey(value)) {
            enums = (Set)valueToEnumMap.get(value);
        } else {
            enums = new HashSet<>();
            valueToEnumMap.put(value, enums);
        }

        ((Set)enums).add(e);
    }

    public Object create(String name) {
        Object o = nameToPrototypeMap.get(name);

        if (o == null) {
            throw new IllegalArgumentException("Storable name not known to factory: " + name);
        } else if (o instanceof Class) {
            try {
                return ((Class)o).getDeclaredConstructor().newInstance();
            } catch (Exception var4) {
                var4.printStackTrace();
                throw new IllegalArgumentException("Storable class not instantiable by factory: " + name, var4);
            }
        } else {
            try {
                return o.getClass().getMethod("clone", (Class[])null).invoke(o, (Object[])null);
            } catch (Exception var5) {
                throw new IllegalArgumentException("Storable prototype not cloneable by factory. Name: " + name, var5);
            }
        }
    }

    public String getName(Object o) {
        String name = o == null ? null : (String)classToNameMap.get(o.getClass());
        if (name == null) {
            name = super.getName(o);
        }

        if (name == null) {
            String var10002 = String.valueOf(o == null ? null : o.getClass());
            throw new IllegalArgumentException("Storable class not known to factory. Storable class:" + var10002 + " Factory:" + String.valueOf(this.getClass()));
        } else {
            return name;
        }
    }

    protected String getEnumName(Enum e) {
        String name = (String)enumClassToNameMap.get(e.getClass());
        if (name == null) {
            throw new IllegalArgumentException("Enum class not known to factory:" + String.valueOf(e.getClass()));
        } else {
            return name;
        }
    }

    protected String getEnumValue(Enum e) {
        return enumToValueMap.containsKey(e) ? (String)enumToValueMap.get(e) : e.toString();
    }

    protected <T extends Enum<T>> Enum<T> createEnum(String name, String value) {
        Class<T> enumClass = (Class)nameToEnumClassMap.get(name);
        if (enumClass == null) {
            throw new IllegalArgumentException("Enum name not known to factory:" + name);
        } else {
            Set<Enum> enums = (Set)valueToEnumMap.get(value);
            if (enums == null) {
                return Enum.valueOf(enumClass, value);
            } else {
                Iterator var5 = enums.iterator();

                Enum e;
                do {
                    if (!var5.hasNext()) {
                        throw new IllegalArgumentException("Enum value not known to factory:" + value);
                    }

                    e = (Enum)var5.next();
                } while(e.getClass() != enumClass);

                return e;
            }
        }
    }
}
