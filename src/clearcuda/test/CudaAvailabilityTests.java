package clearcuda.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import clearcuda.CudaAvailability;

public class CudaAvailabilityTests
{

	@Test
	public void test() throws IOException
	{
		assertTrue(CudaAvailability.isClearCudaOperational());
	}

}
