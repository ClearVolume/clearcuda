package clearcuda.test;

import static org.junit.Assert.assertEquals;
import jcuda.Sizeof;

import org.junit.Test;

import clearcuda.CudaAvailability;
import clearcuda.CudaContext;
import clearcuda.CudaDevice;
import clearcuda.CudaHostPointer;

public class CudaHostPointerTests
{

	@Test
	public void testPinnedMalloc()
	{
		if (!CudaAvailability.isClearCudaOperational())
			return;

		final int lLength = 128 * 129 * 130;

		final CudaDevice lCudaDevice = new CudaDevice(0);
		final CudaContext lCudaContext = new CudaContext(	lCudaDevice,
																											false);
		final CudaHostPointer lCudaHostPointer = CudaHostPointer.mallocPinned(lLength * Sizeof.FLOAT);
		try
		{
			final float[] lFloatsIn = new float[lLength];
			lFloatsIn[lLength / 2] = 123;
			lCudaHostPointer.copyFrom(lFloatsIn, true);

			final float[] lFloatsOut = new float[lLength];
			lCudaHostPointer.copyTo(lFloatsOut, true);
			assertEquals(123, lFloatsOut[lLength / 2], 0);
		}
		finally
		{
			lCudaHostPointer.close();
			lCudaContext.close();
			lCudaDevice.close();
		}
	}

}
