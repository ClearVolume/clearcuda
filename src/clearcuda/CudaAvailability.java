package clearcuda;

import java.io.File;
import java.io.IOException;

import clearcuda.utils.NVCC;

public class CudaAvailability
{
	static public boolean isClearCudaOperational()
	{
		boolean lDeviceAvailable = CudaDevice.isCudaDeviceAvailable();
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
}
