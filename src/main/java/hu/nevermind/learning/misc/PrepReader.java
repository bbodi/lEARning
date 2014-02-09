package hu.nevermind.learning.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import javax.crypto.Cipher;
import sun.misc.BASE64Decoder;

public class PrepReader {

	private static final HashMap qsmapData = new HashMap();
	private static final HashMap toughnessData = new HashMap();
	private static final HashMap sectionData = new HashMap();
	private static final HashMap authorData = new HashMap();
	private static final HashMap imgData = new HashMap();
	private static final HashMap urlData = new HashMap();
	private static final HashMap studyrefData = new HashMap();
	private static final HashMap bankmetaData = new HashMap();
	private static final HashMap testenvData = new HashMap();
	private static final HashMap testcrtData = new HashMap();
	private static final HashMap dndData = new HashMap();
	private static final HashMap dndCompData = new HashMap();
	private static final HashMap dndAssociationData = new HashMap();
	private static final HashMap questionData = new HashMap();
	private static final HashMap questionImgData = new HashMap();
	private static final HashMap questionURLData = new HashMap();
	private static final HashMap questionSpecificPartData = new HashMap();

	public static List<HashMap> getMaps(final String filename) {
		try {
			InputStream fis = PrepReader.class.getClassLoader().getResourceAsStream(filename);
			int totalfilesize = fis.available();
			ObjectInputStream ois = new ObjectInputStream(fis);
			String versioninfile = (String) ois.readObject();
			byte[] arrayOfByte = (byte[]) ois.readObject();
			String keyString = (String) ois.readObject();
			int currentsize = ois.available();

			byte[] cryptedData = (byte[]) ois.readObject();
			PublicKey key = createPublicKey(keyString);
			byte[] wtf2 = A(new ByteArrayInputStream(cryptedData), key);

			ois.close();
			JarInputStream jis = new JarInputStream(new ByteArrayInputStream(wtf2));
			JarEntry localJarEntry = jis.getNextJarEntry();
			ByteArrayOutputStream localObject4 = new ByteArrayOutputStream(wtf2.length * 2);
			int n = -1;
			while ((n = jis.read()) != -1) {
				localObject4.write(n);
			}
			byte[] localObject6 = localObject4.toByteArray();
			ois = new ObjectInputStream(new ByteArrayInputStream(localObject6));

			final List<HashMap> maps = new ArrayList<>();
			try {
				while (true) {
					final HashMap database = (HashMap) ois.readObject();
					maps.add(database);
				}
			} catch (Exception e) {

			}
			return maps;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void loadObject(Object paramObject, Field[] paramArrayOfField, Object[] paramArrayOfObject) {
		int i = paramArrayOfField.length;
		for (int j = 0; j < i; j++) {
			Field localField = paramArrayOfField[j];
			if (localField != null) {
				try {
					localField.set(paramObject, paramArrayOfObject[j]);
				} catch (Exception localException) {
				}
			}
		}
	}

	public static byte[] A(InputStream paramInputStream, Key paramKey)
			throws Exception {
		int i = paramKey.getEncoded().length < 100 ? 1 : 0;
		int j = i != 0 ? 64 : 128;
		Cipher localCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		int k = paramInputStream.available();
		ArrayList localArrayList = new ArrayList(k / j);
		byte[] arrayOfByte2 = new byte[j];
		localCipher.init(2, paramKey);
		int m;
		while ((m = paramInputStream.read(arrayOfByte2)) != -1) {
			byte[] arrayOfByte3 = localCipher.doFinal(A(arrayOfByte2, m));
			localArrayList.add(arrayOfByte3);
		}
		int n = 0;
		Iterator localIterator = localArrayList.iterator();
		while (localIterator.hasNext()) {
			byte[] localObject = (byte[]) localIterator.next();
			n += localObject.length;
		}
		byte[] arrayOfByte1 = new byte[n];
		int i1 = 0;
		Object localObject = localArrayList.iterator();
		while (((Iterator) localObject).hasNext()) {
			byte[] arrayOfByte4 = (byte[]) ((Iterator) localObject).next();
			n = arrayOfByte4.length;
			System.arraycopy(arrayOfByte4, 0, arrayOfByte1, i1, n);
			i1 += n;
		}
		return arrayOfByte1;
	}

	public static byte[] A(byte[] paramArrayOfByte, int paramInt) {
		byte[] arrayOfByte = null;
		if (paramArrayOfByte.length == paramInt) {
			arrayOfByte = paramArrayOfByte;
		} else {
			arrayOfByte = new byte[paramInt];
			System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramInt);
		}
		return arrayOfByte;
	}

	public static PublicKey createPublicKey(String paramString)
			throws Exception {
		BASE64Decoder localBASE64Decoder = new BASE64Decoder();
		KeyFactory localKeyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec localX509EncodedKeySpec = new X509EncodedKeySpec(localBASE64Decoder.decodeBuffer(paramString));
		PublicKey localPublicKey = localKeyFactory.generatePublic(localX509EncodedKeySpec);
		return localPublicKey;
	}

	public static Field[] getFields(Class paramClass, ArrayList<String[]> paramArrayList) {
		Field[] arrayOfField = new Field[paramArrayList.size()];
		for (int i = 0; i < paramArrayList.size(); i++) {
			String[] arrayOfString = (String[]) paramArrayList.get(i);
			try {
				arrayOfField[i] = paramClass.getDeclaredField(arrayOfString[0]);
				arrayOfField[i].setAccessible(true);
			} catch (Exception localException) {
			}
		}
		return arrayOfField;
	}

}
