package com.hxht.autodeploy.utils

class Spcx {
    static  int getSpcx(String name){
        int count=1;
        if("一审".equals(name)){
            count=1;
        }else if("二审".equals(name)||"重审".equals(name)){
            count=2;
        }else if("再审".equals(name)){
            count=3;
        }
        return count;
    }
}
