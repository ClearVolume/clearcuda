package clearcuda;

import static jcuda.driver.JCudaDriver.cuDeviceGetCount;

import java.io.File;
import java.io.IOException;

import clearcuda.utils.NVCC;

public class CudaAvailability
{
	static public boolean isClearCudaOperational()
	{
		boolean lDeviceAvailable = isCudaDeviceAvailable();
		boolean lCompilerIsAvailable = isCudaCompilerAvailable();
		return lDeviceAvailable && lCompilerIsAvailable;
	}

	private static boolean isCudaCompilerAvailable()
	{
		boolean lCompilerIsAvailable;
		try
		{
			File lNVCCCompiler = NVCC.find();
			lCompilerIsAvailable = lNVCCCompiler.exists();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			lCompilerIsAvailable = false;
		}
		return lCompilerIsAvailable;
	}

	private static boolean isCudaDeviceAvailable()
	{
		boolean lDeviceAvailable;
		try
		{
			int[] lDeviceCount = new int[1];
			cuDeviceGetCount(lDeviceCount);
			lDeviceAvailable = lDeviceCount[0] > 0;
		}
		catch (Throwable e1)
		{
			e1.printStackTrace();
			lDeviceAvailable = false;
		}
		return lDeviceAvailable;
	}
}
