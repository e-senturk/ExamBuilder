package tr.edu.yildiz.ertugrulsenturk.service.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

import tr.edu.yildiz.ertugrulsenturk.BuildConfig;
import tr.edu.yildiz.ertugrulsenturk.R;
import tr.edu.yildiz.ertugrulsenturk.model.Attachment;

public class MediaTools {
    /**
     * Auxiliary class for modifying media items
     */
    private MediaTools() {
    }

    // sql can't handle big images so this class resizes image
    public static Bitmap makeImageSmaller(Bitmap image, int maximumSize) {
        int width = image.getWidth();  // 100
        int height = image.getHeight();  // 200
        double bitmapRatio = (double) width / (double) height;
        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static Bitmap initializeImageView(Context context, Uri imageUri, ImageView imageView) {
        try {
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= 28) {
                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), imageUri);
                bitmap = MediaTools.makeImageSmaller(ImageDecoder.decodeBitmap(source), 300);

            } else {
                bitmap = MediaTools.makeImageSmaller(MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri), 300);
            }
            imageView.setImageBitmap(bitmap);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void initializeVideoView(Context context, Uri videoUri, VideoView videoView, boolean isAudio, boolean isStarted) {
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();
        videoView.setMediaController(new MediaController(context));
        videoView.setOnPreparedListener(mp -> mp.setVolume(30f, 30f));
        if (isAudio)
            videoView.setBackground(ContextCompat.getDrawable(context, R.drawable.music));
        else {
            videoView.seekTo(1);
        }
        if (isStarted) {
            videoView.start();
        }
    }

    // initialize image video or audio attachment's to video or image views
    public static void initializeAttachment(Context context, Attachment attachment, VideoView videoView, ImageView imageView) {
        if (attachment != null) {
            switch (attachment.getType()) {
                case "image": {
                    File file = new File(attachment.getPath());
                    Uri imageUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                    Bitmap attachmentBitmap = MediaTools.initializeImageView(context, imageUri, imageView);
                    videoView.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    if (attachmentBitmap != null)
                        imageView.setImageBitmap(attachmentBitmap);
                    break;
                }
                case "video": {
                    File file = new File(attachment.getPath());
                    Uri videoUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                    videoView.setBackground(null);
                    imageView.setImageBitmap(null);
                    imageView.setVisibility(View.INVISIBLE);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setVideoURI(videoUri);
                    videoView.requestFocus();
                    videoView.seekTo(1);
                    videoView.setMediaController(new MediaController(context));
                    break;
                }
                case "audio": {
                    File file = new File(attachment.getPath());
                    Uri audioUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                    imageView.setImageBitmap(null);
                    imageView.setVisibility(View.INVISIBLE);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setVideoURI(audioUri);
                    videoView.requestFocus();
                    videoView.setMediaController(new MediaController(context));
                    videoView.setBackground(ContextCompat.getDrawable(context, R.drawable.music));
                    break;
                }
            }
        }
    }

    // put a blur filter on a text view
    public static void applyBlurMaskFilter(TextView textView, int blurRatio) {
        if (textView == null)
            return;
        float radius = textView.getTextSize() / blurRatio;
        BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
        textView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        textView.getPaint().setMaskFilter(filter);
    }
}
