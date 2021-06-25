/**
 * Created by sakuri on 2019/8/13.
 */
;-function (window) {
    'use strict';
    var ready = {};
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
        core.switcherVolume();
        core.optionToView();//加载配置法庭
    }

    //内部核心属性
    var core = {
        switcherVolume: function(){
            $("#saveCourt").click(function () {
                core.saveToStorage();

            });
        },
        selectpicker: function () {
            $('.selectpicker').selectpicker('render');
        },
        courtVerify: function () {
            option.court = $("[name='courtroom']").val();
            if ($.isEmptyObject(option.court)){
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
        ajaxFuncton: function (chn,ip,isMute){
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
                        swal({
                            title: '操作成功!',
                            type: 'success',
                            confirmButtonText:'确 认'
                        },function () {
                            // window.location.href= contextPath + "api/volume/control/";
                        });
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
        saveToStorage: function(){
            localStorage.setItem('courtroom', $("[name='courtroom']").val()); //当前法庭设备地址
            localStorage.setItem('gsr', $("[name='gsr']").val()); //当前公诉人
            localStorage.setItem('ls', $("[name='ls']").val()); //当前律师
            localStorage.setItem('xcxyr', $("[name='xcxyr']").val()); //当前现场嫌疑人
            localStorage.setItem('ycxyr', $("[name='ycxyr']").val()); //当前远程嫌疑人
            swal({
                title: '操作成功!',
                type: 'success',
                confirmButtonText:'确 认'
            },function () {
                window.location.href= contextPath + "api/volume/control/";
            });
        },
        optionToView: function () {
            var court = localStorage.getItem('courtroom')
            var gsr = localStorage.getItem('gsr')
            var ls = localStorage.getItem('ls')
            var xcxyr = localStorage.getItem('xcxyr')
            var ycxyr = localStorage.getItem('ycxyr')
            if (!$.isEmptyObject(court)){
                $("[name='courtroom']").val(court);
            }
            if (!$.isEmptyObject(gsr)){
                $("[name='gsr']").val(gsr);
            }
            if (!$.isEmptyObject(ls)){
                $("[name='ls']").val(ls);
            }
            if (!$.isEmptyObject(xcxyr)){
                $("[name='xcxyr']").val(xcxyr);
            }
            if (!$.isEmptyObject(ycxyr)){
                $("[name='ycxyr']").val(ycxyr);
            }
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
