package tr.edu.yildiz.ertugrulsenturk.model;

import java.io.Serializable;

public class User implements Serializable {
    /**
     * Class structure that stores the basic information of an user
     */
    private final String userFirstName;
    private final String userLastName;
    private final String phoneNumber;
    private final String eMail;
    private final String password;
    private final byte[] userImage;

    public User(String userFirstName, String userLastName, String phoneNumber, String eMail, String password, byte[] userImage) {
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.phoneNumber = phoneNumber;
        this.eMail = eMail;
        this.password = password;
        this.userImage = userImage;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEMail() {
        return eMail;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getUserImage() {
        return userImage;
    }

}
