package clearcudaj.utils.test;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import clearcudaj.utils.NVCC;

public class CudaUtilsTests
{

	@Test
	public void test() throws IOException
	{
		File lFindNVCC = NVCC.find();
		System.out.println("findNVCC=" + lFindNVCC);
		assertNotNull(lFindNVCC);
	}

}
