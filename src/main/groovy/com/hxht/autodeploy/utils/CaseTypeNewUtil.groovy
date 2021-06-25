package com.hxht.autodeploy.utils

class CaseTypeNewUtil {
    public static final List<String> types = [
            "0100,管辖案件",
            "0101,刑事管辖案件",
            "0102,刑事提级管辖案件,刑辖",
            "0103,刑事指定管辖案件,刑辖",
            "0104,民事管辖案件",
            "0105,民事提级管辖案件,民辖",
            "0106,民事指定管辖案件,民辖",
            "0107,民事移交管辖审批案件,民辖",
            "0108,民事管辖协商案件,民辖",
            "0109,民事管辖上诉案件,民辖终",
            "0110,民事管辖监督案件,民辖监",
            "0111,行政管辖案件",
            "0112,行政提级管辖案件,行辖",
            "0113,行政指定管辖案件,行辖",
            "0114,行政管辖上诉案件,行辖终",
            "0115,行政赔偿管辖案件",
            "0116,行政赔偿提级管辖案件,赔辖",
            "0117,行政赔偿指定管辖案件,赔辖",
            "0118,行政赔偿管辖协商案件,赔辖",
            "0119,行政赔偿管辖上诉案件,赔辖终",
            "0200,刑事案件",
            "0201,刑事一审案件,刑初",
            "0202,刑事二审案件,刑终",
            "0203,刑事审判监督案件",
            "0204,刑事依职权再审审查案件,刑监",
            "0205,刑事申诉再审审查案件,刑申",
            "0206,刑事抗诉再审审查案件,刑抗",
            "0207,刑事再审案件,刑再",
            "0208,申请没收违法所得案件,刑没",
            "0209,刑事复核案件",
            "0210,死刑复核案件,刑核",
            "0211,法定刑以下判处刑罚复核案件,刑核",
            "0212,特殊假释复核案件,刑核",
            "0213,强制医疗案件",
            "0214,申请强制医疗审查案件,刑医",
            "0215,解除强制医疗审查案件,刑医解",
            "0216,强制医疗复议案件,刑医复",
            "0217,强制医疗监督案件,刑医监",
            "0218,停止执行死刑案件",
            "0219,停止执行死刑请求审查案件,刑止",
            "0220,依职权停止执行死刑案件,刑止",
            "0221,停止执行死刑调查案件,刑止调",
            "0222,停止执行死刑调查审核案件,刑止核",
            "0223,刑罚与执行变更案件",
            "0224,刑罚与执行变更审查案件,刑更",
            "0225,刑罚与执行变更监督案件,刑更监",
            "0226,刑罚与执行变更备案案件,刑更备",
            "0227,其他刑事案件,刑他",
            "0228,安置教育案件",
            "0229,申请安置教育审查案件,刑教",
            "0230,解除安置教育审查案件,刑教解",
            "0231,安置教育复议案件,刑教复",
            "0232,安置教育监督案件,刑教监",
            "0300,民事案件",
            "0301,民事一审案件,民初",
            "0302,民事二审案件,民终",
            "0303,民事审判监督案件",
            "0304,民事依职权再审审查案件,民监",
            "0305,民事申请再审审查案件,民申",
            "0306,民事抗诉再审审查案件,民抗",
            "0307,民事再审案件,民再",
            "0308,第三人撤销之诉案件,民撤",
            "0309,特别程序案件",
            "0310,选民资格案件,民特",
            "0311,宣告失踪、宣告死亡案件,民特",
            "0312,财产代管人申请变更代管案件,民特",
            "0313,行为能力认定案件,民特",
            "0314,监护人指定异议案件,民特",
            "0315,监护关系变更案件,民特",
            "0316,认定财产无主案件,民特",
            "0317,实现担保物权案件,民特",
            "0318,调解协议司法确认案件,民特",
            "0319,设立海事赔偿责任限制基金案件,民特",
            "0320,海事债权登记与受偿案件,民特",
            "0321,撤销仲裁裁决案件,民特",
            "0322,申请确认仲裁协议效力案件,民特",
            "0323,民事特别程序监督案件,民特监",
            "0324,催告案件",
            "0325,船舶优先权催告案件,民催",
            "0326,公示催告案件,民催",
            "0327,督促案件",
            "0328,申请支付令审查案件,民督",
            "0329,支付令监督案件,民督监",
            "0330,其他民事案件,民他",
            "0331,人身安全保护令案件",
            "0332,人身安全保护令申请审查案件,民保令",
            "0333,人身安全保护令变更案件,民保更",
            "0400,行政案件",
            "0401,行政一审案件,行初",
            "0402,行政二审案件,行终",
            "0403,行政审判监督案件",
            "0404,行政依职权再审审查案件,行监",
            "0405,行政申请再审审查案件,行申",
            "0406,行政抗诉再审审查案件,行抗",
            "0407,行政再审案件,行再",
            "0408,行政非诉审查案件",
            "0409,非诉行政行为申请执行审查案件,行审",
            "0410,非诉行政行为申请执行审查复议案件,行审复",
            "0411,其他行政案件,行他",
            "0500,国家赔偿与司法救助案件",
            "0501,行政赔偿案件",
            "0502,行政赔偿一审案件,行赔初",
            "0503,行政赔偿二审案件,行赔终",
            "0504,行政赔偿依职权再审审查案件,行赔监",
            "0505,行政赔偿申请再审审查案件,行赔申",
            "0506,行政赔偿抗诉再审审查案件,行赔抗",
            "0507,行政赔偿再审案件,行赔再",
            "0508,其他行政赔偿案件,行赔他",
            "0509,司法赔偿案件",
            "0510,法院作为赔偿义务机关自赔案件,法赔",
            "0511,赔偿委员会审理赔偿案件,委赔",
            "0512,司法赔偿监督审查案件,委赔监",
            "0513,赔偿确认申诉审查案件,赔确监",
            "0514,司法赔偿监督上级法院赔偿委员会重审案件,委赔提",
            "0515,司法赔偿监督本院赔偿委员会重审案件,委赔再",
            "0516,其他赔偿案件,赔他",
            "0517,司法救助案件",
            "0518,刑事司法救助案件,司救刑",
            "0519,民事司法救助案件,司救民",
            "0520,行政司法救助案件,司救行",
            "0521,国家赔偿司法救助案件,司救赔",
            "0522,执行司法救助案件,司救执",
            "0523,涉诉信访司法救助案件,司救访",
            "0524,其他司法救助案件,司救他",
            "0600,区际司法协助案件",
            "0601,认可与执行申请审查案件",
            "0602,认可与执行台湾地区法院裁判审查案件,认台",
            "0603,认可与执行台湾地区仲裁裁决审查案件,认台",
            "0604,认可与执行香港特别行政区法院裁判审查案件,认港",
            "0605,认可与执行香港特别行政区仲裁裁决审查案件,认港",
            "0606,认可与执行澳门特别行政区法院裁判审查案件,认澳",
            "0607,认可与执行澳门特别行政区仲裁裁决审查案件,认澳",
            "0608,认可与执行审查复议案件,认复",
            "0609,认可与执行审查其他案件,认他",
            "0610,送达文书案件",
            "0611,请求台湾地区送达文书审查案件,请台送",
            "0612,请求香港特别行政区法院送达文书审查案件,请港送",
            "0613,请求澳门特别行政区法院送达文书审查案件,请澳送",
            "0614,台湾地区请求送达文书审查案件,台请送",
            "0615,协助台湾地区送达文书案件,台请送",
            "0616,香港特别行政区法院请求送达文书审查案件,港请送",
            "0617,协助香港特别行政区法院送达文书案件,港请送",
            "0618,澳门特别行政区法院请求送达文书审查案件,澳请送",
            "0619,协助澳门特别行政区法院送达文书案件,澳请送",
            "0620,调查取证案件",
            "0621,请求台湾地区调查取证审查案件,请台调",
            "0622,请求香港特别行政区调查取证审查案件,请港调",
            "0623,请求澳门特别行政区法院调查取证审查案件,请澳调",
            "0624,台湾地区请求调查取证审查案件,台请调",
            "0625,协助台湾地区调查取证案件,台请调",
            "0626,香港特别行政区请求调查取证审查案件,港请调",
            "0627,协助香港特别行政区调查取证案件,港请调",
            "0628,澳门特别行政区法院请求调查取证审查案件,澳请调",
            "0629,协助澳门特别行政区法院调查取证案件,澳请调",
            "0630,被判刑人移管案件",
            "0631,接收在台湾地区被判刑人案件,请移管",
            "0632,向台湾地区移管被判刑人案件,助移管",
            "0633,罪赃移交案件",
            "0634,接收台湾地区移交罪赃案件,请移赃",
            "0635,向台湾地区移交罪赃案件,助移赃",
            "0700,国际司法协助案件",
            "0701,承认与执行申请审查案件",
            "0702,承认与执行外国法院裁判审查案件,协外认",
            "0703,承认与执行国外仲裁裁决审查案件,协外认",
            "0704,承认与执行审查其他案件,协他",
            "0705,送达文书案件",
            "0706,外国法院请求送达文书审查案件,协外送",
            "0707,送达外国法院文书案件,协外送",
            "0708,请求外国法院送达文书审查案件,请外送",
            "0709,调查取证案件",
            "0710,外国法院请求调查取证审查案件,协外调",
            "0711,外国法院请求调查取证实施案件,协外调",
            "0712,请求外国法院调查取证审查案件,请外调",
            "0713,被判刑人移管案件",
            "0714,接收在外国被判刑人案件,请外移",
            "0715,向外国移管被判刑人案件,协外移",
            "0716,引渡案件",
            "0717,请求外国引渡案件,请外引",
            "0718,协助外国引渡案件,协外引",
            "0800,司法制裁案件",
            "0801,司法制裁审查案件",
            "0802,司法拘留案件,司惩",
            "0803,司法罚款案件,司惩",
            "0804,司法制裁复议案件,司惩复",
            "0900,非诉保全审查案件",
            "0901,非诉财产保全审查案件,财保",
            "0902,非诉行为保全审查案件,行保",
            "0903,非诉行为保全复议案件,行保复",
            "0904,非诉证据保全审查案件,证保",
            "1000,执行类案件",
            "1001,执行实施类案件",
            "1002,首次执行案件,执",
            "1003,恢复执行案件,执恢",
            "1004,财产保全执行案件,执保",
            "1005,执行审查类案件",
            "1006,执行异议案件,执异",
            "1007,执行复议案件,执复",
            "1008,执行监督案件,执监",
            "1009,执行协调案件,执协",
            "1010,其他执行案件,执他",
            "1100,强制清算与破产案件",
            "1101,强制清算与破产清算申请审查案件",
            "1102,强制清算申请审查案件,清申",
            "1103,破产申请审查案件,破申",
            "1104,强制清算与破产上诉案件",
            "1105,强制清算上诉案件,清终",
            "1106,破产上诉案件,破终",
            "1107,强制清算与破产监督案件",
            "1108,强制清算监督案件,清监",
            "1109,破产监督案件,破监",
            "1110,强制清算案件,强清",
            "1111,破产案件",
            "1112,破产清算案件,破",
            "1113,破产重整案件,破",
            "1114,破产和解案件,破"
    ]

