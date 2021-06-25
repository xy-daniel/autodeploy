/**
 * Created by sakuri on 2019/8/13.
 */
;-function (window) {
    'use strict';
    var ready = {};
    var init = function () {
        init_ready();//加载预设变量
        init_event();//初始化页面事件
    };

    function init_ready() {//初始化预设值
    }

    function init_event() {//初始化页面事件
        //前端校验
        core.parsley();
        //权限选择格式化
        core.selectpicker();
        //验证角色名称和功能权限
        core.validateAuthority();
        core.validateRemark();
        //表单提交
        core.ajax_form();
        //初始化菜单树
        core.getMenuTree();
    }

    //内部核心属性
    var core = {
        parsley : function(){
            $("#form-useradd").parsley({
                errorsContainer: function(pEle) {
                    return pEle.$element.siblings('.errorBlock');
                }
            });
        },
        selectpicker: function () {
            $('.selectpicker').selectpicker('render');
        },
        validateAuthority: function () {
            $("#authority").keyup(function () {
                window.Parsley.addAsyncValidator('checkauthority', function (xhr) {
                    console.log(JSON.parse(xhr.responseText).data);
                    return JSON.parse(xhr.responseText).data == 0;
                }, contextPath + 'role/getRoleByAuthority');
            })
        },
        validateRemark: function () {
            $("#remark").keyup(function () {
                window.Parsley.addAsyncValidator('checkremark', function (xhr) {
                    console.log(JSON.parse(xhr.responseText).data);
                    return JSON.parse(xhr.responseText).data == 0;
                }, contextPath + 'role/getRoleByRemark');
            })
        },
        ajax_form:function(){
            $("#form-useradd").ajaxForm({
                type: 'post',
                dataType: 'json',
                // 提交之前的修改数据
                beforeSerialize: function(){
                    ready.nodes = ready.menu_ztree.getCheckedNodes(true); // 获取所有选中节点
                    ready.ids = "";
                    for (var i = 0; i < ready.nodes.length; i++) {
                        ready.ids += ready.nodes[i].id;
                        ready.ids += ",";
                    }
                    $("#ids").val(ready.ids);
                },
                //回调成功
                success: function (data) {
                    if (data.code === 0) {
                        swal({
                            title: '操作成功!',
                            type: 'success',
                            confirmButtonText:'确 认'
                        },function () {
                            if (data.data == 0 ){
                                window.location.href= contextPath + "role/list";
                            }else{
                            }
                        });
                    }else{
                        swal({
                            title: '操作失败!',
                            type: 'error',
                            confirmButtonText:'确 认'
                        },function () {
                            if (data.data != 0 ){
                                window.location.href= contextPath + "role/add/" + data.data ;
                            }else{
                                window.location.href= contextPath + "role/add";
                            }
                        });
                    }
                },
                //回调失败
                error: function () {
                    swal({
                        title: '数据出错!',
                        type: 'error',
                        confirmButtonText:'确 认'
                    },function () {
                        if (data.data != 0 ){
                            window.location.href= contextPath + "role/add/" + data.data ;
                        }else{
                            window.location.href= contextPath + "role/add";
                        }
                    });
                }
            });
        },
        getMenuTree: function() {
            var menu_setting = {
                data: {
                    simpleData: {
                        enable: true,
                        idKey: "id",
                        pIdKey: "parentId",
                        rootPId: -1
                    },
                    key: {
                        url:"nourl"
                    }
                },
                check:{
                    enable:true,
                    nocheckInherit:true
                }
                /*,
                callback:{    //第一步
                    onClick: core.getDepthTreeNode()
                }*/
            };
            //加载菜单树
            $.ajax({
                url: contextPath + 'menu/list',
                type: 'POST',
                dataType: "json",
                success: (data) => {
                    console.log(data);
                    ready.menu_ztree =$.fn.zTree.init($("#menuTree"), menu_setting, data);//初始化树节点时，添加同步获取的数据
                },
                error: (data) => {
                    console.log(data.message);
                }
            });
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;

}(window);