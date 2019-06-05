package com.boot.aws.s3demo.controller;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.Consumes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.boot.aws.s3demo.config.SecretMngr;
import com.boot.aws.s3demo.service.S3Services;

@RestController
@RequestMapping("/storage")
public class BucketController {

	@Autowired
	private S3Services service;
	@Autowired
	private SecretMngr scMngr;

	private Logger logger = LogManager.getLogger(BucketController.class);

	@RequestMapping("/uploadFile")
	public String upload() {
		logger.info("-----Inside Upload Method------");
		service.uploadFile("fileUpload", "C:\\Users\\faizan.ahmed\\Desktop\\S3-upload.txt");
		logger.info("-----Returning from Upload Method------");
		return "File Uploaded";
	}

	@RequestMapping("/downloadFile")
	public String download() {
		logger.info("-----Inside Download Method------");
		service.download("fileUpload");
		logger.info("-----Returning from Upload Method------");
		return "File Downloaded";
	}

	@PostMapping("/imageUpload")
	@Consumes("multipart/form-data")
	public String image(@RequestParam("filename") String fileName, @RequestParam("file") MultipartFile multiPartfile) throws IOException {
		try {
			logger.info("-----Inside UploadImage EndPoint------");
			String sc = scMngr.getSecret();
			System.out.println("secret:: " + sc);
			logger.info("-----Returning from UploadImage MetEndPointhod------");
			return service.uploadMultiPartImage(fileName, multiPartfile);
		} catch (IOException ioEx) {
			logger.error("IOException Occured :: " + ioEx);
			throw ioEx;
		}
	}

	@RequestMapping("/uploadImg")
	public String image() throws IOException {
		logger.info("-----Inside UploadImage EndPoint------");
		File imgFile = new File("C:\\Users\\Public\\Pictures\\Sample Pictures\\Tulips.jpg");
		String fileUrl = service.uploadImage("img1", imgFile);
		logger.info("-----Returning from UploadImage MetEndPointhod------");
		return "image Uploaded. url :: " + fileUrl;
	}

	@RequestMapping("/downloadImg")
	public String getImage() {
		logger.info("-----Inside downloadImg EndPoint------");
		scMngr.getSecret();
		service.downloadImage("img1");
		logger.info("-----Returning from downloadImg Endpoint------");
		return "image downloaded";
	}

}
