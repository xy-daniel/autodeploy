/**
 * Created by sakuri on 2019/8/13.
 */
;-function (window) {
    'use strict';
    var ready = {
        // isNotHave: true
        archivesPo: ""
    };
    var option = {};
    var init = function () {
        init_ready();//加载预设变量
        init_event();//初始化页面事件
    };

    function init_ready() {//初始化预设值
    }

    function init_event() {//初始化页面事件
        core.wizard();
        core.planWizardBtnAdd();
        core.switcherCheckbox();
        core.selectpicker();
        core.primary();
        core.datetime();
        core.collegial();
        core.renderTable();
        core.validatePlanArchives();
    }

    //内部核心属性
    var core = {
        validatePlanArchives: function () {
            $("#archives").keyup(function () {
                window.Parsley.addAsyncValidator('checkarchives', function (xhr) {
                    var archives = $("#archives").val()
                    var archivesPo = ready.archivesPo
                    if (ready.archivesPo === "" || (archivesPo !== archives)) {
                        return JSON.parse(xhr.responseText).data === 0;
                    } else {
                        return true
                    }
                }, contextPath + 'plan/getPlanByArchives');
            })
            window.Parsley.addAsyncValidator('checkarchives', function (xhr) {
                var archives = $("#archives").val()
                var archivesPo = ready.archivesPo
                if (ready.archivesPo === "" || (archivesPo !== archives)) {
                    return JSON.parse(xhr.responseText).data === 0;
                } else {
                    return true
                }
            }, contextPath + 'plan/getPlanByArchives');
        },
        wizard: function(){
            var wizard = $('#wizard');
            // Toolbar extra buttons
            var btnFinish = $('<button type="submit"></button>').text('完成')
                .addClass('btn btn-primary plan-wizard-btn-finish')
                .on('click', function(){
                });
            wizard.smartWizard({
                lang:{
                    next: '下一步',
                    previous: '上一步'
                },
                selected: 0,
                theme: 'default',
                transitionEffect:'',
                transitionSpeed: 0,
                useURLhash: false,
                showStepURLhash: false,
                toolbarSettings: {
                    toolbarPosition: 'bottom',
                    toolbarExtraButtons: [btnFinish]
                }
            });
            wizard.on('leaveStep', function(e, anchorObject, stepNumber, stepDirection) {
                var res = $('form[name="form-wizard"]').parsley({
                    errorsContainer: function(pEle) {
                        return pEle.$element.siblings('.errorBlock');
                    }
                }).validate('step-' + (stepNumber + 1));
                if (stepNumber == 1) {
                    var tagIt_accuser = document.getElementById("tagIt_accuser");
                    var tagIt_accused = document.getElementById("tagIt_accused");
                    if ($("[name=accuser]").length == 0 && $("[name=accused]").length == 0) {
                        tagIt_accuser.style.borderColor = "#ff5b57";
                        tagIt_accused.style.borderColor = "#ff5b57";
                    }
                    //校验原告
                    if ($("[name=accuser]").length == 0) {
                        tagIt_accuser.style.borderColor = "#ff5b57";
                        return false;
                    }
                    //校验被告
                    if ($("[name=accused]").length == 0) {
                        tagIt_accused.style.borderColor = "#ff5b57";
                        return false;
                    }
                    var archives = $("#archives").val();
                    var archivesPo = ready.archivesPo;
                    if (archives != "") {
                        if (archivesPo === "" || (archives !== archivesPo)) {
                            var responseText = $.ajax({
                                url: contextPath + 'plan/getPlanByArchives?archives='+archives,
                                async: false
                            }).responseText;
                            if (JSON.parse(responseText).data !== 0) {
                                return false
                            }
                        }
                    }
                }
                return res;
            });
            // Step show event
            wizard.on('showStep', function(e, anchorObject, stepNumber, stepDirection, stepPosition) {
                if(stepPosition === 'first'){
                    $('.sw-btn-prev').hide();
                    $('.sw-btn-next').hide();
                    $('.plan-wizard-btn-finish').hide();
                }else if(stepPosition === 'final'){
                    $('.sw-btn-prev').hide();
                    $('.sw-btn-next').hide();
                    $('.plan-wizard-btn-finish').show();
                }else{
                    $('.sw-btn-prev').show();
                    $('.sw-btn-next').show();
                }
            });
            $('.sw-btn-prev').hide();
            $('.sw-btn-next').hide();
            $('.plan-wizard-btn-finish').hide();
        },
        planWizardBtnAdd:function(){
            $('.plan-wizard-btn-add').click(function(){
                $('#wizard').smartWizard('next');
            });
        },
        switcherCheckbox: function(){
            $('.distance-signature').hide();
            $('#switcher_checkbox').change(function(){
                $('.distance-signature').toggle();
            });
        },
        selectpicker: function () {
            $('.selectpicker').selectpicker('render');
        },
        primary: function () {
            $('#tagIt_collegial').tagit({
                fieldName: 'collegialName'
            });
            $('#tagIt-distance_signature').tagit({
                fieldName: 'distanceSignature'
            });
            $('#tagIt_accuser').tagit({
                fieldName: 'accuser'
            });
            $('#tagIt_accused').tagit({
                fieldName: 'accused'
            });
        },
        datetime: function () {
            $('#datetimepicker1').datetimepicker({
                format: 'YYYY/MM/DD HH:mm',
                collapse: false,
                widgetPositioning: {
                    horizontal: 'auto',
                    vertical: 'bottom'
                }
            });
            $('#datetimepicker2').datetimepicker({
                format: 'YYYY/MM/DD HH:mm',
                collapse: false,
                widgetPositioning: {
                    horizontal: 'auto',
                    vertical: 'bottom'
                }
            });
            $('#datetimepicker3').datetimepicker({
                format: 'YYYY/MM/DD HH:mm',
                collapse: false,
                widgetPositioning: {
                    horizontal: 'auto',
                    vertical: 'bottom'
                }
            });
        },
        collegial: function () {
            $('.modal_collegial-submit').click(function () {
                var select_collegial = $('#select_collegial');//成员select
                var select_collegial_type = $('#select_collegial_type');//成员类型select
                var select_collegial_selected = select_collegial.find('option:selected').text();//成员select text
                var select_collegial_selected_idx = select_collegial_selected.indexOf('(');//成员text （ 位置
                var collegial_name = select_collegial_selected.substring(0, select_collegial_selected_idx > 0 ? select_collegial_selected_idx : select_collegial_selected.length);//只取成员名字
                var select_collegial_type_text = select_collegial_type.find('option:selected').text();
                if (select_collegial_type_text === "选择成员类型"){
                    return;
                }
                $('#tagIt_collegial').tagit('createTag', collegial_name + '(' + select_collegial_type_text + ')');
                $(select_collegial).selectpicker('val', '');
                $(select_collegial_type).selectpicker('val', '');
            });
        },
        renderTable: function () {
            $('#data-table').DataTable({
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
                ajax: {
                    url: contextPath + 'case/list',
                    type: 'POST'
                },
                aoColumns: [//初始化要显示的列
                    {
                        'mDataProp': 'archives',
                        'mRender': function (data, type, full) {
                            return data
                        }
                    },
                    {
                        'mDataProp': 'name',
                        'mRender': function (data, type, full) {
                            return data
                        }
                    },
                    {
                        'mDataProp': 'accuser',
                        'mRender': function (data, type, full) {
                            return data
                        }
                    },
                    {
                        'mDataProp': 'accused',
                        'mRender': function (data, type, full) {
                            return data
                        }
                    },
                    {
                        'mDataProp': 'filingDate',
                        'mRender': function (data, type, full) {
                            return data
                        }
                    },
                    {
                        'mDataProp': 'id',//获取列数据，跟服务器返回字段一致
                        'sClass': 'center',//显示样式
                        'mRender': function (data, type, full) {//返回自定义的样式
                            var html =  '<div class="table-text">';
                            html += '<a href="#" class="btn btn-inverse btn-xs m-r-5 btn-select" data-id="' + data + '">选择</a>';
                            html +=  '</div>';
                            return html
                        }
                    }
                ],
                fnDrawCallback: function () {
                    $('.btn-select').click(function(){
                        $.ajax({
                            type: 'get',
                            url: contextPath + 'case/' + $(this).attr('data-id'),
                            dataType: 'json',
                            success: function (result) {
                                if (result.code === 0) {
                                    $('input[name="caseId"]').val(result.data.id);
                                    $('input[name="archives"]').val(result.data.archives);
                                    ready.archivesPo = result.data.archives;
                                    $('input[name="name"]').val(result.data.name);

                                    $('.plan-add-case-type').val(result.data.type == null?"":result.data.type.id);
                                    $('.plan-add-case-type').selectpicker('render');

                                    var accuser = result.data.accuser.toString().split(",")
                                    for (var i=accuser.length-1; i>=0; i--) {
                                        $("#tagIt_accuser").prepend(
                                            "<li class=\"tagit-choice ui-widget-content ui-state-default ui-corner-all tagit-choice-editable\">" +
                                            "   <span class=\"tagit-label\">"+ accuser[i] +"</span>" +
                                            "   <a class=\"tagit-close accuser\">" +
                                            "       <span class=\"text-icon\">×</span>" +
                                            "       <span class=\"ui-icon ui-icon-close\"></span>" +
                                            "   </a>" +
                                            "   <input type=\"hidden\" value=\""+ accuser[i] +"\" name=\"accuser\" class=\"tagit-hidden-field\">" +
                                            "</li>"
                                        )
                                    }
                                    $(".accuser").bind('click', function () {
                                        $(this).parent().remove();
                                    })

                                    var accused = result.data.accused.toString().split(",")
                                    for (var i=accused.length-1; i>=0; i--) {
                                        $("#tagIt_accused").prepend(
                                            "<li class=\"tagit-choice ui-widget-content ui-state-default ui-corner-all tagit-choice-editable\">" +
                                            "   <span class=\"tagit-label\">"+ accused[i] +"</span>" +
                                            "   <a class=\"tagit-close accused\">" +
                                            "       <span class=\"text-icon\">×</span>" +
                                            "       <span class=\"ui-icon ui-icon-close\"></span>" +
                                            "   </a>" +
                                            "   <input type=\"hidden\" value=\""+ accused[i] +"\" name=\"accused\" class=\"tagit-hidden-field\">" +
                                            "</li>"
                                        )
                                    }
                                    $(".accused").bind('click', function () {
                                        $(this).parent().remove();
                                    })

                                    $('input[name="prosecutionCounsel"]').val(result.data.prosecutionCounsel);//原告律师
                                    $('input[name="counselDefence"]').val(result.data.counselDefence);//被告律师
                                    $('input[name="filingDate"]').val(result.data.filingDate);//立案日期
                                    $('input[name="summary"]').val(result.data.summary);//案件概要
                                    $('#modal_case').modal('hide');
                                    $('#wizard').smartWizard('next');
                                }else{
                                }
                            }
                        });
                    });
                }
            });
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
