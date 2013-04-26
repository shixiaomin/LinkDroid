package cn.link.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageButton;
import cn.link.core.SimpleAsyncTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class LKImageView extends ImageButton {
    private static ImageLoaderConfiguration mConfiguration;
    private DisplayImageOptions mDisplayOptions;

    public LKImageView(Context context) {
        super(context);

    }

    public LKImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setDisplayOptions(DisplayImageOptions option) {
        this.mDisplayOptions = option;
    }

    public void display() {
        Bitmap bitmap = null;
        if (mDisplayOptions.isDispalyIfInMemory()) {
            bitmap = mConfiguration.memoryCache.get(mDisplayOptions.getDisplayUrl());
            mDisplayOptions.getDisplayer().display(bitmap, this);
        } else {
            new ImageDisplayTask().execute();
        }
    }

    private class ImageDisplayTask extends SimpleAsyncTask<Void, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = mConfiguration.memoryCache.get(mDisplayOptions.getDisplayUrl());
            if (bitmap == null) {
                bitmap = mConfiguration.discCache.read(mDisplayOptions.getDisplayUrl(), null);
                if (bitmap == null) {
                    try {
                        InputStream inputStream = mConfiguration.downloader.getStream(mDisplayOptions.getDisplayUrl(), null);
                        bitmap = mConfiguration.discCache.decodeAndWrite(inputStream, mDisplayOptions);
                        if (bitmap != null) {
                            mConfiguration.memoryCache.put(mDisplayOptions.getDisplayUrl(), bitmap);
                            return bitmap;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;  //ToDo
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mDisplayOptions.getDisplayer().display(bitmap, this);
        }
    }

}
