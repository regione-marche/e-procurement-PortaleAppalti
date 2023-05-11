package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTokenUtilities {

	/**
	 * 
	 * @param subject
	 * @param expirationInMillis
	 * @param jwtSecretKey
	 * @param data
	 * @return the generated token
	 */
	public static String generateToken(String subject, long expirationInMillis, String jwtSecretKey, Map<String, String> data) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("data", data);
		return Jwts.builder().setClaims(claims ).setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(expirationInMillis))
				.signWith(SignatureAlgorithm.HS512, jwtSecretKey.getBytes()).compact();
	}
	
	/**
	 * Questo metodo permette di decifrare un {@link Jwt} token
	 * @param jwt la stringa che rappresenta un token jwt
	 * @param signingKey la chiave in plain string con cui è stato firmato il token
	 * @return il JSON del body del token jwt
	 * @throws ExpiredJwtException
	 * @throws MalformedJwtException
	 * @throws SignatureException
	 * @throws IllegalArgumentException
	 */
	public static Jwt<?, ?> parseJwt(String jwt,String signingKey) throws ExpiredJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
		return Jwts.parser().setSigningKey(signingKey.getBytes()).parseClaimsJws(jwt);
	}
	
	/**
	 * Questo metodo restituisce il body di un token
	 * @param token
	 * @return il body {@link Jwt#getBody()}
	 */
	public static Object getBodyFromJwt(Jwt<?,?> token) {
		return token.getBody();
	}
	

}
