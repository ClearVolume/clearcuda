package clearcuda;

import static jcuda.driver.JCudaDriver.cuProfilerInitialize;
import static jcuda.driver.JCudaDriver.cuProfilerStart;
import static jcuda.driver.JCudaDriver.cuProfilerStop;

import java.io.File;

import jcuda.runtime.cudaOutputMode;

public class CudaProfiler
{

	private final File mConfigFile;
	private final File mOutputFile;

	public CudaProfiler(File pConfigFile, File pOutputFile)
	{
		super();
		mConfigFile = pConfigFile;
		mOutputFile = pOutputFile;
		cuProfilerInitialize(	pConfigFile.getAbsolutePath(),
													pOutputFile.getAbsolutePath(),
													cudaOutputMode.cudaCSV);
	}

	public void start()
	{
		cuProfilerStart();
	}

	public void stop()
	{
		cuProfilerStop();
	}

	public File getConfigFile()
	{
		return mConfigFile;
	}

	public File getOutputFile()
	{
		return mOutputFile;
	}

}
