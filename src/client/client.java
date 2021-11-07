package client;

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class client {
	private String name;
	private int port;
	
	public client(int port)
	{
		this.port = port;
	}
	
	void setName(String name)
	{
		this.name = name;
	}
	
	String getName()
	{
		return this.name;
	}
	
	public void connectServer()
	{
		try 
		{
			Socket s = new Socket("localhost", port);
			System.out.println("Group Joined at " + port);
			
			
			readMessage rm = new readMessage(s, this);
			Thread t1 = new Thread(rm);
			t1.start();
			writeMessage wm = new writeMessage(s,this);
			Thread t2 = new Thread(wm);
			t2.start();
		
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	
	// For reading message from server.
	public class readMessage implements Runnable
	{
		String msg;
		BufferedReader br1;
		client c;
		Socket s;
		
		public readMessage(Socket s, client c)
		{
			this.c = c;
			this.s = s;
			try
			{
				InputStream is = s.getInputStream();
				br1 = new BufferedReader(new InputStreamReader(is));
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
		
		public void run()
		{
			while(true)
			{
				try
				{
					
					msg = br1.readLine();
					System.out.println("\n" + msg);
					
					if(c.getName() != null)
					{
						System.out.print(c.getName() + " : ");
					}
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
					break;
				}
			}
		}
		
	}
	
	
	// For writing message to server.
	public class writeMessage implements Runnable
	{
		client c;
		PrintWriter pw;
		Socket s;
		String word;
		
		public writeMessage(Socket s, client c)
		{
			this.c = c;
			this.s = s;
			try
			{
				OutputStream os = s.getOutputStream();
				pw = new PrintWriter(os, true);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
		
		public void run()
		{
			System.out.println("Enter your Name: ");
			Scanner input = new Scanner(System.in);
			String clientName = input.nextLine();
			c.setName(clientName);
			pw.println(clientName);
			
			do {
				System.out.print(clientName + " : ");
				word = input.nextLine();
				pw.println(word);
			}while(!word.equals("exit"));
			
			try
			{
				input.close();
				s.close();
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
	}
	
		
	//Main method of client class
	public static void main(String[] args)
	{
		
		int port = Integer.parseInt(args[0]);
		client c = new client(port);
		c.connectServer();
	}
}
