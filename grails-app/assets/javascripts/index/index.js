;-function (window) {
    const ready = {};
    const core = {};
    'use strict';
    const init = function () {
        init_ready();
        init_event();
    };

    function init_ready() {
    }

    function init_event() {
        memory_space();
        setInterval(memory_space,60000)
        function memory_space() {
            $.get(
                contextPath + "index/memory_space",
                {},
                function (result) {
                    const memory = [];
                    for (let i = 0; i < 30; i++) {
                        memory[i] = [i + 1, result.data.memory[i]];
                    }
                    const space = [];
                    for (let i = 0; i < 30; i++) {
                        space[i] = [i + 1, result.data.space[i]];
                    }
                    const x = [
                        [1, ""], [2, ""], [3, ""], [4, ""], [5, "25"], [6, ""], [7, ""], [8, ""], [9, ""], [10, "20"],
                        [11, ""], [12, ""], [13, ""], [14, ""], [15, "15"], [16, ""], [17, ""], [18, ""], [19, ""], [20, "10"],
                        [21, ""], [22, ""], [23, ""], [24, ""], [25, "5"], [26, ""], [27, ""], [28, ""], [29, ""], [30, "现在"]
                    ];
                    const infoChart = $("#infoChart");
                    $.plot(infoChart, [
                        {
                            data: memory,
                            label: "运行内存",
                            color: COLOR_GREEN_DARKER,
                            lines: {show: !0, fill: !0, lineWidth: 2},
                            points: {show: !0, radius: 3, fillColor: COLOR_WHITE},
                            shadowSize: 3
                        },
                        {
                            data: space,
                            label: "磁盘",
                            color: COLOR_RED_DARKER,
                            lines: {show: !0, fill: !0, lineWidth: 2},
                            points: {show: !0, radius: 3, fillColor: COLOR_WHITE},
                            shadowSize: 3
                        }
                    ], {
                        xaxis: {ticks: x, tickDecimals: 0, tickColor: COLOR_BLACK_TRANSPARENT_2},
                        yaxis: {ticks: 10, tickColor: COLOR_BLACK_TRANSPARENT_2, min: 0, max: 100},
                        grid: {
                            hoverable: !0,
                            clickable: !0,
                            tickColor: COLOR_BLACK_TRANSPARENT_2,
                            borderWidth: 1,
                            backgroundColor: 'transparent',
                            borderColor: COLOR_BLACK_TRANSPARENT_2
                        },
                        legend: {labelBoxBorderColor: COLOR_BLACK_TRANSPARENT_2, margin: 10, noColumns: 1, show: !0}
                    });
                    let l = null;
                    infoChart.bind("plothover", function (e, i, t) {
                        if ($("#x").text(i.x.toFixed(2)), $("#y").text(i.y.toFixed(2)), t) {
                            if (l !== t.dataIndex) {
                                l = t.dataIndex;
                                $("#tooltip").remove();
                                const n = t.datapoint[1].toFixed(2), o = t.series.label + " " + n;
                                a(t.pageX, t.pageY, o)
                            }
                        } else {
                            $("#tooltip").remove();
                            l = null;
                        }
                        e.preventDefault()
                    })
                }, 'json'
            )
        }
    }

    const page = {};
    init();
    window.p = page;
}(window);
