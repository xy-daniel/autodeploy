package com.hxht.autodeploy.court

class PlanTrial implements Serializable {
    private static final long serialVersionUID = 1
    PlanInfo planInfo
    TrialInfo trialInfo
    static mapping = {
        version false
    }
}
