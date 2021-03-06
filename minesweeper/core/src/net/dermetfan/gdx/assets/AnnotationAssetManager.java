/*
 * Copyright 2015 Robin Stumm (serverkorken@gmail.com, http://dermetfan.net)
 * <br>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <br>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <br>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.dermetfan.gdx.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import dot.empire.ms.Minesweeper;

import java.lang.annotation.*;


/**
 * An AssetManager that loads assets from annotated fields and methods using reflection.
 *
 * @author dermetfan
 * @author Matthew 'siD' Van der Bijl
 * @see <a href="https://bitbucket.org/dermetfan/libgdx-utils/src/default/src/main/net/dermetfan/gdx/assets/AnnotationAssetManager.java">dermetfan's Annotation Asset Manager</a>
 */
// https://bitbucket.org/dermetfan/libgdx-utils/src/default/src/main/net/dermetfan/gdx/assets/AnnotationAssetManager.java
public class AnnotationAssetManager extends AssetManager {

    public AnnotationAssetManager() {
        super();
    }

    /**
     * @param field     the field which value to get
     * @param container An instance of the field's declaring class. May be null if it's static.
     * @return the value of the field
     */
    private static Object get(Field field, Object container) throws IllegalArgumentException {
        if (container == null && !field.isStatic()) {
            throw new IllegalArgumentException("field is not static but container instance is null: " + field.getName());
        }
        boolean accessible = field.isAccessible();
        if (!accessible) {
            field.setAccessible(true);
        }
        Object obj = null;
        try {
            obj = field.get(container);
        } catch (ReflectionException re) {
            Gdx.app.error("AnnotationAssetManager", "could not access " + field, re);
        }
        if (!accessible) {
            field.setAccessible(false);
        }
        return obj;
    }

    /**
     * @param method     the method to invoke
     * @param container  An instance of the method's declaring class. May be null if it's static.
     * @param parameters the parameters with which to invoke the method
     * @return the return value of the method
     */
    private static Object invoke(Method method, Object container, Object... parameters) throws IllegalArgumentException {
        if (container == null && !method.isStatic()) {
            throw new IllegalArgumentException("method is not static but container instance is null: " + method.getName());
        }
        boolean accessible = method.isAccessible();
        if (!accessible) {
            method.setAccessible(true);
        }
        Object obj = null;
        try {
            obj = method.invoke(container, parameters);
        } catch (ReflectionException re) {
            Gdx.app.error("AnnotationAssetManager", "could not invoke " + method, re);
        }
        if (!accessible) {
            method.setAccessible(false);
        }
        return obj;
    }

    /**
     * @param pathObj the Object specifying a path of an asset
     * @return the path of the Asset specified by the given Object
     */
    private static String getAssetPath(Object pathObj) {
        if (pathObj instanceof FileHandle) {
            return ((FileHandle) pathObj).path();
        } else if (pathObj instanceof AssetDescriptor) {
            return ((AssetDescriptor<?>) pathObj).fileName;
        } else {
            return pathObj.toString();
        }
    }

    /**
     * @param asset   The Asset annotation the field or method pathObj was extracted from is annotated with. May be null if pathObj is an AssetDescriptor.
     * @param pathObj the Object specifying a path of an asset, extracted from the field or method annotated with the given Asset annotation
     * @return the type of the specified asset
     */
    private static Class getAssetType(Asset asset, Object pathObj) {
        if (pathObj instanceof AssetDescriptor) {
            return ((AssetDescriptor<?>) pathObj).type;
        }
        return asset.value();
    }

    /**
     * @param asset     the Asset annotation the field or method pathObj was extracted from is annotated with
     * @param pathObj   the Object specifying a path of an asset, extracted from the field or method annotated with the given Asset annotation
     * @param container An instance of the field's or method's declaring class. May be null if it's static.
     * @return the AssetLoaderParameters associated with the specified asset
     */
    private static AssetLoaderParameters getAssetLoaderParameters(Asset asset, Object pathObj,
                                                                  Class containerType, Object container) {
        if (pathObj instanceof AssetDescriptor) {
            return ((AssetDescriptor) pathObj).params;
        } else if (asset.param().isEmpty()) {
            return null;
        }

        String location = asset.param();
        boolean method = location.endsWith("()"); // if a method contains the AssetLoaderParameters
        Class<?> clazz; // class of the field or method containing the AssetLoaderParameters
        String name; // the name of the field or method inside clazz
        if (location.contains(".")) { // fully qualified path
            int end = location.lastIndexOf('.');
            String className = location.substring(0, end);
            name = location.substring(end + 1, method ? location.lastIndexOf("()") : 0);
            try {
                clazz = ClassReflection.forName(className);
            } catch (ReflectionException re) {
                throw new IllegalArgumentException("Failed to load AssetLoaderParameters from " + location + ": class " + className + " does not exist");
            }
        } else { // in container class
            clazz = containerType;
            name = method ? location.substring(0, location.lastIndexOf("()")) : location;
        }

        if (method) {
            Method m;
            boolean withParams;
            try {
                m = ClassReflection.getDeclaredMethod(clazz, name, Class.class, String.class, Object.class);
                withParams = true;
            } catch (ReflectionException re0) {
                try {
                    m = ClassReflection.getDeclaredMethod(clazz, name);
                    withParams = false;
                } catch (ReflectionException re1) {
                    throw new GdxRuntimeException("failed to access method " + name, re1);
                }
            }
            if (!ClassReflection.isAssignableFrom(AssetLoaderParameters.class, m.getReturnType())) {
                throw new IllegalArgumentException("AssetLoaderParameters supplier method does not return AssetLoaderParameters: " + m.getReturnType());
            } else if (withParams) {
                return (AssetLoaderParameters) invoke(m, container, getAssetType(asset, pathObj), getAssetPath(pathObj), pathObj);
            } else {
                return (AssetLoaderParameters) invoke(m, container);
            }
        } else {
            try {
                Field f = ClassReflection.getDeclaredField(clazz, name);
                return (AssetLoaderParameters) get(f, container);
            } catch (ReflectionException e) {
                throw new GdxRuntimeException("failed to access field " + name, e);
            }
        }
    }

