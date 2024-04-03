using System.Net.Sockets;
using System.Net;
using System.Text;
using TagLib;

namespace audio_server
{
	internal class Program
	{
        static TCPServer tcpServer = new TCPServer();
		static void Main(string[] args)
		{
			


			tcpServer.Start();
			tcpServer.DataReceived += TcpServer_DataReceived;
			Console.WriteLine("Press any key to stop the server...");
			Console.ReadLine();
			

			tcpServer.Stop();
		}

		private static void TcpServer_DataReceived(string clientId, string data)
		{
			data = data.Replace("\r", "").Replace("\n", "");

			if (data.StartsWith("<PLAY>"))
			{
				// extragem numele piesei din mesaju trimis de client, scoatem <play> si <eos>
				string songName = data.Replace("<PLAY>", "").Replace("<EOS>", "");
				Console.WriteLine($"Sending song: {songName}");

				//  mergem in folderu songs si luam piesa respectiva si-o trasformam intr un byte array
				string filePath = @$"songs\{songName}.mp3";
				byte[] fileBytes = System.IO.File.ReadAllBytes(filePath);

				// encodam arrayu intr un string base64 pe care ulterior il trimitem la client si clientru
				// o sa fac pasii opusi, primeste stringu encodat, il face byte array, il face fisier si dupa ii da play
				string base64String = Convert.ToBase64String(fileBytes);

				// trimitem stringu in chunkuri de cate 8096 bytes, trimitem la inceput <PLAY> si la final \r\n, ca sa detectam in client datele
				int chunkSize = 1048576;
				int bytesSent = 0;
				while (bytesSent < base64String.Length)
				{
					int remainingBytes = base64String.Length - bytesSent;
					int bytesToSend = Math.Min(remainingBytes, chunkSize);
					string chunk = "<PLAY>" + base64String.Substring(bytesSent, bytesToSend) + "\r\n";
					byte[] buffer = Encoding.UTF8.GetBytes(chunk);
                    tcpServer.Send(clientId, chunk);
					bytesSent += bytesToSend;
				}

                // la final trimitem <PLAY><EOS> ca clientu sa stie ca toate chunkurile au venit si poate face urmatorii pasi
                tcpServer.Send(clientId, "<PLAY><EOS>");
			}
			if (data.StartsWith("<LIST>")) //  luam toate fisierele din folderu songs, facem un string din ele, eg. Killa - Fetele palide;Nicolae Guta- Campion;
			{                                // si in client cand le primim punem un Split(";") si le putem adauga intr o lista sau ceva
				string strExePath = System.Reflection.Assembly.GetExecutingAssembly().Location;
				string strPath = Path.GetDirectoryName(strExePath) + @"\songs";

				string songs = "";
                if (Directory.Exists(strPath))
                {
                    string[] songFiles = Directory.GetFiles(strPath);
                    foreach (string songFile in songFiles)
                    {
                        string eyo = Path.GetFileName(songFile).Replace(".mp3", "");
                        string title = "";
                        string artist = "";
                        string album = "";
                        string genre = "";

                        try
                        {
                            var tagFile = TagLib.File.Create(songFile);
                            title = tagFile.Tag.Title ?? "";
                            artist = tagFile.Tag.FirstPerformer ?? "";
                            album = tagFile.Tag.Album ?? "";
                            genre = tagFile.Tag.FirstGenre ?? "";
                        }
                        catch (Exception ex)
                        {
                            Console.WriteLine($"Error getting metadata for {songFile}: {ex.Message}");
                        }

                        songs += $"{eyo}##{title}##{artist}##{album}##{genre};";
                    }
                    songs = songs.Substring(0, songs.Length - 1);
                    Console.WriteLine($"<LIST>{songs}<EOS>");
                    tcpServer.Send(clientId, $"<LIST>{songs}<EOS>");
                }
            }
		}

    }
}