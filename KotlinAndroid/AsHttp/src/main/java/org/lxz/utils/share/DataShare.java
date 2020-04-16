package org.lxz.utils.share;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class DataShare {
    private static String filename = "DataObject";
    private static DataShare dataObjectShare;
    private static Context context;
    private AndroidShare share;

    private static Map<Class, Object> map = new HashMap<>();

    public static void init(Context ctx) {
        context = ctx;
        getInstance();
    }

    public static DataShare getInstance() {
        if (context == null) {
            throw new RuntimeException("context must no null ,It is recommended to initialize in Appcation");
        }
        return dataObjectShare == null ? dataObjectShare = new DataShare() : dataObjectShare;
    }


    public DataShare() {
        super();
        share = new AndroidShare(context, filename);
    }

    public static void clear() {
        map.clear();
        getInstance().share.clear(context, filename);
    }





    public Build begin(){
        return new Build();
    }

    public class Build{
        SharedPreferences.Editor localEditor = share.settings.edit();
        public  void put(String key,String value) {
            SharedPreferences.Editor localEditor = share.settings.edit();
            localEditor.putString(key, value);
        }
        public  void saveJsonObject(String key,String value) {
            SharedPreferences.Editor localEditor = share.settings.edit();
            localEditor.putString(key, value);
        }
        public void commit(){
            localEditor.commit();
        };
    }

    public static boolean saveJsonObject(final Object obj) {

        try {

            map.put(obj.getClass(), obj);
            getInstance().share.put(obj.getClass().getName(),
                    new Gson().toJson(obj));

            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }


    public static Object saveJsonObject(String key,final Object obj) {
            getInstance().share.put(key,
                    new Gson().toJson(obj));
            return obj;
    }

    public static <T> T getJsonObject(Class<T> cls) {

        try {

            Object obj = map.get(cls);
            if (obj != null) return (T) obj;
            String content = getInstance().share.getString(cls.getName());
            return new Gson().fromJson(content, cls);
        } catch (Exception e) {

        }
        return null;
    }

    public static <T> Observable<T> getJsonObjectObservable(final Class<T> cls) {
        return Observable.defer(new Callable<ObservableSource<? extends T>>() {
            @Override
            public ObservableSource<? extends T> call() throws Exception {
                return Observable.create(new ObservableOnSubscribe<T>() {
                    @Override
                    public void subscribe(ObservableEmitter<T> e) throws Exception {
                        Object obj = map.get(cls);
                        if (obj != null) {
                            e.onNext((T) obj);
                        }else {
                            String content = getInstance().share.getString(cls.getName());
                            if (content != null) {
                                obj = new Gson().fromJson(content, cls);
                                if (obj != null) {
                                    e.onNext(new Gson().fromJson(content, cls));
                                } else {
                                    e.onError(new NullPointerException(cls.getName()));
                                }
                            } else {
                                e.onError(new NullPointerException(cls.getName()));
                            }
                        }

                        e.onComplete();
                    }


                });

            }
        });


    }



    public static boolean clean(Class cls) {
        map.remove(cls);
        getInstance().share.put(cls.getName(), "");
        return true;

    }

    public static void put(String key, String value) {getInstance().share.put(key, value);}

    public static void put(String key, Integer value) {
        getInstance().share.put(key, value);
    }

    public static void put(String key, Float value) {
        getInstance().share.put(key, value);
    }

    public static void put(String key, Boolean value) {
        getInstance().share.put(key, value);
    }

    public static void put(String key, Long value) {
        getInstance().share.put(key, value);
    }


    public static String getString(String key) {
        return getInstance().share.getString(key);
    }

    public static String getString(String key, String defaultvalue) {
        return getInstance().share.getString(key, defaultvalue);
    }

    public static Integer getInt(String key) {
        return getInstance().share.getInt(key);
    }

    public static Integer getInt(String key, Integer defaultvalue) {
        return getInstance().share.getInt(key, defaultvalue);
    }

    public static boolean getBoolean(String key) {
        return getInstance().share.getBoolean(key,false);
    }

    public static boolean getBoolean(String key, Boolean defaultvalue) {
        return getInstance().share.getBoolean(key, defaultvalue);
    }


    public static long getLong(String key) {
        return getInstance().share.getLong(key);
    }

    public static long getLong(String key, Long defaultvalue) {
        return getInstance().share.getLong(key, defaultvalue);
    }

    public static float getFloat(String key) {
        return getInstance().share.getFloat(key,0);
    }

    public static float getFloat(String key, float defaultvalue) {
        return getInstance().share.getFloat(key, defaultvalue);
    }




    public void clear(String filename) {
        getInstance().share.clear(context, filename);
    }

    public AndroidShare getAndroidShare() {
        return share;
    }
}
