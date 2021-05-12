package tr.edu.yildiz.ertugrulsenturk.service.tools;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import tr.edu.yildiz.ertugrulsenturk.BuildConfig;

public class StorageTools {
    /**
     * Auxiliary class for managing internal storage access
     */
    private StorageTools() {
    }

    // stores file and returns its new path and original name
    public static String storeAndGetPath(Context context, Uri uri) {
        String realName = queryName(context, uri);
        String[] splitRealName = realName.split("\\.");
        String hashName = java.util.UUID.randomUUID().toString() + "." + splitRealName[splitRealName.length - 1];
        String path = context.getFilesDir().getPath() + File.separatorChar + hashName;
        System.out.println(path);
        File destinationFilename = new File(path);
        try (InputStream ins = context.getContentResolver().openInputStream(uri)) {
            createFileFromStream(ins, destinationFilename);
        } catch (Exception ex) {
            Log.e("Save File", ex.getMessage());
            ex.printStackTrace();
        }

        return path + "~" + realName;
    }

    // creates an output stream and copies file to given destination
    public static void createFileFromStream(InputStream ins, File destination) {
        try (OutputStream outputStream = new FileOutputStream(destination)) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = ins.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // opens a new file and writes string into that file
    public static void share(Context context, String text, String file_name, String info, String title) {
        try {
            File path = context.getCacheDir();
            File textFile = new File(path, file_name);
            try (FileOutputStream stream = new FileOutputStream(textFile)) {
                stream.write(text.getBytes());
            }
            Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", textFile);
            shareIntent(context, uri, "text/*", info, title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // creates a share intent with file uri
    public static void shareIntent(Context context, Uri uri, String type, String info, String title) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        String[] mimeTypeArray = new String[]{type};

        intentShareFile.setType(type);

        // Add the uri as a ClipData
        intentShareFile.setClipData(new ClipData(
                info,
                mimeTypeArray,
                new ClipData.Item(uri)
        ));

        // EXTRA_STREAM is kept for compatibility with old applications
        intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);

        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                info);
        intentShareFile.putExtra(Intent.EXTRA_TEXT, info);
        intentShareFile.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intentShareFile, title));
    }

    // gets file's original name from uri
    private static String queryName(Context context, Uri uri) {
        Cursor returnCursor =
                context.getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }
}
