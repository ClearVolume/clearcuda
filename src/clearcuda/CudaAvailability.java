package clearcuda;

import static org.junit.Assert.assertEquals;

import java.io.File;

import clearcuda.test.CudaCompilerTests;
import clearcuda.utils.NVCC;

public class CudaAvailability
{
	static public boolean isClearCudaOperational()
	{
		try
		{
			return CudaDevice.isCudaDeviceAvailable() && isCudaCompilerAvailable()
							&& isCudaCompilerWorking()
							&& isCudaMemoryCopyWorking();
		}
		catch (Throwable e)
		{
			System.err.println(e.getLocalizedMessage());
			System.out.println("CUDA NOT AVAILABLE");
			return false;
		}
	}

	private static boolean isCudaCompilerWorking()
	{
		try
		{
			CudaCompiler lCUDACompiler = new CudaCompiler(null, "test");

			lCUDACompiler.setParameter("funcname", "bozo");

			File lPrimaryFile = lCUDACompiler.addFile(CudaCompilerTests.class,
																								"cu/example.cu");
			lCUDACompiler.addFile(CudaCompilerTests.class,
														"cu/helper_math.h");

			File lPTX = lCUDACompiler.compile(lPrimaryFile);
			return lPTX.exists();
		}
		catch (Throwable e)
		{
			System.err.println(e.getLocalizedMessage());
			e.printStackTrace();
			System.out.println("CUDA NOT AVAILABLE");
			return false;
		}
	}

	private static boolean isCudaMemoryCopyWorking()
	{
		try
		{
			long lChannels = 2;
			long lWidth = 128;
			long lHeight = 1;
			long lDepth = 1;
			int lLength = (int) (lChannels * lWidth * lHeight * lDepth);

			try (CudaDevice lCudaDevice = new CudaDevice(0);
					CudaContext lCudaContext = new CudaContext(	lCudaDevice,
																											false);
					CudaArray lCudaArray = new CudaArray(	lChannels,
																								lWidth,
																								lHeight,
																								lDepth,
																								4,
																								true,
																								false,
																								true))
			{
				float[] lFloatsIn = new float[lLength];
				lFloatsIn[lLength / 2] = 123;
				lCudaArray.copyFrom(lFloatsIn, true);

				float[] lFloatsOut = new float[lLength];
				lCudaArray.copyTo(lFloatsOut, true);
				assertEquals(123, lFloatsOut[lLength / 2], 0);
			}
			return true;
		}
		catch (Throwable e)
		{
			System.err.println(e.getLocalizedMessage());
			System.err.println("CUDA KERNEL TEST CALL FAILED!");
			return false;
		}
	}

	private static boolean isCudaCompilerAvailable()
	{
		boolean lCompilerIsAvailable;
		try
		{
			File lNVCCCompiler = NVCC.find();
			lCompilerIsAvailable = lNVCCCompiler.exists();
		}
		catch (Throwable e)
		{
			System.err.println(e.getLocalizedMessage());
			System.out.println("CUDA COMPILER NOT AVAILABLE");
			lCompilerIsAvailable = false;
		}
		return lCompilerIsAvailable;
	}
}
