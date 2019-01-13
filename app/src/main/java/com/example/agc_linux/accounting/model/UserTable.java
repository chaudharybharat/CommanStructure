package com.example.agc_linux.accounting.model;

import com.example.agc_linux.accounting.db.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Bharat on 9/3/2017.
 */
@Table(database = MyDatabase.class)
public class UserTable extends BaseModel {

    @PrimaryKey
    @Column
    int num;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Column
    String profile_pic;

    public static UserTable getUserData(){
        UserTable users = SQLite.select().from(UserTable.class).querySingle();
        if(users!=null){
            return users;
        }else {
            return null;
        }
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }
}
