package com.hxht.autodeploy

import com.hxht.autodeploy.utils.OSUtil

class OSAnalysisJob {

    static Map<String, String[]> oSAnalysisData = new HashMap<>()

    static triggers = {
        cron cronExpression: "0 0/1 * * * ? *" //每1分钟执行一次
    }

    def execute() {
        exec()
    }

    static exec() {
        def memory = oSAnalysisData.get("memory")
        if (!memory) {
            String[] memoryArr = new String[30]
            for (int i = 0; i < memoryArr.length; i++) {
                memoryArr[i] = "0"
            }
            memory = memoryArr

        } else {
            for (int i = 0; i < memory.length - 1; i++) {
                memory[i] = memory[i + 1]
            }
            memory[29] = OSUtil.memory()
        }
        oSAnalysisData.put("memory", memory)
        def space = oSAnalysisData.get("space")
        if (!space) {
            String[] spaceArr = new String[30]
            for (int i = 0; i < spaceArr.length; i++) {
                spaceArr[i] = "0"
            }
            space = spaceArr
        } else {
            for (int i = 0; i < space.length - 1; i++) {
                space[i] = space[i + 1]
            }
            space[29] = OSUtil.space()
        }
        oSAnalysisData.put("space", space)
    }
}
