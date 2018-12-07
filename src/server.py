import socket
import spotipy
from spotipy.oauth2 import SpotifyClientCredentials

clientID = "NOLOOKING"
clientSecret = "NOOLOOKING"

def spotifyAPI():
       client_credentials_manager = SpotifyClientCredentials(client_id=clientID, client_secret=clientSecret) 
       sp = spotipy.Spotify(client_credentials_manager=client_credentials_manager)
       return sp
            
def playlistnames(api,user):
       PLAYLISTNAMES = []
       playlists = api.user_playlists(user)
       for playlist in playlists['items']:
              if playlist['owner']['id'] == user:
                     PLAYLISTNAMES.append(playlist['name'])
       return PLAYLISTNAMES

def gettracksbyplaylistname(api,name,withartist,user):
       TRACKNAMES = []
       playlists = api.user_playlists(user)
       for playlist in playlists['items']:
              if playlist['owner']['id'] == user:
                     if playlist['name'] == name:
                            results = api.user_playlist(user, playlist['id'], fields="tracks,next")
                            tracks = results['tracks']
                            for i, item in enumerate(tracks['items']):
                                   track = item['track']
                                   if withartist:
                                          TRACKNAMES.append(track['artists'][0]['name']+" - "+track['name'])
                                   else:
                                          TRACKNAMES.append(track['name']) 
                            return TRACKNAMES
                     else: pass
       return None


def setup(address_,port_):
       soc = socket.socket()
       host = address_
       port = port_
       soc.bind((host, port))
       soc.listen(5)
       conn, addr = soc.accept()
       return conn

def main():
       api_ = spotifyAPI()
       stream = setup("localhost",5000)
       username = ""
       
       while True:
              data = stream.recv(1024)
              
              try:
                     data = data[2:].decode("utf-8")
                     print(data)
                     
                     if "/setuser" in data:
                            username = data[data.index(" ")+1:]
                            tosend = "Username set to " + username+"\n"
                            stream.send(tosend.encode("utf-8"))
                            
                     if "/getplaylists" in data:
                            playlists = playlistnames(api_,username)
                            tosend = ":::".join(playlists)+ "\n"
                            stream.send(tosend.encode("utf-8"))
                            
                     if "/gettracks" in data:
                            playlistName = data[data.index(" ")+1:]
                            tracks = gettracksbyplaylistname(api_,playlistName,True,username)
                            tosend = ":::".join(tracks)+ "\n"
                            stream.send(tosend.encode("utf-8"))
                            
              except Exception as e:
                     tosend = "ERROR\n"
                     stream.send(tosend.encode("utf-8"))
main()
              






       
