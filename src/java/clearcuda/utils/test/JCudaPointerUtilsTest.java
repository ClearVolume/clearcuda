package clearcuda.utils.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import jcuda.Pointer;

import org.junit.Test;

import clearcuda.utils.JCudaPointerUtils;

public class JCudaPointerUtilsTest
{

	@Test
	public void test()
	{
		try
		{
			Pointer lPointer = JCudaPointerUtils.create(12345);
			assertNotNull(lPointer);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			fail();
		}

	}

}
