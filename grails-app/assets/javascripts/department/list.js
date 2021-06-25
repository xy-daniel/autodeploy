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
        core.render_table();
        core.delCheckedBtn();
        core.addDepartment();
    }

    //内部核心属性
    var core = {

        //添加处室
        addDepartment: function(){
            $("#addDepartment").click(function () {
                var hrefStr = window.location.href;
                var hrefNum = window.location.href.lastIndexOf("/");
                var id = hrefStr.substring(hrefNum+1);
                if (id==="list"){
                    id = 0;
                }
                window.location.href = contextPath + "department/add?id="+id;
            })
        },
        /**
         * 删除所选择的部门
         */
        delCheckedBtn:function(){
            $("#checkedBtn").click(function () {
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
                    var checkedBox = document.getElementsByName("checkbox-select");
                    var departmentIds = "";
                    for (var i=0; i<checkedBox.length; i++){
                        if (checkedBox[i].checked){
                            if (departmentIds==""){
                                departmentIds = checkedBox[i].value;
                            }else{
                                departmentIds = departmentIds + "," + checkedBox[i].value;
                            }
                        }
                    }
                    $.get(
                        contextPath + 'department/del',
                        {
                            departmentIds:departmentIds
                        },
                        function (result) {
                            if (result.code==0){
                                swal({
                                    title: '删除成功!',
                                    type: 'success',
                                    confirmButtonText:'确 认'
                                },function () {
                                    window.location.href = contextPath + 'department/list';
                                });
                            }else if (result.code===410){
                                swal({
                                    title: '请选择数据',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                })
                            }else if (result.code==1){
                                swal({
                                    title: '不允许您删除含有员工或子部门的部门...',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                },function () {
                                    window.location.href = contextPath + 'department/list';
                                });
                            }
                        },'json'
                    )
                })
            })
        },
        handleSelect: function () {
            $('#table_checkbox_all').click(function (e) {
                var checkbox = $("input[type='checkbox']");
                if ($(this).is(':checked')) {
                    checkbox.prop("checked", true);
                } else {
                    checkbox.prop("checked", false);
                }
            });
            //每一次的小checkbox点击事件
            $(".ck").click(function () {
                //获取所有的小的checkbox
                var checkboxs = $(".ck");
                for (var i=0; i<checkboxs.length; i++){
                    if (!checkboxs[i].checked){
                        $('#table_checkbox_all').prop("checked", false);
                        return;
                    }
                }
                $('#table_checkbox_all').prop("checked", true);
            })
        },
        render_table: function () {
            var href = window.location.href;
            var index = href.lastIndexOf("/");
            var id = href.substring(index+1);
            if (id == "list"){
                id = 0
            }
            $('#data-table').DataTable({
                ajax: {
                    url:contextPath + 'department/list?id='+id,
                    type:"POST"
                },
                language: {
                    'sProcessing': '处理中...',
                    'sLengthMenu': '显示 _MENU_ 项结果',
                    'sZeroRecords': '没有匹配结果',
                    'sInfo': '显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项',
                    'sInfoEmpty': '显示第 0 至 0 项结果，共 0 项',
                    'sInfoFiltered': '(由 _MAX_ 项结果过滤)',
                    'sInfoPostFix': '',
                    'sSearch': '搜索：',
                    'sUrl': '',
                    'sEmptyTable': '表中数据为空',
                    'sLoadingRecords': '载入中...',
                    'sInfoThousands': ',',
                    'oPaginate': {
                        'sFirst': '首页',
                        'sPrevious': '上页',
                        'sNext': '下页',
                        'sLast': '末页'
                    },
                    'oAria': {
                        'sSortAscending': ': 以升序排列此列',
                        'sSortDescending': ': 以降序排列此列'
                    }
                },
                serverSide: true,
                ordering: false,
                bProcessing: true,
                aoColumns: [//初始化要显示的列
                    //id
                    {
                        "mDataProp": "id",
                        "sClass": "table-select",
                        "mRender": function (data, type, full) {
                            return '<td class="with-checkbox">' +
                                '<div class="checkbox checkbox-css">' +
                                '<input class="ck" type="checkbox" name="checkbox-select" value="' + data + '" id="table_checkbox_' + data + '" data-user="' + data + '" />' +
                                '<label for="table_checkbox_' + data + '">&nbsp;</label>' +
                                '</div>'
                        }
                    },
                    //机构名称
                    {
                        "mDataProp": "name",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    //所属部门
                    {
                        "mDataProp": "fromDept",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    //对该部门的操作
                    {
                        "mDataProp": "id",//获取列数据，跟服务器返回字段一致
                        "sClass": "center",//显示样式
                        "mRender": function (data, type, full) {//返回自定义的样式
                            var html =  '<div class="table-text">';
                            html +=  '<a href="'+contextPath+'department/list/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">下属部门</a>';
                            html +=  '<a href="'+contextPath+'department/edit/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>';
                            html +=  '</div>';
                            return html
                        }
                    }
                ],
                fnDrawCallback: function () {
                    core.handleSelect();
                }
            });
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
