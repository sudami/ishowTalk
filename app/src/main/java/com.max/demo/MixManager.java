package com.max.demo;

public class MixManager {
	static {
		System.loadLibrary("ffmpeg");
		System.loadLibrary("transwav");
	}

	public native String getStringJni();

	public native int TransMp3ToWav(String infile, String outFile, int sample,
									int channels, int volume);

	public native int MixMp4(String video, String audio, String outfile);

	public native int TransVideo(String acc, String ogg);

	public native int MixAudio(String audio, String aa, String mp4File);

	public native int getCurrSizeMp3();

	public native int getTotailLen(String infile);

}
