package de.huxhorn.lilith.data.eventsource;

/**
 * This class is a simple datatype to hold informations about the size of an event while
 * "on the wire".
 */
public final class TransferSizeInfo
	implements Cloneable
{
	public Long transferSize;
	public Long uncompressedSize;

	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TransferSizeInfo that = (TransferSizeInfo) o;

		if (transferSize != null ? !transferSize.equals(that.transferSize) : that.transferSize != null) return false;
		if (uncompressedSize != null ? !uncompressedSize.equals(that.uncompressedSize) : that.uncompressedSize != null)
		{
			return false;
		}

		return true;
	}

	public int hashCode()
	{
		int result;
		result = (transferSize != null ? transferSize.hashCode() : 0);
		result = 31 * result + (uncompressedSize != null ? uncompressedSize.hashCode() : 0);
		return result;
	}

	public TransferSizeInfo clone() throws CloneNotSupportedException
	{
		return (TransferSizeInfo) super.clone();
	}
}
