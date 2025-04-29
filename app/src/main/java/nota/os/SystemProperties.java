package nota.os;

import java.lang.reflect.Method;

import nota.reflect.ReflectionCache;

public class SystemProperties {

    /**
     * Get the value for the given key.
     *
     * @return an empty string if the key isn't found
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static String get(String key) throws IllegalArgumentException {

        String ret;

        try {
            @SuppressWarnings("rawtypes") Class systemProperties = ReflectionCache.build().forName("android.os.SystemProperties");

            //Parameters Types
            @SuppressWarnings("rawtypes") Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;

            Method get = ReflectionCache.build().getMethod(systemProperties, "get", paramTypes);

            //Parameters
            Object[] params = new Object[1];
            params[0] = key;

            ret = (String) get.invoke(systemProperties, params);

        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            ret = "";
        }

        return ret;

    }

    /**
     * Get the value for the given key.
     *
     * @return if the key isn't found, return def if it isn't null, or an empty string otherwise
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static String get(String key, String def) throws IllegalArgumentException {

        String ret;

        try {

            @SuppressWarnings("rawtypes") Class systemProperties = ReflectionCache.build().forName("android.os.SystemProperties");

            //Parameters Types
            @SuppressWarnings("rawtypes") Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = String.class;

            Method get = ReflectionCache.build().getMethod(systemProperties, "get", paramTypes);

            //Parameters
            Object[] params = new Object[2];
            params[0] = key;
            params[1] = def;

            ret = (String) get.invoke(systemProperties, params);

        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            ret = def;
        }

        return ret;

    }

    /**
     * Get the value for the given key, and return as an integer.
     *
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as an integer, or def if the key isn't found or
     * cannot be parsed
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static Integer getInt(String key, int def) throws IllegalArgumentException {

        Integer ret;

        try {

            @SuppressWarnings("rawtypes") Class systemProperties = ReflectionCache.build().forName("android.os.SystemProperties");

            //Parameters Types
            @SuppressWarnings("rawtypes") Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = int.class;

            Method getInt = ReflectionCache.build().getMethod(systemProperties, "getInt", paramTypes);

            //Parameters
            Object[] params = new Object[2];
            params[0] = key;
            params[1] = Integer.valueOf(def);

            ret = (Integer) getInt.invoke(systemProperties, params);

        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            ret = def;
        }

        return ret;

    }

    /**
     * Get the value for the given key, and return as a long.
     *
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as a long, or def if the key isn't found or
     * cannot be parsed
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static Long getLong(String key, long def) throws IllegalArgumentException {

        Long ret;

        try {

            @SuppressWarnings("rawtypes") Class systemProperties = ReflectionCache.build().forName("android.os.SystemProperties");

            //Parameters Types
            @SuppressWarnings("rawtypes") Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = long.class;

            Method getLong = ReflectionCache.build().getMethod(systemProperties, "getLong", paramTypes);

            //Parameters
            Object[] params = new Object[2];
            params[0] = key;
            params[1] = Long.valueOf(def);

            ret = (Long) getLong.invoke(systemProperties, params);

        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            ret = def;
        }

        return ret;

    }

    /**
     * Get the value for the given key, returned as a boolean.
     * Values 'n', 'no', '0', 'false' or 'off' are considered false.
     * Values 'y', 'yes', '1', 'true' or 'on' are considered true.
     * (case insensitive).
     * If the key does not exist, or has any other value, then the default
     * result is returned.
     *
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as a boolean, or def if the key isn't found or is
     * not able to be parsed as a boolean.
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static Boolean getBoolean(String key, boolean def) throws IllegalArgumentException {
        Boolean ret;

        try {

            @SuppressWarnings("rawtypes") Class systemProperties = ReflectionCache.build().forName("android.os.SystemProperties");

            //Parameters Types
            @SuppressWarnings("rawtypes") Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = boolean.class;

            Method getBoolean = ReflectionCache.build().getMethod(systemProperties, "getBoolean", paramTypes);

            //Parameters
            Object[] params = new Object[2];
            params[0] = key;
            params[1] = Boolean.valueOf(def);

            ret = (Boolean) getBoolean.invoke(systemProperties, params);

        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            ret = def;
        }

        return ret;

    }
}
