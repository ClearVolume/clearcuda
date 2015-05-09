package clearcuda.memory.test;

import static org.junit.Assert.assertEquals;
import jcuda.Sizeof;

import org.junit.Test;

import clearcuda.CudaAvailability;
import clearcuda.CudaContext;
import clearcuda.CudaDevice;
import clearcuda.CudaHostPointer;
import clearcuda.memory.CudaMemory;
import coremem.interfaces.MemoryType;
import coremem.test.ContiguousMemoryTestsHelper;

public class CudaMemoryTests
{

	@Test
	public void testBasics()
	{

		if (!CudaAvailability.isClearCudaOperational())
			return;

		final int lLength = 1024;

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

			final CudaMemory lCudaMemory = new CudaMemory(lCudaHostPointer);

			assertEquals(123, lCudaMemory.getFloatAligned(lLength / 2), 0);

		}
		finally
		{
			lCudaHostPointer.close();
			lCudaContext.close();
			lCudaDevice.close();
		}

	}

	@Test
	public void delegatedTests()
	{

		if (!CudaAvailability.isClearCudaOperational())
			return;

		final int lLength = 1024;

		final CudaDevice lCudaDevice = new CudaDevice(0);
		final CudaContext lCudaContext = new CudaContext(	lCudaDevice,
																											false);
		final CudaHostPointer lCudaHostPointer1 = CudaHostPointer.mallocPinned(lLength * Sizeof.FLOAT);
		final CudaHostPointer lCudaHostPointer2 = CudaHostPointer.mallocPinned(lLength * Sizeof.FLOAT);
		try
		{

			final CudaMemory lCudaMemory1 = new CudaMemory(lCudaHostPointer1);
			final CudaMemory lCudaMemory2 = new CudaMemory(lCudaHostPointer2);

			ContiguousMemoryTestsHelper.testBasics(	lCudaMemory1,
																							MemoryType.CPURAMGPUMAPPED,
																							false);

			ContiguousMemoryTestsHelper.testCopyChecks(	lCudaMemory1,
																									lCudaMemory2);

			ContiguousMemoryTestsHelper.testWriteRead(lCudaMemory1);

		}
		finally
		{
			lCudaHostPointer2.close();
			lCudaHostPointer1.close();
			lCudaContext.close();
			lCudaDevice.close();
		}

	}
}
