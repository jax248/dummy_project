package com.pracify;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.pracify.util.CommonHelpers;
import com.pracify.util.PracifyConstants;
import com.pracify.util.VisualizerView;
import com.pracify.util.Renderer.BarGraphRenderer;
import com.pracify.util.Renderer.CircleBarRenderer;
import com.pracify.util.Renderer.CircleRenderer;
import com.pracify.util.Renderer.LineRenderer;

/**
 * Demo to show how to use VisualizerView
 */
public class Visualize extends Activity {
  private MediaPlayer mPlayer;
  private MediaPlayer mSilentPlayer;  /* to avoid tunnel player issue */
  private VisualizerView mVisualizerView;
  private String mFileName = null;
  private static final String LOG_TAG = "SaveRecording";
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    Intent intent = getIntent();
	mFileName = intent.getStringExtra(PracifyConstants.filePathIntent);
	Log.d(LOG_TAG, "Got File : " + mFileName);

	//fileName = (EditText) findViewById(R.id.editText1);
	//fileDescription = (EditText) findViewById(R.id.editText2);

	//fileName.setText(CommonHelpers.getCurrentTimestamp());

  }

  @Override
  protected void onResume()
  {
    super.onResume();
    try {
		init();
	} catch (RuntimeException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }

  @Override
  protected void onPause()
  {
    cleanUp();
    super.onPause();
  }

  @Override
  protected void onDestroy()
  {
    cleanUp();
    super.onDestroy();
  }

  private void init() throws RuntimeException, IOException
  {
    mPlayer = new MediaPlayer();
    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	try {
		mPlayer.setDataSource(mFileName);
	} catch (IllegalArgumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SecurityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalStateException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    mPlayer.setLooping(true);
    mPlayer.prepare();
    mPlayer.start();

    // We need to link the visualizer view to the media player so that
    // it displays something
    mVisualizerView = (VisualizerView) findViewById(R.id.VisualizerView);
    mVisualizerView.link(mPlayer);

    // Start with just line renderer
    addLineRenderer();
  }

  private void cleanUp()
  {
    if (mPlayer != null)
    {
      mVisualizerView.release();
      mPlayer.release();
      mPlayer = null;
    }
    
    if (mSilentPlayer != null)
    {
      mSilentPlayer.release();
      mSilentPlayer = null;
    }
  }
  
  // Workaround (for Galaxy S4)
  //
  // "Visualization does not work on the new Galaxy devices"
  //    https://github.com/felixpalmer/android-visualizer/issues/5
  //
  // NOTE: 
  //   This code is not required for visualizing default "test.mp3" file,
  //   because tunnel player is used when duration is longer than 1 minute.
  //   (default "test.mp3" file: 8 seconds)
  //
 
  // Methods for adding renderers to visualizer
  private void addBarGraphRenderers()
  {
    Paint paint = new Paint();
    paint.setStrokeWidth(50f);
    paint.setAntiAlias(true);
    paint.setColor(Color.argb(200, 56, 138, 252));
    BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(16, paint, false);
    mVisualizerView.addRenderer(barGraphRendererBottom);

    Paint paint2 = new Paint();
    paint2.setStrokeWidth(12f);
    paint2.setAntiAlias(true);
    paint2.setColor(Color.argb(200, 181, 111, 233));
    BarGraphRenderer barGraphRendererTop = new BarGraphRenderer(4, paint2, true);
    mVisualizerView.addRenderer(barGraphRendererTop);
  }

  private void addCircleBarRenderer()
  {
    Paint paint = new Paint();
    paint.setStrokeWidth(8f);
    paint.setAntiAlias(true);
    paint.setXfermode(new PorterDuffXfermode(Mode.LIGHTEN));
    paint.setColor(Color.argb(255, 222, 92, 143));
    CircleBarRenderer circleBarRenderer = new CircleBarRenderer(paint, 32, true);
    mVisualizerView.addRenderer(circleBarRenderer);
  }

  private void addCircleRenderer()
  {
    Paint paint = new Paint();
    paint.setStrokeWidth(3f);
    paint.setAntiAlias(true);
    paint.setColor(Color.argb(255, 222, 92, 143));
    CircleRenderer circleRenderer = new CircleRenderer(paint, true);
    mVisualizerView.addRenderer(circleRenderer);
  }

  private void addLineRenderer()
  {
    Paint linePaint = new Paint();
    linePaint.setStrokeWidth(1f);
    linePaint.setAntiAlias(true);
    linePaint.setColor(Color.argb(88, 0, 128, 255));

    Paint lineFlashPaint = new Paint();
    lineFlashPaint.setStrokeWidth(5f);
    lineFlashPaint.setAntiAlias(true);
    lineFlashPaint.setColor(Color.argb(188, 255, 255, 255));
    LineRenderer lineRenderer = new LineRenderer(linePaint, lineFlashPaint, true);
    mVisualizerView.addRenderer(lineRenderer);
  }

  // Actions for buttons defined in xml
  public void startPressed(View view) throws IllegalStateException, IOException
  {
	if(mPlayer.isPlaying())
    {
      return;
    }
    mPlayer.prepare();
    mPlayer.start();
  }

  public void stopPressed(View view)
  {
    //mPlayer.stop();
	  try {
			if (mPlayer != null)
			{
				mPlayer.stop();
				mPlayer.release();
				mPlayer = null;
				finish();
			}
		}
	  catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
			e.printStackTrace();
		}  
  }

 }
