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
		final File lPTX = getPTX();

		System.out.println(lPTX);

		assertNotNull(lPTX);
	}

	public static final File getPTX() throws IOException
	{
		final CudaCompiler lCUDACompiler = new CudaCompiler(null, "test");

		lCUDACompiler.setParameter("funcname", "bozo");

		final File lPrimaryFile = lCUDACompiler.addFile(CudaCompilerTests.class,
																										"./cu/example.cu",
																										true);
		lCUDACompiler.addFile(CudaCompiler.class,
													"./includes/helper_math.h",
													true);

		final File lPTX = lCUDACompiler.compile(lPrimaryFile);
		return lPTX;
	}

}
