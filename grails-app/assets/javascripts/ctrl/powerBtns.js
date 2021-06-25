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

    function init_event() {//初始化页面事件
        core.cp_ck();
        core.editCp();
        core.delPresets();
    }
    //内部核心属性
    var core = {
        /**
         * del some power buttons
         */
        delPresets : function(){
            $('#delButtons').click(function () {
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
                    var powerUid = $("#powerUid").val();
                    //选择所有的ck
                    var checkedBox = document.getElementsByClassName("cp_single");
                    var uuids = "";
                    for (var i=0; i<checkedBox.length; i++){
                        if (checkedBox[i].checked){
                            if (uuids === ""){
                                uuids = checkedBox[i].value;
                            }else{
                                uuids = uuids + "," + checkedBox[i].value;
                            }
                        }
                    }
                    $.post(
                        contextPath + 'ctrl/delPowerBtns',
                        {
                            id:roomId,
                            powerUid:powerUid,
                            uuids:uuids
                        },
                        function (result) {
                            if (result.code === 0){
                                swal({
                                    title: '删除成功!',
                                    type: 'success',
                                    confirmButtonText:'确 认'
                                },function () {
                                    var ck = document.getElementsByClassName("cp_single");
                                    for (var i=ck.length-1; i>=0; i--){
                                        if (ck[i].checked){
                                            ck[i].parentElement.parentElement.parentElement.remove()
                                        }
                                    }
                                });
                            }
                            if (result.code === 1){
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
         * update power button
         */
        editCp : function(){
            $(".editBtn").click(function () {
                //get this data uuid
                var courtroom = $("#courtroom").val();
                var powerUid = $("#powerUid").val();
                var uuid = $(this).prev().val();
                window.location.href = contextPath + "ctrl/power/button/edit/"+courtroom+"/"+ powerUid +"?uuid="+uuid;
            })
        },
        /**
         * click cp_all or cp_single checkbox
         */
        cp_ck : function(){
            //for camera_all
            $(".cp_all").click(function () {
                //get all cp_single checkbox
                var cp_single_all = $(".cp_single");
                if ($(this).is(':checked')) {
                    cp_single_all.prop("checked", true);
                } else {
                    cp_single_all.prop("checked", false);
                }
            });
            //for cp_single
            $(".cp_single").click(function () {
                //获取所有的小的checkbox
                var cp_single_all = $(".cp_single");
                for (var i=0; i<cp_single_all.length; i++){
                    if (!cp_single_all[i].checked){
                        $('.cp_all').prop("checked", false);
                        return;
                    }
                }
                $('.cp_all').prop("checked", true);
            })
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
