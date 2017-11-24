package cn.hangman.client;


import java.io.IOException;  
import java.net.InetSocketAddress;  
import java.nio.ByteBuffer;  
import java.nio.channels.SelectionKey;  
import java.nio.channels.Selector;  
import java.nio.channels.SocketChannel;  
import java.util.Scanner;  

public class TCPClient {  
  // �ŵ�ѡ����  
  private Selector selector;  

  // �������ͨ�ŵ��ŵ�  
  SocketChannel socketChannel;  

  // Ҫ���ӵķ�����Ip��ַ  
  private String hostIp;  

  // Ҫ���ӵ�Զ�̷������ڼ����Ķ˿�  
  private int hostListenningPort;  

  public TCPClient(String HostIp, int HostListenningPort) throws IOException {  
      this.hostIp = HostIp;  
      this.hostListenningPort = HostListenningPort;  

      initialize();  
  }  
  /**  
   * ��ʼ��  
   *   
   * @throws IOException  
   */  
  private void initialize() throws IOException {  
      // �򿪼����ŵ�������Ϊ������ģʽ  
      socketChannel = SocketChannel.open(new InetSocketAddress(hostIp,  
              hostListenningPort));  
      socketChannel.configureBlocking(false);  

      // �򿪲�ע��ѡ�������ŵ�  
      selector = Selector.open();  
      socketChannel.register(selector, SelectionKey.OP_READ);  

      // ������ȡ�߳�  
      new TCPClientReadThread(selector);  
  }  
  /**  
   * �����ַ�����������  
   *   
   * @param message  
   * @throws IOException  
   */  
  public void sendMsg(String message) throws IOException {  
      ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes("UTF-8"));  
      socketChannel.write(writeBuffer);  
      writeBuffer.clear();
  }  
  static TCPClient client;  
  static boolean mFlag = true;  
  public static void main(String[] args) throws IOException {  
      client = new TCPClient("localhost", 8888);  
      new Thread() {  
          @Override  
          public void run() {  
              try {  
                  client.sendMsg("It's ready.");  
                  while (mFlag) {  
                      Scanner scan = new Scanner(System.in);//������������  
                      String string = scan.nextLine();  
                      client.sendMsg(string);  
                  }  
              } catch (IOException e) {  
                  mFlag = false;  
                  e.printStackTrace();  
              } finally {  
                  mFlag = false;  
              }  
              super.run();  
          }  
      }.start();  
  }  
}  
