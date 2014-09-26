package clearcudaj;

import static jcuda.driver.JCudaDriver.cuModuleGetGlobal;

public class CudaGlobal extends CudaDevicePointer
{

	public CudaGlobal(CudaModule pCudaModule, String pGlobalName)
	{
		super();
		long[] lArrayOfLong = new long[1];
		cuModuleGetGlobal(getPeer(),
											lArrayOfLong,
											pCudaModule.getPeer(),
											pGlobalName);
		mSizeInBytes = lArrayOfLong[0];
	}

}
