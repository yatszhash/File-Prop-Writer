package data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ FileInfoIteratorTest.class, FileInfoTest.class, TextFileInfoWriterTest.class, FileOperationWithJnaTest.class,
			XlsFileInfoWriterTest.class, XlsxFileInfoWriterTest.class})
public class AllTestSuite
{}
