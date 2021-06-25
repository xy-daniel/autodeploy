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
        core.cb_ck();
        core.editCb();
        core.delButtons();
    }
    //内部核心属性
    var core = {
        /**
         * del some cb
         */
        delButtons : function(){
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
                    //选择所有的ck
                    var checkedBox = document.getElementsByClassName("cb_single");
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
                        contextPath + 'ctrl/delButtons',
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
                                    var ck = document.getElementsByClassName("cb_single");
                                    for (var i=ck.length-1; i>=0; i--){
                                        if (ck[i].checked){
                                            ck[i].parentElement.parentElement.parentElement.remove()
                                        }
                                    }
                                });
                            }
                            if (result.code==1){
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
         * update cb
         */
        editCb : function(){
            $(".editCb").click(function () {
                //get this data uuid
                var courtroom = $("#courtroom").val();
                var uuid = $(this).prev().val();
                window.location.href = contextPath + "ctrl/buttons/edit/"+courtroom+"?uuid="+uuid;
            })
        },
        /**
         * click cb_all or cb_single checkbox
         */
        cb_ck : function(){
            //for camera_all
            $(".cb_all").click(function () {
                //get all cb_single checkbox
                var cb_single_all = $(".cb_single");
                if ($(this).is(':checked')) {
                    cb_single_all.prop("checked", true);
                } else {
                    cb_single_all.prop("checked", false);
                }
            });
            //for cb_single
            $(".cb_single").click(function () {
                //获取所有的小的checkbox
                var cb_single_all = $(".cb_single");
                for (var i=0; i<cb_single_all.length; i++){
                    if (!cb_single_all[i].checked){
                        $('.cb_all').prop("checked", false);
                        return;
                    }
                }
                $('.cb_all').prop("checked", true);
            })
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
