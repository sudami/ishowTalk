package com.example.ishow.Bean;

/**
 * Created by MRME on 2016-04-13.
 */
public class MusicEntry {
    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getHasTime() {
        return hasTime;
    }

    public void setHasTime(String hasTime) {
        this.hasTime = hasTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    String totalTime;
    String hasTime;

    public int getAnimationTime() {
        return animationTime;
    }

    public void setAnimationTime(int animationTime) {
        this.animationTime = animationTime;
    }

    int animationTime;

    @Override
    public String toString() {
        return "MusicEntry{" +
                "totalTime=" + totalTime +
                ", hasTime=" + hasTime +
                ", state=" + state +
                ", progress=" + progress +
                '}';
    }

    int state;//0 1 2 3 4 5  缓冲 播放 暂停 恢复 seekto error;
    float progress;
}
