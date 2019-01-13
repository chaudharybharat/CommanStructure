package com.example.agc_linux.accounting.model;

import com.example.agc_linux.accounting.db.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agc-linux on 14/8/17.
 */

@Table(database = MyDatabase.class)
public class Customer extends BaseModel {
    @Column
    String name;
    @Column
    String address;
    @Column
    String mobile;
    @Column
    String profile_pic;
    @PrimaryKey(autoincrement = true)
    @Column
    int custome_id;

    public Customer() {
    }
    public static List<Customer>  getCustomerList(){
        List<Customer> users = SQLite.select().from(Customer.class).orderBy(Customer_Table.name,true).queryList();
        if(users!=null){

            return users;
        }else {
            return users =new ArrayList<>();
        }
    }
    public static Customer getCustomer(int custome_id){
        Customer users = SQLite.select().from(Customer.class).where(Customer_Table.custome_id.eq(custome_id)).querySingle();
        if(users!=null){
            return users;
        }else {
            return null;
        }
    }
     public static Customer getCustomerNameExists(String custome_id){
        Customer users = SQLite.select().from(Customer.class).where(Customer_Table.name.eq(custome_id)).querySingle();
        if(users!=null){
            return users;
        }else {
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public int getCustome_id() {
        return custome_id;
    }

    public void setCustome_id(int custome_id) {
        this.custome_id = custome_id;
    }
}
