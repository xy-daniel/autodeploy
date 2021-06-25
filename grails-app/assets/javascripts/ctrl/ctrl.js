/**
 * Created by sakuri on 2019/8/13.
 */
;-function (window) {
    'use strict';
    var ready = {};
    var option = {};
    var init = function () {
        init_ready();//加载预设变量
        init_event();//初始化页面事件
    };

    function init_ready() {//初始化预设值
    }

    function init_event() {
        //上报远程法庭信息
        core.uploadCourtInfo();
        //编码器
        core.editEncode();core.encode_ck();core.delEncode();
        //远程编码器
        core.editYcEncode();core.YcEncode_ck();core.delYcEncode();
        //解码器
        core.editDecode();core.decode_ck();core.delDecode();
        //VIDEO矩阵
        core.editVideo();core.video_ck();core.delVideo();
        //VGA矩阵
        core.editVga();core.vga_ck();core.delVga();
        //输出控制
        core.editOut();core.out_ck();core.delOut();
        //红外控制
        core.editIrctrl();core.irctrl_ck();core.delIrctrl();
        //音量控制
        core.editSound();core.sound_ck();core.delSound();
        //综合控制
        core.editTotal();core.total_ck();core.delTotal();
        //强电控制
        core.editPower();core.power_ck();core.delPower();
        //摄像头控制
        core.editCamera();core.camera_ck();core.delCamera();
        //电源控制
        core.editPowerNew();core.powerNew_ck();core.delPowerNew();
        //页面跳转
        core.pageSkipping();
    }
    //内部核心属性
    var core = {
        //上报远程法庭信息
        uploadCourtInfo: function(){
            $("#uploadCourtInfo").click(function () {
                $.get(
                    contextPath + 'remote/synrooms',
                    {
                        id : $("#courtroom").val()
                    },
                    function (xhr) {
                        if (xhr.code===0){
                            //上报成功
                            swal({
                                title: '上报成功!',
                                type: 'success',
                                confirmButtonText:'确 认'
                            });
                        }else{
                            var msg = xhr.data;
                            if (msg===""){
                             msg = "连接失败";
                            }
                            //上报失败
                            swal({
                                title: msg,
                                type: 'error',
                                confirmButtonText:'确 认'
                            });
                        }
                    },'json'
                )
            })
        },
        delYcEncode: function(){
            $('#delYcEncode').click(function () {
                swal({
                    title:"确认删除?",
                    type:"warning",
                    showCancelButton:true,
                    cancelButtonText:'取 消',
                    cancelButtonColor:'#b9b9b9',
                    showConfirmButton:true,
                    confirmButtonText:'确 认',
                    confirmButtonColor:"#dd6b55",
                    closeOnConfirm:false,
                    closeOnCancel:true
                },function(){
                    var roomId = $("#courtroom").val();
                    //选择所有的ck
                    var checkedBox = document.getElementsByClassName("ycEncode_single");
                    var uuids = "";
                    for (var i=0; i<checkedBox.length; i++){
                        if (checkedBox[i].checked){
                            if (uuids===""){
                                uuids = checkedBox[i].value;
                            }else{
                                uuids = uuids + "," + checkedBox[i].value;
                            }
                        }
                    }
                    $.get(
                        contextPath + 'ctrl/delYcEncodes',
                        {
                            uuids:uuids,
                            id:roomId
                        },
                        function (result) {
                            if (result.code===0){
                                swal({
                                    title: '删除成功!',
                                    type: 'success',
                                    confirmButtonText:'确 认'
                                },function () {
                                    var ck = document.getElementsByClassName("ycEncode_single");
                                    for (var i=ck.length-1; i>=0; i--){
                                        if (ck[i].checked){
                                            ck[i].parentElement.parentElement.parentElement.remove()
                                        }
                                    }
                                });
                            }else if (result.code===410){
                                swal({
                                    title: '请选择数据',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                })
                            }else if (result.code===1){
                                swal({
                                    title: '数据删除失败，请重新操作...',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                });
                            }
                        },'json'
                    )
                });
            })
        },
        //编辑远程编码器
        editYcEncode: function(){
            $(".editYcEncode").click(function () {
                //get this data uuid
                var courtroom = $("#courtroom").val();
                var uuid = $(this).prev().val();
                window.location.href = contextPath + "ctrl/ycEncode/edit/"+courtroom+"?uuid="+uuid;
            })
        },
        //远程编码器全选按钮
        YcEncode_ck: function(){
            //for ycEncode_all
            $(".ycEncode_all").click(function () {
                //get all encode_single checkbox
                var encode_single_all = $(".ycEncode_single");
                if ($(this).is(':checked')) {
                    encode_single_all.prop("checked", true);
                } else {
                    encode_single_all.prop("checked", false);
                }
            });
            //for encode_single
            $(".ycEncode_single").click(function () {
                //获取所有的小的checkbox
                var encode_single_all = $(".ycEncode_single");
                for (var i=0; i<encode_single_all.length; i++){
                    if (!encode_single_all[i].checked){
                        $('.ycEncode_all').prop("checked", false);
                        return;
                    }
                }
                $('.ycEncode_all').prop("checked", true);
            })
        },
        /**
         * 页面跳转
         */
        pageSkipping : function(){
            var flag = $("#flag").val();
            if (flag==="0"){
                $("#encode").click();
            }
            if (flag==="1"){
                $("#decode").click();
            }
            if (flag==="2"){
                $("#video").click();
            }
            if (flag==="3"){
                $("#vga").click();
            }
            if (flag==="4"){
                $("#out").click();
            }
            if (flag==="5"){
                $("#sound").click();
            }
            if (flag==="6"){
                $("#total").click();
            }
            if (flag==="7"){
                $("#power").click();
            }
            if (flag==="8"){
                $("#ir").click();
            }
            if (flag==="9"){
                $("#camera").click();
            }
            if (flag==="10"){
                $("#ycEncode").click();
            }
        },
        /**
         * del some camera
         */
        delCamera : function(){
            $('#delCamera').click(function () {
                swal({
                    title:"确认删除?",
                    type:"warning",
                    showCancelButton:true,
                    cancelButtonText:'取 消',
                    cancelButtonColor:'#b9b9b9',
                    showConfirmButton:true,
                    confirmButtonText:'确 认',
                    confirmButtonColor:"#dd6b55",
                    closeOnConfirm:false,
                    closeOnCancel:true
                },function(){
                    var roomId = $("#courtroom").val();
                    //选择所有的ck
                    var checkedBox = document.getElementsByClassName("camera_single");
                    var uuids = "";
                    for (var i=0; i<checkedBox.length; i++){
                        if (checkedBox[i].checked){
                            if (uuids==""){
                                uuids = checkedBox[i].value;
                            }else{
                                uuids = uuids + "," + checkedBox[i].value;
                            }
                        }
                    }
                    $.post(
                        contextPath + 'ctrl/delCameras',
                        {
                            uuids:uuids,
                            id:roomId
                        },
                        function (result) {
                            if (result.code==0){
                                swal({
                                    title: '删除成功!',
                                    type: 'success',

                                    confirmButtonText:'确 认'
                                },function () {
                                    var ck = document.getElementsByClassName("camera_single");
                                    for (var i=ck.length-1; i>=0; i--){
                                        if (ck[i].checked){
                                            ck[i].parentElement.parentElement.parentElement.remove()
                                        }
                                    }
                                });
                            }else if (result.code===410){
                                swal({
                                    title: '请选择数据',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                })
                            }else if (result.code==1){
                                swal({
                                    title: '数据删除失败，请重新操作...',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                });
                            }
                        },'json'
                    )
                })
            })
        },
        /**
         * click camera_all or camera_single checkbox
         */
        camera_ck : function(){
            //for camera_all
            $(".camera_all").click(function () {
                //get all camera_single checkbox
                var camera_single_all = $(".camera_single");
                if ($(this).is(':checked')) {
                    camera_single_all.prop("checked", true);
                } else {
                    camera_single_all.prop("checked", false);
                }
            });
            //for camera_single
            $(".camera_single").click(function () {
                //获取所有的小的checkbox
                var camera_single_all = $(".camera_single");
                for (var i=0; i<camera_single_all.length; i++){
                    if (!camera_single_all[i].checked){
                        $('.camera_all').prop("checked", false);
                        return;
                    }
                }
                $('.camera_all').prop("checked", true);
            })
        },
        /**
         * update camera
         */
        editCamera : function(){
            $(".editCamera").click(function () {
                //get this data uuid
                var courtroom = $("#courtroom").val();
                var uuid = $(this).prev().val();
                window.location.href = contextPath + "ctrl/camera/edit/"+courtroom+"?uuid="+uuid;
            })
        },
        /**
         * del some power
         */
        delPower : function(){
            $('#delPower').click(function () {
                swal({
                    title:"确认删除?",
                    type:"warning",
                    showCancelButton:true,
                    cancelButtonText:'取 消',
                    cancelButtonColor:'#b9b9b9',
                    showConfirmButton:true,
                    confirmButtonText:'确 认',
                    confirmButtonColor:"#dd6b55",
                    closeOnConfirm:false,
                    closeOnCancel:true
                },function(){
                    var roomId = $("#courtroom").val();
                    //选择所有的ck
                    var checkedBox = document.getElementsByClassName("power_single");
                    var uuids = "";
                    for (var i=0; i<checkedBox.length; i++){
                        if (checkedBox[i].checked){
                            if (uuids==""){
                                uuids = checkedBox[i].value;
                            }else{
                                uuids = uuids + "," + checkedBox[i].value;
                            }
                        }
                    }
                    $.post(
                        contextPath + 'ctrl/delPowers',
                        {
                            uuids:uuids,
                            id:roomId
                        },
                        function (result) {
                            if (result.code==0){
                                swal({
                                    title: '删除成功!',
                                    type: 'success',
                                    confirmButtonText:'确 认'
                                },function () {
                                    var ck = document.getElementsByClassName("power_single");
                                    for (var i=ck.length-1; i>=0; i--){
                                        if (ck[i].checked){
                                            ck[i].parentElement.parentElement.parentElement.remove()
                                        }
                                    }
                                });
                            }else if (result.code===410){
                                swal({
                                    title: '请选择数据',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                })
                            }else if (result.code==1){
                                swal({
                                    title: '数据删除失败，请重新操作...',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                });
                            }
                        },'json'
                    )
                })
            })
        },
        /**
         * click power_all or power_single checkbox
         */
        power_ck : function(){
            //for power_all
            $(".power_all").click(function () {
                //get all power_single checkbox
                var power_single_all = $(".power_single");
                if ($(this).is(':checked')) {
                    power_single_all.prop("checked", true);
                } else {
                    power_single_all.prop("checked", false);
                }
            });
            //for power_single
            $(".power_single").click(function () {
                //获取所有的小的checkbox
                var power_single_all = $(".power_single");
                for (var i=0; i<power_single_all.length; i++){
                    if (!power_single_all[i].checked){
                        $('.power_all').prop("checked", false);
                        return;
                    }
                }
                $('.power_all').prop("checked", true);
            })
        },
        /**
         * update power
         */
        editPower : function(){
            $(".editPower").click(function () {
                //get this data uuid
                var courtroom = $("#courtroom").val();
                var uuid = $(this).prev().val();
                window.location.href = contextPath + "ctrl/power/edit/"+courtroom+"?uuid="+uuid;
            })
        },
        /**
         * del some powerNew
         */
        delPowerNew : function(){
            $('#delPowerNew').click(function () {
                swal({
                    title:"确认删除?",
                    type:"warning",
                    showCancelButton:true,
                    cancelButtonText:'取 消',
                    cancelButtonColor:'#b9b9b9',
                    showConfirmButton:true,
                    confirmButtonText:'确 认',
                    confirmButtonColor:"#dd6b55",
                    closeOnConfirm:false,
                    closeOnCancel:true
                },function(){
                    var roomId = $("#courtroom").val();
                    //选择所有的ck
                    var checkedBox = document.getElementsByClassName("powerNew_single");
                    var uuids = "";
                    for (var i=0; i<checkedBox.length; i++){
                        if (checkedBox[i].checked){
                            if (uuids==""){
                                uuids = checkedBox[i].value;
                            }else{
                                uuids = uuids + "," + checkedBox[i].value;
                            }
                        }
                    }
                    $.post(
                        contextPath + 'ctrl/delPowerNew',
                        {
                            uuids:uuids,
                            id:roomId
                        },
                        function (result) {
                            if (result.code==0){
                                swal({
                                    title: '删除成功!',
                                    type: 'success',
                                    confirmButtonText:'确 认'
                                },function () {
                                    var ck = document.getElementsByClassName("powerNew_single");
                                    for (var i=ck.length-1; i>=0; i--){
                                        if (ck[i].checked){
                                            ck[i].parentElement.parentElement.parentElement.remove()
                                        }
                                    }
                                });
                            }else if (result.code===410){
                                swal({
                                    title: '请选择数据',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                })
                            }else if (result.code==1){
                                swal({
                                    title: '数据删除失败，请重新操作...',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                });
                            }
                        },'json'
                    )
                })
            })
        },
        /**
         * click powerNew_all or powerNew_single checkbox
         */
        powerNew_ck : function(){
            //for power_all
            $(".powerNew_all").click(function () {
                //get all powerNew_single checkbox
                var powerNew_single_all = $(".powerNew_single");
                if ($(this).is(':checked')) {
                    powerNew_single_all.prop("checked", true);
                } else {
                    powerNew_single_all.prop("checked", false);
                }
            });
            //for powerNew_single
            $(".powerNew_single").click(function () {
                //获取所有的小的checkbox
                var powerNew_single_all = $(".powerNew_single");
                for (var i=0; i<powerNew_single_all.length; i++){
                    if (!powerNew_single_all[i].checked){
                        $('.powerNew_all').prop("checked", false);
                        return;
                    }
                }
                $('.powerNew_all').prop("checked", true);
            })
        },
        /**
         * update powerNew
         */
        editPowerNew : function(){
            $(".editPowerNew").click(function () {
                //get this data uuid
                var courtroom = $("#courtroom").val();
                var uuid = $(this).prev().val();
                window.location.href = contextPath + "ctrl/powerNew/edit/"+courtroom+"?uuid="+uuid;
            })
        },
        /**
         * del some total
         */
        delTotal : function(){
            $('#delTotal').click(function () {
                swal({
                    title:"确认删除?",
                    type:"warning",
                    showCancelButton:true,
                    cancelButtonText:'取 消',
                    cancelButtonColor:'#b9b9b9',
                    showConfirmButton:true,
                    confirmButtonText:'确 认',
                    confirmButtonColor:"#dd6b55",
                    closeOnConfirm:false,
                    closeOnCancel:true
                },function(){
                    var roomId = $("#courtroom").val();
                    //选择所有的ck
                    var checkedBox = document.getElementsByClassName("total_single");
                    var uuids = "";
                    for (var i=0; i<checkedBox.length; i++){
                        if (checkedBox[i].checked){
                            if (uuids==""){
                                uuids = checkedBox[i].value;
                            }else{
                                uuids = uuids + "," + checkedBox[i].value;
                            }
                        }
                    }
                    $.post(
                        contextPath + 'ctrl/delTotals',
                        {
                            uuids:uuids,
                            id:roomId
                        },
                        function (result) {
                            if (result.code==0){
                                swal({
                                    title: '删除成功!',
                                    type: 'success',
                                    confirmButtonText:'确 认'
                                },function () {
                                    var ck = document.getElementsByClassName("total_single");
                                    for (var i=ck.length-1; i>=0; i--){
                                        if (ck[i].checked){
                                            ck[i].parentElement.parentElement.parentElement.remove()
                                        }
                                    }
                                });
                            }else if (result.code===410){
                                swal({
                                    title: '请选择数据',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                })
                            }else if (result.code==1){
                                swal({
                                    title: '数据删除失败，请重新操作...',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                });
                            }
                        },'json'
                    )
                })
            })
        },
        /**
         * click total_all or total_single checkbox
         */
        total_ck : function(){
            //for total_all
            $(".total_all").click(function () {
                //get all total_single checkbox
                var total_single_all = $(".total_single");
                if ($(this).is(':checked')) {
                    total_single_all.prop("checked", true);
                } else {
                    total_single_all.prop("checked", false);
                }
            });
            //for total_single
            $(".total_single").click(function () {
                //获取所有的小的checkbox
                var total_single_all = $(".total_single");
                for (var i=0; i<total_single_all.length; i++){
                    if (!total_single_all[i].checked){
                        $('.total_all').prop("checked", false);
                        return;
                    }
                }
                $('.total_all').prop("checked", true);
            })
        },
        /**
         * update total
         */
        editTotal : function(){
            $(".editTotal").click(function () {
                //get this data uuid
                var courtroom = $("#courtroom").val();
                var uuid = $(this).prev().val();
                window.location.href = contextPath + "ctrl/total/edit/"+courtroom+"?uuid="+uuid;
            })
        },
        /**
         * del some sound
         */
        delSound : function(){
            $('#delSound').click(function () {
                swal({
                    title:"确认删除?",
                    type:"warning",
                    showCancelButton:true,
                    cancelButtonText:'取 消',
                    cancelButtonColor:'#b9b9b9',
                    showConfirmButton:true,
                    confirmButtonText:'确 认',
                    confirmButtonColor:"#dd6b55",
                    closeOnConfirm:false,
                    closeOnCancel:true
                },function(){
                    var roomId = $("#courtroom").val();
                    //选择所有的ck
                    var checkedBox = document.getElementsByClassName("sound_single");
                    var uuids = "";
                    for (var i=0; i<checkedBox.length; i++){
                        if (checkedBox[i].checked){
                            if (uuids==""){
                                uuids = checkedBox[i].value;
                            }else{
                                uuids = uuids + "," + checkedBox[i].value;
                            }
                        }
                    }
                    $.post(
                        contextPath + 'ctrl/delSounds',
                        {
                            uuids:uuids,
                            id:roomId
                        },
                        function (result) {
                            if (result.code==0){
                                swal({
                                    title: '删除成功!',
                                    type: 'success',
                                    confirmButtonText:'确 认'
                                },function () {
                                    var ck = document.getElementsByClassName("sound_single");
                                    for (var i=ck.length-1; i>=0; i--){
                                        if (ck[i].checked){
                                            ck[i].parentElement.parentElement.parentElement.remove()
                                        }
                                    }
                                });
                            }else if (result.code===410){
                                swal({
                                    title: '请选择数据',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                })
                            }else if (result.code==1){
                                swal({
                                    title: '数据删除失败，请重新操作...',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                });
                            }
                        },'json'
                    )
                })
            })
        },
        /**
         * click sound_all or sound_single checkbox
         */
        sound_ck : function(){
            //for sound_all
            $(".sound_all").click(function () {
                //get all sound_single checkbox
                var sound_single_all = $(".sound_single");
                if ($(this).is(':checked')) {
                    sound_single_all.prop("checked", true);
                } else {
                    sound_single_all.prop("checked", false);
                }
            });
            //for sound_single
            $(".sound_single").click(function () {
                //获取所有的小的checkbox
                var sound_single_all = $(".sound_single");
                for (var i=0; i<sound_single_all.length; i++){
                    if (!sound_single_all[i].checked){
                        $('.sound_all').prop("checked", false);
                        return;
                    }
                }
                $('.sound_all').prop("checked", true);
            })
        },
        /**
         * update sound
         */
        editSound : function(){
            $(".editSound").click(function () {
                //get this data uuid
                var courtroom = $("#courtroom").val();
                var uuid = $(this).prev().val();
                window.location.href = contextPath + "ctrl/sound/edit/"+courtroom+"?uuid="+uuid;
            })
        },
        /**
         * del some irctrl
         */
        delIrctrl : function(){
            $('#delIrctrl').click(function () {
                swal({
                    title:"确认删除?",
                    type:"warning",
                    showCancelButton:true,
                    cancelButtonText:'取 消',
                    cancelButtonColor:'#b9b9b9',
                    showConfirmButton:true,
                    confirmButtonText:'确 认',
                    confirmButtonColor:"#dd6b55",
                    closeOnConfirm:false,
                    closeOnCancel:true
                },function(){
                    var roomId = $("#courtroom").val();
                    //选择所有的ck
                    var checkedBox = document.getElementsByClassName("irctrl_single");
                    var uuids = "";
                    for (var i=0; i<checkedBox.length; i++){
                        if (checkedBox[i].checked){
                            if (uuids==""){
                                uuids = checkedBox[i].value;
                            }else{
                                uuids = uuids + "," + checkedBox[i].value;
                            }
                        }
                    }
                    $.get(
                        contextPath + 'ctrl/delIrctrls',
                        {
                            uuids:uuids,
                            id:roomId
                        },
                        function (result) {
                            if (result.code==0){
                                swal({
                                    title: '删除成功!',
                                    type: 'success',
                                    confirmButtonText:'确 认'
                                },function () {
                                    var ck = document.getElementsByClassName("irctrl_single");
                                    for (var i=ck.length-1; i>=0; i--){
                                        if (ck[i].checked){
                                            ck[i].parentElement.parentElement.parentElement.remove()
                                        }
                                    }
                                });
                            }else if (result.code===410){
                                swal({
                                    title: '请选择数据',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                })
                            }else if (result.code==1){
                                swal({
                                    title: '数据删除失败，请重新操作...',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                });
                            }
                        },'json'
                    )
                })
            })
        },
        /**
         * click irctrl_all or irctrl_single checkbox
         */
        irctrl_ck : function(){
            //for irctrl_all
            $(".irctrl_all").click(function () {
                //get all out_single checkbox
                var irctrl_single_all = $(".irctrl_single");
                if ($(this).is(':checked')) {
                    irctrl_single_all.prop("checked", true);
                } else {
                    irctrl_single_all.prop("checked", false);
                }
            });
            //for irctrl_single
            $(".irctrl_single").click(function () {
                //获取所有的小的checkbox
                var irctrl_single_all = $(".irctrl_single");
                for (var i=0; i<irctrl_single_all.length; i++){
                    if (!irctrl_single_all[i].checked){
                        $('.irctrl_all').prop("checked", false);
                        return;
                    }
                }
                $('.irctrl_all').prop("checked", true);
            })
        },
        /**
         * update irctrl
         */
        editIrctrl : function(){
            $(".editIrctrl").click(function () {
                //get this data uuid
                var courtroom = $("#courtroom").val();
                var uuid = $(this).prev().val();
                window.location.href = contextPath + "ctrl/irctrl/edit/"+courtroom+"?uuid="+uuid;
            })
        },
        /**
         * del some out
         */
        delOut : function(){
            $('#delOut').click(function () {
                swal({
                    title:"确认删除?",
                    type:"warning",
                    showCancelButton:true,
                    cancelButtonText:'取 消',
                    cancelButtonColor:'#b9b9b9',
                    showConfirmButton:true,
                    confirmButtonText:'确 认',
                    confirmButtonColor:"#dd6b55",
                    closeOnConfirm:false,
                    closeOnCancel:true
                },function(){
                    var roomId = $("#courtroom").val();
                    //选择所有的ck
                    var checkedBox = document.getElementsByClassName("out_single");
                    var uuids = "";
                    for (var i=0; i<checkedBox.length; i++){
                        if (checkedBox[i].checked){
                            if (uuids==""){
                                uuids = checkedBox[i].value;
                            }else{
                                uuids = uuids + "," + checkedBox[i].value;
                            }
                        }
                    }
                    $.get(
                        contextPath + 'ctrl/delOuts',
                        {
                            uuids:uuids,
                            id:roomId
                        },
                        function (result) {
                            if (result.code==0){
                                swal({
                                    title: '删除成功!',
                                    type: 'success',
                                    confirmButtonText:'确 认'
                                },function () {
                                    var ck = document.getElementsByClassName("out_single");
                                    for (var i=ck.length-1; i>=0; i--){
                                        if (ck[i].checked){
                                            ck[i].parentElement.parentElement.parentElement.remove()
                                        }
                                    }
                                });

                            }else if (result.code===410){
                                swal({
                                    title: '请选择数据',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                })
                            }else if (result.code==1){
                                swal({
                                    title: '数据删除失败，请重新操作...',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                });
                            }
                        },'json'
                    )
                })
            })
        },
        /**
         * click out_all or out_single checkbox
         */
        out_ck : function(){
            //for vag_all
            $(".out_all").click(function () {
                //get all out_single checkbox
                var out_single_all = $(".out_single");
                if ($(this).is(':checked')) {
                    out_single_all.prop("checked", true);
                } else {
                    out_single_all.prop("checked", false);
                }
            });
            //for out_single
            $(".out_single").click(function () {
                //获取所有的小的checkbox
                var out_single_all = $(".out_single");
                for (var i=0; i<out_single_all.length; i++){
                    if (!out_single_all[i].checked){
                        $('.out_all').prop("checked", false);
                        return;
                    }
                }
                $('.out_all').prop("checked", true);
            })
        },
        /**
         * update vga
         */
        editOut : function(){
            $(".editOut").click(function () {
                //get this data uuid
                var courtroom = $("#courtroom").val();
                var uuid = $(this).prev().val();
                window.location.href = contextPath + "ctrl/out/edit/"+courtroom+"?uuid="+uuid;
            })
        },
        /**
         * del some vag
         */
        delVga : function(){
            $('#delVag').click(function () {
                swal({
                    title:"确认删除?",
                    type:"warning",
                    showCancelButton:true,
                    cancelButtonText:'取 消',
                    cancelButtonColor:'#b9b9b9',
                    showConfirmButton:true,
                    confirmButtonText:'确 认',
                    confirmButtonColor:"#dd6b55",
                    closeOnConfirm:false,
                    closeOnCancel:true
                },function(){
                    var roomId = $("#courtroom").val();
                    //选择所有的ck
                    var checkedBox = document.getElementsByClassName("vag_single");
                    var uuids = "";
                    for (var i=0; i<checkedBox.length; i++){
                        if (checkedBox[i].checked){
                            if (uuids==""){
                                uuids = checkedBox[i].value;
                            }else{
                                uuids = uuids + "," + checkedBox[i].value;
                            }
                        }
                    }
                    $.get(
                        contextPath + 'ctrl/delVgas',
                        {
                            uuids:uuids,
                            id:roomId
                        },
                        function (result) {
                            if (result.code==0){
                                swal({
                                    title: '删除成功!',
                                    type: 'success',
                                    confirmButtonText:'确 认'
                                },function () {
                                    var ck = document.getElementsByClassName("vag_single");
                                    for (var i=ck.length-1; i>=0; i--){
                                        if (ck[i].checked){
                                            ck[i].parentElement.parentElement.parentElement.remove()
                                        }
                                    }
                                });
                            }else if (result.code===410){
                                swal({
                                    title: '请选择数据',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                })
                            }else if (result.code==1){
                                swal({
                                    title: '数据删除失败，请重新操作...',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                });
                            }
                        },'json'
                    )
                })
            })
        },
        /**
         * click vag_all or vag_single checkbox
         */
        vga_ck : function(){
            //for vag_all
            $(".vag_all").click(function () {
                //get all vag_single checkbox
                var vag_single_all = $(".vag_single");
                if ($(this).is(':checked')) {
                    vag_single_all.prop("checked", true);
                } else {
                    vag_single_all.prop("checked", false);
                }
            });
            //for vag_single
            $(".vag_single").click(function () {
                //获取所有的小的checkbox
                var vag_single_all = $(".vag_single");
                for (var i=0; i<vag_single_all.length; i++){
                    if (!vag_single_all[i].checked){
                        $('.vag_all').prop("checked", false);
                        return;
                    }
                }
                $('.vag_all').prop("checked", true);
            })
        },
        /**
         * update vga
         */
        editVga : function(){
            $(".editVga").click(function () {
                //get this data uuid
                var courtroom = $("#courtroom").val();
                var uuid = $(this).prev().val();
                window.location.href = contextPath + "ctrl/vga/edit/"+courtroom+"?uuid="+uuid;
            })
        },
        /**
         * del some video
         */
        delVideo : function(){
            $('#delVideo').click(function () {
                swal({
                    title:"确认删除?",
                    type:"warning",
                    showCancelButton:true,
                    cancelButtonText:'取 消',
                    cancelButtonColor:'#b9b9b9',
                    showConfirmButton:true,
                    confirmButtonText:'确 认',
                    confirmButtonColor:"#dd6b55",
                    closeOnConfirm:false,
                    closeOnCancel:true
                },function(){
                    var roomId = $("#courtroom").val();
                    //选择所有的ck
                    var checkedBox = document.getElementsByClassName("video_single");
                    var uuids = "";
                    for (var i=0; i<checkedBox.length; i++){
                        if (checkedBox[i].checked){
                            if (uuids==""){
                                uuids = checkedBox[i].value;
                            }else{
                                uuids = uuids + "," + checkedBox[i].value;
                            }
                        }
                    }
                    $.get(
                        contextPath + 'ctrl/delVideos',
                        {
                            uuids:uuids,
                            id:roomId
                        },
                        function (result) {
                            if (result.code==0){
                                swal({
                                    title: '删除成功!',
                                    type: 'success',
                                    confirmButtonText:'确 认'
                                },function () {
                                    var ck = document.getElementsByClassName("video_single");
                                    for (var i=ck.length-1; i>=0; i--){
                                        if (ck[i].checked){
                                            ck[i].parentElement.parentElement.parentElement.remove()
                                        }
                                    }
                                });
                            }else if (result.code===410){
                                swal({
                                    title: '请选择数据',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                })
                            }else if (result.code==1){
                                swal({
                                    title: '数据删除失败，请重新操作...',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                });
                            }
                        },'json'
                    )
                })
            })
        },
        /**
         * click video_all or video_single checkbox
         */
        video_ck : function(){
            //for video_all
            $(".video_all").click(function () {
                //get all encode_single checkbox
                var video_single_all = $(".video_single");
                if ($(this).is(':checked')) {
                    video_single_all.prop("checked", true);
                } else {
                    video_single_all.prop("checked", false);
                }
            });
            //for video_single
            $(".video_single").click(function () {
                //获取所有的小的checkbox
                var video_single_all = $(".video_single");
                for (var i=0; i<video_single_all.length; i++){
                    if (!video_single_all[i].checked){
                        $('.video_all').prop("checked", false);
                        return;
                    }
                }
                $('.video_all').prop("checked", true);
            })
        },
        /**
         * update video
         */
        editVideo : function(){
            $(".editVideo").click(function () {
                //get this data uuid
                var courtroom = $("#courtroom").val();
                var uuid = $(this).prev().val();
                window.location.href = contextPath + "ctrl/video/edit/"+courtroom+"?uuid="+uuid;
            })
        },
        /**
         * del some decode
         */
        delDecode : function(){
            $('#delDecode').click(function () {
                    swal({
                        title:"确认删除?",
                        type:"warning",
                        showCancelButton:true,
                        cancelButtonText:'取 消',
                        cancelButtonColor:'#b9b9b9',
                        showConfirmButton:true,
                        confirmButtonText:'确 认',
                        confirmButtonColor:"#dd6b55",
                        closeOnConfirm:false,
                        closeOnCancel:true
                    },function(){
                        var roomId = $("#courtroom").val();
                        //选择所有的ck
                        var checkedBox = document.getElementsByClassName("decode_single");
                        var uuids = "";
                        for (var i=0; i<checkedBox.length; i++){
                            if (checkedBox[i].checked){
                                if (uuids==""){
                                    uuids = checkedBox[i].value;
                                }else{
                                    uuids = uuids + "," + checkedBox[i].value;
                                }
                            }
                        }
                        $.get(
                            contextPath + 'ctrl/delDecodes',
                            {
                                uuids:uuids,
                                id:roomId
                            },
                            function (result) {
                                if (result.code==0){
                                    swal({
                                        title: '删除成功!',
                                        type: 'success',
                                        confirmButtonText:'确 认'
                                    },function () {
                                        var ck = document.getElementsByClassName("decode_single");
                                        for (var i=ck.length-1; i>=0; i--){
                                            if (ck[i].checked){
                                                ck[i].parentElement.parentElement.parentElement.remove()
                                            }
                                        }
                                    });
                                }else if (result.code===410){
                                    swal({
                                        title: '请选择数据',
                                        type: 'error',
                                        confirmButtonText:'确 认'
                                    })
                                }else if (result.code==1){
                                    swal({
                                        title: '数据删除失败，请重新操作...',
                                        type: 'error',
                                        confirmButtonText:'确 认'
                                    });
                                }
                            },'json'
                        )
                    })
            })
        },
        /**
         * click decode_all or decode_single checkbox
         */
        decode_ck : function(){
            //for decode_all
            $(".decode_all").click(function () {
                //get all decode_single checkbox
                var decode_single_all = $(".decode_single");
                if ($(this).is(':checked')) {
                    decode_single_all.prop("checked", true);
                } else {
                    decode_single_all.prop("checked", false);
                }
            });
            //for decode_single
            $(".decode_single").click(function () {
                //获取所有的小的checkbox
                var decode_single_all = $(".decode_single");
                for (var i=0; i<decode_single_all.length; i++){
                    if (!decode_single_all[i].checked){
                        $('.decode_all').prop("checked", false);
                        return;
                    }
                }
                $('.decode_all').prop("checked", true);
            })
        },
        /**
         * update decode
         */
        editDecode : function(){
            $(".editDecode").click(function () {
                //get this data uuid
                var courtroom = $("#courtroom").val();
                var uuid = $(this).prev().val();
                window.location.href = contextPath + "ctrl/decode/edit/"+courtroom+"?uuid="+uuid;
            })
        },
        /**
         * del some encode
         */
        delEncode : function(){
          $('#delEncode').click(function () {
              swal({
                  title:"确认删除?",
                  type:"warning",
                  showCancelButton:true,
                  cancelButtonText:'取 消',
                  cancelButtonColor:'#b9b9b9',
                  showConfirmButton:true,
                  confirmButtonText:'确 认',
                  confirmButtonColor:"#dd6b55",
                  closeOnConfirm:false,
                  closeOnCancel:true
              },function(){
                  var roomId = $("#courtroom").val();
                  //选择所有的ck
                  var checkedBox = document.getElementsByClassName("encode_single");
                  var uuids = "";
                  for (var i=0; i<checkedBox.length; i++){
                      if (checkedBox[i].checked){
                          if (uuids==""){
                              uuids = checkedBox[i].value;
                          }else{
                              uuids = uuids + "," + checkedBox[i].value;
                          }
                      }
                  }
                  $.get(
                      contextPath + 'ctrl/delEncodes',
                      {
                          uuids:uuids,
                          id:roomId
                      },
                      function (result) {
                          if (result.code==0){
                              swal({
                                  title: '删除成功!',
                                  type: 'success',
                                  confirmButtonText:'确 认'
                              },function () {
                                  var ck = document.getElementsByClassName("encode_single");
                                  for (var i=ck.length-1; i>=0; i--){
                                      if (ck[i].checked){
                                          ck[i].parentElement.parentElement.parentElement.remove()
                                      }
                                  }
                              });
                          }else if (result.code===410){
                              swal({
                                  title: '请选择数据',
                                  type: 'error',
                                  confirmButtonText:'确 认'
                              })
                          }else if (result.code==1){
                              swal({
                                  title: '数据删除失败，请重新操作...',
                                  type: 'error',
                                  confirmButtonText:'确 认'
                              });
                          }
                      },'json'
                  )
              });
          })
        },
        /**
         * click encode_all or encode_single checkbox
         */
        encode_ck : function(){
            //for encode_all
            $(".encode_all").click(function () {
                //get all encode_single checkbox
                var encode_single_all = $(".encode_single");
                if ($(this).is(':checked')) {
                    encode_single_all.prop("checked", true);
                } else {
                    encode_single_all.prop("checked", false);
                }
            });
            //for encode_single
            $(".encode_single").click(function () {
                //获取所有的小的checkbox
                var encode_single_all = $(".encode_single");
                for (var i=0; i<encode_single_all.length; i++){
                    if (!encode_single_all[i].checked){
                        $('.encode_all').prop("checked", false);
                        return;
                    }
                }
                $('.encode_all').prop("checked", true);
            })
        },
        /**
         * update encode
         */
        editEncode : function(){
            $(".editEncode").click(function () {
                //get this data uuid
                var courtroom = $("#courtroom").val();
                var uuid = $(this).prev().val();
                window.location.href = contextPath + "ctrl/encode/edit/"+courtroom+"?uuid="+uuid;
            })
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
