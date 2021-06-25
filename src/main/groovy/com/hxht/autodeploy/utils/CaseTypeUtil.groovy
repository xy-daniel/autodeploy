package com.hxht.autodeploy.utils

class CaseTypeUtil {

    /**
     * 根据案号获取案件分类（行事、民事、行政、执行、信访、其他）
     * @param archives
     * @return
     */
    static String getCategoryByArchives(String archives) {
        //可能是刑事案件 但是需要进一步判断
        String[] criminalArr = ["刑初", "刑二初", "刑监", "刑终", "刑再", "刑令", "刑复", "刑结",
                                "刑医", "刑执", "刑他", "刑提", "刑申", "刑抗", "刑辖", "刑核",
                                "刑类推"]
        for (String criminal : criminalArr) {
            if (archives.indexOf(criminal) > -1) {
                //肯定为刑事案件
                return "a1"
            }
        }
        //可能是行政案件 但是需要进一步判断
        String[] administrativeArr = ["行初", "行终", "行赔", "行审", "行申", "行再", "行监", "行他",
                                      "行抗", "行提", "行辖"]
        for (String administrative : administrativeArr) {
            if (archives.indexOf(administrative) > -1) {
                //肯定为行政案件
                return "a5"
            }
        }
        //可能是执行案件 但是需要进一步判断
        String[] executiveArr = ["执异", "执复", "执审", "执行", "执字", "执3号", "执保", "执他", "执协", "执督",
                                 "执监", "执1456456号", "执123号", "执1号", "执552552号", "执123321号", "执317号"]
        for (String executive : executiveArr) {
            if (archives.indexOf(executive) > -1) {
                //肯定为执行案件
                return "a3"
            }
        }
        //可能是赔偿案件 但是需要进一步判断
        String[] indemnifyArr = ["赔初", "赔终", "委赔", "赔他", "法赔", "行赔", "法检赔", "法检赔"]
        for (String indemnify : indemnifyArr) {
            if (archives.indexOf(indemnify) > -1) {
                //肯定为赔偿案件
                return "a4"

            }
        }
        //可能是信访案件 但是需要进一步判断
        String[] petitionArr = ["信字", "霞信20160817号", "闽06信0908050606号", "闽06信0201703280号", "邵信1号"]
        for (String petition : petitionArr) {
            if (archives.indexOf(petition) > -1) {
                //肯定为信访案件
                return "a6"
            }
        }
        //可能是民事案件 但是需要进一步判断
        String[] civilArr = ["民初", "民终", "民再", "民特", "民申", "民催", "民认", "民监", "民提", "民撤",
                             "民辖", "民他", "民算", "民破", "民督", "民保", "民结", "民清", "民抗", "民类推11111号",
                             "商初"]
        for (String civil : civilArr) {
            if (archives.indexOf(civil) > -1) {
                //肯定为民事案件
                return "a2"
            }
        }
        return null
    }
}
