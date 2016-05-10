package com.example.ishow.Bean;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MRME on 2016-05-08.
 */
public class VideoEntry implements Parcelable {

   /* public VideoEntry(int id,String filePath,long duration,String mineType,String bucketName){
        this.duration = duration;
        this.bucketName =bucketName;
        this.filePath = filePath;
        this.mediaId = id;
        this.ymimeTpe =mineType;
    }*/
    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getYmimeTpe() {
        return ymimeTpe;
    }

    public void setYmimeTpe(String ymimeTpe) {
        this.ymimeTpe = ymimeTpe;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    int mediaId ;
    String filePath ;
    String ymimeTpe;
    long duration ;
    String bucketName;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    long size;


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    Bitmap bitmap;
    int fileCount;

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }




    @Override
    public String toString() {
        return "VideoEntry{" +
                "mediaId=" + mediaId +
                ", filePath='" + filePath + '\'' +
                ", ymimeTpe='" + ymimeTpe + '\'' +
                ", duration='" + duration + '\'' +
                ", bucketName='" + bucketName + '\'' +
                ", bitmap='" + bitmap + '\'' +
                ", fileCount='" + fileCount + '\'' +
                ", size='" + size + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mediaId);
        dest.writeString(this.filePath);
        dest.writeString(this.ymimeTpe);
        dest.writeLong(this.duration);
        dest.writeString(this.bucketName);
        dest.writeParcelable(this.bitmap, flags);
        dest.writeInt(this.fileCount);
        dest.writeLong(this.size);
    }

    public VideoEntry() {
    }

    protected VideoEntry(Parcel in) {
        this.mediaId = in.readInt();
        this.filePath = in.readString();
        this.ymimeTpe = in.readString();
        this.duration = in.readLong();
        this.bucketName = in.readString();
        this.bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        this.fileCount = in.readInt();
        this.size = in.readLong();
    }

    public static final Creator<VideoEntry> CREATOR = new Creator<VideoEntry>() {
        @Override
        public VideoEntry createFromParcel(Parcel source) {
            return new VideoEntry(source);
        }

        @Override
        public VideoEntry[] newArray(int size) {
            return new VideoEntry[size];
        }
    };
}
