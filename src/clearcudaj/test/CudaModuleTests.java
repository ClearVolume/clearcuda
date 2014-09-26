package clearcudaj.test;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import clearcudaj.CudaComputeCapability;
import clearcudaj.CudaContext;
import clearcudaj.CudaDevice;
import clearcudaj.CudaFunction;
import clearcudaj.CudaModule;

public class CudaModuleTests
{


	@Test
	public void test() throws IOException
	{


		CudaDevice lCudaDevice = new CudaDevice(0);
		CudaComputeCapability lComputeCapability = lCudaDevice.getComputeCapability();
		System.out.println(lComputeCapability);

		CudaContext lCudaContext = new CudaContext(lCudaDevice);

		File lPTX = CudaCompilerTests.getPTX();
		CudaModule lCudaModule = CudaModule.moduleFromPTX(lPTX);
		assertNotNull(lCudaModule);

		CudaFunction lFunction = lCudaModule.getFunction("bozo");
		assertNotNull(lFunction);

		lCudaContext.close();
	}

}
