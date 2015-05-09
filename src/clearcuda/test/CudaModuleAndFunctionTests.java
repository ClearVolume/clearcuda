package clearcuda.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import jcuda.Sizeof;

import org.junit.Test;

import clearcuda.CudaAvailability;
import clearcuda.CudaComputeCapability;
import clearcuda.CudaContext;
import clearcuda.CudaDevice;
import clearcuda.CudaDevicePointer;
import clearcuda.CudaFunction;
import clearcuda.CudaHostPointer;
import clearcuda.CudaModule;

public class CudaModuleAndFunctionTests
{

	@Test
	public void test() throws IOException
	{
		if (!CudaAvailability.isClearCudaOperational())
			return;

		final CudaDevice lCudaDevice = new CudaDevice(0);
		final CudaContext lCudaContext = new CudaContext(	lCudaDevice,
																											false);
		final CudaModule lCudaModule = CudaModule.moduleFromPTX(CudaCompilerTests.getPTX());
		try
		{

			final CudaComputeCapability lComputeCapability = lCudaDevice.getComputeCapability();
			System.out.println(lComputeCapability);
			assertNotNull(lCudaModule);

			final CudaFunction lFunction = lCudaModule.getFunction("bozo");
			assertNotNull(lFunction);

			final int length = 1024;
			final float[] a = new float[length];
			for (int i = 0; i < length; i++)
				a[i] = i;
			final float[] b = new float[length];
			for (int i = 0; i < length; i++)
				b[i] = length - i;
			final float[] c = new float[length];

			CudaDevicePointer lPtrA = CudaDevicePointer.malloc(length * Sizeof.FLOAT);
			CudaDevicePointer lPtrB = CudaDevicePointer.malloc(length * Sizeof.FLOAT);
			CudaDevicePointer lPtrC = CudaDevicePointer.malloc(length * Sizeof.FLOAT);

			try
			{
				lPtrA.copyFrom(a, true);
				lPtrB.copyFrom(b, true);

				lFunction.setBlockDim(32);
				lFunction.setGridDim(1024 / 32);
				lFunction.launch(length, lPtrA, lPtrB, lPtrC);

				lPtrC.copyTo(c, true);
				for (int i = 0; i < length; i++)
					assertEquals(length, c[i], 0);
			}
			finally
			{
				lPtrA.close();
				lPtrB.close();
				lPtrC.close();
			}

			lPtrA = CudaHostPointer.mallocPinned(length * Sizeof.FLOAT);
			lPtrB = CudaHostPointer.mallocPinned(length * Sizeof.FLOAT);
			lPtrC = CudaHostPointer.mallocPinned(length * Sizeof.FLOAT);
			try
			{
				lPtrA.copyFrom(a, true);
				lPtrB.copyFrom(b, true);

				lFunction.setBlockDim(32);
				lFunction.setGridDim(1024 / 32);
				lFunction.launch(length, lPtrA, lPtrB, lPtrC);

				lPtrC.copyTo(c, true);
				for (int i = 0; i < length; i++)
					assertEquals(length, c[i], 0);
			}
			finally
			{
				lPtrA.close();
				lPtrB.close();
				lPtrC.close();
			}

		}
		finally
		{
			lCudaDevice.close();
			lCudaContext.close();
			lCudaModule.close();
		}

	}
}
