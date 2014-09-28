package clearcuda.test;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import clearcuda.CudaCompiler;

public class CudaCompilerTests
{

	@Test
	public void test() throws IOException
	{

		File lPTX = getPTX();

		System.out.println(lPTX);

		assertNotNull(lPTX);
	}

	public static final File getPTX() throws IOException
	{
		CudaCompiler lCUDACompiler = new CudaCompiler(null, "test");

		lCUDACompiler.setParameter("funcname", "bozo");

		File lPrimaryFile = lCUDACompiler.addFile(CudaCompilerTests.class,
																							"./cu/example.cu");
		lCUDACompiler.addFile(CudaCompilerTests.class,
													"./cu/helper_math.h");
		
		File lPTX = lCUDACompiler.compile(lPrimaryFile);
		return lPTX;
	}

}
