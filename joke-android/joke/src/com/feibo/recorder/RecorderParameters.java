package com.feibo.recorder;

import android.media.AudioFormat;


public class RecorderParameters {

    // TODO
    private static final int AV_CODEC_ID_MPEG4 = 1;
    private static final int AV_CODEC_ID_AAC = 2;
    private static final int AV_CH_LAYOUT_MONO = AudioFormat.CHANNEL_IN_MONO;

	private int videoCodec = AV_CODEC_ID_MPEG4;
	private int videoFrameRate = 15;
	private int videoQuality = 12;
	private int videoBitrate = 1000000;

	private int audioCodec = AV_CODEC_ID_AAC;
	private int audioChannel = AV_CH_LAYOUT_MONO;
	private int audioBitrate = 22050;
	private int audioSamplingRate = 22050;

	public int getAudioSamplingRate() {
		return audioSamplingRate;
	}

	public void setAudioSamplingRate(int audioSamplingRate) {
		this.audioSamplingRate = audioSamplingRate;
	}

	public int getVideoCodec() {
		return videoCodec;
	}

	public int getVideoFrameRate() {
		return videoFrameRate;
	}

	public void setVideoFrameRate(int videoFrameRate) {
		this.videoFrameRate = videoFrameRate;
	}

	public int getVideoQuality() {
		return videoQuality;
	}

	public void setVideoQuality(int videoQuality) {
		this.videoQuality = videoQuality;
	}

	public int getAudioCodec() {
		return audioCodec;
	}

	public int getAudioChannel() {
		return audioChannel;
	}

	public void setAudioChannel(int audioChannel) {
		this.audioChannel = audioChannel;
	}

	public int getAudioBitrate() {
		return audioBitrate;
	}

	public void setAudioBitrate(int audioBitrate) {
		this.audioBitrate = audioBitrate;
	}

	public int getVideoBitrate() {
		return videoBitrate;
	}

	public void setVideoBitrate(int videoBitrate) {
		this.videoBitrate = videoBitrate;
	}
}
