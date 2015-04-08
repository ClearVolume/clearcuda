package clearcuda.utils;

import java.lang.reflect.Field;

import jcuda.Pointer;
import coremem.ContiguousMemoryInterface;

public class JCudaPointerUtils
{

	public static Pointer pointTo(ContiguousMemoryInterface pContiguousMemoryInterface)
	{
		return create(pContiguousMemoryInterface.getAddress());
	}

	public static Pointer create(long pNativeAddress)
	{
		final Pointer lPointer = new Pointer();
		setNativeAddress(lPointer, pNativeAddress);
		return lPointer;
	}

	public static long getNativeAddress(Pointer pPointer)
	{
		try
		{
			final Field lField = getField(Pointer.class, "nativePointer");
			lField.setAccessible(true);
			final long lNativeAddress = lField.getLong(pPointer);
			lField.setAccessible(false);
			return lNativeAddress;
		}
		catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	public static void setNativeAddress(Pointer pPointer,
																			long pNativeAddress)
	{
		try
		{
			final Field lField = getField(Pointer.class, "nativePointer");
			lField.setAccessible(true);
			lField.setLong(pPointer, pNativeAddress);
			lField.setAccessible(false);
		}
		catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	private static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException
	{
		try
		{
			return clazz.getDeclaredField(fieldName);
		}
		catch (final NoSuchFieldException e)
		{
			final Class<?> superClass = clazz.getSuperclass();
			if (superClass == null)
			{
				throw e;
			}
			else
			{
				return getField(superClass, fieldName);
			}
		}
	}
}
