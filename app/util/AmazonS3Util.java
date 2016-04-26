/**
 * 
 */
package util;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import play.Logger;
import play.i18n.Messages;

/**
 * @author Felipe Guerra
 * <p>
 * 	Classe responsável por abstrair as operações de escrita/leitura no Bucket do S3
 * </p>
 */
public class AmazonS3Util implements Serializable {

	private static final long serialVersionUID = -109897654321L;

	private static AmazonS3Client amazonS3Client = null;
	private static AWSCredentials credentials = null;
	
	static {
		try {
			credentials = new BasicAWSCredentials(Messages.get("application.aws.accesKeyId", ""), 
												Messages.get("application.aws.secretKey", ""));
			amazonS3Client = new AmazonS3Client(credentials);
			amazonS3Client.setRegion(
					RegionUtils.getRegion(Messages.get("application.aws.region", ""))
					);
			
			
		}catch(Exception ex) {
			Logger.error("Ocorreu um erro na inicialização da classe de acesso ao S3.", ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	
	public static boolean deleteFileInS3(String bucket, String filePathName) {
		String _bucket = bucket==null ? Messages.get("application.aws.bucketName", "") : bucket.trim();
		DeleteObjectRequest request = null;
		
		try {
			request = new DeleteObjectRequest(_bucket, filePathName);
			
			amazonS3Client.deleteObject(request);
			
			return true;
			
		}catch(Exception e) {
			Logger.error(e, "Ocorreu um erro ao tentar apagar um arquivo no S3 [bucket: ]", _bucket);
			return false;
		}
	}
	
	/**
	 * Faz o PUT do objeto File ao Bucket do S3. Se o valor do bucket for null, ele considera o padrão da aplicação.
	 * @param bucket
	 * @param file
	 * @return VersionId
	 */
	public static String sendFileToS3(String bucket, File file, String filePathName) {
		String _bucket = bucket==null ? Messages.get("application.aws.bucketName", "") : bucket.trim();
		PutObjectResult result = null;
		PutObjectRequest request = null;
		ObjectMetadata metadata = null;
		
		try {
			request = new PutObjectRequest(_bucket, 
											filePathName, 
											file);
			
			metadata = new ObjectMetadata();
			metadata.addUserMetadata("Cache-Control", "public,max-age=31104000");
			
			result = amazonS3Client.putObject(request
												.withCannedAcl(CannedAccessControlList.PublicRead)
												.withMetadata(metadata)
											);
			
			return result.getETag();
			
		}catch(Exception e) {
			Logger.error(e, "Ocorreu um erro ao tentar enviar um arquivo ao S3 [bucket: ]", _bucket);
			return null;
		}		
	}
	
}
