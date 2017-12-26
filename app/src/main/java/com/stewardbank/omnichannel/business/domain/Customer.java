package com.stewardbank.omnichannel.business.domain;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.Date;
import java.util.List;

/**
 * @uthor Tasu Muzinda
 */
@Table(name = "customer")
public class Customer extends Model {

    @Expose
    @Column
    public String firstName;
    @Expose
    @Column
    public String lastName;
    @Expose
    @Column
    public Long phoneNumber;
    @Expose
    @Column
    public String address;
    @Expose
    @Column
    public Long cardNumber;
    @Expose
    @Column
    public String idNumber;
    @Expose
    @Column
    public String timeCreated;
    @Column
    @Expose
    public String date;
    @Column
    public String signatureFilePath;
    @Column
    public String headshotFilePath;
    @Column
    public String idFilePath;
    @Column
    public String proofOfResidenceFilePath;
    @Column
    public Integer isComplete = 0;
    @Column
    @Expose
    public User createdBy;

    public Customer(){
        super();
    }

    public static List<Customer> findAll(){
        return new Select()
                .from(Customer.class)
                .execute();
    }

    public static Customer findByIdNumber(String idNumber){
        return new Select()
                .from(Customer.class)
                .where("idNumber = ?", idNumber)
                .executeSingle();
    }

    public static List<Customer> findComplete(){
        return new Select()
                .from(Customer.class)
                .where("isComplete = ?", 1)
                .execute();
    }
}
