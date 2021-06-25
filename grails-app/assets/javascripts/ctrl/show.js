/**
 * Created by liyechao on 2021/04/13.
 */
;-function (window) {
    'use strict';
    var ready = {
        //摄像头部分
        cameraNumber : $('.camera-chn li').find('a').attr('data'),
        cameraName : $('.camera-chn li').find('a').find('span').text(),
        cameraCommand :  $.parseJSON($('.control-top').attr('data')),
        presentsCommand :  $.parseJSON($('.presents').attr('data')),//预置位设置

        //输出控制部分
        outputNumber : $('.output-chn li').find('a').attr('data'),
        outputName : $('.output-chn li').find('a').find('span').text(),

        //红外控制部分
        infraredNumber : $('.infrared-chn li').find('a').attr('data'),
        infraredname : $('.infrared-chn li').find('a').find('span').text(),

        //设备控制部分
        equipmentNumber : $('.equipment-chn li').find('a').attr('data'),
        equipmentname : $('.equipment-chn li').find('a').find('span').text(),

        //电源控制部分
        powerNewNumber : $('.powerNew-chn li').find('a').attr('data'),
        powerNewName : $('.powerNew-chn li').find('a').find('span').text(),
        
        //法庭庭审主机设备ip
        deviceIp : $('#courtroom-deviceIp').val()
    };
    var option = {};
    var init = function () {
        init_ready();//加载预设变量
        init_event();//初始化页面事件
    };

    function init_ready() {//初始化预设值
        $(".control-top").mouseover(function(){
            $(".control-btn").removeClass("backColor")
            $(".control-top").addClass("backColor");
        });
        $(".control-left").mouseover(function(){
            $(".control-btn").removeClass("backColor")
            $(".control-left").addClass("backColor");
        });
        $(".control-bottom").mouseover(function(){
            $(".control-btn").removeClass("backColor")
            $(".control-bottom").addClass("backColor");
        });
        $(".control-right").mouseover(function(){
            $(".control-btn").removeClass("backColor")
            $(".control-right").addClass("backColor");
        });
    }

    function init_event() {
        core.cameraControl();//切换摄像头初始化
        core.outputCommand();//切换输出控制初始化
        core.infraredCommand();//切换红外控制初始化
        core.equipmentCommand();//切换设备控制初始化
        core.powerNewCommand();//切换电源控制初始化
        core.presentsCommand();//摄像头预置位控制初始化
        core.soundCommand();//音量控制初始化
        core.totalCommand();//综合控制初始化
    }
    //内部核心属性
    var core = {
        //摄像机控制部分
       cameraControl: function (){
           $('.camera-chn li').click(function () { //点击切换通道事件
               var cameraNumber = $(this).find('a').attr('data');
               var cameraName = $(this).find('a').find('span').text();
               console.log("切换摄像头命令：" + cameraNumber + " / 切换摄像头名称：" + cameraName);
               ready.cameraNumber = cameraNumber;
               ready.cameraName = cameraName;
           });
           $('.control-top').mousedown(function () { //点击摄像头向上
               console.log("向上摄像头按下" + ready.cameraCommand);
               core.cameraDown("上")
               $('.fa-chevron-up').addClass('colorDirection');
               
           });
           $('.control-top').mouseup(function () { //点击摄像头向上
               console.log("向上摄像头抬起");
               core.cameraUp("上")
               $('.fa-chevron-up').removeClass('colorDirection');

           });
           $('.control-left').mousedown(function () { //点击摄像头向左
               console.log("向左摄像头按下");
               core.cameraDown("左")
               $('.fa-chevron-left').addClass('colorDirection');

           });
           $('.control-left').mouseup(function () { //点击摄像头向左
               console.log("向左摄像头抬起");
               core.cameraUp("左")
               $('.fa-chevron-left').removeClass('colorDirection');

           });
           $('.control-bottom').mousedown(function () { //点击摄像头向下
               console.log("向下摄像头按下");
               core.cameraDown("下")
               $('.fa-chevron-down').addClass('colorDirection');

           });
           $('.control-bottom').mouseup(function () { //点击摄像头向下
               console.log("向下摄像头抬起");
               core.cameraUp("下")
               $('.fa-chevron-down').removeClass('colorDirection');

           });
           $('.control-right').mousedown(function () { //点击摄像头向右
               console.log("向右摄像头按下");
               core.cameraDown("右")
               $('.fa-chevron-right').addClass('colorDirection');

           });
           $('.control-right').mouseup(function () { //点击摄像头向右
               console.log("向右摄像头抬起");
               core.cameraUp("右")
               $('.fa-chevron-right').removeClass('colorDirection');

           });

            //监听光圈鼠标事件
           $('.gq-minus').mousedown(function () { //点击光圈－
               console.log("光圈-按下");
               core.cameraDown("光圈-")
           });
           $('.gq-minus').mouseup(function () {
               console.log("光圈－抬起");
               core.cameraUp("光圈-")
           });
           $('.gq-plus').mousedown(function () {
               console.log("光圈+按下");
               core.cameraDown("光圈+")
           });
           $('.gq-plus').mouseup(function () {
               console.log("光圈+抬起");
               core.cameraUp("光圈+")
           });
           
           //监听变倍事件
           $('.bb-minus').mousedown(function () {
               console.log("变倍-按下");
               core.cameraDown("变倍-")
           });
           $('.bb-minus').mouseup(function () {
               console.log("变倍-抬起");
               core.cameraUp("变倍-")
           });
           $('.bb-plus').mousedown(function () {
               console.log("变倍+按下");
               core.cameraDown("变倍+")
           });
           $('.bb-plus').mouseup(function () {
               console.log("变倍+抬起");
               core.cameraUp("变倍+")
           });
           
           //焦距监听
           $('.jj-minus').mousedown(function () {
               console.log("焦距-按下");
               core.cameraDown("焦距-")
           });
           $('.jj-minus').mouseup(function () {
               console.log("焦距-抬起");
               core.cameraUp("焦距-")
           });
           $('.jj-plus').mousedown(function () {
               console.log("焦距+按下");
               core.cameraDown("焦距+")
           });
           $('.jj-plus').mouseup(function () {
               console.log("焦距+抬起");
               core.cameraUp("焦距+")
           });
       },
       //输出控制部分
        outputCommand: function (){
            $('.output-chn li').click(function () { //点击切换通道事件
                var outputNumber = $(this).find('a').attr('data');
                var outputName = $(this).find('a').find('span').text();
                console.log("切换输出控制命令：" + outputNumber + " / 切换输出控制名称：" + outputName);
                ready.outputNumber = outputNumber;
                ready.outputName = outputName;
            });

            $(':radio').click(function(){
                ready.checkValue = $(this).val();
                console.log(ready.outputNumber + ready.checkValue);
                core.postCommand(ready.outputNumber + ready.checkValue);
            });
           
        },
        //红外控制部分
        infraredCommand: function (){
            $('.infrared-chn li').click(function () { //点击切换通道事件
                var infraredNumber = $(this).find('a').attr('data');
                var infraredName = $(this).find('a').find('span').text();
                console.log("切换红外控制命令：" + infraredNumber + " / 切换红外控制名称：" + infraredName);
                ready.infraredNumber = infraredNumber;
                ready.infraredName = infraredName;
            });
            
            $('.irc-button-commamd').click(function(){
                ready.infraredCommand = $(this).attr('data');
                console.log(ready.infraredNumber + ready.infraredCommand);
                core.postCommand(ready.infraredNumber + ready.infraredCommand);
            });

        },
        //设备控制部分
        equipmentCommand: function (){
            $('.equipment-chn li').click(function () { //点击切换通道事件
                var equipmentNumber = $(this).find('a').attr('data');
                var equipmentName = $(this).find('a').find('span').text();
                console.log("切换设备控制命令：" + equipmentNumber + " / 切换设备控制名称：" + equipmentName);
                ready.equipmentNumber = equipmentNumber;
                ready.equipmentName = equipmentName;
            });

            $('.pow-button-commamd').click(function(){
                ready.equipmentCommand = $(this).attr('data');
                console.log(ready.equipmentNumber + ready.equipmentCommand);
                core.postCommand(ready.equipmentNumber + ready.equipmentCommand);
            });

        },
        //电源控制部分
        powerNewCommand: function (){
            $('.powerNew-chn li').click(function () { //点击切换通道事件
                var powerNewNumber = $(this).find('a').attr('data');
                var powerNewName = $(this).find('a').find('span').text();
                console.log("切换设备控制命令：" + powerNewNumber + " / 切换设备控制名称：" + powerNewName);
                ready.powerNewNumber = powerNewNumber;
                ready.powerNewName = powerNewName;
            });

            $('.powNew-button-commamd').click(function(){
                ready.powerNewCommand = $(this).attr('data');
                console.log("ip地址：" + ready.powerNewName + "----16进制字符串：" + ready.powerNewCommand);
                core.postCommandPowerNew(ready.powerNewName , ready.powerNewCommand);
            });

        },
        //音量控制部分
        soundCommand: function (){
           $('.sound-button-commamd').click(function(){
                ready.soundCommand = $(this).attr('data');
                console.log(ready.soundCommand);
                core.postCommand(ready.soundCommand);
            });

        },
        //音量控制部分
        totalCommand: function (){
            $('.total-button-commamd').click(function(){
                ready.totalCommand = $(this).attr('data');
                console.log(ready.totalCommand);
                core.postCommand(ready.totalCommand);
            });

        },
        //预置位控制部分
        presentsCommand: function (){
            $('.pre-button-commamd-save').click(function(){
                ready.presentsCamera = $("#cameraPre").val();
                if (ready.presentsCamera == null || ready.presentsCamera == "") {
                    swal({
                        title: '请选择预置位!',
                        type: 'error'
                    });
                    return
                }
                for (var i=0; i<ready.presentsCommand.length;i++){
                    if (ready.presentsCamera == "pre" + ready.presentsCommand[i].uuid){
                        ready.preCommand = ready.presentsCommand[i].save
                    }
                }
                console.log(ready.cameraNumber + ready.preCommand);
                core.postCommand(ready.cameraNumber + ready.preCommand);
            });
            $('.pre-button-commamd-call').click(function(){
                ready.presentsCamera = $("#cameraPre").val();
                if (ready.presentsCamera == null || ready.presentsCamera == "") {
                    swal({
                        title: '请选择预置位!',
                        type: 'error'
                    });
                    return
                }
                for (var i=0; i<ready.presentsCommand.length;i++){
                    if (ready.presentsCamera == "pre" + ready.presentsCommand[i].uuid){
                        ready.preCommand = ready.presentsCommand[i].call
                    }
                }
                console.log(ready.cameraNumber + ready.preCommand);
                core.postCommand(ready.cameraNumber + ready.preCommand);
            });

        },

        postCommand: function (command){
            $.ajax({
                url: contextPath + "ctrl/tcpCommand",
                type: 'POST',
                data: {
                    ip: ready.deviceIp,
                    command: command
                },
                dataType: 'json',
                success: function (result) {
                    if (result.code === 0) {
                    } else {
                        swal({
                            title: '错误!',
                            text: result.msg,
                            type: 'error'
                        });
                    }
                },
                error: function () {
                    swal({
                        title: '出现未知错误!',
                        type: 'error'
                    });
                }
            });
       },
        postCommandPowerNew: function (ip, command){
            $.ajax({
                url: contextPath + "ctrl/tcpCommandPowerNew",
                type: 'POST',
                data: {
                    ip: ip,
                    command: command
                },
                dataType: 'json',
                success: function (result) {
                    if (result.code === 0) {
                    } else {
                        swal({
                            title: '错误!',
                            text: result.msg,
                            type: 'error'
                        });
                    }
                },
                error: function () {
                    swal({
                        title: '出现未知错误!',
                        type: 'error'
                    });
                }
            });
        },
        cameraUp: function (direction){
            for (var i = 0; i<ready.cameraCommand.length; i++){
                if (ready.cameraCommand[i].name.indexOf(direction) !== -1){
                    console.log(ready.cameraNumber + ready.cameraCommand[i].codeUp);
                    core.postCommand(ready.cameraNumber + ready.cameraCommand[i].codeUp);
                }
            }
        },
        cameraDown: function (direction){
            for (var i = 0; i<ready.cameraCommand.length; i++){
                if (ready.cameraCommand[i].name.indexOf(direction) !== -1){
                    console.log(ready.cameraNumber + ready.cameraCommand[i].codeDown);
                    core.postCommand(ready.cameraNumber + ready.cameraCommand[i].codeDown);
                }
            }
        },
        
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