    public static String getCodeByArchives(String archives) {
        if (archives.indexOf("刑辖") > -1) {
            return "0101"
        }

        if (archives.indexOf("民辖") > -1) {
            return "0104"
        }

        if (archives.indexOf("行辖") > -1) {
            return "0111"
        }

        if (archives.indexOf("赔辖") > -1) {
            return "0115"
        }

        if (archives.indexOf("刑初") > -1) {
            return "0201"
        }

        if (archives.indexOf("刑终") > -1) {
            return "0202"
        }

        if (archives.indexOf("刑监") > -1) {
            return "0204"
        }

        if (archives.indexOf("刑申") > -1) {
            return "0205"
        }

        if (archives.indexOf("刑抗") > -1) {
            return "0206"
        }

        if (archives.indexOf("刑再") > -1) {
            return "0207"
        }

        if (archives.indexOf("刑没") > -1) {
            return "0208"
        }

        if (archives.indexOf("刑核") > -1) {
            return "0209"
        }

        if (archives.indexOf("刑医解") > -1) {
            return "0215"
        }
        if (archives.indexOf("刑医复") > -1) {
            return "0216"
        }
        if (archives.indexOf("刑医监") > -1) {
            return "0217"
        }
        if (archives.indexOf("刑医") > -1) {
            return "0214"
        }

        if (archives.indexOf("刑止调") > -1) {
            return "0221"
        }
        if (archives.indexOf("刑止核") > -1) {
            return "0222"
        }
        if (archives.indexOf("刑止") > -1) {
            return "0218"
        }

        if (archives.indexOf("刑更备") > -1) {
            return "0226"
        }
        if (archives.indexOf("刑更监") > -1) {
            return "0225"
        }
        if (archives.indexOf("刑更") > -1) {
            return "0224"
        }

        if (archives.indexOf("刑他") > -1) {
            return "0227"
        }

        if (archives.indexOf("刑教监") > -1) {
            return "0232"
        }
        if (archives.indexOf("刑教复") > -1) {
            return "0231"
        }
        if (archives.indexOf("刑教解") > -1) {
            return "0230"
        }
        if (archives.indexOf("刑教") > -1) {
            return "0229"
        }


        if (archives.indexOf("民初") > -1) {
            return "0301"
        }

        if (archives.indexOf("民终") > -1) {
            return "0302"
        }

        if (archives.indexOf("民监") > -1) {
            return "0304"
        }

        if (archives.indexOf("民申") > -1) {
            return "0305"
        }

        if (archives.indexOf("民抗") > -1) {
            return "0306"
        }

        if (archives.indexOf("民再") > -1) {
            return "0307"
        }

        if (archives.indexOf("民撤") > -1) {
            return "0308"
        }

        if (archives.indexOf("民特监") > -1) {
            return "0323"
        }
        if (archives.indexOf("民特") > -1) {
            return "0309"
        }

        if (archives.indexOf("民催") > -1) {
            return "0324"
        }

        if (archives.indexOf("民督监") > -1) {
            return "0329"
        }
        if (archives.indexOf("民督") > -1) {
            return "0328"
        }

        if (archives.indexOf("民他") > -1) {
            return "0330"
        }

        if (archives.indexOf("民保令") > -1) {
            return "0332"
        }

        if (archives.indexOf("民保更") > -1) {
            return "0333"
        }

        if (archives.indexOf("行初") > -1) {
            return "0401"
        }

        if (archives.indexOf("行终") > -1) {
            return "0402"
        }

        if (archives.indexOf("行监") > -1) {
            return "0404"
        }

        if (archives.indexOf("行申") > -1) {
            return "0405"
        }

        if (archives.indexOf("行抗") > -1) {
            return "0406"
        }

        if (archives.indexOf("行再") > -1) {
            return "0407"
        }

        if (archives.indexOf("行审") > -1) {
            return "0409"
        }

        if (archives.indexOf("行审复") > -1) {
            return "0410"
        }

        if (archives.indexOf("行他") > -1) {
            return "0411"
        }

        if (archives.indexOf("行赔初") > -1) {
            return "0502"
        }

        if (archives.indexOf("行赔终") > -1) {
            return "0503"
        }

        if (archives.indexOf("行赔监") > -1) {
            return "0504"
        }

        if (archives.indexOf("行赔申") > -1) {
            return "0505"
        }

        if (archives.indexOf("行赔抗") > -1) {
            return "0506"
        }

        if (archives.indexOf("行赔再") > -1) {
            return "0507"
        }

        if (archives.indexOf("行赔他") > -1) {
            return "0508"
        }

        if (archives.indexOf("法赔") > -1) {
            return "0510"
        }

        if (archives.indexOf("委赔再") > -1) {
            return "0515"
        }
        if (archives.indexOf("委赔提") > -1) {
            return "0514"
        }
        if (archives.indexOf("赔确监") > -1) {
            return "0513"
        }
        if (archives.indexOf("委赔监") > -1) {
            return "0512"
        }
        if (archives.indexOf("委赔") > -1) {
            return "0511"
        }

        if (archives.indexOf("赔他") > -1) {
            return "0516"
        }

        if (archives.indexOf("司救刑") > -1) {
            return "0518"
        }

        if (archives.indexOf("司救民") > -1) {
            return "0519"
        }

        if (archives.indexOf("司救行") > -1) {
            return "0520"
        }

        if (archives.indexOf("司救赔") > -1) {
            return "0521"
        }

        if (archives.indexOf("司救执") > -1) {
            return "0522"
        }

        if (archives.indexOf("司救访") > -1) {
            return "0523"
        }

        if (archives.indexOf("司救他") > -1) {
            return "0524"
        }

        if (archives.indexOf("认台") > -1) {
            return "0601"
        }

        if (archives.indexOf("认港") > -1) {
            return "0601"
        }

        if (archives.indexOf("认澳") > -1) {
            return "0601"
        }

        if (archives.indexOf("认复") > -1) {
            return "0608"
        }

        if (archives.indexOf("认他") > -1) {
            return "0609"
        }

        if (archives.indexOf("请台送") > -1) {
            return "0611"
        }

        if (archives.indexOf("请港送") > -1) {
            return "0612"
        }

        if (archives.indexOf("请澳送") > -1) {
            return "0613"
        }

        if (archives.indexOf("台请送") > -1) {
            return "0610"
        }

        if (archives.indexOf("港请送") > -1) {
            return "0610"
        }

        if (archives.indexOf("澳请送") > -1) {
            return "0610"
        }

        if (archives.indexOf("请台调") > -1) {
            return "0621"
        }

        if (archives.indexOf("请港调") > -1) {
            return "0622"
        }

        if (archives.indexOf("请澳调") > -1) {
            return "0623"
        }

        if (archives.indexOf("台请调") > -1) {
            return "0620"
        }

        if (archives.indexOf("港请调") > -1) {
            return "0620"
        }

        if (archives.indexOf("澳请调") > -1) {
            return "0620"
        }

        if (archives.indexOf("请移管") > -1) {
            return "0631"
        }

        if (archives.indexOf("助移管") > -1) {
            return "0632"
        }

        if (archives.indexOf("请移赃") > -1) {
            return "0634"
        }

        if (archives.indexOf("助移赃") > -1) {
            return "0635"
        }

        if (archives.indexOf("协他") > -1) {
            return "0704"
        }
        if (archives.indexOf("协外认") > -1) {
            return "0701"
        }

        if (archives.indexOf("请外送") > -1) {
            return "0708"
        }
        if (archives.indexOf("协外送") > -1) {
            return "0705"
        }

        if (archives.indexOf("请外调") > -1) {
            return "0712"
        }
        if (archives.indexOf("协外调") > -1) {
            return "0709"
        }

        if (archives.indexOf("请外移") > -1) {
            return "0714"
        }

        if (archives.indexOf("协外移") > -1) {
            return "0715"
        }

        if (archives.indexOf("请外引") > -1) {
            return "0717"
        }

        if (archives.indexOf("协外引") > -1) {
            return "0718"
        }

        if (archives.indexOf("司惩复") > -1) {
            return "0804"
        }
        if (archives.indexOf("司惩") > -1) {
            return "0801"
        }

        if (archives.indexOf("财保") > -1) {
            return "0901"
        }

        if (archives.indexOf("行保复") > -1) {
            return "0903"
        }
        if (archives.indexOf("行保") > -1) {
            return "0902"
        }

        if (archives.indexOf("证保") > -1) {
            return "0904"
        }

        if (archives.indexOf("执保") > -1) {
            return "1004"
        }
        if (archives.indexOf("执恢") > -1) {
            return "1003"
        }
        if (archives.indexOf("执异") > -1) {
            return "1006"
        }
        if (archives.indexOf("执复") > -1) {
            return "1007"
        }
        if (archives.indexOf("执监") > -1) {
            return "1008"
        }
        if (archives.indexOf("执协") > -1) {
            return "1009"
        }
        if (archives.indexOf("执他") > -1) {
            return "1010"
        }
        if (archives.indexOf("执") > -1) {
            return "1002"
        }

        if (archives.indexOf("清申") > -1) {
            return "1102"
        }

        if (archives.indexOf("破申") > -1) {
            return "1103"
        }

        if (archives.indexOf("清终") > -1) {
            return "1105"
        }

        if (archives.indexOf("破终") > -1) {
            return "1106"
        }

        if (archives.indexOf("清监") > -1) {
            return "1108"
        }

        if (archives.indexOf("破监") > -1) {
            return "1109"
        }

        if (archives.indexOf("强清") > -1) {
            return "1110"
        }

        if (archives.indexOf("破") > -1) {
            return "1111"
        }
        return "0100"
    }
}
