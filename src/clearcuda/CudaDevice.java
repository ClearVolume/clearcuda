package clearcuda;

import static jcuda.driver.JCudaDriver.cuDeviceComputeCapability;
import static jcuda.driver.JCudaDriver.cuDeviceGet;
import static jcuda.driver.JCudaDriver.cuDeviceGetAttribute;
import static jcuda.driver.JCudaDriver.cuDeviceGetCount;
import static jcuda.driver.JCudaDriver.cuDeviceGetName;
import static jcuda.driver.JCudaDriver.cuDeviceGetProperties;
import static jcuda.driver.JCudaDriver.cuDeviceTotalMem;
import static jcuda.driver.JCudaDriver.cuInit;
import jcuda.driver.CUdevice;
import jcuda.driver.CUdevprop;
import jcuda.driver.JCudaDriver;

public class CudaDevice implements CudaCloseable
{
	public final CUdevice mCUdevice = new CUdevice();

	public CudaDevice(int pOrdinal)
	{
		super();
		JCudaDriver.setExceptionsEnabled(true);
		cuInit(0);
		cuDeviceGet(mCUdevice, pOrdinal);
	}

	public String getName()
	{
		byte[] lByteArray = new byte[256];
		cuDeviceGetName(lByteArray, lByteArray.length, mCUdevice);
		String lName = new String(lByteArray);
		return lName.trim();
	}

	public final CudaComputeCapability getComputeCapability()
	{
		int[] lMajor = new int[1];
		int[] lMinor = new int[1];
		cuDeviceComputeCapability(lMajor, lMinor, mCUdevice);

		CudaComputeCapability lCudaComputeCapability = new CudaComputeCapability(	lMajor[0],
																																							lMinor[0]);
		return lCudaComputeCapability;
	}

	public CUdevprop getProperties()
	{
		CUdevprop lCUdevprop = new CUdevprop();
		cuDeviceGetProperties(lCUdevprop, mCUdevice);
		return lCUdevprop;
	}

	/**
	 * @param pAttribute
	 *          attributes defined in class CUdevice_attribute
	 * @return
	 */
	public int getAttribute(int pAttribute)
	{
		int[] lAttributeValue = new int[1];
		cuDeviceGetAttribute(lAttributeValue, pAttribute, mCUdevice);
		return lAttributeValue[0];
	}

	public final long getTotalMem()
	{
		long[] lDeviceTotalMem = new long[1];
		cuDeviceTotalMem(lDeviceTotalMem, mCUdevice);
		return lDeviceTotalMem[0];
	}

	public static final int getNumberOfCudaDevices()
	{
		int[] lDeviceCount = new int[1];
		cuDeviceGetCount(lDeviceCount);
		return lDeviceCount[0];
	}

	public CUdevice getPeer()
	{
		return mCUdevice;
	}

	@Override
	public String toString()
	{
		return "CudaDevice [getName()=" + getName()
						+ ", getComputeCapability()="
						+ getComputeCapability()
						+ ", getTotalMem()="
						+ getTotalMem()
						+ "]";
	}

	@Override
	public void close()
	{
		
	}

}
