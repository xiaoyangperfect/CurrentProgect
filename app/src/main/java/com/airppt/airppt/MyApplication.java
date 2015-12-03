package com.airppt.airppt;

import android.app.Application;
import android.support.v4.app.FragmentActivity;

import com.flurry.android.FlurryAgent;
import com.airppt.airppt.util.ImageOptUtil;

import java.util.Stack;

/**
 * Created by user on 2015/4/7.
 */
public class MyApplication extends Application {

    private static MyApplication instance;
    private Stack<FragmentActivity> stack;

    private String FLURRYKEY = "PP7Y5FBC6W5WVQ3VMR7B";

    @Override
    public void onCreate() {
        super.onCreate();

        ImageOptUtil.initImageLoader(getApplicationContext());
        instance = this;
        stack = new Stack<>();
        FlurryAgent.setLogEnabled(false);
        FlurryAgent.init(this, FLURRYKEY);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public void popActivity(){
        FragmentActivity activity= stack.lastElement();
        if(activity!=null){
            activity.finish();
            activity=null;
        }
    }
    public void popActivity(FragmentActivity activity) {
        if (activity != null) {
            activity.finish();
            stack.remove(activity);
            activity = null;
        }
    }

    public FragmentActivity currentActivity(){
        FragmentActivity activity= stack.lastElement();
        return activity;
    }
    public void pushActivity(FragmentActivity activity){
        if(stack==null){
            stack=new Stack<FragmentActivity>();
        }
        stack.add(activity);
    }
    public void popAllActivityExceptOne(Class cls){
//        for (FragmentActivity activity:stack) {
        for (int i = 0; i < stack.size(); i++) {
            FragmentActivity activity = stack.get(i);
            if (activity == null){
                continue;
            }
            if (activity.getClass().equals(cls)) {
                continue;
            }
            popActivity(activity);
        }
    }

    public void finished() {
        for (int i = 0; i < stack.size(); i++) {
            FragmentActivity activity = stack.get(i);
            if (activity == null){
                continue;
            }
            activity.finish();
        }
    }

}
