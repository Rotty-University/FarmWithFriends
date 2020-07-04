package edu.brown.cs.jzhang30.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class DatabaseUtils {
  private DatabaseUtils() {
    // concealed constructor
  }

  public static byte[] convertToByteArray(Object obj) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);

    oos.writeObject(obj);
    oos.flush();
    oos.close();
    bos.close();

    return bos.toByteArray();
  }

  public static <T> T convertByteArrayToObject(byte[] bytes, Class<T> clazz)
      throws IOException, ClassNotFoundException {
    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
    ObjectInputStream ins = new ObjectInputStream(bais);
    T obj = (T) ins.readObject();

    return obj;
  }
}
