var utils = {
    get_date : function(add_day){
        var dd = new Date();
        dd.setDate(dd.getDate() + add_day);//获取AddDayCount天后的日期
        var y = dd.getFullYear();
        var m = dd.getMonth() + 1;//获取当前月份的日期
        var d = dd.getDate();
        m = (m < 10 ? "0" + m : m);
        d = (d < 10 ? "0" + d : d);
        return y + "-" + m + "-" + d;
    },
    get_url_param : function(name){
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        if (r !== null) return decodeURI(r[2]);
        return null;
    },
    set_cookie : function(name, value) {
        $.cookie(name, value, {
            expires: 7,
            path: '/'
        });
    },
    remove_cookie : function(name) {
        $.removeCookie(name, {
            expires: 7,
            path: '/'
        });
    }
};