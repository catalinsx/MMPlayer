namespace audio_server
{
	using System;
	using System.Collections.Generic;
	using System.Net;
	using System.Net.Sockets;
	using System.Text;
	using System.Threading;

	public class TCPServer
	{
		private TcpListener listener;
		private List<ClientHandler> clients = new List<ClientHandler>();

		public event Action<string, string> DataReceived;


		public TCPServer()
		{
			IPAddress address = IPAddress.Any;
			listener = new TcpListener(address, 7777);
		}

		public void Start()
		{
			listener.Start();
			Console.WriteLine("Server started...");

			// Start accepting client connections asynchronously
			listener.BeginAcceptTcpClient(HandleIncomingConnection, null);
		}

		private void HandleIncomingConnection(IAsyncResult ar)
		{
			TcpClient client = listener.EndAcceptTcpClient(ar);

			// Create a new ClientHandler for this client
			ClientHandler handler = new ClientHandler(client);
			handler.DataReceived += HandleDataReceived;

			// Add the client to the list
			clients.Add(handler);

			Console.WriteLine("connection incoming: " + client.Client.RemoteEndPoint.ToString());


			// Start listening for data from this client
			handler.Start();

			// Accept the next client connection
			listener.BeginAcceptTcpClient(HandleIncomingConnection, null);
		}

		private void HandleDataReceived(string clientId, string data)
		{
			DataReceived?.Invoke(clientId, data);
		}


		public void Send(string clientId, string data)
		{
			var client = clients.Find(c => c.ClientId == clientId);
			if (client != null)
			{
				client.Send(data);
			}
			else
			{
				Console.WriteLine($"Client with ID {clientId} not found.");
			}
		}

		public void Stop()
		{
			foreach (var client in clients)
			{
				client.Disconnect();
			}
			clients.Clear();
			listener.Stop();
		}
	}

	public class ClientHandler
	{
		private TcpClient client;
		private NetworkStream stream;
		private Thread receiveThread;

		public string ClientId { get; private set; }
		public string ClientIp { get; private set; }

		public event Action<string, string> DataReceived;
		public event Action<string> ClientDisconnected;

		public ClientHandler(TcpClient tcpClient)
		{
			client = tcpClient;
			stream = client.GetStream();
			ClientId = Guid.NewGuid().ToString();
			ClientIp = client.Client.RemoteEndPoint.ToString();
		}

		public void Start()
		{
			receiveThread = new Thread(Receive);
			receiveThread.Start();
		}

		public void Send(string data)
		{
			byte[] buffer = Encoding.UTF8.GetBytes(data + "\r\n");
			try
			{
				stream.Write(buffer, 0, buffer.Length);
			}
			catch (Exception ex)
			{
				Console.WriteLine($"Error sending data: {ex.Message}");
			}
		}


		private void Receive()
		{
			byte[] buffer = new byte[8192];
			StringBuilder receivedData = new StringBuilder();
			string remainingData = string.Empty;

			while (client.Connected)
			{
				try
				{
					
					int bytesRead = stream.Read(buffer, 0, buffer.Length);
					if (bytesRead > 0)
					{
						string receivedText = remainingData + Encoding.UTF8.GetString(buffer, 0, bytesRead);
						
						// Split received text into messages based on "\r\n"
						string[] messages = receivedText.Split(new string[] { "\r\n" }, StringSplitOptions.None);

						// Process complete messages, excluding the last (potentially incomplete) message
						for (int i = 0; i < messages.Length - 1; i++)
						{
							DataReceived?.Invoke(ClientId, messages[i]);
						}

						// Store any incomplete message for the next iteration
						remainingData = messages[messages.Length - 1];

						Array.Clear(buffer, 0, buffer.Length);
					}
				}
				catch (Exception ex)
				{
					Console.WriteLine($"Error receiving data: {ex.Message}");
					Disconnect();
				}
			}

			Disconnect();
		}



		public void Disconnect()
		{
			try
			{
				stream.Close();
				client.Close();
				ClientDisconnected?.Invoke(ClientId);
			}
			catch (Exception ex)
			{
				Console.WriteLine($"Error disconnecting client: {ex.Message}");
			}
		}
	}

}
