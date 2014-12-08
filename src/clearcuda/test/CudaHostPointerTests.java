package clearcuda.test;

import static org.junit.Assert.assertEquals;
import jcuda.Sizeof;

import org.junit.Test;

import clearcuda.CudaContext;
import clearcuda.CudaDevice;
import clearcuda.CudaHostPointer;

public class CudaHostPointerTests
{

	@Test
	public void testPinnedMalloc()
	{

		int lLength = 1024;

		try (CudaDevice lCudaDevice = new CudaDevice(0);
				CudaContext lCudaContext = new CudaContext(lCudaDevice, false);
				CudaHostPointer lCudaHostPointer = CudaHostPointer.mallocPinned(lLength * Sizeof.FLOAT))
		{
			float[] lFloatsIn = new float[lLength];
			lFloatsIn[lLength / 2] = 123;
			lCudaHostPointer.copyFrom(lFloatsIn, true);

			float[] lFloatsOut = new float[lLength];
			lCudaHostPointer.copyTo(lFloatsOut, true);
			assertEquals(123, lFloatsOut[lLength / 2], 0);
		}
	}

}
