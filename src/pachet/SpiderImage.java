package pachet;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.List;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.Object;
//import javax.lang.model.util.Elements;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
//import javax.swing.text.Document;
//import javax.swing.text.Element;
import javax.swing.Timer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
public class SpiderImage extends JFrame{
	 JLabel label;//label-ul in care se vor afisa imaginile
	 String[] imagesList=new String[200];//String-ul ce va contine adresele absolute ale imaginilor din folder
	 Timer tm;//timer-ul ce creaza slide-show-ul
	 final int counter = 0;//numarul total de adrese de imagini din folder
	 int poz=0;
	 private static final String folderPath="C://WebsitesImages/";//calea catre folderul in care se vor salva imaginile
	 
	public void Downloader()
	{
		//se creaza interfata grafica principala ce contine textfieldu-l pentru scrierea linkurilor si 3 butoane
		JFrame mainFrame= new JFrame("Spider APP");
		mainFrame.setLayout(new BorderLayout());
		//mainFrame.setSize(1080,640);
	
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setSize(600,600);
		
		JButton downloadBtn= new JButton("Download");
		JButton showImages=new JButton("Show Images");
		JButton abandon=new JButton("Abandon");
		JTextArea linkArea=new JTextArea();
		//linkArea.setSize(360,480);
		//mainFrame.add(linkArea,BorderLayout.CENTER);
		panel.add(linkArea,BorderLayout.CENTER);
		JPanel panel1=new JPanel();
		panel1.setLayout(new GridLayout(1,3));
		panel1.add(downloadBtn);
		panel1.add(showImages);
		panel1.add(abandon);
		//mainFrame.add(panel,BorderLayout.SOUTH);
		panel.add(panel1,BorderLayout.SOUTH);
		setContentPane(panel);
		
		mainFrame.getContentPane().add(panel, BorderLayout.CENTER);
		
		
		//se creaza actiunea pentru butonul de Download
		downloadBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				String urlText=linkArea.getText();//se preia textul din zona de text de pe interfata
				BufferedReader strIn=new BufferedReader(new StringReader(urlText));//se creaza un buffer pentru preluarea randurilor din zona text
				Vector<URL> urlVector=new Vector<URL>();//se creaza un vector de URL-uri
				
				try{
					
					String lin=strIn.readLine();//se citeste fiecare linie din zona text si se introduce in vectorul de URL-uri
					while(lin!=null)
					{
						urlVector.add(new URL(lin));
						lin=strIn.readLine();
					}
					strIn.close();
					
					File f=new File(folderPath);//se creaza un obiect de tip FILE pentru calea catre folderul de download
					//daca folderul specificat nu exista acesta se va crea
					if(!f.exists())
						f.mkdir();
					//se parcurge vectorul de linkuri pentru a se downloada imaginile de pe fiecare in parte
					for(URL urlelement : urlVector)
					{
							//se conecteaza la site-ul cu URL=urlelement
							Document doc = Jsoup.connect(urlelement.toString()).timeout(5000).get();
							Elements img = doc.getElementsByTag("img");//filtreaza imaginile dupa tag-ul img
							//colectia de imagini este salvata in img
							for (Element el : img) {
					           
									String src = el.absUrl("src");//se preia adresa absoluta a fiecarei imagini
									getImages(src);//se descarca fiecare imagine pe rand
									try
									{
										Thread.sleep(1000);//se seteaza un deelay de o secunda la download pentru a nu fi blocati de securitate
									}catch(InterruptedException ie)
									{
									 
									}
					            }
					}
				}catch(IOException ex)
				{
					System.err.println("There was an error");
					Logger.getLogger(SpiderImage.class.getName()).log(Level.SEVERE, null, ex);
				}
				
			}});
		showImages.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				//se creaza interfata grafica ce contine label-ul pentru afisarea imaginilor si butonul pentru abandon
				JFrame frame=new JFrame("SlideShow");
				frame.setLayout(null);
				//frame.setSize(1080,640);
				JButton button = new JButton("Quit");
				button.setBounds(315,400,100,50);
			    JLabel label = new JLabel("Label is here!");
			    label.setBounds(40,30,640,360);
			    frame.add(label,BorderLayout.NORTH);
				frame.add(button,BorderLayout.SOUTH);
			   
			    int i=0;
			    //se cauta adresa absoluta a imaginilor din folder si se adauga la String-ul imagesList
			    File path = new File(folderPath);
			    File [] files = path.listFiles(new ImageFileFilter());
			    for (File file : files){
		            if (file.isFile()){
		            	 imagesList[i]=file.getAbsolutePath().toString();
		            	 System.out.println(imagesList[i]);
		            	 i=i+1;
		            }}
			    
			    int counter = 0;
			    //se obtine numarul total al imaginilor din String
			    for (int j = 0; j < imagesList.length; j++)
			        if (imagesList[j] != null)
			            counter ++;
			    System.out.println(counter);
			    
			    final Integer counts = new Integer(counter);
		        //se creaza actiunea pentru timer-ul care afiseaza imaginile
			    tm=new Timer(2000,new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						//se redimenzioneaza fiecare imagine dupa dimensiunile labelului in care vor fi afisate
						ImageIcon newimg=new ImageIcon(imagesList[poz]);
				        Image img= newimg.getImage();
				        Image image = img.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
				        ImageIcon fin=new ImageIcon(image);
				        label.setIcon(fin);//se afiseaza imaginea in label
						poz += 1;//se trece la pozitia urmatoare in Stringul cu imagini
						if(poz >= counts)//daca s-a ajuns la sfarsitul Stringului se va relua de la inceput derularea imaginilor
							poz=0;
						
					}});
			    
				//se creaza actiunea pentru butonul de abandon care va contine o casuta de dialog cu 3 optiuni
		        button.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						JFrame window=new JFrame();
						int result = JOptionPane.showConfirmDialog(window,
						        "Are you sure you want to quit?",
						        "Confirm Quit", JOptionPane.YES_NO_CANCEL_OPTION);
						if (result == JOptionPane.YES_OPTION) System.exit(0);
					}});
			    
			    	tm.start();//se porneste timer-ul pentru derularea pozelor in slideshow
			    	
			    	frame.pack();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
			
		});
		
		//se creaza actiunea pentru butonul de abandon care va contine o casuta de dialog cu 3 optiuni
		abandon.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JFrame window=new JFrame();
				int result = JOptionPane.showConfirmDialog(window,
				        "Are you sure you want to quit?",
				        "Confirm Quit", JOptionPane.YES_NO_CANCEL_OPTION);
				if (result == JOptionPane.YES_OPTION) System.exit(0);
			}});	
		
		mainFrame.setLocationRelativeTo(null);
		mainFrame.pack();
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private static void getImages(String src)throws IOException
	{
		//extrage indexul de la care incepe numele imaginii din stringul src
		int indexname = src.lastIndexOf("/");
		
		 if (indexname == src.length())
		 {
			 src = src.substring(1, indexname);
		}
		indexname = src.lastIndexOf("/");
		//in variabila name se salveaza numele imaginii
		String name = src.substring(indexname, src.length());
		
		//deschide o conexiune url catre imaginea cu adresa src
		URL url = new URL(src);
		BufferedInputStream in = new BufferedInputStream(url.openStream());
		//se declara buffer-ul ce contine calea catre folderul unde se vor salva imaginiile
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(folderPath+name));
		//se parcurge buffer-ul cu imagini cat timp mai exista imagini in el si se descarca in folder
		for (int b; (b = in.read()) != -1;) {
		out.write(b);
		}
		out.close();
		in.close();	
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		SwingUtilities.invokeLater(new Runnable(){
	        public void run() {
	            SpiderImage yea=new SpiderImage();
	            yea.Downloader();
	            //apelarea metodei principale
	        }
	});

}
}
