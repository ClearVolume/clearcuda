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
import jcuda.runtime.JCuda;

public class CudaDevice implements CudaCloseable
{
	private static boolean sCudaInitialized = false;
	static
	{
		try
		{
			cuInit(0);
			JCuda.setExceptionsEnabled(true);
			JCudaDriver.setExceptionsEnabled(true);
			sCudaInitialized = true;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
	}

	public static boolean isCudaDeviceAvailable()
	{
		return sCudaInitialized && getNumberOfCudaDevices() > 0;
	}

	public CUdevice mCUdevice = new CUdevice();

	public static CudaDevice getBestCudaDevice()
	{
		final int lNumberOfCudaDevices = getNumberOfCudaDevices();

		CudaDevice lBestCudaDevice = null;
		long lMaximumTotalMemory = Long.MIN_VALUE;
		for (int i = 0; i < lNumberOfCudaDevices; i++)
		{
			@SuppressWarnings("resource")
			final CudaDevice lCudaDevice = new CudaDevice(i);
			final long lTotalMem = lCudaDevice.getTotalMem();
			if (lTotalMem > lMaximumTotalMemory)
			{
				lMaximumTotalMemory = lTotalMem;
				if (lBestCudaDevice != null)
					lBestCudaDevice.close();
				lBestCudaDevice = lCudaDevice;
			}
		}
		return lBestCudaDevice;
	}

	public CudaDevice(int pOrdinal)
	{
		super();
		JCudaDriver.setExceptionsEnabled(true);
		cuDeviceGet(mCUdevice, pOrdinal);
	}

	public String getName()
	{
		final byte[] lByteArray = new byte[256];
		cuDeviceGetName(lByteArray, lByteArray.length, mCUdevice);
		final String lName = new String(lByteArray);
		return lName.trim();
	}

	public final CudaComputeCapability getComputeCapability()
	{
		final int[] lMajor = new int[1];
		final int[] lMinor = new int[1];
		cuDeviceComputeCapability(lMajor, lMinor, mCUdevice);

		final CudaComputeCapability lCudaComputeCapability = new CudaComputeCapability(	lMajor[0],
																																										lMinor[0]);
		return lCudaComputeCapability;
	}

	public CUdevprop getProperties()
	{
		final CUdevprop lCUdevprop = new CUdevprop();
		cuDeviceGetProperties(lCUdevprop, mCUdevice);
		return lCUdevprop;
	}

	/**
	 * @param pAttribute
	 *          attributes defined in class CUdevice_attribute
	 * @return attribute value
	 */
	public int getAttribute(int pAttribute)
	{
		final int[] lAttributeValue = new int[1];
		cuDeviceGetAttribute(lAttributeValue, pAttribute, mCUdevice);
		return lAttributeValue[0];
	}

	public final long getTotalMem()
	{
		final long[] lDeviceTotalMem = new long[1];
		cuDeviceTotalMem(lDeviceTotalMem, mCUdevice);
		return lDeviceTotalMem[0];
	}

	public long getAvailableMem()
	{
		final long[] lFreeMemory = new long[1];
		final long[] lTotalMemory = new long[1];
		JCuda.cudaMemGetInfo(lFreeMemory, lTotalMemory);
		return lFreeMemory[0];
	}

	public static final int getNumberOfCudaDevices()
	{
		final int[] lDeviceCount = new int[1];
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
		if (mCUdevice != null)
		{
			mCUdevice = null;
		}
	}

}
