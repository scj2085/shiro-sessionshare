package com.gome.meidian.account.shiro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.SerializationException;
import org.apache.shiro.codec.Base64;

import com.google.common.collect.Lists;
import com.google.common.io.Closeables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class SerializationUtils {
    public static byte[] serializeObject(final Object obj) {
        if (obj instanceof Serializable) {
            return serialize((Serializable) obj);
        } else {
            // TODO convert objToString
            throw new SerializationException();
        }
    }

	public static byte[] serialize(Serializable obj) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
		serialize(obj, baos);
		return baos.toByteArray();
	}

	public static void serialize(Serializable obj, ByteArrayOutputStream outputStream) {
		if (outputStream == null) {
			throw new IllegalArgumentException("The OutputStream must not be null");
		}
		ObjectOutputStream out = null;
		try {
			// stream closed in the finally
			out = new ObjectOutputStream(outputStream);
			out.writeObject(obj);

		} catch (final IOException ex) {
			throw new SerializationException(ex);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (final IOException ex) { // NOPMD
				// ignore close exception
			}
		}
	}

	public static String serializeToString(Serializable obj) {
		try {
			byte[] value = serialize(obj);
			return Base64.encodeToString(value);
		} catch (Exception e) {
			throw new RuntimeException("serialize session error", e);
		}
	}

	public static <T> T deserializeFromString(String base64) {
		try {
			byte[] objectData = Base64.decode(base64);
			return deserialize(objectData);
		} catch (Exception e) {
			throw new RuntimeException("deserialize session error", e);
		}
	}

	public static <T> Collection<T> deserializeFromStringController(Collection<String> base64s) {
		try {
			List<T> list = Lists.newLinkedList();
			for (String base64 : base64s) {
				byte[] objectData = Base64.decode(base64);
				T t = deserialize(objectData);
				list.add(t);
			}
			return list;
		} catch (Exception e) {
			throw new RuntimeException("deserialize session error", e);
		}
	}

	/**
	 * 自定义的反序列化
	 */
	public static <T> T deserialize(byte[] in) {
		ByteArrayInputStream bis = null;
		ObjectInputStream is = null;
		try {
			if (in != null) {

                bis = new ByteArrayInputStream(in);
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				// ClassLoader loader = RedisSe.loader
				// !=null?ServiceStatus.loader :
				// Thread.currentThread().getContextClassLoader();
				// //用一个全局的类存下加载用户代码的classloader
                is = new DeserializableObject(bis, loader);
				return (T) is.readObject();
			}
		} catch (IOException | ClassNotFoundException e) {
			throw new SerializationException(e);
		} finally {
			Closeables.closeQuietly(is);
			Closeables.closeQuietly(bis);
		}
		return null;
	}

	/**
	 * 反序列化字节流，使用一个指定的classloader序列化class流。
	 *
	 */
	private static class DeserializableObject extends ObjectInputStream {
		private ClassLoader loader;

		DeserializableObject(InputStream in, ClassLoader loader) throws IOException {
			super(in);
			this.loader = loader;
		}

		@Override
		protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
			return Class.forName(desc.getName(), true, loader);
		}
	}
}