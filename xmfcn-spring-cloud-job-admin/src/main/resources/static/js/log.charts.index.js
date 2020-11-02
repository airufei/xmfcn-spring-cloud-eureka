/**
 * Created by xuxueli on 17/4/24.
 */
$(function () {

    var  format="YYYY-MM-DD HH:mm:ss";
    InitDateControl(format,freshChartDate);//初始化时间控件
    var startTime=rangesConf[I18n.daterangepicker_ranges_recent_month][0];
    var endTime=rangesConf[I18n.daterangepicker_ranges_recent_month][1];
    freshChartDate(startTime,endTime);//初始化报表数据

    /**
     * fresh Chart Date
     *
     * @param startDate
     * @param endDate
     */
    function freshChartDate(startDate, endDate) {
        $.ajax({
            type: 'POST',
            url: base_url + '/logcharts/getLogLevelCharts',
            data: {
                'startTime': startDate.format('YYYY-MM-DD HH:mm:ss'),
                'endTime': endDate.format('YYYY-MM-DD HH:mm:ss')
            },
            dataType: "json",
            success: function (data) {
                if (data.code == 200) {
                    lineChartInit(data)
                    pieChartInit(data);
                } else {
                    layer.open({
                        title: I18n.system_tips,
                        btn: [I18n.system_ok],
                        content: (data.msg || I18n.job_dashboard_report_loaddata_fail),
                        icon: '2'
                    });
                }
            }
        });
    }

    /**
     * line Chart Init
     */
    function lineChartInit(data) {
        var option = {
            title: {
                text: "日志级别比例图"
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross',
                    label: {
                        backgroundColor: '#6a7985'
                    }
                }
            },
            legend: {
                data: ["info", "error", "warn"]
            },
            toolbox: {
                feature: {
                    /*saveAsImage: {}*/
                }
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: [
                {
                    type: 'category',
                    boundaryGap: false,
                    data: data.content.dayList
                }
            ],
            yAxis: [
                {
                    type: 'value'
                }
            ],
            series: [
                {
                    name: "infoCount",
                    type: 'line',
                    stack: 'Total',
                    areaStyle: {normal: {}},
                    data: data.content.dayCountInfoList
                },
                {
                    name: "errorCount",
                    type: 'line',
                    stack: 'Total',
                    label: {
                        normal: {
                            show: true,
                            position: 'top'
                        }
                    },
                    areaStyle: {normal: {}},
                    data: data.content.dayCountErrorList
                },
                {
                    name: "warnCount",
                    type: 'line',
                    stack: 'Total',
                    areaStyle: {normal: {}},
                    data: data.content.dayCountWarnList
                },
                {
                    name: "debugCount",
                    type: 'line',
                    stack: 'Total',
                    areaStyle: {normal: {}},
                    data: data.content.dayCountDebugList
                }
            ],
            color: ['#00A65A', '#c23632', '#F39C12']
        };

        var lineChart = echarts.init(document.getElementById('lineChart'));
        lineChart.setOption(option);
    }

    /**
     * pie Chart Init
     */
    function pieChartInit(data) {
        var option = {
            title: {
                text: I18n.job_dashboard_rate_report,
                /*subtext: 'subtext',*/
                x: 'center'
            },
            tooltip: {
                trigger: 'item',
                formatter: "{b} : {c} ({d}%)"
            },
            legend: {
                orient: 'vertical',
                left: 'left',
                data: ["info", "error", "warn","debug"]
            },
            series: [
                {
                    //name: '分布比例',
                    type: 'pie',
                    radius: '55%',
                    center: ['50%', '60%'],
                    data: [
                        {
                            name: "info",
                            value: data.content.infoCount
                        },
                        {
                            name: "error",
                            value: data.content.errorCount
                        },
                        {
                            name: "warn",
                            value: data.content.warnCount
                        },
                        {
                            name: "debug",
                            value: data.content.debugCount
                        }
                    ],
                    itemStyle: {
                        emphasis: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    }
                }
            ],
            color: ['#00A65A', '#c23632', '#F39C12']
        };
        var pieChart = echarts.init(document.getElementById('pieChart'));
        pieChart.setOption(option);
    }

});
