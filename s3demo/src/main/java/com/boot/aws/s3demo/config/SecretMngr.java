package com.boot.aws.s3demo.config;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.DecryptionFailureException;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;

@Component
public class SecretMngr {

	@Value("${aws.access_key_id}")
	private String accessKey;
	@Value("${aws.secret_access_key}")
	private String secretKey;

	public String getSecret() {

		String secretName = "local/aws/s3/cred";
		String region = "us-east-2";

		// Create a Secrets Manager client
		BasicAWSCredentials awsCred = new BasicAWSCredentials(accessKey, secretKey);

		AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard().withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(awsCred)).build();
		String secret = null, decodedBinarySecret = null;
		GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretName);
		GetSecretValueResult getSecretValueResult = null;

		try {
			getSecretValueResult = client.getSecretValue(getSecretValueRequest);
		} catch (DecryptionFailureException e) {
			throw e;
		}

		// Decrypts secret using the associated KMS CMK.
		// Depending on whether the secret is a string or binary, one of these fields
		// will be populated.
		if (getSecretValueResult.getSecretString() != null) {
			secret = getSecretValueResult.getSecretString();
		} else {
			decodedBinarySecret = new String(
					Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
		}

		System.out.println("decodedBinarySecret:: " + decodedBinarySecret);
		return secret;
	}

}
