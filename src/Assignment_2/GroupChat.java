package Assignment_2;

import java.io.*;
import java.util.*;
import java.net.*;




public class GroupChat{
	public int port;  // for storing port number
	public ArrayList<String> clientName = new ArrayList<String>(); // for storing users
	public Set<mainThread> userThread = new HashSet<>();  // storing client Threads
	
	public GroupChat(int port)
	{
		this.port = port;
	}

	public void createClientThread()
	{
		try(ServerSocket ss = new ServerSocket(port))
		{
			System.out.println("Chat is on port: "+ port);
			while(true)
			{
				Socket s = ss.accept();
				System.out.println("New Member Joined.");
				
			    mainThread ct = new mainThread(s, this);// Thread Creation for new every new user Connected.
			    userThread.add(ct);
			    ct.start();
						
			}
		}catch(IOException e)
		{
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	public void broadCastMessage(String msg, mainThread ct)
	{
		for(mainThread a : userThread)
		{
			if(a != ct)
			{
				a.sendMessage(msg);
			}
		}
	}
	
	public void setName(String ClientName)
	{
		clientName.add(ClientName);
	}
	
	public void clientLeft(String ClientName, mainThread a)
	{
		userThread.remove(a);
		System.out.println(ClientName + " "+ "left");
	}
	
	void removeUser(String offlineClient, mainThread mt)
	{
		clientName.remove(offlineClient);
		clientLeft(offlineClient, mt);
	}
	
	
	// Thread Creation for every user connected....
	public class mainThread extends Thread {
		
		public Socket s;
		public GroupChat gc;
		public PrintWriter pw;
		
		public mainThread(Socket s, GroupChat gc)
		{
			this.s = s;
			this.gc = gc;
		}
		
		public void run()
		{
			try
			{
				InputStream is = s.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader (is));
				
				OutputStream os = s.getOutputStream();
				pw = new PrintWriter(os, true);
				
				String name = br.readLine();
				gc.setName(name);
				
				String msg = name + " Joined.";
				gc.broadCastMessage(msg,this);
				
				String clientMsg;
				
				do {
					clientMsg = br.readLine();
					msg = name + " : " + clientMsg;
					gc.broadCastMessage(msg, this);
				}while(!clientMsg.equals("exit"));
				
				 
				gc.removeUser(name, this);
				s.close();
				
				msg = name + " went OFFLINE.";
				gc.broadCastMessage(msg,this);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
			
		}
		
		void sendMessage(String msg) 
		{
			pw.println(msg);
		}
		
	}

	// main method of Group Chat Server.....
	public static void main(String[] args)
	{
		int port = Integer.parseInt(args[0]);
		GroupChat gc = new GroupChat(port);
		gc.createClientThread();
	}
	
	
}



