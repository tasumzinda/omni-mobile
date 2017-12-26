package com.stewardbank.omnichannel.business.domain;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @uthor Tasu Muzinda
 */
@Table(name = "user")
public class User extends Model{

    @Expose
    @Column
    public String password;
    @Expose
    @Column
    public String firstName;
    @Expose
    @Column
    public String lastName;
    @Expose
    @Column
    public String userName;
    @Expose
    @Column
    @SerializedName("id")
    public String serverId;

    public User(){
        super();
    }

    public static List<User> getAll(){
        return new Select()
                .from(User.class)
                .execute();
    }

    public static User findByServerId(String serverId){
        return new Select()
                .from(User.class)
                .where("serverId = ?", serverId)
                .executeSingle();
    }

    public static User findByUserName(String userName){
        return new Select()
                .from(User.class)
                .where("userName = ?", userName)
                .executeSingle();
    }
}
