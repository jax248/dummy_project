package com.pracify.sound;

import android.media.AudioRecord;

import com.pracify.*;
public class CSleeper
  implements Runnable
{
  private Boolean done = Boolean.valueOf(false);
  private RecordingActivity m_ma;
  private CSampler m_sampler;
  private AudioRecord ar;
  public CSleeper(RecordingActivity paramMainActivity, CSampler paramCSampler)
  {
    m_ma = paramMainActivity;
    m_sampler = paramCSampler;
  }

  public void run()
  {
    m_sampler.Init(ar);
    while (true)
      try
      {
        Thread.sleep(1000L);
        System.out.println("Tick");
        continue;
      }
      catch (InterruptedException localInterruptedException)
      {
        localInterruptedException.printStackTrace();
      }
  }
}

