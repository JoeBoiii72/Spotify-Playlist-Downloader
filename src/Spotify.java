
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import org.json.JSONException;
import org.json.JSONObject;


public class Spotify {

          private static String username;
          private static String[] playlists;
          private static ArrayList<ArrayList<String>> playlist_tracks = new ArrayList<ArrayList<String>>();//[[playlistname,song1,song2],[playlistname,song1,song2]];
          private static String output;
          static private DataOutputStream soc_OStream;
          static private BufferedReader soc_IStream;
          private static String APIKEY = "NOLOOKING";
          
          static void init()
          {
                    try
                    {      
                    Socket soc=new Socket("localhost",5000);
                    soc_OStream = new DataOutputStream(soc.getOutputStream());
                    soc_IStream = new BufferedReader(new InputStreamReader(soc.getInputStream(), "UTF-8"));
                    App.display("\nConnection to python server successful!\n");
                    }
                    catch (IOException ex)
                    {
                              App.display("\nCouldn't connect to python server!");
                              App.display("\nPlease run server.py first...\nType /init to try again\n");
                    }
          }

          static void setUsername(String cmd)
          {
                    Spotify.username = cmd.substring(cmd.indexOf(" ")+1,cmd.length());
                    
                    try{
                              
                    soc_OStream.writeUTF(cmd);
                    output = soc_IStream.readLine();
                    App.display(output+"\n");
                    
                    }catch (IOException ex){}
                    
                    Spotify.updatePlaylists("/getplaylists");//updates playlists
          }

          static void updateTracks(String cmd)
          {
                    try{
                    
                    //create new playlist that will be added
                    ArrayList playlist = new ArrayList<>();
                    String playlistTitle = cmd.substring(cmd.indexOf(" ")+1,cmd.length());
                    playlist.add(playlistTitle);//add title first
                    
                    //send command and recieve output
                    soc_OStream.writeUTF(cmd);
                    output = soc_IStream.readLine();
                    
                    //split tracks and add them to our playlist
                    String[] playlistsongs = output.split(":::");

                    for(int i=0;i<playlistsongs.length;i++)
                    {
                              playlist.add(playlistsongs[i]);
                    }
                    
                    //update if already there
                    int update = -1;
                    for(int i=0;i<playlist_tracks.size();i++)
                    {
                              if(playlist_tracks.get(i).contains(playlistTitle)){update = i;break;}
                    }
                    if(update != -1){playlist_tracks.set(update,playlist);}//update it
                    else{playlist_tracks.add(playlist);}//just add it
                    
                    }catch (IOException ex){}
          }

          static void updatePlaylists(String cmd)
          {
                    try{
                              
                    soc_OStream.writeUTF(cmd);
                    output = soc_IStream.readLine();
                    playlists = output.split(":::");
                    
                    }catch (IOException ex){}
          }

          static void downloadPlaylist(String cmd) throws IOException, JSONException
          {
                    
                    ArrayList<String> song_urls = new ArrayList<>();
                    String playlistTitle = cmd.substring(cmd.indexOf(" ")+1,cmd.length());
                    
                    
                    for(int pl=0;pl<playlist_tracks.size();pl++)
                    {
                              if(playlist_tracks.get(pl).get(0).equals(playlistTitle))
                              {
                                        File dir = new File("src\\"+playlistTitle);
                                        dir.mkdir();
                                        for(int i=0;i<playlist_tracks.get(pl).size();i++)
                                        {
                                                  if(i != 0){ //element 0 is playlist title
                                                            try{
                                                            String song = playlist_tracks.get(pl).get(i);
                                                            song = song.replace(" ", "+");

                                                            String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=2&order=viewCount&q=" + song + "&key="+APIKEY;

                                                            URL link = new URL(url);
                                                            Scanner scan = new Scanner(link.openStream());
                                                            String str = new String();

                                                            //System.out.println(url);

                                                            while (scan.hasNext()){str += scan.nextLine();}
                                                            scan.close();
                                                            JSONObject obj = new JSONObject(str);

                                                            JSONObject res = obj.getJSONArray("items").getJSONObject(0);
                                                            String youtubeurl = "https://www.youtube.com/watch?v=" + res.getJSONObject("id").get("videoId");
                                                            
                                                            song_urls.add(youtubeurl);
                                                            
                                                            String path = "PATH TO DOWNLOAD";
                                                            String command = "youtube-dl -c -o "+path+playlistTitle+"\\%(title)s-%(id)s.%(ext)s " + youtubeurl;
                                                            System.out.println(command);
                                                            
                                                            Runtime run = Runtime.getRuntime();
                                                            Process p = run.exec(path+"\\"+command);
                                                            
                                                            p.waitFor();
                                                            
                                                            System.out.println(song_urls.get(i));}catch(Exception e){}
                                                  }
                                                  
                                        }
                                        break;
                              }
                    }                    
          
          }
          
          static public String[] getPlaylists()
          {
                    return Spotify.playlists;  
          }
          
          static public List<String> getTracks(String title)
          {
                    for(int i = 0;i<Spotify.playlist_tracks.size();i++)
                    {
                              if(Spotify.playlist_tracks.get(i).get(0).equals(title))
                              {
                                        return Spotify.playlist_tracks.get(i).subList(1,Spotify.playlist_tracks.get(i).size());
                              }
                    }
                    return null;
          }

          
}
