package com.rangi.nanodet;

import android.graphics.Color;
import android.graphics.RectF;

import java.util.Random;

public class Box {
    public float x0,y0,x1,y1;
    private int label;
    private float score;
    private static String[] labels={"A", "B", "C", "D", "E", "F", "G", "比心","我爱你","好的","耶","H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};
    public Box(float x0,float y0, float x1, float y1, int label, float score){
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.label = label;
        this.score = score;
    }

    public RectF getRect(){
        return new RectF(x0,y0,x1,y1);
    }

    public String getLabel(){
        return labels[label];
    }

    public float getScore(){
        return score;
    }

    public int getColor(){
        Random random = new Random(label);
        return Color.argb(255,random.nextInt(256),random.nextInt(256),random.nextInt(256));
    }
}
