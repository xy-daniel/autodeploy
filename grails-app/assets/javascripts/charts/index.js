/**
 * Created by Arctic on 2019.11.25.
 */
;-function (window) {

    //内部核心属性
    var core = {
        cutTag: function () {
            var yearSelect = $("#year");
            //首先显示法官统计
            showJudge(yearSelect.val());
            $(".judgeAnalyze").click(function () {
                $(".activeAnalyze").attr("class", "");
                $(this).parent().attr("class", "active activeAnalyze");
                // $("#currentYear").selected();
                var currentTag = $("#tag").text().toString();
                if (currentTag.indexOf("法官统计") !== -1) {
                    return;
                }
                showJudge(yearSelect.val());
            });
            $(".courtAnalyze").click(function () {
                $(".activeAnalyze").attr("class", "");
                $(this).parent().attr("class", "active activeAnalyze");
                // $("#currentYear").selected();
                var currentTag = $("#tag").text();
                if (currentTag.indexOf("法庭统计") !== -1) {
                    return;
                }
                showCourt(yearSelect.val());
            });
            $(".caseAnalyze").click(function () {
                $(".activeAnalyze").attr("class", "");
                $(this).parent().attr("class", "active activeAnalyze");
                // $("#currentYear").selected();
                var currentTag = $("#tag").text();
                if (currentTag.indexOf("案件统计") !== -1) {
                    return;
                }
                showCase(yearSelect.val());
            });
            $(".deptAnalyze").click(function () {
                $(".activeAnalyze").attr("class", "");
                $(this).parent().attr("class", "active activeAnalyze");
                // $("#currentYear").selected();
                var currentTag = $("#tag").text();
                if (currentTag.indexOf("部门统计") !== -1) {
                    return;
                }
                showDept(yearSelect.val());
            });
            $(".speechAnalyze").click(function () {
                $(".activeAnalyze").attr("class", "");
                $(this).parent().attr("class", "active activeAnalyze");
                // $("#currentYear").selected();
                var currentTag = $("#tag").text();
                if (currentTag.indexOf("语音识别周统计") !== -1) {
                    return;
                }
                showSpeech();
            });

            yearSelect.change(function () {
                //获取此按钮的数值
                var year = yearSelect.val();
                //获取tag标签值
                var currentTag = $("#tag").text().toString();
                if (currentTag.indexOf("法官统计") !== -1) {
                    showJudge(year);
                }
                if (currentTag.indexOf("法庭统计") !== -1) {
                    showCourt(year);
                }
                if (currentTag.indexOf("案件统计") !== -1) {
                    showCase(year);
                }
                if (currentTag.indexOf("部门统计") !== -1) {
                    showDept(year);
                }
            })
        }
    };
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
        core.cutTag();
    }

    //对外公开的方法
    var page = {};
    init();
    window.p = page;

    function handleMainDiv() {
        $("#main").remove();
        $(".mainDiv").append("<div id=\"main\" style=\"width: 100%;\"></div>");
    }
    function handleMainDivSpeech() {
        $("#main").remove();
        $("#tag").remove();
        $(".mainDiv").append("<div id=\"main\" style=\"width: 100%;\"> " +
            "<div class=\"row\">\n" +
            "    <div class=\"col-md-4\">\n" +
            "        <div class=\"col-xs-6\">\n" +
            "            <input type=\"text\" class=\"form-control\"  id=\"startDate\" placeholder=\"开始时间\"/>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "    <div class=\"col-md-4\">\n" +
            "        <div class=\"col-xs-6\">\n" +
            "            <input type=\"button\" class=\"btn btn-inverse\" value=\"查询\" id=\"query\" name=\"query\"/>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</div>" +
            "<table id=\"table\"></table> " +
            "</div>");
    }

    function showJudge(year) {
        $.get(
            contextPath + "charts/getChartsInfoByJudge",
            {
                year: year
            },
            function (xhr) {
                $("#tag").text(year+"年法官统计");
                handleMainDiv();
                var days = ['1月', '2月', '3月', '4月', '5月'
                    , '6月', '7月', '8月', '9月', '10月'
                    , '11月', '12月'];
                var judgesPo = xhr.data.split(",");
                var judges = [];
                var data = [];
                for (var i = 0; i < judgesPo.length; i++) {
                    var info = judgesPo[i];
                    var dataArr = info.split("、");
                    judges.push(dataArr[0]);
                    for (var j = 1; j < dataArr.length; j++) {
                        var arr = [];
                        var trialNum = dataArr[j];
                        if (trialNum !== "0") {
                            arr.push(i);
                            arr.push(j - 1);
                            arr.push(trialNum);
                            data.push(arr);
                        }
                    }
                }
                var height = judges.length * 30;
                $('#main').css('height', height + 150);
                data = data.map(function (item) {
                    return [item[1], item[0], item[2] || '-'];
                });
                option = {
                    tooltip: {
                        position: 'top'
                    },
                    toolbox: {
                        show: true,
                        feature: {
                            magicType: {show: true},
                            saveAsImage: {
                                show: true,
                                title: "图表下载",
                                type: "png",
                                name: "各法官每月庭审图表",
                                pixelRatio: 2
                            }
                        }
                    },
                    animation: false,
                    grid: {
                        height: height,
                        y: '15px'
                    },
                    xAxis: {
                        type: 'category',
                        data: days,
                        splitArea: {
                            show: true
                        }
                    },
                    yAxis: {
                        type: 'category',
                        data: judges,
                        splitArea: {
                            show: true
                        }
                    },
                    visualMap: {
                        min: 0,
                        max: 10,
                        calculable: true,
                        orient: 'horizontal',
                        left: 'center',
                        bottom: '0'
                    },
                    series: [{
                        name: '庭审数',
                        type: 'heatmap',
                        data: data,
                        label: {
                            normal: {
                                show: true
                            }
                        },
                        itemStyle: {
                            emphasis: {
                                shadowBlur: 10,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                    }]
                };
                var myChart = echarts.init(document.getElementById('main'));
                myChart.setOption(option);
            }, 'json'
        )
    }

    function showCourt(year) {
        $.get(
            contextPath + "charts/getChartsInfoByCourt",
            {
                year: year
            },
            function (xhr) {
                $("#tag").text(year+"年法庭统计");
                handleMainDiv();
                var courtsPo = xhr.data.split(",");
                var courts = [];
                var data1 = [];
                var data2 = [];
                var data3 = [];
                var data4 = [];
                var data5 = [];
                var data6 = [];
                var data7 = [];
                var data8 = [];
                var data9 = [];
                var data10 = [];
                var data11 = [];
                var data12 = [];
                for (var i = 0; i < courtsPo.length; i++) {
                    //一个部门按照、分割
                    var dataArr = courtsPo[i].split("、");
                    //第一个数据是部门名称
                    courts.push(dataArr[0]);
                    //第2个到第13个
                    data1.push(dataArr[1] === '0' ? '' : dataArr[1]);
                    data2.push(dataArr[2] === '0' ? '' : dataArr[2]);
                    data3.push(dataArr[3] === '0' ? '' : dataArr[3]);
                    data4.push(dataArr[4] === '0' ? '' : dataArr[4]);
                    data5.push(dataArr[5] === '0' ? '' : dataArr[5]);
                    data6.push(dataArr[6] === '0' ? '' : dataArr[6]);
                    data7.push(dataArr[7] === '0' ? '' : dataArr[7]);
                    data8.push(dataArr[8] === '0' ? '' : dataArr[8]);
                    data9.push(dataArr[9] === '0' ? '' : dataArr[9]);
                    data10.push(dataArr[10] === '0' ? '' : dataArr[10]);
                    data11.push(dataArr[11] === '0' ? '' : dataArr[11]);
                    data12.push(dataArr[12] === '0' ? '' : dataArr[12]);
                }
                var height = courts.length * 30;
                $('#main').css('height', height + 150);
                option = {
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                            type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                        }
                    },
                    toolbox: {
                        show: true,
                        feature: {
                            magicType: {show: true},
                            saveAsImage: {
                                show: true,
                                title: "图表下载",
                                type: "png",
                                name: "各法庭庭审图表",
                                pixelRatio: 2
                            }
                        }
                    },
                    //标签
                    legend: {
                        data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
                    },
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    },
                    //x轴
                    xAxis: {
                        type: 'value'
                    },
                    //y轴
                    yAxis: {
                        type: 'category',
                        data: courts
                    },
                    series: [
                        {
                            name: '1月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data1
                        },
                        {
                            name: '2月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data2
                        },
                        {
                            name: '3月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data3
                        },
                        {
                            name: '4月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data4
                        },
                        {
                            name: '5月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data5
                        },
                        {
                            name: '6月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data6
                        },
                        {
                            name: '7月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data7
                        },
                        {
                            name: '8月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data8
                        },
                        {
                            name: '9月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data9
                        },
                        {
                            name: '10月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data10
                        },
                        {
                            name: '11月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data11
                        },
                        {
                            name: '12月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data12
                        }
                    ]
                };
                var myChart = echarts.init(document.getElementById('main'));
                myChart.setOption(option);
            }, 'json'
        )
    }

    function showCase(year) {
        $.get(
            contextPath + "charts/getChartsInfoByCase",
            {
                year: year
            },
            function (xhr) {
                $("#tag").text(year+"年案件统计");
                handleMainDiv();
                var courtsPo = xhr.data.split("、");
                var data = [];
                var date = new Date();
                var height = 0;
                for (var i = 0; i < date.getMonth() + 1; i++) {
                    if (courtsPo[i] > height) {
                        height = courtsPo[i]
                    }
                    data.push(courtsPo[i]);
                }
                $('#main').css('height', height * 15 + 200);
                // data = [15,12,45,20];
                option = {
                    title: {
                        // text: '当前年案件统计'
                    },
                    tooltip: {
                        trigger: 'axis'
                    },
                    legend: {
                        data: ['案件数']
                    },
                    toolbox: {
                        show: true,
                        feature: {
                            magicType: {show: true, type: ['bar', 'line']},
                            saveAsImage: {
                                show: true,
                                title: "图表下载",
                                type: "png",
                                name: "各案件庭审统计图",
                                pixelRatio: 2
                            }
                        }
                    },
                    calculable: true,
                    xAxis: [
                        {
                            type: 'category',
                            data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
                        }
                    ],
                    yAxis: [
                        {
                            type: 'value'
                        }
                    ],
                    series: [
                        {
                            name: '案件数',
                            type: 'bar',
                            data: data,
                            markPoint: {
                                data: [
                                    {type: 'max', name: '最大值'},
                                    {type: 'min', name: '最小值'}
                                ]
                            },
                            markLine: {
                                data: [
                                    {type: 'average', name: '平均值'}
                                ]
                            }
                        }
                    ]

                };
                var myChart = echarts.init(document.getElementById('main'));
                myChart.setOption(option);
            }, 'json'
        )
    }

    function showDept(year) {
        $.get(
            contextPath + "charts/getChartsInfoByDept",
            {
                year: year
            },
            function (xhr) {
                $("#tag").text(year+"年部门统计");
                handleMainDiv();
                var deptsPo = xhr.data.split(",");
                var depts = [];
                var data1 = [];
                var data2 = [];
                var data3 = [];
                var data4 = [];
                var data5 = [];
                var data6 = [];
                var data7 = [];
                var data8 = [];
                var data9 = [];
                var data10 = [];
                var data11 = [];
                var data12 = [];
                for (var i = 0; i < deptsPo.length; i++) {
                    //一个部门按照、分割
                    var dataArr = deptsPo[i].split("、");
                    //第一个数据是部门名称
                    depts.push(dataArr[0]);
                    //第2个到第13个
                    data1.push(dataArr[1] === '0' ? '' : dataArr[1]);
                    data2.push(dataArr[2] === '0' ? '' : dataArr[2]);
                    data3.push(dataArr[3] === '0' ? '' : dataArr[3]);
                    data4.push(dataArr[4] === '0' ? '' : dataArr[4]);
                    data5.push(dataArr[5] === '0' ? '' : dataArr[5]);
                    data6.push(dataArr[6] === '0' ? '' : dataArr[6]);
                    data7.push(dataArr[7] === '0' ? '' : dataArr[7]);
                    data8.push(dataArr[8] === '0' ? '' : dataArr[8]);
                    data9.push(dataArr[9] === '0' ? '' : dataArr[9]);
                    data10.push(dataArr[10] === '0' ? '' : dataArr[10]);
                    data11.push(dataArr[11] === '0' ? '' : dataArr[11]);
                    data12.push(dataArr[12] === '0' ? '' : dataArr[12]);
                }
                var height = depts.length * 30;
                $('#main').css('height', height + 150);
                option = {
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                            type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                        }
                    },
                    toolbox: {
                        show: true,
                        feature: {
                            magicType: {show: true},
                            saveAsImage: {
                                show: true,
                                title: "图表下载",
                                type: "png",
                                name: "各部门庭审图表",
                                pixelRatio: 2
                            }
                        }
                    },
                    //标签
                    legend: {
                        data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
                    },
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    },
                    //x轴
                    xAxis: {
                        type: 'value'
                    },
                    //y轴
                    yAxis: {
                        type: 'category',
                        data: depts
                    },
                    series: [
                        {
                            name: '1月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data1
                        },
                        {
                            name: '2月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data2
                        },
                        {
                            name: '3月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data3
                        },
                        {
                            name: '4月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data4
                        },
                        {
                            name: '5月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data5
                        },
                        {
                            name: '6月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data6
                        },
                        {
                            name: '7月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data7
                        },
                        {
                            name: '8月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data8
                        },
                        {
                            name: '9月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data9
                        },
                        {
                            name: '10月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data10
                        },
                        {
                            name: '11月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data11
                        },
                        {
                            name: '12月',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: data12
                        }
                    ]
                };

                var myChart = echarts.init(document.getElementById('main'));
                myChart.setOption(option);
            }, 'json'
        )
    }

    function datetime() {
        $('#startDate').datetimepicker({
            format: 'YYYY/MM/DD',
            collapse: false,
            widgetPositioning: {
                horizontal: 'auto',
                vertical: 'bottom'
            }
        });
    }

    var date1 = null;
    var date2 = null;
    var date3 = null;
    var date4 = null;
    var date5 = null;
    function datetimeSpeech() {
        $.ajax({
            url: contextPath + "charts/getStatisSpeechDate",
            type: 'POST',
            dataType: "json",
            async: false,
            data: {
                startDate: startDate
            },
            success: (data) => {
                date1 = data.trialDate1;
                date2 = data.trialDate2;
                date3 = data.trialDate3;
                date4 = data.trialDate4;
                date5 = data.trialDate5;
            },
            error: (data) => {
                console.log(data.message);
            }
        });

    }
    var startDate = null
    function queryCondition(){
        $("#query").bind("click", function () { //点击查询按钮 触发table重新请求服务器
            startDate = $("#startDate").val();
            $("#table").bootstrapTable('destroy');
            datetimeSpeech();
            bootstrapTale();
        });
    }
    //计算所有行的 百分号之前的数字相加
    function totalFormatterPerCopy(data) {
        var field = this.field
        var numCourt = null
        var all =  data.map(function (row) {
            if (row[field].indexOf("%") !== -1){
                numCourt = numCourt + 1
            }
            return +row[field].substring(0,row[field].indexOf("%"))
        }).reduce(function (sum, i) {
            return sum + i
        }, 0)
        var total = data.map(function (total) {
            return total
        })
        return (all / numCourt).toFixed(2) + "%"
    }
    //所有行相加
    function totalFormatter(data) {
        var field = this.field
        return  data.map(function (row) {
            return +row[field]
        }).reduce(function (sum, i) {
            return sum + i
        }, 0)
    }
    //语音识别统计分析
    function showSpeech() {
        handleMainDivSpeech();
        queryCondition();
        datetime();
        bootstrapTale();
    }
    function bootstrapTale(){
        $("#table").bootstrapTable({
            dataType: "json",
            method: 'get',
            contentType: "application/x-www-form-urlencoded",
            cache: false,
            pagination: false, // 是否分页
            striped: true,   //是否显示行间隔色
            url: contextPath + "charts/getStatisSpeech",
            queryParams: function (params){
                var filter={};
                filter.startDate=startDate;
                return filter;
            },
            // height: 550,
            showExport: true,  //是否显示导出按钮
            exportDataType: "all",  //basic', 'all', 'selected'.
            buttonsAlign:"right",  //按钮位置
            exportTypes:['csv',"excel"],  //导出文件类型
            Icons:'glyphicon-export',
            showColumns: true,                  //是否显示所有的列
            showRefresh: false,                  //是否显示刷新按钮
            minimumCountColumns: 1,             //最少允许的列数
            clickToSelect: true,                //是否启用点击选中行
            showToggle:false,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表
            showFooter: true,
            exportOptions:{
                //ignoreColumn: [0,1],  //忽略某一列的索引
                fileName: '语音识别系统开庭使用率统计表',  //文件名称设置
                worksheetName: 'sheet1',  //表格工作区名称
                tableName: '语音识别系统开庭使用率统计表',
                excelstyles: ['background-color', 'color', 'font-size', 'font-weight']
                // onMsoNumberFormat: DoOnMsoNumberFormat
            },
            columns:[
                [
                    {
                        "title": "语音识别系统开庭使用率统计表",
                        "halign":"center",
                        "align":"center",
                        "colspan": 19
                    }
                ],
                [
                    {
                        field: 'courtRoomName',
                        title: "法庭",
                        valign:"middle",
                        align:"center",
                        colspan: 1,
                        rowspan: 2,
                        footerFormatter:'周统计'
                    },
                    {
                        title: date1,
                        valign:"middle",
                        align:"center",
                        colspan: 3,
                        rowspan: 1
                    },
                    {
                        title: date2,
                        valign:"middle",
                        align:"center",
                        colspan: 3,
                        rowspan: 1
                    },
                    {
                        title: date3,
                        valign:"middle",
                        align:"center",
                        colspan: 3,
                        rowspan: 1
                    },
                    {
                        title: date4,
                        valign:"middle",
                        align:"center",
                        colspan: 3,
                        rowspan: 1
                    },
                    {
                        title: date5,
                        valign:"middle",
                        align:"center",
                        colspan: 3,
                        rowspan: 1
                    },
                    {
                        title: "周统计",
                        valign:"middle",
                        align:"center",
                        colspan: 3,
                        rowspan: 1
                    }
                ],
                [
                    {
                        field: 'trialNum1',
                        title: '开庭数',
                        valign:"middle",
                        footerFormatter: totalFormatter,
                        align:"center"
                    },
                    {
                        field: 'speechNum1',
                        title: '语音识别使用数',
                        valign:"middle",
                        footerFormatter: totalFormatter,
                        align:"center"
                    },
                    {
                        field: 'speechPercent1',
                        title: '使用比例',
                        valign:"middle",
                        footerFormatter: function totalFormatterPer(data) {
                            var f1 = data.map(function (row) {
                                return +row['speechNum1']
                            }).reduce(function (sum, i) {
                                return sum + i
                            }, 0)
                            var f2 = data.map(function (row) {
                                return +row['trialNum1']
                            }).reduce(function (sum, i) {
                                return sum + i
                            }, 0)
                            return (f1*100 / f2).toFixed(2) + "%"
                        },
                        align:"center"
                    },
                    {
                        field: 'trialNum2',
                        title: '开庭数',
                        valign:"middle",
                        footerFormatter: totalFormatter,
                        align:"center"
                    },
                    {
                        field: 'speechNum2',
                        title: '语音识别使用数',
                        valign:"middle",
                        footerFormatter: totalFormatter,
                        align:"center"
                    },
                    {
                        field: 'speechPercent2',
                        title: '使用比例',
                        valign:"middle",
                        footerFormatter: function totalFormatterPer(data) {
                            var f1 = data.map(function (row) {
                                return +row['speechNum2']
                            }).reduce(function (sum, i) {
                                return sum + i
                            }, 0)
                            var f2 = data.map(function (row) {
                                return +row['trialNum2']
                            }).reduce(function (sum, i) {
                                return sum + i
                            }, 0)
                            return (f1*100 / f2).toFixed(2) + "%"
                        },
                        align:"center"
                    },
                    {
                        field: 'trialNum3',
                        title: '开庭数',
                        valign:"middle",
                        footerFormatter: totalFormatter,
                        align:"center"
                    },
                    {
                        field: 'speechNum3',
                        title: '语音识别使用数',
                        valign:"middle",
                        footerFormatter: totalFormatter,
                        align:"center"
                    },
                    {
                        field: 'speechPercent3',
                        title: '使用比例',
                        valign:"middle",
                        footerFormatter: function totalFormatterPer(data) {
                            var f1 = data.map(function (row) {
                                return +row['speechNum3']
                            }).reduce(function (sum, i) {
                                return sum + i
                            }, 0)
                            var f2 = data.map(function (row) {
                                return +row['trialNum3']
                            }).reduce(function (sum, i) {
                                return sum + i
                            }, 0)
                            return (f1*100 / f2).toFixed(2) + "%"
                        },
                        align:"center",

                    },
                    {
                        field: 'trialNum4',
                        title: '开庭数',
                        valign:"middle",
                        footerFormatter: totalFormatter,
                        align:"center"
                    },
                    {
                        field: 'speechNum4',
                        title: '语音识别使用数',
                        valign:"middle",
                        footerFormatter: totalFormatter,
                        align:"center"
                    },
                    {
                        field: 'speechPercent4',
                        title: '使用比例',
                        valign:"middle",
                        footerFormatter: function totalFormatterPer(data) {
                            var f1 = data.map(function (row) {
                                return +row['speechNum4']
                            }).reduce(function (sum, i) {
                                return sum + i
                            }, 0)
                            var f2 = data.map(function (row) {
                                return +row['trialNum4']
                            }).reduce(function (sum, i) {
                                return sum + i
                            }, 0)
                            return (f1*100 / f2).toFixed(2) + "%"
                        },
                        align:"center"
                    },
                    {
                        field: 'trialNum5',
                        title: '开庭数',
                        valign:"middle",
                        footerFormatter: totalFormatter,
                        align:"center"
                    },
                    {
                        field: 'speechNum5',
                        title: '语音识别使用数',
                        valign:"middle",
                        footerFormatter: totalFormatter,
                        align:"center"
                    },
                    {
                        field: 'speechPercent5',
                        title: '使用比例',
                        valign:"middle",
                        footerFormatter: function totalFormatterPer(data) {
                            var f1 = data.map(function (row) {
                                return +row['speechNum5']
                            }).reduce(function (sum, i) {
                                return sum + i
                            }, 0)
                            var f2 = data.map(function (row) {
                                return +row['trialNum5']
                            }).reduce(function (sum, i) {
                                return sum + i
                            }, 0)
                            return (f1*100 / f2).toFixed(2) + "%"
                        },
                        align:"center"
                    },
                    {
                        field: 'trialNumAll',
                        title: '开庭数',
                        valign:"middle",
                        footerFormatter: totalFormatter,
                        align:"center"
                    },
                    {
                        field: 'speechNumAll',
                        title: '语音识别使用数',
                        valign:"middle",
                        footerFormatter: totalFormatter,
                        align:"center"
                    },
                    {
                        field: 'speechPercentAll',
                        title: '使用比例',
                        valign:"middle",
                        footerFormatter: function totalFormatterPer(data) {
                            var f1 = data.map(function (row) {
                                return +row['speechNumAll']
                            }).reduce(function (sum, i) {
                                return sum + i
                            }, 0)
                            var f2 = data.map(function (row) {
                                return +row['trialNumAll']
                            }).reduce(function (sum, i) {
                                return sum + i
                            }, 0)
                            return (f1*100 / f2).toFixed(2) + "%"
                        },
                        align:"center"
                    }
                ]
            ]
        })
        $(".caret").remove()
    }


}(window);