package tr.edu.yildiz.ertugrulsenturk.service.tools;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;

import tr.edu.yildiz.ertugrulsenturk.BuildConfig;
import tr.edu.yildiz.ertugrulsenturk.R;

public class DialogTools {
    /**
     * Auxiliary class for showing image or video dialog
     */
    private DialogTools() {
    }

    // showing image dialog from given path
    public static void imageDialog(Context context, String path) {
        Dialog imageDialog = new Dialog(context);
        imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imageDialog.setContentView(R.layout.dialog_image);
        ImageView imageView = (ImageView) imageDialog.findViewById(R.id.dialogImageView);
        File file = new File(path);
        Uri imageUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        MediaTools.initializeImageView(context, imageUri, imageView);
        imageDialog.show();
    }

    // showing video dialog from given path
    public static void videoDialog(Context context, String path, boolean isAudio, boolean start) {
        Dialog videoDialog = new Dialog(context);
        videoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        videoDialog.setContentView(R.layout.dialog_video);
        VideoView videoView = (VideoView) videoDialog.findViewById(R.id.dialogVideoView);
        File file = new File(path);
        Uri videoUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        if (isAudio) {
            //in audio there are no thumbnail so we used a background image
            videoView.setBackground(ContextCompat.getDrawable(context, R.drawable.music));
        } else {
            // used for showing video thumbnail
            videoView.seekTo(1);
        }
        if (start) {
            videoView.start();
        }
        videoView.setVideoURI(videoUri);
        MediaController mc = new MediaController(context);
        ((ViewGroup) mc.getParent()).removeView(mc);
        ((FrameLayout) videoDialog.findViewById(R.id.videoViewWrapper)).addView(mc);
        videoView.setMediaController(mc);
        videoDialog.show();
    }

}
