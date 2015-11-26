package com.xutong.uploadlocation;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2015/10/20.
 */
public class UploadService extends Service {

//    private static final String FILE_NAME = Environment.getExternalStorageDirectory() + File.separator +
//            "Download/_name.txt";

      private static final String FILE_NAME = Environment.getExternalStorageDirectory() + File.separator +
               "widgetone/apps/11434235/data/_name.txt";

    private CompositeSubscription mCompositeSubscription;

    private UserInfo mUserInfo;

    private LocationManager locationManager;
    private String locationProvider;

    private String mUserId;
    private String mPwd;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME,Context.MODE_PRIVATE);
        mUserId = prefs.getString(Constants.KEY_USERID,"");
        mPwd = prefs.getString(Constants.KEY_PWD,"");

        mCompositeSubscription = new CompositeSubscription();
    //    userLogin();

        //获取地理位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        final Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        locationCriteria.setPowerRequirement(Criteria.POWER_LOW);
        locationProvider = locationManager
                .getBestProvider(locationCriteria, true);

        pushLocation();



        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.drawable.ic_menu_add_dark);
        builder.setTicker("Foreground Service Start");
        builder.setContentTitle("Foreground Service");
        builder.setContentText("Make this service run in the foreground.");
        Notification notification = builder.build();
        startForeground(1, notification);
    }

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            LogUtil.i("Grace", "onLocationChanged");
            sendLocation(location);
        }
    };

    private void sendLocation(Location location) {
        Subscription subscription = Observable.just(location)
                .map(new Func1<Location, String>() {
                    @Override
                    public String call(Location location) {
                        LogUtil.i("Grace", "lon = " + location.getLongitude() + " lat = " + location.getLatitude());
                        if (mUserInfo != null) {
                            try {
                                Object object = WebserviceUtil.saveLocationMes(mUserInfo.getLoginUser(), String.valueOf(location.getLongitude()),
                                        String.valueOf(location.getLatitude()), "");
                                if (object != null && object instanceof SoapObject) {
                                    //    String json = ((SoapObject) object).getProperty(0).toString();
                                    //    LogUtil.i("Grace", "updateLocations json = " + json);
                                    return "upload success";
                                }
                            } catch (XmlPullParserException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return "no result";
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.i("Grace", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.i("Grace", "onError " + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(String s) {
                        LogUtil.i("Grace", "onNext " + s);
                    }
                });

        mCompositeSubscription.add(subscription);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        LogUtil.i("Grace", "UploadService onDestroy");
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
       /* if (locationManager != null) {
            try {
                locationManager.removeUpdates(locationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }*/
    }


    private void userLogin() {
        Subscription subscription = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onStart();
                try {
                    String json = null;
                    FileInputStream inputStream = new FileInputStream(FILE_NAME);
                    int length = inputStream.available();
                    byte[] buffer = new byte[length];
                    inputStream.read(buffer);
                    json = new String(buffer, "UTF-8");
                    subscriber.onNext(json);
                    subscriber.onCompleted();
                } catch (FileNotFoundException e) {
                    subscriber.onError(e);
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).map(new Func1<String, UserInfo>() {
            @Override
            public UserInfo call(String json) {
                Gson gson = new Gson();
                mUserInfo = gson.fromJson(json, UserInfo.class);
                return mUserInfo;
            }
        }).map(new Func1<UserInfo, Boolean>() {
            @Override
            public Boolean call(UserInfo userInfo) {
                boolean uploadSuccess = false;
                try {
                    Object object = WebserviceUtil.userLogin(userInfo.getLoginUser(), userInfo.getPw());
                    if (object != null && object instanceof SoapObject) {
                        String json = ((SoapObject) object).getProperty(0).toString();
                        LogUtil.i("Grace", "userLogin json = " + json);
                    }

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return uploadSuccess;
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
        mCompositeSubscription.add(subscription);
    }

    private void pushLocation() {
        Subscription subscription = Observable.just(true)
                .interval(10, TimeUnit.SECONDS)
                .map(new Func1<Long, Location>() {
                    @Override
                    public Location call(Long aLong) {
                        try{
                            return locationManager.getLastKnownLocation(locationProvider);
                        }catch (SecurityException e){
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .map(new Func1<Location, String>() {
                    @Override
                    public String call(Location location) {
                        if (location != null) {
                            try {
                                Object object = WebserviceUtil.saveLocationMes(mUserId, String.valueOf(location.getLongitude()),
                                        String.valueOf(location.getLatitude()), "");
                                if (object != null && object instanceof SoapObject) {
                                    //    String json = ((SoapObject) object).getProperty(0).toString();
                                    //    LogUtil.i("Grace", "updateLocations json = " + json);
                                    return "upload success";
                                }
                            } catch (XmlPullParserException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return "no result";
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.i("Grace", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.i("Grace", "onError " + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(String s) {
                        LogUtil.i("Grace", "onNext " + s);
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    private void updateLocations() {
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final LocationService locationService = new LocationService(locationManager);


        Subscription subscription = Observable.just(true)
                .interval(10, TimeUnit.SECONDS)
                .flatMap(new Func1<Long, Observable<Location>>() {
                    @Override
                    public Observable<Location> call(Long aLong) {
                        return locationService.getLocation();
                    }
                })
                .map(new Func1<Location, String>() {
                    @Override
                    public String call(Location location) {
                        LogUtil.i("Grace","lon = "+location.getLongitude()+" lat = "+location.getLatitude());
                        if (mUserInfo != null) {
                            try {
                                Object object = WebserviceUtil.saveLocationMes(mUserInfo.getLoginUser(), String.valueOf(location.getLongitude()),
                                        String.valueOf(location.getLatitude()), "");
                                if (object != null && object instanceof SoapObject) {
                                //    String json = ((SoapObject) object).getProperty(0).toString();
                                //    LogUtil.i("Grace", "updateLocations json = " + json);
                                    return "upload success";
                                }
                            } catch (XmlPullParserException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return "no result";
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.i("Grace", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.i("Grace", "onError " + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(String s) {
                        LogUtil.i("Grace", "onNext " + s);
                    }
                });

     /*   Subscription subscription = locationService.getLocation()
                .map(new Func1<Location, String>() {
                    @Override
                    public String call(Location location) {
                        LogUtil.i("Grace", "lon = " + location.getLongitude() + " lat = " + location.getLatitude());
                        if (mUserInfo != null) {
                            try {
                                Object object = WebserviceUtil.saveLocationMes(mUserInfo.getLoginUser(), String.valueOf(location.getLongitude()),
                                        String.valueOf(location.getLatitude()), "");
                                if (object != null && object instanceof SoapObject) {
                                    //    String json = ((SoapObject) object).getProperty(0).toString();
                                    //    LogUtil.i("Grace", "updateLocations json = " + json);
                                    return "upload success";
                                }
                            } catch (XmlPullParserException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return "no result";
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.i("Grace", "onCompleted");

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.i("Grace", "onError " + e.getMessage());
                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(String s) {
                        LogUtil.i("Grace", "onNext " + s);

                    }
                });*/
        mCompositeSubscription.add(subscription);
    }

}
