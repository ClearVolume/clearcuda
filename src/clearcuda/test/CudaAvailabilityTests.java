package clearcuda.test;

import java.io.IOException;

import org.junit.Test;

import clearcuda.CudaAvailability;

public class CudaAvailabilityTests
{

	@Test
	public void test() throws IOException
	{
		if (!CudaAvailability.isClearCudaOperational())
		{
			System.err.println("CUDA NOT AVAILABLE");
		}
	}

}
