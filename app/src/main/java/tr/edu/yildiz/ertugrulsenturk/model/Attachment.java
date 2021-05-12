package tr.edu.yildiz.ertugrulsenturk.model;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Attachment implements Serializable {
    /**
     * Class structure that stores the attachment's type, file path, and name
     */
    private final String fileName;
    private String type;
    private String path;

    public Attachment(String fileName, String type, String path) {
        this.fileName = fileName;
        this.type = type;
        this.path = path;
    }

    // class can be generated from single string with ~ separated
    public Attachment(String fullAttachment) {
        String[] result = fullAttachment.split("~");
        this.type = result[0];
        this.path = result[1];
        this.fileName = result[2];
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return this.type + "~" + this.path + "~" + this.fileName + "~";
    }
}
