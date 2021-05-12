package tr.edu.yildiz.ertugrulsenturk.service.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BlobTools {
    /**
     * Auxiliary class for converting blob's to object and object's to blob
     */
    private BlobTools() {
    }

    // converts arraylists of string to blob
    public static byte[] arrayListToBlob(ArrayList<String> arrayList) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);

        for (String element : arrayList) {
            out.writeUTF(element);
        }
        return bos.toByteArray();
    }

    // converts blob to arraylists of string
    public static ArrayList<String> blobToArrayList(byte[] blob) throws IOException {
        ArrayList<String> choicesList = new ArrayList<>();
        if (blob == null || blob.length == 0) {
            return choicesList;
        }
        ByteArrayInputStream bin = new ByteArrayInputStream(blob);
        DataInputStream in = new DataInputStream(bin);
        while (in.available() > 0) {
            String element = in.readUTF();
            choicesList.add(element);
        }
        return choicesList;
    }

    // converts bitmap to blob
    public static byte[] getBytesFromImage(Bitmap image) {
        if (image == null)
            return null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        return outputStream.toByteArray();
    }

    // converts blob to bitmap
    public static Bitmap getImageFromBytes(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
