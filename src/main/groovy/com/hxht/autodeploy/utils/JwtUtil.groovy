package com.hxht.autodeploy.utils

import com.hxht.techcrt.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm

import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter

class JwtUtil {
    static Claims parse(String jsonWebToken, String security) {
        try {
            return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(security)).parseClaimsJws(jsonWebToken).getBody()
        } catch (Exception ignored) {
            return null
        }
    }

    static String create(User user, String security, long TTLMillis) {
        def signatureAlgorithm = SignatureAlgorithm.HS256
        def nowMillis = System.currentTimeMillis()
        def now = new Date(nowMillis)
        //生成签名密钥
        def apiKeySecretBytes = DatatypeConverter.parseBase64Binary(security)
        def signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName())
        //添加构成JWT的参数
        def builder = Jwts.builder().setHeaderParam("typ", "JWT")
                .claim("id", user.getId())
                .claim("username", user.getUsername())
                .claim("realName", user.getRealName())
                .signWith(signatureAlgorithm, signingKey)
        //添加Token过期时间
        if (TTLMillis >= 0) {
            def expMillis = nowMillis + TTLMillis
            def exp = new Date(expMillis)
            builder.setExpiration(exp).setNotBefore(now)
        }
        //生成JWT
        return builder.compact()
    }

    /**
     * 根据认证码生成对应的token
     * @param security    JWT校验字符串
     * @param TTLMillis   超时时间
     * @param rzm         认证码
     * @return
     */
    static String token(String security, long TTLMillis, String authenticationCode) {
        def signatureAlgorithm = SignatureAlgorithm.HS256
        def nowMillis = System.currentTimeMillis()
        def now = new Date(nowMillis)
        //生成签名密钥
        def apiKeySecretBytes = DatatypeConverter.parseBase64Binary(security)
        def signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName())
        //添加构成JWT的参数
        def builder = Jwts.builder().setHeaderParam("typ", "JWT")
                .claim("authenticationCode", authenticationCode)
                .signWith(signatureAlgorithm, signingKey)
        //添加Token过期时间
        if (TTLMillis >= 0) {
            def expMillis = nowMillis + TTLMillis
            def exp = new Date(expMillis)
            builder.setExpiration(exp).setNotBefore(now)
        }
        //生成JWT
        return builder.compact()
    }
}
