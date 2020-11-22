package com.example.examplequerydslspringdatajpamaven.Validator;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

/**
 * Create JWK token for logged user
 * @author fuinco
 *
 */
@Component
public class JWKValidator {
	

	@Value("${spring.security.jwtsecretkey}")
	private String base64SecretBytes;
 
	@Value("${spring.security.jwtTokenId}")
	private String id;

	@Value("${spring.security.jwtTokenIssuer}")
	private String issuer;

	@Value("${spring.security.jwtTokenSubject}")
	private String subject;
	
	/**
	 * This method is used to create the secret key that is placed in properties
	 * file ${spring.security.jwtsecretkey} Currently not used again after initial
	 * creation
	 */
	@SuppressWarnings("unused")
	private void createSecretKey() {

		Key secret = MacProvider.generateKey(SignatureAlgorithm.HS256);
		byte[] secretBytes = secret.getEncoded();

		base64SecretBytes = Base64.getEncoder().encodeToString(secretBytes);
	}
	
	public String createJWT(String string, Long ttlMillis) {

		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		// We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64SecretBytes);

		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		// Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder().setId(id).setIssuedAt(now).setSubject(subject).setIssuer(issuer)
				.signWith(signatureAlgorithm, signingKey);

		// if it has been specified, let's add the expiration
		if (ttlMillis != null) {
			long expMillis = nowMillis + ttlMillis;
			Date exp = new Date(expMillis);
			builder.setExpiration(exp);
		}

		builder.claim("userName", string);

		// Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}
	//Sample method to validate and read the JWT
	public Claims validateJWT(String jwt) {

		try {
			// This line will throw an exception if it is not
			// a signed JWS (as expected)
			Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(base64SecretBytes))
					.parseClaimsJws(jwt).getBody();


			return claims;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
