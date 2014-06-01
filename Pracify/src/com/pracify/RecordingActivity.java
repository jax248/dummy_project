package com.pracify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;

import com.pracify.util.CommonHelpers;
import com.pracify.util.PracifyConstants;

public class RecordingActivity extends ActionBarActivity {

	private Button startRecording, stopRecording;
	private static final String LOG_TAG = "Recording";
	private String mFileName = null;

	private MediaRecorder mRecorder = null;
	private int channelConfiguration = 16;
	private int audioEncoding = 2;
	// Variable declaration for Real Time visualisation
	public static final int RECORDER_BPP = 16;
	public static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
	public static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	public static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
	public static final int RECORDER_SAMPLERATE = 44100;
	public static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
	public static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	// public static final int RECORDER_AUDIO_ENCODING =
	// AudioFormat.CHANNEL_OUT_MONO;
	private static int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };
	public AudioRecord recorder = null;
	public int bufferSize = 0;
	public Thread recordingThread = null;
	public boolean isRecording = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recording);

		// ActionBar actionBar = getSupportActionBar();
		// actionBar.setDisplayHomeAsUpEnabled(true);
		Intent intent = getIntent();
		// startRecording = (Button) findViewById(R.id.start_record);
		// stopRecording = (Button) findViewById(R.id.stop_record);

		// stopRecording.setEnabled(false);
		bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
				RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
	}

	
	public void strtRecording(View view) {
		try {

//			recorder = new AudioRecord(1, 44100, RECORDER_CHANNELS,
//					RECORDER_AUDIO_ENCODING, AudioRecord.getMinBufferSize(
//							44100, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING));
            recorder =  new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, 
            		RECORDER_AUDIO_ENCODING,AudioFormat.ENCODING_PCM_16BIT, bufferSize);  
            
			recorder.release();
			recorder.startRecording();
			isRecording = true;

			recordingThread = new Thread(new Runnable() {

				@Override
				public void run() {
					writeAudioDataToFile();
				}
			}, "AudioRecorder Thread");

			recordingThread.start();
			CommonHelpers.showLongToast(this, "Recording Started");
		} catch (Exception e) {
			CommonHelpers.showLongToast(this, "Error!! Try again later.");
			Log.e(LOG_TAG, "Recording failed : " + e.getMessage());
		}
	}

	public AudioRecord findAudioRecord() {
		for (int rate : mSampleRates) {
			for (short audioFormat : new short[] {
					AudioFormat.ENCODING_PCM_8BIT,
					AudioFormat.ENCODING_PCM_16BIT }) {
				for (short channelConfig : new short[] {
						AudioFormat.CHANNEL_IN_MONO,
						AudioFormat.CHANNEL_IN_STEREO }) {
					try {
						// Log.d(C.TAG, "Attempting rate " + rate + "Hz, bits: "
						// + audioFormat + ", channel: "
						// + channelConfig);
						int bufferSize = AudioRecord.getMinBufferSize(rate,
								channelConfig, RECORDER_AUDIO_ENCODING);

						if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
							// check if we can instantiate and have a success
							AudioRecord recorder = new AudioRecord(1, rate,
									channelConfig, RECORDER_AUDIO_ENCODING, bufferSize);

							if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
								return recorder;
						}
					} catch (Exception e) {
						// Log.e(C.TAG, rate + "Exception, keep trying.",e);
					}
				}
			}
		}
		return null;
	}

	public void stopRecording(View view) {
		// mRecorder.stop();
		// mRecorder.release();
		// mRecorder = null;
		// // startRecording.setEnabled(true);
		// // stopRecording.setEnabled(false);
		// Log.d(LOG_TAG, "Saved File Path : " + mFileName);
		// CommonHelpers.showLongToast(this, "Recording Stopped");
		// Intent intent = new Intent(this, SaveRecordingActivity.class);
		// intent.putExtra(PracifyConstants.filePathIntent, mFileName);
		// intent.putExtra(PracifyConstants.fileID, "NULL");
		// startActivity(intent);
		// finish();
		if (null != recorder) {
			isRecording = false;

			recorder.stop();
			recorder.release();

			recorder = null;
			recordingThread = null;

			Intent intent = new Intent(this, SaveRecordingActivity.class);
			intent.putExtra(PracifyConstants.filePathIntent, mFileName);
			intent.putExtra(PracifyConstants.fileID, "NULL");
			startActivity(intent);
			finish();
		}

		copyWaveFile(getTempFilename(), getFilename());
		deleteTempFile();
	}

	private void deleteTempFile() {
		File file = new File(getTempFilename());

		file.delete();
	}

	private void copyWaveFile(String inFilename, String outFilename) {
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = RECORDER_SAMPLERATE;
		int channels = 2;
		long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;

		byte[] data = new byte[bufferSize];

		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;

			// AppLog.logString("File size: " + totalDataLen);

			WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
					longSampleRate, channels, byteRate);

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

	private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
			long totalDataLen, long longSampleRate, int channels, long byteRate)
			throws IOException {

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
		header[32] = (byte) (2 * 16 / 8); // block align
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

	private String getFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}
		mFileName = file.getAbsolutePath();
		return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_WAV);
	}

	private String getTempFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}

		File tempFile = new File(filepath, AUDIO_RECORDER_TEMP_FILE);

		if (tempFile.exists())
			tempFile.delete();

		return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
	}

	private void writeAudioDataToFile() {
		byte data[] = new byte[bufferSize];
		String filename = getTempFilename();
		FileOutputStream os = null;

		try {
			os = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int read = 0;

		if (null != os) {
			while (isRecording) {
				read = recorder.read(data, 0, bufferSize);

				if (AudioRecord.ERROR_INVALID_OPERATION != read) {
					try {
						os.write(data);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
