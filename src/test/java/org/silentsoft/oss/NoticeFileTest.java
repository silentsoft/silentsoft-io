package org.silentsoft.oss;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;
import org.silentsoft.oss.license.MITLicense;

public class NoticeFileTest {
	
	private static final License MIT_LICENSE = new MITLicense();
	
	@Test
	public void noticeFileTest() throws Exception {
		String markdown = generateSilentsoftIoNoticeMarkdown();
		
		System.out.println("--------START OF THE NOTICE FILE--------");
		System.out.println(markdown);
		System.out.println("---------END OF THE NOTICE FILE---------");
		
		Assert.assertEquals(markdown, readFile());
	}
	
	private String generateSilentsoftIoNoticeMarkdown() {
		return NoticeFileGenerator.newInstance("silentsoft-io", "silentsoft.org")
			.addText("This product includes software developed by The Apache Software Foundation (http://www.apache.org/).")
			.addLibrary("slf4j-api 1.7.25", "https://github.com/qos-ch/slf4j", MIT_LICENSE)
			.generate();
	}
	
	private String readFile() throws Exception {
		return String.join("\r\n", Files.readAllLines(Paths.get(System.getProperty("user.dir"), "NOTICE.md"), StandardCharsets.UTF_8));
	}

}
