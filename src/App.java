import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import org.json.JSONException;


public class App extends JFrame implements ActionListener{
          
          private static final Scanner reader = new Scanner(System.in);
          private static String input;  //current input
          private static String output; //current output
          private static int WIDTH_;
          private static int HEIGHT_;
          JTextField usernameBox;
          JComboBox playlistBox;
          JButton loaduserBtn;
          JButton loadsongsBtn;
          JButton downloadBtn;
          JList songslistbox;
          JScrollPane scroll;
          JPanel p;
          DefaultListModel<String> currentSongs = new DefaultListModel<>();
          
          
          public App(int width,int height)
          {
                    this.WIDTH_ = width;
                    this.HEIGHT_ = height;
                    Spotify.init();
                    
                    super.setPreferredSize(new Dimension(WIDTH_, HEIGHT_));
                    super.setTitle("Spotify Client v0.1.1");
                    super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    super.setResizable(false);
                    super.setVisible(true);
                    super.setFocusable(true);
                    
                    p = new JPanel();
                    
                    super.getContentPane().add(p);
                    p.setBackground(new Color(100,100,100));
                    p.setLayout(null);
                    
                    usernameBox = new JTextField(10);
                    usernameBox.setBounds(100, 0, WIDTH_-100, 50);
                    p.add(usernameBox);
                    
                    loaduserBtn = new JButton("Load");
                    loaduserBtn.addActionListener(this);
                    loaduserBtn.setBounds(0, 0, 100, 50);
                    p.add(loaduserBtn);
                    
                    loadsongsBtn = new JButton("Songs");
                    loadsongsBtn.addActionListener(this);
                    loadsongsBtn.setBounds(WIDTH_-105, 50, 100, 50);
                    p.add(loadsongsBtn);
                    
                    downloadBtn = new JButton("Download");
                    downloadBtn.addActionListener(this);
                    downloadBtn.setBounds(0, 200, WIDTH_, 50);
                    p.add(downloadBtn);
                    
                    playlistBox = new JComboBox();
                    playlistBox.setBounds(0, 50, WIDTH_-105, 50);
                    p.add(playlistBox);
                    
                    songslistbox = new JList(currentSongs);
                    songslistbox.setBounds(0, 100, 100, 100);
                    
                    
                    scroll = new JScrollPane(songslistbox);
                    scroll.setBounds (0, 100, WIDTH_-5, 100);
                    p.add(scroll);
                    super.pack();
          }
        

          public static void main(String[] args) throws SocketException, IOException 
          {     
                    Runtime run = Runtime.getRuntime();
                    Process p = run.exec("runserver.bat");
                    App app = new App(400,350);
          }
          
          public static void display(String s){System.out.print("\n"+s);}

          @Override
          public void actionPerformed(ActionEvent e)
          {
                    switch(e.getActionCommand())
                    {
                              case"Load":LOAD();break;
                              case"Songs":SONGS();break;
                              case"Download":try {DOWNLOAD();}catch (Exception ex){System.out.println(ex);}break;

                    }
          }

          private void LOAD()
          {
                    Spotify.setUsername("/setuser "+usernameBox.getText());//set username
                    String[] playlists = Spotify.getPlaylists();  //get playlists
                    
                    p.remove(playlistBox);
                    playlistBox = new JComboBox();
                    playlistBox.setBounds(0, 50, WIDTH_-105, 50);
                    p.add(playlistBox);
                    
                    p.repaint();
                    super.pack();
                    
                    for(int i = 0;i < playlists.length;i++)
                    {
                              playlistBox.addItem(playlists[i]);
                    }
          }

          private void SONGS()
          {
                    String playlistTitle = (String)playlistBox.getSelectedItem();
                    Spotify.updateTracks("/gettracks " + playlistTitle);
                    
                    currentSongs = new DefaultListModel<>();
                    
                    List<String> tracks = Spotify.getTracks(playlistTitle);
                    for(int i = 0;i<tracks.size();i++)
                    {
                            currentSongs.addElement(tracks.get(i));
                    }
                    
                    p.remove(songslistbox);
                    p.remove(scroll);
                    
                    songslistbox = new JList(currentSongs);
                    songslistbox.setBounds(0, 100, 100, 100);
                    
                    scroll = new JScrollPane(songslistbox);
                    scroll.setBounds(0, 100, WIDTH_-5, 100);
                    
                    p.add(scroll);
                    
                    p.repaint();
                    super.pack();
          }

          private void DOWNLOAD() throws IOException, JSONException
          {
                  String playlistTitle = (String)playlistBox.getSelectedItem();
                  Spotify.downloadPlaylist("/download " + playlistTitle);
          }
    
}
