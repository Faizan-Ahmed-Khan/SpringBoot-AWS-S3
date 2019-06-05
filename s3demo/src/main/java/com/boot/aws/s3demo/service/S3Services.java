package com.boot.aws.s3demo.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.boot.aws.s3demo.config.AmazonClient;
import com.boot.aws.s3demo.utility.Utility;

@Service
public class S3Services {
	@Autowired
	private AmazonClient client;

	private Logger logger = LogManager.getLogger(S3Services.class);

	@Value("${s3.bucket}")
	private String bucketName;

	@Value("${aws.endpoint.url}")
	private String s3url;

	public void uploadFile(String key, String uploadFilePath) {
		try {
			client.s3Client().putObject(new PutObjectRequest(bucketName, key, new File(uploadFilePath)));
		} catch (AmazonServiceException awsEx) {
			logger.info("Caught an AmazonServiceException from Upload requests, rejected reasons:");
			logger.info("Error Message:    " + awsEx.getMessage());
			logger.info("HTTP Status Code: " + awsEx.getStatusCode());
			logger.info("AWS Error Code:   " + awsEx.getErrorCode());
			logger.info("Error Type:       " + awsEx.getErrorType());
			logger.info("Request ID:       " + awsEx.getRequestId());
		} catch (AmazonClientException ace) {
			logger.info("Caught an AmazonClientException: ");
			logger.info("Error Message: " + ace.getMessage());
		}

	}

	public void download(String keyName) {
		try {
			S3Object s3object = client.s3Client().getObject(new GetObjectRequest(bucketName, keyName));
			System.out.println("Content-Type: " + s3object.getObjectMetadata().getContentType());
			Utility.displayText(s3object.getObjectContent());
		} catch (AmazonServiceException awsEx) {
			logger.info("Caught an AmazonServiceException from GET requests, rejected reasons:");
			logger.info("Error Message:    " + awsEx.getMessage());
			logger.info("HTTP Status Code: " + awsEx.getStatusCode());
			logger.info("AWS Error Code:   " + awsEx.getErrorCode());
			logger.info("Error Type:       " + awsEx.getErrorType());
			logger.info("Request ID:       " + awsEx.getRequestId());
		} catch (AmazonClientException ace) {
			logger.error("Caught an AmazonClientException: ");
			logger.info("Error Message: " + ace.getMessage());
		} catch (IOException ioEx) {
			logger.error("Caught an IOException: ");
			logger.info("Error Message: " + ioEx.getMessage());
		}
	}

	public String uploadMultiPartImage(String key, MultipartFile multiPartfile) throws IOException {

		try {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(multiPartfile.getSize());
			return client.s3Client()
					.putObject(new PutObjectRequest(bucketName, key, multiPartfile.getInputStream(), metadata)
							.withCannedAcl(CannedAccessControlList.PublicRead))
					.getVersionId();
		} catch (AmazonServiceException awsEx) {
			logger.error("Caught an AmazonServiceException from PUT Image requests, rejected reasons:");
			logger.error("Error Message:    " + awsEx.getMessage());
			logger.error("HTTP Status Code: " + awsEx.getStatusCode());
			logger.error("AWS Error Code:   " + awsEx.getErrorCode());
			logger.error("Error Type:       " + awsEx.getErrorType());
			logger.error("Request ID:       " + awsEx.getRequestId());
			throw awsEx;
		} catch (AmazonClientException ace) {
			logger.error("Caught an AmazonClientException: ");
			logger.error("Error Message: " + ace.getMessage());
			throw ace;
		}
	}

	public String uploadImage(String key, File file) throws IOException {
		String url = "";
		try {
			client.s3Client().putObject(
					new PutObjectRequest(bucketName, key, file).withCannedAcl(CannedAccessControlList.PublicRead));
			url = s3url + "/" + bucketName + "/" + key;
		} catch (AmazonServiceException awsEx) {
			logger.info("Caught an AmazonServiceException from PUT Image requests, rejected reasons:");
			logger.info("Error Message:    " + awsEx.getMessage());
			logger.info("HTTP Status Code: " + awsEx.getStatusCode());
			logger.info("AWS Error Code:   " + awsEx.getErrorCode());
			logger.info("Error Type:       " + awsEx.getErrorType());
			logger.info("Request ID:       " + awsEx.getRequestId());
		} catch (AmazonClientException ace) {
			logger.error("Caught an AmazonClientException: ");
			logger.info("Error Message: " + ace.getMessage());
		}
		return url;
	}

	public void downloadImage(String key) {
		try {
			S3Object obj = client.s3Client().getObject(new GetObjectRequest(bucketName, key));
			logger.info("size:: {}", obj.getObjectMetadata().getContentLength());
			logger.info("Type:: {}", obj.getObjectMetadata().getContentType());

			BufferedInputStream bis = new BufferedInputStream(obj.getObjectContent());
			byte[] imgArr = new byte[(int) obj.getObjectMetadata().getContentLength()];
			bis.read(imgArr);

			File file = new File("C:\\\\Users\\\\faizan.ahmed\\\\Desktop\\downloadedImg.jpg");
			FileOutputStream fout = new FileOutputStream(file);
			fout.write(imgArr);
			fout.close();
		} catch (AmazonServiceException awsEx) {
			logger.info("Caught an AmazonServiceException from PUT Image requests, rejected reasons:");
			logger.info("Error Message:    " + awsEx.getMessage());
			logger.info("HTTP Status Code: " + awsEx.getStatusCode());
			logger.info("AWS Error Code:   " + awsEx.getErrorCode());
			logger.info("Error Type:       " + awsEx.getErrorType());
			logger.info("Request ID:       " + awsEx.getRequestId());
		} catch (AmazonClientException ace) {
			logger.error("Caught an AmazonClientException: ");
			logger.info("Error Message: " + ace.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
