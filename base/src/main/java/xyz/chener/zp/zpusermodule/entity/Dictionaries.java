package xyz.chener.zp.zpusermodule.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;


public class Dictionaries extends Model<Dictionaries> {


    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String keyEn;
    
    private String keyCn;
    
    private String value0;
    
    private String value1;
    
    private String value2;
    
    private String value3;
    
    private String value4;
    
    private String value5;
    
    private String value6;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKeyEn() {
        return keyEn;
    }

    public void setKeyEn(String keyEn) {
        this.keyEn = keyEn;
    }

    public String getKeyCn() {
        return keyCn;
    }

    public void setKeyCn(String keyCn) {
        this.keyCn = keyCn;
    }

    public String getValue0() {
        return value0;
    }

    public void setValue0(String value0) {
        this.value0 = value0;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getValue3() {
        return value3;
    }

    public void setValue3(String value3) {
        this.value3 = value3;
    }

    public String getValue4() {
        return value4;
    }

    public void setValue4(String value4) {
        this.value4 = value4;
    }

    public String getValue5() {
        return value5;
    }

    public void setValue5(String value5) {
        this.value5 = value5;
    }

    public String getValue6() {
        return value6;
    }

    public void setValue6(String value6) {
        this.value6 = value6;
    }

}

