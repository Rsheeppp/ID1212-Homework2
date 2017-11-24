package cn.hangman.server;

import java.io.IOException;  
import java.net.InetSocketAddress;  
import java.nio.channels.SelectionKey;  
import java.nio.channels.Selector;  
import java.nio.channels.ServerSocketChannel;  
import java.util.Iterator;  

public class TCPServer {  
  // ��������С  
  private static final int BufferSize = 1024;  
  // ��ʱʱ�䣬��λ����  
  private static final int TimeOut = 3000;  
  // ���ؼ����˿�  
  private static final int ListenPort = 8888;  
  public static void main(String[] args) throws IOException {  
      // ����ѡ����  
      Selector selector = Selector.open();  
      // �򿪼����ŵ�  
      ServerSocketChannel listenerChannel = ServerSocketChannel.open();  
      // �뱾�ض˿ڰ�  
      listenerChannel.socket().bind(new InetSocketAddress(ListenPort));  
      // ����Ϊ������ģʽ  
      listenerChannel.configureBlocking(false);  
      // ��ѡ�����󶨵������ŵ�,ֻ�з������ŵ��ſ���ע��ѡ����.����ע�������ָ�����ŵ����Խ���Accept����  
      listenerChannel.register(selector, SelectionKey.OP_ACCEPT);  
      // ����һ������Э���ʵ����,�������������  
      TCPProtocol protocol = new TCPProtocolImpl(BufferSize);  

      // ����ѭ��,�ȴ�IO  
      while (true) {  
          // �ȴ�ĳ�ŵ�����(��ʱ)  
          if (selector.select(TimeOut) == 0) {// ����ע��ͨ������������ע��� IO  
                                              // �������Խ���ʱ���ú������أ�������Ӧ��  
                                              // SelectionKey ���� selected-key  
                                              // set  
              System.out.print("Waiting.");  
              continue;  
          }  
          // ȡ�õ�����.selectedKeys()�а�����ÿ��׼����ĳһI/O�������ŵ���SelectionKey  
          // Selected-key Iterator ����������ͨ�� select() ������⵽���Խ��� IO ������ channel  
          // ��������Ͽ���ͨ�� selectedKeys() �õ�  
          Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();  
          while (keyIter.hasNext()) {  
              SelectionKey key = keyIter.next();  
              SelectionKey key1;  
              if (keyIter.hasNext()) {  
                  key1 = keyIter.next();  
              }  
              try {  
                  if (key.isAcceptable()) {  
                      // �пͻ�����������ʱ  
                      protocol.handleAccept(key);  
                  }  
                  if (key.isReadable()) {// �ж��Ƿ������ݷ��͹���  
                      // �ӿͻ��˶�ȡ����  
                      protocol.handleRead(key);  
                  }  
                  if (key.isValid() && key.isWritable()) {// �ж��Ƿ���Ч�����Է��͸��ͻ���  
                      // �ͻ��˿�дʱ  
                      protocol.handleWrite(key);  
                  }  
              } catch (IOException ex) {  
                  // ����IO�쳣����ͻ��˶Ͽ����ӣ�ʱ�Ƴ�������ļ�  
                  keyIter.remove();  
                  continue;  
              }  
              // �Ƴ�������ļ�  
              keyIter.remove();  
          }  
      }  
  }  
}  