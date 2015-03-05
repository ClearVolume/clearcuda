package clearcuda.test;

import java.util.Arrays;

import jcuda.driver.CUdevice_attribute;

import org.junit.Test;

import clearcuda.CudaAvailability;
import clearcuda.CudaContext;
import clearcuda.CudaDevice;

public class CudaDeviceTests
{

	@Test
	public void test()
	{
		if (!CudaAvailability.isClearCudaOperational())
			return;

		System.out.println("CudaDevice.getNumberOfCudaDevices()=" + CudaDevice.getNumberOfCudaDevices());
		for (int lDeviceId = 0; lDeviceId < CudaDevice.getNumberOfCudaDevices(); lDeviceId++)
			try (CudaDevice lCudaDevice = new CudaDevice(lDeviceId);
					CudaContext lCudaContext = new CudaContext(	lCudaDevice,
																											false))
			{
				System.out.println("________________________________________________");
				System.out.println("CudaDevice " + lCudaDevice);

				System.out.println("clockRate=" + lCudaDevice.getProperties().clockRate);
				System.out.println("memPitch=" + lCudaDevice.getProperties().memPitch);
				System.out.println("regsPerBlock=" + lCudaDevice.getProperties().regsPerBlock);
				System.out.println("sharedMemPerBlock=" + lCudaDevice.getProperties().sharedMemPerBlock);
				System.out.println("SIMDWidth=" + lCudaDevice.getProperties().SIMDWidth);
				System.out.println("textureAlign=" + lCudaDevice.getProperties().textureAlign);
				System.out.println("totalConstantMemory=" + lCudaDevice.getProperties().totalConstantMemory);
				System.out.println("maxGridSize=" + lCudaDevice.getProperties().maxThreadsPerBlock);
				System.out.println("maxThreadsDim=" + Arrays.toString(lCudaDevice.getProperties().maxThreadsDim));
				System.out.println("maxGridSize=" + Arrays.toString(lCudaDevice.getProperties().maxGridSize));

				System.out.println("UnifiedAddressing=" + lCudaDevice.getAttribute(CUdevice_attribute.CU_DEVICE_ATTRIBUTE_UNIFIED_ADDRESSING));
				System.out.println("MannagedAllocation=" + lCudaDevice.getAttribute(CUdevice_attribute.CU_DEVICE_ATTRIBUTE_MANAGED_MEMORY));
				System.out.println(" ");
			}
		/**/
	}
}
