package cn.hangman.server;

import java.io.IOException;  
import java.nio.ByteBuffer;  
import java.nio.channels.SelectionKey;  
import java.nio.channels.ServerSocketChannel;  
import java.nio.channels.SocketChannel;  
import java.nio.charset.Charset;  

public class TCPProtocolImpl implements TCPProtocol {  
	private int bufferSize;  
	String word = "";
	char[] result ;
	int count;
	private int score =0;
	public TCPProtocolImpl(int bufferSize) {  
		this.bufferSize = bufferSize;  
	}  

	public void handleAccept(SelectionKey key) throws IOException {  
		// create this key channel, accept the client to establish a connection request, and return SocketChannel object
		SocketChannel clientChannel = ((ServerSocketChannel) key.channel())  
				.accept();  
		// nonblocking
		clientChannel.configureBlocking(false);  
		// register
		clientChannel.register(key.selector(), SelectionKey.OP_READ,  
				ByteBuffer.allocate(bufferSize));  
		word = Common.getWord().toLowerCase();
		result = new char[word.length()];
		for (int i = 0; i < result.length; i++) {
			result[i]='-'; 
		}
		count = word.length();
	}  

	public void handleRead(SelectionKey key) throws IOException {  
		// Get the channel to communicate with the client
		SocketChannel clientChannel = (SocketChannel) key.channel();  
		// Get and empty the buffer  
		ByteBuffer buffer = (ByteBuffer) key.attachment();  
		buffer.clear();  
		// The number of bytes read from the read message
		long bytesRead = clientChannel.read(buffer);  
		if (bytesRead == -1) {  

			clientChannel.close();  
		} else {  
			// Prepare the buffer for outgoing data  
			buffer.flip();  
			// Convert bytes to a string of UTF-16
			String receivedString = Charset.forName("UTF-8").newDecoder()  
					.decode(buffer).toString();  

			System.out.println("From"  
					+ clientChannel.socket().getRemoteSocketAddress() + "Message:"  
					+ receivedString);  
			// The text to be sent
			if(receivedString.equals("It's ready.")){
				String sendString = "Begin now.Input a word or a letter.";  
				buffer = ByteBuffer.wrap(sendString.getBytes("UTF-8"));  
				clientChannel.write(buffer);  
				// Set to prepare for the next read or write
				key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);  
				return; 
			}
			count--;
			result = Common.compare(word, receivedString, result);
			String revert=new String(result);
			if(revert.equals(word)){
				score++;
				revert = revert+"\tBingo!"+(word.length()-count)+"chances used.Score:"+score;
				word = Common.getWord().toLowerCase();
				result = new char[word.length()];
				for (int i = 0; i < result.length; i++) {
					result[i]='-'; 
				}
				count = word.length();
				buffer = ByteBuffer.wrap(revert.getBytes("UTF-8"));  
				clientChannel.write(buffer);  
				// Set to prepare for the next read or write
				key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);  
			}else{
				revert = revert+"\tCome on!"+count+"chances left.Score:"+score;
				if(count==0){
					score--;
					revert = "\tAnswer:"+word+"Score"+score;
				}
				buffer = ByteBuffer.wrap(revert.getBytes("UTF-8"));  
				clientChannel.write(buffer);  
				// Set to prepare for the next read or write
				key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);  
			}
			if(count==0){
				word = Common.getWord().toLowerCase();
				result = new char[word.length()];
				for (int i = 0; i < result.length; i++) {
					result[i]='-'; 
				}
				count = word.length();
			}
		}
	}  

	public void handleWrite(SelectionKey key) throws IOException {  
		// do nothing  
	}

}  
