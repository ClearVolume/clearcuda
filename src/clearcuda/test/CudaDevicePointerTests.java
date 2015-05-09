package clearcuda.test;

import static org.junit.Assert.assertEquals;
import jcuda.Sizeof;
import jcuda.driver.CUdevice_attribute;

import org.junit.Test;

import clearcuda.CudaAvailability;
import clearcuda.CudaContext;
import clearcuda.CudaDevice;
import clearcuda.CudaDevicePointer;

public class CudaDevicePointerTests
{

	@Test
	public void testMalloc()
	{
		if (!CudaAvailability.isClearCudaOperational())
			return;

		final int lLength = 1024;

		final CudaDevice lCudaDevice = new CudaDevice(0);
		final CudaContext lCudaContext = new CudaContext(	lCudaDevice,
																											false);
		final CudaDevicePointer lCudaDevicePointer = CudaDevicePointer.malloc(lLength * Sizeof.FLOAT);

		try
		{
			final float[] lFloatsIn = new float[lLength];
			lFloatsIn[lLength / 2] = 123;
			lCudaDevicePointer.copyFrom(lFloatsIn, true);

			final float[] lFloatsOut = new float[lLength];
			lCudaDevicePointer.copyTo(lFloatsOut, true);
			assertEquals(123, lFloatsOut[lLength / 2], 0);
		}
		finally
		{
			lCudaDevicePointer.close();
			lCudaContext.close();
			lCudaDevice.close();
		}

	}

	@Test
	public void testMallocManaged()
	{
		if (!CudaAvailability.isClearCudaOperational())
			return;

		final int lLength = 1024;

		final CudaDevice lCudaDevice = new CudaDevice(0);
		final CudaContext lCudaContext = new CudaContext(	lCudaDevice,
																											false);

		try
		{
			if (lCudaDevice.getAttribute(CUdevice_attribute.CU_DEVICE_ATTRIBUTE_MANAGED_MEMORY) > 0)
			{
				final CudaDevicePointer lCudaDevicePointer = CudaDevicePointer.mallocManaged(lLength * Sizeof.FLOAT);
				try
				{
					final float[] lFloatsIn = new float[lLength];
					lFloatsIn[lLength / 2] = 123;
					lCudaDevicePointer.copyFrom(lFloatsIn, true);

					final float[] lFloatsOut = new float[lLength];
					lCudaDevicePointer.copyTo(lFloatsOut, true);
					assertEquals(123, lFloatsOut[lLength / 2], 0);
				}
				finally
				{
					lCudaDevicePointer.close();
				}
			}
			else
			{
				System.out.println("Managed memory not supported!");
			}
		}
		finally
		{
			lCudaContext.close();
			lCudaDevice.close();
		}
	}
}
