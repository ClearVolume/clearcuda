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
			System.out.println("CUDA NOT AVAILABLE");
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
			System.out.println("CUDA COMPILER NOT AVAILABLE");
			lCompilerIsAvailable = false;
		}
		return lCompilerIsAvailable;
	}
}
