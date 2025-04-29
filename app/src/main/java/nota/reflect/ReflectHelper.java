package nota.reflect;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectHelper {
    /**
     * 获取一个class的实例，需要调用者指定参数类型，主要用来调用含有基本类型{如int,double等}的构造方法
     *
     * @param className   需要构造的类类型
     * @param paramsTypes 参数类型数组，需要与params一一对应
     * @param params      参数数组
     * @return 返回实例后的对象
     * @throws Exception 如果实例化不成功，抛出异常
     */
    public static Object reflectConstructor(String className, Class<?>[] paramsTypes, Object[] params) throws Exception {
        if (params != null && params.length > 0) {
            Class clazz = ReflectionCache.build().forName(className);
            Constructor<?> con = clazz.getConstructor(paramsTypes);
            return con.newInstance(params);
        } else {
            Class clazz = ReflectionCache.build().forName(className);
            Constructor<?> con = clazz.getConstructor();
            return con.newInstance();
        }
    }

    /**
     * 通过反射执行一个类的静态方法，如果无参数，params传入null
     *
     * @param className   需要执行操作的对象实例
     * @param methodName  需要执行的方法名
     * @param paramsTypes 参数类型数组，需要与params一一对应
     * @param params      参数数组
     * @return 返回执行结果
     * @throws Exception 如果执行失败，抛出异常
     */
    public static Object invokeStatic(String className, String methodName, Class<?>[] paramsTypes, Object[] params) throws Exception {
        Class<?> objClass = ReflectionCache.build().forName(className);
        return invoke(objClass, null, methodName, paramsTypes, params);
    }

    /**
     * 通过反射执行一个类的静态方法，如果无参数，params传入null
     *
     * @param className  需要执行操作的类名称,包括包名
     * @param methodName 需要执行的方法名
     * @param params     参数数组
     * @return 返回执行结果
     * @throws Exception 如果执行失败，抛出异常
     */
    public static Object invokeStatic(String className, String methodName, Object[] params) throws Exception {

        Class<?> objClass = ReflectionCache.build().forName(className);
        return invoke(objClass, null, methodName, params);
    }

    private static Class<?>[] getParamsTypes(Object[] params) {
        Class<?>[] paramsType = new Class[params.length];
        for (int i = 0; i < paramsType.length; ++i) {
            paramsType[i] = params[i].getClass();
        }
        return paramsType;
    }

    /**
     * 通过反射执行一个对象的方法，如果无参数，params传入null
     *
     * @param target     需要执行操作的对象实例
     * @param methodName 需要执行的方法名
     * @param params     参数数组
     * @return 返回执行结果
     * @throws Exception 如果执行失败，抛出异常
     */
    public static Object invoke(Object target, String methodName, Object[] params) throws Exception {
        return invoke(target.getClass(), target, methodName, params);
    }

    private static Object invoke(Class<?> objClass, Object target, String methodName, Object[] params) throws Exception {
        if (params == null || params.length == 0) {
            Method method = ReflectionCache.build().getDeclaredMethod(objClass, methodName);
            method.setAccessible(true);
            return method.invoke(target);
        } else {
            Method method = ReflectionCache.build().getDeclaredMethod(objClass, methodName, getParamsTypes(params));
            method.setAccessible(true);
            return method.invoke(target, params);
        }
    }

    /**
     * 通过反射执行一个对象的方法，如果无参数，params传入null
     *
     * @param target      需要执行操作的对象实例
     * @param methodName  需要执行的方法名
     * @param paramsTypes 参数类型数组，需要与params一一对应
     * @param params      参数数组
     * @return 返回执行结果
     * @throws Exception 如果执行失败，抛出异常
     */
    public static Object invoke(Object target, String methodName, Class<?>[] paramsTypes, Object[] params) throws Exception {
        return invoke(target.getClass(), target, methodName, paramsTypes, params);
    }

    private static Object invoke(Class<?> objClass, Object target, String methodName, Class<?>[] paramsTypes, Object[] params) throws Exception {
        if (params == null || params.length == 0) {
            Method method = ReflectionCache.build().getDeclaredMethod(objClass, methodName);
            method.setAccessible(true);
            return method.invoke(target);
        } else {
            Method method = ReflectionCache.build().getDeclaredMethod(objClass, methodName, paramsTypes);
            method.setAccessible(true);
            return method.invoke(target, params);
        }
    }

    /**
     * 设置某一个成员变量的值，注意，该变量必须是在desClass类所定义的变量。
     *
     * @param desObj    需要设置的对象
     * @param desClass  目标变量所定义的类
     * @param fieldName 变量名称
     * @param value     需要设置的值
     * @return 是否设置成功
     */
    public static boolean setField(Object desObj, Class<?> desClass, String fieldName, Object value) {
        if (desClass == null || fieldName == null) {
            throw new IllegalArgumentException("parameter can not be null!");
        }
        try {
            // 注:使用desClass.getField(fieldName)这个方法只能获取到public的field
            Field field = desClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(desObj, value);
            return true;
        } catch (Exception ignore) {
            //e.printStackTrace();
        }
        return false;
    }

    private static boolean setFieldStepwise(Object desObj, Class<?> rootClass, String fieldName, Object value) {
        Class<?> desClass = rootClass;
        while (desClass != null) {
            if (setField(desObj, desClass, fieldName, value)) {
                return true;
            } else {
                try {
                    desClass = desClass.getSuperclass();
                } catch (Exception e) {
                    desClass = null;
                }
            }
        }
        return false;
    }

    /**
     * 设置某一个静态成员变量的值。
     *
     * @param className 需要设置的类名
     * @param fieldName 变量名称
     * @param value     需要设置的值
     * @return 是否设置成功
     */
    public static boolean setStaticField(String className, String fieldName, Object value) {
        if (className == null || fieldName == null) {
            throw new IllegalArgumentException("parameter can not be null!");
        }
        Class<?> objClass;
        try {
            objClass = ReflectionCache.build().forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("className not found");
        }
        return setFieldStepwise(null, objClass, fieldName, value);
    }

    /**
     * 获取某一个成员变量的值，注意，该变量必须是在desClass类所定义的变量。
     * <br/>如果不知道变量定义在类继承层次结构哪一个类，请使用{@link #getField(Object, String)}
     *
     * @param desObj    需要获取的对象
     * @param desClass  目标变量所定义的类
     * @param fieldName 变量名称
     * @return 返回获取结果
     * @throws NoSuchFieldException 如果找不到该变量，则抛出异常
     */
    public static Object getField(Object desObj, Class<?> desClass, String fieldName) throws NoSuchFieldException {
        if (desClass == null || fieldName == null) {
            throw new IllegalArgumentException("parameter can not be null!");
        }
        try {
            Field field = ReflectionCache.build().getDeclaredField(desClass, fieldName);
            field.setAccessible(true);
            return field.get(desObj);
        } catch (Exception ignore) {
            throw new NoSuchFieldException(fieldName);
        }
    }

    /**
     * 获取某一个成员变量的值。
     * <br/>为了提高效率，如果知道变量定义在类继承层次结构哪一个类，请使用{@link #getField(Object, Class, String)}
     *
     * @param desObj    需要获取的对象
     * @param fieldName 变量名称
     * @return 返回获取结果
     * @throws NoSuchFieldException 如果找不到该变量，则抛出异常
     */
    public static Object getField(Object desObj, String fieldName) throws NoSuchFieldException {
        if (fieldName == null) {
            throw new IllegalArgumentException("parameter can not be null!");
        }
        Class<?> desClass = desObj.getClass();
        return getFieldStepwise(desObj, desClass, fieldName);
    }

    private static Object getFieldStepwise(Object desObj, Class<?> rootClass, String fieldName) throws NoSuchFieldException {
        Class<?> desClass = rootClass;
        while (desClass != null) {
            try {
                return getField(desObj, desClass, fieldName);
            } catch (NoSuchFieldException ignore) {
            }
            try {
                desClass = desClass.getSuperclass();
            } catch (Exception e) {
                desClass = null;
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    /**
     * 获取某一个静态成员变量的值。
     *
     * @param className 需要获取的类名
     * @param fieldName 变量名称
     * @return 返回获取结果
     * @throws NoSuchFieldException 如果找不到该变量，则抛出异常
     */
    public static Object getStaticField(String className, String fieldName) throws NoSuchFieldException {
        if (className == null || fieldName == null) {
            throw new IllegalArgumentException("parameter can not be null!");
        }
        Class<?> objClass;
        try {
            objClass = ReflectionCache.build().forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("className not found");
        }
        return getFieldStepwise(null, objClass, fieldName);
    }

}
