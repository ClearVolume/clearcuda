package clearcudaj;

import java.nio.ByteBuffer;

import jcuda.Pointer;
import clearcudaj.utils.JCudaPointerUtils;

public interface CopyFromToInterface
{
	public void copyFrom(Pointer pPointer, boolean pSync);

	public void copyTo(Pointer pPointer, boolean pSync);

	public default void copyFrom(byte[] pArray, boolean pSync)
	{
		Pointer lPointer = Pointer.to(pArray);
		copyFrom(lPointer, pSync);
	}

	public default void copyFrom(char[] pArray, boolean pSync)
	{
		Pointer lPointer = Pointer.to(pArray);
		copyFrom(lPointer, pSync);
	}

	public default void copyFrom(short[] pArray, boolean pSync)
	{
		Pointer lPointer = Pointer.to(pArray);
		copyFrom(lPointer, pSync);
	}

	public default void copyFrom(int[] pArray, boolean pSync)
	{
		Pointer lPointer = Pointer.to(pArray);
		copyFrom(lPointer, pSync);
	}

	public default void copyFrom(float[] pArray, boolean pSync)
	{
		Pointer lPointer = Pointer.to(pArray);
		copyFrom(lPointer, pSync);
	}

	public default void copyFrom(double[] pArray, boolean pSync)
	{
		Pointer lPointer = Pointer.to(pArray);
		copyFrom(lPointer, pSync);
	}

	public default void copyTo(byte[] pArray, boolean pSync)
	{
		Pointer lPointer = Pointer.to(pArray);
		copyTo(lPointer, pSync);
	}

	public default void copyTo(char[] pArray, boolean pSync)
	{
		Pointer lPointer = Pointer.to(pArray);
		copyTo(lPointer, pSync);
	}

	public default void copyTo(short[] pArray, boolean pSync)
	{
		Pointer lPointer = Pointer.to(pArray);
		copyTo(lPointer, pSync);
	}

	public default void copyTo(int[] pArray, boolean pSync)
	{
		Pointer lPointer = Pointer.to(pArray);
		copyTo(lPointer, pSync);
	}

	public default void copyTo(float[] pArray, boolean pSync)
	{
		Pointer lPointer = Pointer.to(pArray);
		copyTo(lPointer, pSync);
	}

	public default void copyTo(double[] pArray, boolean pSync)
	{
		Pointer lPointer = Pointer.to(pArray);
		copyTo(lPointer, pSync);
	}

	public default void copyFrom(long pNativeAddress, boolean pSync)
	{
		Pointer lPointer = JCudaPointerUtils.create(pNativeAddress);
		copyFrom(lPointer, pSync);
	}

	public default void copyTo(long pNativeAddress, boolean pSync)
	{
		Pointer lPointer = JCudaPointerUtils.create(pNativeAddress);
		copyTo(lPointer, pSync);
	}

	public default void copyFrom(ByteBuffer pByteBuffer, boolean pSync)
	{
		copyFrom(Pointer.to(pByteBuffer), pSync);
	}

	public default void copyTo(ByteBuffer pByteBuffer, boolean pSync)
	{
		copyTo(Pointer.to(pByteBuffer), pSync);
	}
}
