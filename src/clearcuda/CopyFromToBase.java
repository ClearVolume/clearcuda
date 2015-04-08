package clearcuda;

import java.nio.ByteBuffer;

import jcuda.Pointer;
import clearcuda.utils.JCudaPointerUtils;
import coremem.ContiguousMemoryInterface;

public abstract class CopyFromToBase
{
	public abstract void copyFrom(Pointer pPointer, boolean pSync);

	public abstract void copyTo(Pointer pPointer, boolean pSync);

	public void copyFrom(byte[] pArray, boolean pSync)
	{
		final Pointer lPointer = Pointer.to(pArray);
		copyFrom(lPointer, pSync);
	}

	public void copyFrom(char[] pArray, boolean pSync)
	{
		final Pointer lPointer = Pointer.to(pArray);
		copyFrom(lPointer, pSync);
	}

	public void copyFrom(short[] pArray, boolean pSync)
	{
		final Pointer lPointer = Pointer.to(pArray);
		copyFrom(lPointer, pSync);
	}

	public void copyFrom(int[] pArray, boolean pSync)
	{
		final Pointer lPointer = Pointer.to(pArray);
		copyFrom(lPointer, pSync);
	}

	public void copyFrom(float[] pArray, boolean pSync)
	{
		final Pointer lPointer = Pointer.to(pArray);
		copyFrom(lPointer, pSync);
	}

	public void copyFrom(double[] pArray, boolean pSync)
	{
		final Pointer lPointer = Pointer.to(pArray);
		copyFrom(lPointer, pSync);
	}

	public void copyTo(byte[] pArray, boolean pSync)
	{
		final Pointer lPointer = Pointer.to(pArray);
		copyTo(lPointer, pSync);
	}

	public void copyTo(char[] pArray, boolean pSync)
	{
		final Pointer lPointer = Pointer.to(pArray);
		copyTo(lPointer, pSync);
	}

	public void copyTo(short[] pArray, boolean pSync)
	{
		final Pointer lPointer = Pointer.to(pArray);
		copyTo(lPointer, pSync);
	}

	public void copyTo(int[] pArray, boolean pSync)
	{
		final Pointer lPointer = Pointer.to(pArray);
		copyTo(lPointer, pSync);
	}

	public void copyTo(float[] pArray, boolean pSync)
	{
		final Pointer lPointer = Pointer.to(pArray);
		copyTo(lPointer, pSync);
	}

	public void copyTo(double[] pArray, boolean pSync)
	{
		final Pointer lPointer = Pointer.to(pArray);
		copyTo(lPointer, pSync);
	}

	public void copyFrom(long pNativeAddress, boolean pSync)
	{
		final Pointer lPointer = JCudaPointerUtils.create(pNativeAddress);
		copyFrom(lPointer, pSync);
	}

	public void copyTo(long pNativeAddress, boolean pSync)
	{
		final Pointer lPointer = JCudaPointerUtils.create(pNativeAddress);
		copyTo(lPointer, pSync);
	}

	public void copyFrom(ByteBuffer pByteBuffer, boolean pSync)
	{
		copyFrom(Pointer.to(pByteBuffer), pSync);
	}

	public void copyTo(ByteBuffer pByteBuffer, boolean pSync)
	{
		copyTo(Pointer.to(pByteBuffer), pSync);
	}

	public void copyFrom(	ContiguousMemoryInterface pContiguousMemoryInterface,
												boolean pSync)
	{
		pContiguousMemoryInterface.complainIfFreed();
		final Pointer lPointTo = JCudaPointerUtils.pointTo(pContiguousMemoryInterface);
		// final long lStartNs = System.nanoTime();
		copyFrom(	lPointTo,
							pSync);
		// final long lStopNs = System.nanoTime();
		// System.out.println("ELPASED:" + (lStopNs - lStartNs) / 1.0e6);
		pContiguousMemoryInterface.complainIfFreed();
	}

	public void copyTo(	ContiguousMemoryInterface pContiguousMemoryInterface,
											boolean pSync)
	{
		pContiguousMemoryInterface.complainIfFreed();
		final Pointer lPointTo = JCudaPointerUtils.pointTo(pContiguousMemoryInterface);
		copyTo(lPointTo,
						pSync);
		pContiguousMemoryInterface.complainIfFreed();
	}



}
