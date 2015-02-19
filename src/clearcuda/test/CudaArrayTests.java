package clearcuda.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import clearcuda.CudaArray;
import clearcuda.CudaContext;
import clearcuda.CudaDevice;

public class CudaArrayTests
{

	@Test
	public void test()
	{

		long lChannels = 2;
		long lWidth = 128;
		long lHeight = 1;
		long lDepth = 1;
		int lLength = (int) (lChannels * lWidth * lHeight * lDepth);

		try (CudaDevice lCudaDevice = new CudaDevice(0);
				CudaContext lCudaContext = new CudaContext(lCudaDevice, false);
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

	}
}
