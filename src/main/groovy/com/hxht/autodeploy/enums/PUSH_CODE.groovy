package com.hxht.autodeploy.enums

enum PUSH_CODE {

    SUCCESS(0, "同步成功。"),

    NORMAL_FAILED(1, "提交失败，本次提交在任务中重试提交。"),

    COURTROOM_FAILED(4010, "缺少法庭信息,需要重新查找法庭信息补充提交。"),

    CASE_FAILED(4020, "缺少案件信息,需要重新查找案件信息补充提交。"),

    PLAN_FAILED(4030, "缺少排期信息,需要重新查找排期信息补充提交。"),

    TRIAL_FAILED(4040, "缺少开庭记录信息,需要重新查找开庭记录补充提交。"),

    VIDEO_FAILED(4050, "缺少视频信息,这条错误超出我的能力范围。"),

    JUDGE_FAILED(4060, "缺少法官信息,需要重新查找法官补充提交。"),

    SECRETARY_FAILED(4070, "缺少书记员信息,需要重新查找书记员补充提交。");

    private final int code

    private final String message

    PUSH_CODE(int code, String message) {
        this.code = code
        this.message = message
    }

    int CODE() {
        return code
    }

    String MESSAGE() {
        return message
    }
}
