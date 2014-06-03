package com.pracify.util;

//Data class to explicitly indicate that these bytes are raw audio data
public class FFTData
{
public FFTData(byte[] bytes)
{
 this.bytes = bytes;
}

public byte[] bytes;
}