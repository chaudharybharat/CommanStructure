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
 * Created by agc-linux on 16/8/17.
 */
@Table(database = MyDatabase.class)
public class CustomerTranscation extends BaseModel {

    @Column
    String transcation_type;
    @Column
    int customer_id;
    @Column
    String amount;
    @Column
    String transcation_date;

    @Column
    String paymentTermdate;
    @Column
    String descraption;
    @PrimaryKey(autoincrement = true)
    @Column
    int uniqeId;
    public static List<CustomerTranscation> getTranscationList(){
        List<CustomerTranscation> transcationList = SQLite.select().from(CustomerTranscation.class).orderBy(CustomerTranscation_Table.transcation_date,true).queryList();
        if(transcationList!=null){

            return transcationList;
        }else {
            return null;
        }
    }

    public static List<CustomerTranscation> getTranscationList(int customer_id){
        List<CustomerTranscation> transcationList = SQLite.select().from(CustomerTranscation.class).where(CustomerTranscation_Table.customer_id.is(customer_id)).orderBy(CustomerTranscation_Table.transcation_date,true).queryList();
        if(transcationList!=null){

            return transcationList;
        }else {
            return transcationList =new ArrayList<>();
        }
    }
    public static CustomerTranscation getTranscation(int transcation_id){
        CustomerTranscation transcation = SQLite.select().from(CustomerTranscation.class).where(CustomerTranscation_Table.uniqeId.is(transcation_id)).querySingle();
        if(transcation!=null){
            return transcation;
        }else {
            return null;
        }
    }
    public static List<CustomerTranscation> getTranscationListDate(String data){
        List<CustomerTranscation> transcation = SQLite.select().from(CustomerTranscation.class).where(CustomerTranscation_Table.paymentTermdate.is(data)).queryList();
        if(transcation!=null){
            return transcation;
        }else {
            return null;
        }
    }

    public String getPaymentTermdate() {
        return paymentTermdate;
    }

    public void setPaymentTermdate(String paymentTermdate) {
        this.paymentTermdate = paymentTermdate;
    }

    public int getUniqeId() {
        return uniqeId;
    }

    public void setUniqeId(int uniqeId) {
        this.uniqeId = uniqeId;
    }

    public String getTranscation_type() {
        return transcation_type;
    }

    public void setTranscation_type(String transcation_type) {
        this.transcation_type = transcation_type;
    }
    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTranscation_date() {
        return transcation_date;
    }

    public void setTranscation_date(String transcation_date) {
        this.transcation_date = transcation_date;
    }

    public String getDescraption() {
        return descraption;
    }

    public void setDescraption(String descraption) {
        this.descraption = descraption;
    }
}
