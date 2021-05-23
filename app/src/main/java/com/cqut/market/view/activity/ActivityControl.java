package com.cqut.market.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityControl {
    private static List<AppCompatActivity> activities=new ArrayList<>();

    public static void addActivity(AppCompatActivity activity){
        activities.add(activity);
    }
    public static void finishAllActivities(){
        for (AppCompatActivity baseActivity:activities)
            baseActivity.finish();
        activities.clear();
    }

    public static void remove(AppCompatActivity activity){
        activities.remove(activity);
    }
}
