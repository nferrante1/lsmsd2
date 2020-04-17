package app.datamodel.pojos;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class SecureString implements CharSequence
{
	private final char[] chars;
	private transient boolean deleted;
	private String hash;
	
	public SecureString(String hash)
	{
		this.hash = hash;
		this.chars = null;
	}

	public SecureString(char[] chars)
	{
		this.chars = new char[chars.length];
		System.arraycopy(chars, 0, this.chars, 0, chars.length);
	}

	public SecureString(char[] chars, int start, int end)
	{
		this.chars = new char[end - start];
		System.arraycopy(chars, start, this.chars, 0, this.chars.length);
	}
	
	public boolean hasPlaintext()
	{
		return chars != null && !deleted;
	}

	@Override
	public int length()
	{
		return hasPlaintext() ? chars.length : 0;
	}

	@Override
	public char charAt(int index)
	{
		if (index < 0 || index >= length())
			return Character.MIN_VALUE;
		return chars[index];
	}

	@Override
	public CharSequence subSequence(int start, int end)
	{
		if (hasPlaintext())
			return new SecureString(this.chars, start, end);
		return null;
	}
	
	public char[] toChars()
	{
		return chars;
	}
	
	public byte[] toBytes()
	{
		if (!hasPlaintext())
			return null;
		CharBuffer charBuffer = CharBuffer.wrap(chars);
		ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
		byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
			byteBuffer.position(), byteBuffer.limit());
		Arrays.fill(byteBuffer.array(), (byte)0);
		return bytes;
	}
	
	public String toSHA256Hash()
	{
		if (hash != null)
			return hash;
		if (!hasPlaintext())
			return "";
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException ex) {
			throw new UnsupportedOperationException(ex);
		}
		byte[] bytes = md.digest(toBytes());
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < bytes.length; i++)
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		hash = sb.toString();
		return hash;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof SecureString)
			return false;
		SecureString ss = (SecureString)obj;
		if (ss.length() != length())
			return false;
		if (!hasPlaintext() || !ss.hasPlaintext())
			return hashEquals(ss.toSHA256Hash());
		for (int i = 0; i < length(); i++)
			if (ss.charAt(i) != charAt(i))
				return false;
		return true;
	}
	
	public boolean hashEquals(String hash)
	{
		return !hash.isEmpty() && toSHA256Hash().equals(hash);
	}
	
	@Override
	public String toString()
	{
		return new String(chars);
	}

	public void clear()
	{
		Arrays.fill(chars, Character.MIN_VALUE);
		deleted = true;
	}

	@Override
	public void finalize()
	{
		clear();
	}
}
