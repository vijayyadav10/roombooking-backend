package com.vizz.roombooking.services;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Service
public class JWTService {

	private RSAPrivateKey privateKey;
	private RSAPublicKey publicKey;
	private long expirationTime = 1800000;

	@PostConstruct
	private void initKeys() throws NoSuchAlgorithmException {
		/*
		 * I'm going to generate the keys using an object of type KeyPairGenerator. So,
		 * I'll just import that - again it comes from Java security. I'll call this
		 * generator, and let's instantiate it. And the way we do that is by using a
		 * static method of the KeyPairGenerator class. Which is called getInstance. And
		 * in here, we provide the string which is the name of the algorithm we want to
		 * use. And the algorithm is simply the letters, RSA in quotes. That will give
		 * us the RSA-256 algorithm.
		 */
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

		/*
		 * So that will be generator.initialize and in here we're going to provide the
		 * size of the key and we'll want to put in here, 2048. That means we're going
		 * to generate a private public key which has a size of 2048,
		 */
		generator.initialize(2048);

		KeyPair keyPair = generator.generateKeyPair();

		/*
		 * what we should've done is set up the keys ready to use in our token. And
		 * because these keys will be generated once every time the service starts
		 */

		privateKey = (RSAPrivateKey) keyPair.getPrivate();
		publicKey = (RSAPublicKey) keyPair.getPublic();

	}

	public String generateToken(String name, String role) {
		return JWT.create()
				// payload == withClaim(key, value)
				.withClaim("user", name).withClaim("role", role)
				.withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
				/*
				 * finally we want to call the method, sign. And that's what's going to actually
				 * generate our token and as you can see, we need to specify what's the
				 * algorithm that we're going to use to do the signing.
				 */
				.sign(Algorithm.RSA256(publicKey, privateKey));
	}

	public String validateToken(String token) throws JWTVerificationException {
		/*
		 * JWT.require = require is the way that we're going to validate a token, and
		 * what we'll need to do is put into here an algorithm. Well, it's the same
		 * algorithm as we used in the sign method. the method called build. That's
		 * generating something called a JWT verifier. Once we've got that we can call
		 * verify, and that's where we'll pass in our token, so we're saying please
		 * verify this token is valid, using that algorithm. In other words, all this is
		 * doing, is saying does this signature match? Is the signature, using that
		 * algorithm with this private or public key, and does it then contain the data
		 * that's in the payload? If it does, as you can see that gives us an object of
		 * type decoded JWT.
		 */

		/*
		 * what would happen if this particular verify failed, if verifier said well
		 * actually no this isn't a valid token. Well if that happens then actually this
		 * method is going to throw an exception, and it's an exception of type
		 * JWTVerificationException,
		 */
		String encodedPayload = JWT.require(Algorithm.RSA256(publicKey, privateKey)).build().verify(token).getPayload();

		return new String(Base64.getDecoder().decode(encodedPayload));
	}
}

//IMP NOTES

/*
 * KeyPairGenerator, and actually that could throw an exception, which is a no
 * such algorithm exception. So, I think we'll add the throws to the method
 * signature. Rather than trying to catch that because of course, we shouldn't
 * expect to see that kind of exception because we know we should always have
 * this RSA algorithm available to us
 */
