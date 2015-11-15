package com.feibo.recorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import fbcore.log.LogUtil;
import fbcore.utils.Files;

public class AudioRecorder extends Thread {

    private static final int RECORDER_BPP = 16;
    private static final String PCM_FILE_SUFFIX = ".raw";
    private static final String AAC_FILE_SUFFIX = ".aac";
    private static final String WAV_FILE_SUFFIX = ".wav";

    private RecorderParameters parameters;
    private boolean isKilled = false;
    private boolean needRecording = false;

    private AudioRecord record;
    private int read;
    private byte[] audioData;
    private FileOutputStream fos;

    private String filePrefix;

    public AudioRecorder(RecorderParameters parameters) {
        this.parameters = parameters;
        this.record = createAudioRecord();
        audioData = new byte[getAudioRecordMinBufferSize()];
        record.startRecording();
    }

    /**
     * 录制音频文件
     * @param saveFilePrefix 保存文件的前缀
     */
    public void recording(String saveFilePrefix) {
        try {
            this.filePrefix = saveFilePrefix;
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String tmpFilePath = getTmpFilePath();
            try {
                Files.create(new File(tmpFilePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            fos = new FileOutputStream(tmpFilePath);
            audioData = new byte[getAudioRecordMinBufferSize()];
            this.needRecording = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public interface AudioConvertListener {
        void onSuccess(String filePrefix, String filePath);

        void onFail(String filePrefix);
    }

    public void pause(final AudioConvertListener listener) {
        needRecording = false;
        final String tmpFileName = getTmpFilePath();
        final String wavFileName = getWavFilePath();
        final String aacFileName = getAACFilePath();
        new Thread() {
            @Override
            public void run() {
                long startPcm = System.currentTimeMillis();
                pcm2Wav(tmpFileName, wavFileName);
                LogUtil.d("FeiboRecorder", "pcm2Wav elapsed:" + (System.currentTimeMillis() - startPcm));

                if (new File(wavFileName).exists()) {
                    if (listener != null) {
                        listener.onSuccess(filePrefix, aacFileName);
                    }
                } else {
                    if (listener != null) {
                        listener.onFail(filePrefix);
                    }
                }

                /*if (FFmpeg.transcodeAudio(wavFileName, aacFileName)) {
                    Files.delete(tmpFileName);
                    Files.delete(wavFileName);
                    LogUtil.d("FeiboRecorder", "pcm2aac elapsed:" + (System.currentTimeMillis() - startPcm));
                    if (listener != null) {
                        listener.onSuccess(filePrefix, aacFileName);
                    }
                } else {
                    Files.delete(tmpFileName);
                    Files.delete(wavFileName);
                    Files.delete(aacFileName);
                    if (listener != null) {
                        listener.onFail(filePrefix);
                    }
                }*/
            };
        }.start();
    }

    public void stopRecording() {
        isKilled = true;
    }

    private String getTmpFilePath() {
        return filePrefix + PCM_FILE_SUFFIX;
    }

    private String getAACFilePath() {
        return filePrefix + AAC_FILE_SUFFIX;
    }

    private String getWavFilePath() {
        return filePrefix + WAV_FILE_SUFFIX;
    }

    private AudioRecord createAudioRecord() {
        return new AudioRecord(MediaRecorder.AudioSource.MIC, parameters.getAudioSamplingRate(), parameters.getAudioChannel(), AudioFormat.ENCODING_PCM_16BIT, getAudioRecordMinBufferSize());
    }

    private int getAudioRecordMinBufferSize() {
        return AudioRecord.getMinBufferSize(parameters.getAudioSamplingRate(), parameters.getAudioChannel(), AudioFormat.ENCODING_PCM_16BIT);
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        while(!isKilled) {
                read = record.read(audioData, 0, getAudioRecordMinBufferSize());
                if (read > 0 && (needRecording)) {
                    try {
                        if (fos != null) {
                            fos.write(audioData);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        try {
            if (fos != null) {
                fos.flush();
                fos.close();
                fos = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        record.stop();
        record.release();
    }

    /**
     * 将PCM文件转换为wav文件.
     * @param inFilename
     * @param outFilename
     */
    private void pcm2Wav(String inFilename, String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        int channels = 1;
        long byteRate = RECORDER_BPP * parameters.getAudioSamplingRate() * channels / 8;

        byte[] data = new byte[getAudioRecordMinBufferSize()];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen, parameters.getAudioSamplingRate(), channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate,
            int channels, long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (1 * 16 / 8); // block align
        header[33] = 0;
        header[34] = RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }
}
