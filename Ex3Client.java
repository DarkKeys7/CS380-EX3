import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class Ex3Client {
  public static void main(String[] args) throws Exception {
    try {
      Socket socket = new Socket("18.221.102.182", 38103);
      System.out.println("Connected to server.");

      InputStream is = socket.getInputStream();
      OutputStream os = socket.getOutputStream();

      int numBytes = signedToUnsigned((byte) is.read());
      System.out.println("Reading " + numBytes + " bytes.");

      byte[] bytes = new byte[numBytes];
      for (int i = 0; i < numBytes; i++) {
        bytes[i] = (byte) signedToUnsigned((byte) is.read());
        System.out.print(String.format("%02x", bytes[i]));
        if ((i + 1) % 10 == 0 && i != 0)
          System.out.println();
      }
      System.out.println();

      ByteBuffer output = ByteBuffer.allocate(2);
      output.putShort(checksum(bytes));
      byte[] outputArray = output.array();

      System.out.print("Checksum calculated: 0x");
      for (int i = 0; i < outputArray.length; i++)
        System.out.print(String.format("%02x", bytes[i]));
      System.out.println();

      os.write(outputArray);
      if (is.read() == 1)
        System.out.println("Response good");
      else
        System.out.println("Response bad");
      socket.close();
      System.out.println("Disconnected from server\n");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static int signedToUnsigned(byte b) {
    return b & 0xFF;
  }

  public static short checksum(byte[] b) {
    int sum = 0;
    for (int i = 0; i < b.length / 2; i++) {
      int x = b[i * 2] & 0xFF;
      int y = b[i * 2 + 1] & 0xFF;
      x = x << 8;
      x = x ^ y;
      sum += x;
      if ((sum & 0xFFFF0000) != 0) {
        sum &= 0xFFFF;
        sum++;
      }
    }
    if (b.length % 2 != 0) {
      int x = b[b.length - 1] & 0xFF;
      int y = 0;
      x = x << 8;
      x = x ^ y;
      sum += x;
      if ((sum & 0xFFFF0000) != 0) {
        sum &= 0xFFFF;
        sum++;
      }
    }
    return (short) ~(sum & 0xFFFF);
  }
}