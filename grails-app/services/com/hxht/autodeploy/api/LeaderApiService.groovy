package com.hxht.autodeploy.api

import com.hxht.techcrt.User
import com.hxht.techcrt.utils.JwtUtil
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import org.grails.web.util.WebUtils

@Transactional
class LeaderApiService {
    GrailsApplication grailsApplication
    def getUserByUsernameAndPassword = { String username, String password ->
        User.findByUsernameAndPasswordAndEnabledAndaccountLocked(username, password, true, false)
    }
    def currentUser = {
        def webUtils = WebUtils.retrieveGrailsWebRequest()
        def request = webUtils.getCurrentRequest()
        def auth = request.getHeader("Authorization")
        if (auth != null) {
            if (auth.startsWith("Bearer ")) {
                auth = auth.replace("Bearer ", "")
            }
            def secret = grailsApplication.config.getProperty('jwt.info.secret')
            def claims = JwtUtil.parse(auth, secret)
            return User.get(claims.get("id") as long)
        }
        return null
    }
}
