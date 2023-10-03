package com.ngontro86.dboe.web3j.encryption

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm

class KeyHashUtils {

    static String sign(String privateKey, String signedKey, Date expiry) {
        return Jwts.builder().setIssuer("DBOE")
                .setSubject("Private Key Signing")
                .setExpiration(expiry)
                .addClaims(
                        [
                                'commonPhrase': 'DBOE',
                                'privateKey'  : privateKey
                        ])
                .signWith(SignatureAlgorithm.HS256, signedKey)
                .compact()
    }

    static String unhashedKey(String hashedKey, String signedKey) {
        Jws<Claims> claims = Jwts.parser().requireIssuer("DBOE")
                .setSigningKey(signedKey)
                .parseClaimsJws(hashedKey)
        if (claims.getBody().get('commonPhrase') != 'DBOE') {
            throw new IllegalArgumentException('Corrupted signed string')
        }
        return claims.getBody().get('privateKey')
    }

}
