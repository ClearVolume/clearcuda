package clearcuda;

import java.io.File;

import clearcuda.utils.NVCC;

public class CudaAvailability
{
	static public boolean isClearCudaOperational()
	{
		try
		{
			boolean lDeviceAvailable = CudaDevice.isCudaDeviceAvailable();
			boolean lCompilerIsAvailable = isCudaCompilerAvailable();
			return lDeviceAvailable && lCompilerIsAvailable;
		}
		catch (Throwable e)
		{
			e.printStackTrace();
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
			e.printStackTrace();
			lCompilerIsAvailable = false;
		}
		return lCompilerIsAvailable;
	}
}
