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
		catch (final Throwable e)
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
			final CudaCompiler lCUDACompiler = new CudaCompiler(null, "test");

			lCUDACompiler.setParameter("funcname", "bozo");

			final File lPrimaryFile = lCUDACompiler.addFile(CudaCompilerTests.class,
																								"cu/example.cu");
			lCUDACompiler.addFile(CudaCompilerTests.class,
														"cu/helper_math.h");

			final File lPTX = lCUDACompiler.compile(lPrimaryFile);
			return lPTX.exists();
		}
		catch (final Throwable e)
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
			final long lChannels = 2;
			final long lWidth = 128;
			final long lHeight = 1;
			final long lDepth = 1;
			final int lLength = (int) (lChannels * lWidth * lHeight * lDepth);

			final CudaDevice lCudaDevice = new CudaDevice(0);
			final CudaContext lCudaContext = new CudaContext(	lCudaDevice,
																												false);
			final CudaArray lCudaArray = new CudaArray(	lChannels,
																									lWidth,
																									lHeight,
																									lDepth,
																									4,
																									true,
																									false,
																									true);
			try
			{
				final float[] lFloatsIn = new float[lLength];
				lFloatsIn[lLength / 2] = 123;
				lCudaArray.copyFrom(lFloatsIn, true);

				final float[] lFloatsOut = new float[lLength];
				lCudaArray.copyTo(lFloatsOut, true);
				assertEquals(123, lFloatsOut[lLength / 2], 0);
			}
			finally
			{
				lCudaArray.close();
				lCudaContext.close();
				lCudaDevice.close();
			}
			return true;
		}
		catch (final Throwable e)
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
			final File lNVCCCompiler = NVCC.find();
			lCompilerIsAvailable = lNVCCCompiler.exists();
		}
		catch (final Throwable e)
		{
			System.err.println(e.getLocalizedMessage());
			System.out.println("CUDA COMPILER NOT AVAILABLE");
			lCompilerIsAvailable = false;
		}
		return lCompilerIsAvailable;
	}
}
