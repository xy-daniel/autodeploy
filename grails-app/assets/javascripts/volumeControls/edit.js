/**
 * Created by sakuri on 2019/8/13.
 */
;-function (window) {
    'use strict';
    var ready = {
        gsrLogo: 0,
        lsLogo:0,
        xcxyrLogo:0,
        ycxyrLogo:0

    };
    var option = {
        court: null
    };
    var init = function () {
        init_ready();//加载预设变量
        init_event();//初始化页面事件
    };

    function init_ready() {//初始化预设值
    }

    function init_event() {//初始化页面事件
        core.switcherVolume();//初始化页面点击事件
        core.initView()//加载配置项
    }

    //内部核心属性
    var core = {
        switcherVolume: function(){
            $("#editConfig").click(function () {
                window.location.href = contextPath + "api/volume/control/editConfig"
            });
            $("#gsr").click(function () {
                core.courtVerify();
                if ($.isEmptyObject(ready.court)){
                    return
                }
                if (ready.gsrLogo === 0){
                    core.ajaxFuncton(ready.gsrArr,ready.court,1,"gsr");
                    ready.gsrLogo = 1
                }else{
                    core.ajaxFuncton(ready.gsrArr,ready.court,0,"gsr");
                    ready.gsrLogo = 0
                }
            });
            $("#ls").click(function () {
                core.courtVerify();
                if ($.isEmptyObject(ready.court)){
                    return
                }
                if (ready.lsLogo === 0){
                    core.ajaxFuncton(ready.lsArr,ready.court,1,"ls");
                    ready.lsLogo = 1
                }else{
                    core.ajaxFuncton(ready.lsArr,ready.court,0,"ls");
                    ready.lsLogo = 0
                }
            });
            $("#xcxyr").click(function () {
                core.courtVerify();
                if ($.isEmptyObject(ready.court)){
                    return
                }
                if (ready.xcxyrLogo === 0){
                    core.ajaxFuncton(ready.xcxyrArr,ready.court,1,"xcxyr");
                    ready.xcxyrLogo = 1
                }else{
                    core.ajaxFuncton(ready.xcxyrArr,ready.court,0,"xcxyr");
                    ready.xcxyrLogo = 0
                }
            });
            $("#ycxyr").click(function () {
                core.courtVerify();
                if ($.isEmptyObject(ready.court)){
                    return
                }
                if (ready.ycxyrLogo === 0){
                    core.ajaxFuncton(ready.ycxyrArr,ready.court,1,"ycxyr");
                    ready.ycxyrLogo = 1
                }else{
                    core.ajaxFuncton(ready.ycxyrArr,ready.court,0,"ycxyr");
                    ready.ycxyrLogo = 0
                }
            });
            $("#saveCourt").click(function () {
                core.saveToStorage();
            });
        },
        selectpicker: function () {
            $('.selectpicker').selectpicker('render');
        },
        courtVerify: function () {
            if ($.isEmptyObject(ready.court)){
                swal({
                    title: '请选择对应法庭!',
                    type: 'error',
                    confirmButtonText:'确 认'
                },function () {
                    return
                });
                return
            }
        },
        ajaxFuncton: function (chn,ip,isMute,but){
            $.ajax({
                type: "POST",
                url: contextPath + 'api/volume/control/send',
                dataType: "json",
                async:false,
                data:{
                    chn:chn,
                    ip:ip,
                    isMute:isMute
                },
                success: function (data) {
                    if (data.code === 0) {
                        if (isMute === 0){
                            if (but === 'gsr'){
                                $("#gsr").removeClass("red");
                                $("#gsr").addClass("green");
                            }else if (but === 'ls'){
                                $("#ls").removeClass("red");
                                $("#ls").addClass("green");
                            }else if (but === 'xcxyr'){
                                $("#xcxyr").removeClass("red");
                                $("#xcxyr").addClass("green");
                            }else if (but === 'ycxyr'){
                                $("#ycxyr").removeClass("red");
                                $("#ycxyr").addClass("green");
                            }
                        }
                        if (isMute === 1){
                            if (but === 'gsr'){
                                $("#gsr").removeClass("green");
                                $("#gsr").addClass("red");
                            }else if (but === 'ls'){
                                $("#ls").removeClass("green");
                                $("#ls").addClass("red");
                            }else if (but === 'xcxyr'){
                                $("#xcxyr").removeClass("green");
                                $("#xcxyr").addClass("red");
                            }else if (but === 'ycxyr'){
                                $("#ycxyr").removeClass("green");
                                $("#ycxyr").addClass("red");
                            }
                        }
                    }else{
                        swal({
                            title: '操作失败!',
                            type: 'error',
                            confirmButtonText:'确 认'
                        },function () {
                            // window.location.href= contextPath + "api/volume/control/";
                        });
                    }
                }
            });
        },
        initView: function (){
            ready.court = localStorage.getItem('courtroom');
            ready.gsr = localStorage.getItem('gsr');
            ready.ls = localStorage.getItem('ls');
            ready.xcxyr = localStorage.getItem('xcxyr');
            ready.ycxyr = localStorage.getItem('ycxyr');
            ready.chnArr = [];
            ready.gsrArr = ready.gsr.split(",")
            for (var i = 0; i<ready.gsr.length ;i++ ){
                ready.chnArr.push(ready.gsrArr[i])
            }
            ready.lsArr = ready.ls.split(",")
            for (var i = 0; i<ready.ls.length ;i++ ){
                ready.chnArr.push(ready.lsArr[i])
            }
            ready.xcxyrArr = ready.xcxyr.split(",")
            for (var i = 0; i<ready.xcxyr.length ;i++ ){
                ready.chnArr.push(ready.xcxyrArr[i])
            }
            ready.ycxyrArr = ready.ycxyr.split(",")
            for (var i = 0; i<ready.ycxyr.length ;i++ ){
                ready.chnArr.push(ready.ycxyrArr[i])
            }
            if ($.isEmptyObject(ready.court)){
                swal({
                    title: '请选择对应法庭!',
                    type: 'error',
                    confirmButtonText:'确 认'
                },function () {
                    return
                });
                return
            }
            core.ajaxFuncton(ready.chnArr,ready.court,0);
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
