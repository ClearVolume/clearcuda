package clearcudaj;

import static jcuda.driver.JCudaDriver.cuMemGetInfo;

public class CudaMemory
{
	static public final long getFreeMemory()
	{
		long[] lFree = new long[1];
		long[] lTotal = new long[1];
		cuMemGetInfo(lFree, lTotal);

		return lFree[0];
	}

	static public final long getTotalMemory()
	{
		long[] lFree = new long[1];
		long[] lTotal = new long[1];
		cuMemGetInfo(lFree, lTotal);

		return lTotal[0];
	}

}
