package com.hxht.autodeploy.court

import com.hxht.techcrt.CollegialType


/**
 * 合议庭 是由三名以上审判员或者审判员和人民陪审员集体审判案件的组织形式。
 * 它是人民法院进行审判活动的基本主体，根据三大诉讼法以及法院组织法的规定，
 * 法院对第一审的民事、经济纠纷，刑事案件，除一部分简单的适用简易程序外，
 * 其余的全部实行合议制，行政案件，无论繁简，均由合议庭进行审判。
 */
class Collegial {
    /**
     * 成员名称
     */
    String name
    /**
     * 成员类型
     * 1 审判长
     * 2 审判员
     * 4 人民陪审员
     * 255 其他
     */
    Integer type

    String synchronizationId

    String getInfo(){
        return this.name + "(${CollegialType.getString(this.type)})"
    }

    static constraints = {
        synchronizationId nullable: true, maxSize: 120
    }

    static mapping = {
        name comment: "成员名称"
        type comment: "是否是审判长：" +
                " 1 审判长" +
                " 2 审判员" +
                " 4 人民陪审员" +
                " 255 其他"
        synchronizationId comment: "数据同步主键"
        comment "合议庭成员表。"
    }
}