    /**
     * @param asset         The @Asset annotation annotating the field or method obj was extracted from. May be null if obj is an AssetDescriptor.
     * @param objPath       the Object describing the asset path, extracted from a field or method
     * @param containerType The class containing the field or method obj was extracted from. May be null if obj is an AssetDescriptor or no AssetLoaderParameters are specified by the @Asset annotation.
     * @param container     The instance of containerType. May be null if the field or method containing the AssetLoaderParameters is static or no AssetLoaderParameters are specified by the @Asset annotation.
     */
    private void load(Asset asset, Object objPath, Class<?> containerType, Object container) {
        if (objPath instanceof Object[]) {
            Object[] objPaths = (Object[]) objPath;
            for (Object path : objPaths) {
                load(asset, path, containerType, container);
            }
        } else {
            load(getAssetPath(objPath), getAssetType(asset, objPath),
                    getAssetLoaderParameters(asset, objPath, containerType, container));
        }
    }

    /**
     * @param container the class which fields and methods annotated with {@link Asset Asset} to load
     * @param instance  an instance of the container class
     */
    private <T> void load(Class<T> container, T instance) {
        for (Method method : ClassReflection.getDeclaredMethods(container)) {
            if (method.isAnnotationPresent(Asset.class)) {
                Asset asset = method.getDeclaredAnnotation(Asset.class).getAnnotation(Asset.class);
                if (asset.load()) {
                    load(method, instance);
                }
            }
        }
        for (Field field : ClassReflection.getDeclaredFields(container)) {
            if (field.isAnnotationPresent(Asset.class)) {
                Asset asset = field.getDeclaredAnnotation(Asset.class).getAnnotation(Asset.class);
                if (asset.load()) {
                    load(field, instance);
                }
            }
        }
    }

    /**
     * See {@code load(Class, Object)}.
     */
    @SuppressWarnings("unchecked")
    public <T> void load(T container) {
        load((Class<T>) container.getClass(), container);
    }

    /**
     * @param field     the field which value to load
     * @param container An instance of the field's declaring class. May be null if it's static.
     */
    private void load(Field field, Object container) {
        load(field.isAnnotationPresent(Asset.class) ? field.getDeclaredAnnotation(Asset.class).getAnnotation(Asset.class)
                : null, get(field, container), field.getDeclaringClass(), container);
    }

    /**
     * @param method    the method which return value to load
     * @param container An instance of the method's declaring class. May be null if it's static.
     */
    private void load(Method method, Object container) throws IllegalArgumentException {
        if (method.getParameterTypes().length != 0) {
            throw new IllegalArgumentException(method.getName() + " takes parameters. Methods that take parameters are not supported.");
        } else if (method.getReturnType().isPrimitive()) {
            throw new IllegalArgumentException(method.getName() + " returns " + method.getReturnType() + ". Methods that return primitives are not supported.");
        }
        load(method.isAnnotationPresent(Asset.class) ? method.getDeclaredAnnotation(Asset.class).getAnnotation(Asset.class)
                : null, invoke(method, container), method.getDeclaringClass(), container);
    }

    public synchronized <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
        if (!isLoaded(fileName, type)) {
            Gdx.app.debug(Minesweeper.TAG, String.format("Loading %s = %s", type.getSimpleName(), fileName));
            super.load(fileName, type, parameter);
        }
    }

    @Override
    public synchronized <T> T get(String fileName, Class<T> type) {
        // Gdx.app.debug(Minesweeper.TAG, String.format("Getting %s = %s", type.getSimpleName(), fileName));
        return super.get(fileName, type);
    }

    /**
     * Provides information about assets that fields or methods represent. The toString value of the value of the field
     * or return value of the method annotated is used as path (except for {@link FileHandle} and {@link AssetDescriptor}
     * which get special treatment). Methods annotated with this annotation must not return a primitive and have no parameters.
     *
     * @author dermetfan
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD})
    public @interface Asset {

        /**
         * @return Whether this field or method should be loaded by {@code AnnotationAssetManager#load(Class, Object)}.
         * Default is {@code true}.
         */
        boolean load() default true;

        /**
         * @return the type of the asset this field or method represents
         */
        Class<?> value() default void.class;

        /**
         * Methods referenced by this can either have no parameters or take a {@code Class}, {@code String} and
         * {@link Object} in this order. The Class is the type of the asset, the String is the path of the asset and the
         * Object is the value of the field or return value of the method from which the asset is being loaded.
         *
         * @return The fully qualified or simple name of a field or method providing AssetLoaderParameters.
         * If the name is simple, the declaring class of this field or method is assumed to be the declaring class of the
         * AssetLoaderParameters field or method as well.
         */
        String param() default "";
    }
}
