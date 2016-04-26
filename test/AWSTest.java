import java.io.File;

import org.junit.Test;

import play.test.UnitTest;
import util.AmazonS3Util;

/**
 * 
 */

/**
 * @author Administrator
 *
 */
public class AWSTest extends UnitTest {

	@Test
	public void testPutIntoS3() {
		try {
			File test = new File("file://Users/Administrator/Projetos/Pessoal/logs/teste.txt");
		
			//AmazonS3Util.sendFileToS3(null, test);
		
		}catch(Exception ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
}
