
import eu.anorien.mhl.Fact;
import eu.anorien.mhl.lisbonimpl.g;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import tracker.TargetFact;
import tracker.Tracker;


/**
 *
 * @author David Miguel Antunes <davidmiguel [ at ] antunes.net>
 */

/* To add a new target, we have to follow this steps : 
 * 
 * -1- Choose an ID of vehicule (ex ID = 112) (examples : "/root/Documents/sumo/real_trace/Rome/Taxi-2014-02-03.txt"; )
 * -2- add this code in the main function :   
 * 		 String [][] DataSet_v_ID = uploadDataSet("ID", "2014-02-03") ; 
 *       int len_ID = nb_Position ; 
 *       double [][] x_y_z_vID =  convDataSet2coor(DataSet_v_ID); 
 * -3- Add this code : 
 *     else if (j==n)
 *                {
 *           		
 *               	System.out.println( "x_y_z_vID["+count+"][0]" + x_y_z_vID[count][0] + ";"+ "x_y_z_vID["+count+"][1]" + x_y_z_vID[count][1]);
 *           		target.update(x_y_z_vID[count][0] - 200, 650+ x_y_z_vID[count][1]); 
 *                }
 *      J++ in the previous instuction 
 * -4- Change the targets number in this code : 
 * 
 * for (int i = 0; i < 3; i++) {
 *          // targets.add(new Target(i, x_y_z[0][0], x_y_z[0][1]));
 *          targets.add(new Target(i, 100, 380));
 *       }
 *       
 *    //   3 => n 
 * -5- xxx
 * -6- xxx
 * 
 * */


public class Main {
	
	static int  nb_Position = 0  ; 
	public static double[] WGS84toOSBG36(double radWGlat, double radWGlon, double Height)
    {

        double [] tab = new double [3] ;  
		double deg2rad = Math.PI / 180;
        double rad2deg = 180.0 / Math.PI;
        //first off convert to radians
        radWGlat = radWGlat * deg2rad;
        radWGlon = radWGlon * deg2rad;

        //these are the values for WGS86(GRS80) to OSGB36(Airy)
        double a = 6378137;              // WGS84_AXIS
        double e = 0.00669438037928458;  // WGS84_ECCENTRIC
        double h = Height;               // height above datum  (likely to be defaulted to 0 on entry)
        double a2 = 6377563.396;         // OSGB_AXIS
        double e2 = 0.0066705397616;     // OSGB_ECCENTRIC 

        double xp = -446.448;
        double yp = 125.157;
        double zp = -542.06;
        double xr = -0.1502;
        double yr = -0.247;
        double zr = -0.8421;
        double s = 20.4894;

        // convert to cartesian; lat, lon are in radians
        double sf = s * 0.000001;
        double v = a / (Math.sqrt(1 - (e * (Math.sin(radWGlat) * Math.sin(radWGlat)))));
        double x = (v + h) * Math.cos(radWGlat) * Math.cos(radWGlon);
        double y = (v + h) * Math.cos(radWGlat) * Math.sin(radWGlon);
        double z = ((1 - e) * v + h) * Math.sin(radWGlat);

        // transform cartesian
        double xrot = (xr / 3600) * deg2rad;
        double yrot = (yr / 3600) * deg2rad;
        double zrot = (zr / 3600) * deg2rad;
        double hx = x + (x * sf) - (y * zrot) + (z * yrot) + xp;
        double hy = (x * zrot) + y + (y * sf) - (z * xrot) + yp;
        double hz = (-1 * x * yrot) + (y * xrot) + z + (z * sf) + zp;

      /* Console.WriteLine(xrot);
        Console.WriteLine(yrot);
        Console.WriteLine(zrot);*/
     //   Console.WriteLine("hx, hy, hz");
     //   Console.WriteLine(hx);
     //   Console.WriteLine(hy);
     //   Console.WriteLine(hz);
        
        tab[0] = hx*Math.pow(10, -1) - 463700 ; 
        tab[1] = hy*Math.pow(10, -1) - 103300 ;  
        tab[2] = hz*Math.pow(10, -1) ;  
      //  return new LatLon(hx, hy);
        return  tab ;         

    }
	
	public static String[][] uploadDataSet(String ID, String Date) throws FileNotFoundException
	{
        String DataSet[][] = new String[2100][7];
       
        // Depend on DAte input; we have to select the right file 
    
     //For test    
    //   String filePath = "/root/Documents/sumo/real_trace/Rome/Test/Test/Taxi-2014-02-01.txt";
       String filePath = "/root/Documents/sumo/real_trace/Rome/Taxi-2014-02-03.txt"; 
   //     String filePath = "/root/Documents/sumo/real_trace/Rome/outputs/2014-02-01.txt"; 
   
        
        Scanner scanner= new Scanner(new File(filePath));
		String AllLine = "";
		String sID = "";
		String sDATE = "";
		String sX = "";
		String sY = "";
		String sV = "";
		String sH = "";
		String sA = "";
		//String[] sXY = new String[2] ;
		
		int i = 0 ; 
		nb_Position = 0 ; 
		System.out.println(" Starting the scan..."); 
		// On boucle sur chaque champ detecté
		while (scanner.hasNextLine()) {
		    String line = scanner.nextLine();
		     
		   
		    String[] datas = line.split(";");

		    if (datas.length < 3) // datas.length < 7)
		    {
		        System.err.println("Line with bad format : "+line);
		        continue;
		    }
			
		    if (datas[0].contains(ID)){
		    //	ID ; Date & Time ; X ; Y ; V ; H ; Acc
		    
		    nb_Position ++ ; 
		    System.out.println("nb_Position=" + nb_Position); 
		    sID = datas[0];
		     sDATE = datas[1];
		   
		     sX = datas[2].substring(6).split(" ")[0];
		     sY = datas[2].substring(6,datas[2].length()-1).split(" ")[1];
		     
		     System.out.println("Id =" + sID); 
		     System.out.println("sDATE =" + sDATE); 
		     System.out.println("sX =" + sX); 
		     System.out.println("sY =" + sY); 
		     
		 /*  
		     sX = datas[2];
		     sY = datas[3];
		     sV = datas[4];
		     sH = datas[5];
		     sA = datas[6];
		     
		     */
		 //   Double.parseDouble(datas[0])
		 
		     DataSet[i][0] = sID;
		     DataSet[i][1] = sDATE;
     	     DataSet[i][2] = sX;
     	     DataSet[i][3] = sY;
     	     
     	    DataSet[i][4] = "";
		    DataSet[i][5] = "";
		    DataSet[i][6] = "";
		 /*    
		    DataSet[i][0] = datas[0];
		    DataSet[i][1] = datas[1];
		    DataSet[i][2] = datas[2];
		    DataSet[i][3] = datas[3];
		    DataSet[i][4] = datas[4];
		    DataSet[i][5] = datas[5];
		    DataSet[i][6] = datas[6];
   */
		//    System.out.println( DataSet[i][1]);
		 
		    i++ ; 
		   
		    }
		    
		}
		 
		//nb_Position = i -1 ; 
		scanner.close();
		return DataSet;
		
	}
   
	public static double[][] convDataSet2coor(String[][] s){
		 double [][] x_y_z = new double [3000][3]; 
		
		 double [] x_y_coord = WGS84toOSBG36(Double.parseDouble(s [0][2]), Double.parseDouble(s [0][3]),29 ) ; 
	        
	      System.out.println(" s [0][2] =" +  s [0][2]); 
	      System.out.println(" s [0][3] =" +  s [0][3]);     
	       // System.out.println( s [0][2]);
	       // System.out.println( s [0][3]);
	        
	     //   DecimalFormat df = new DecimalFormat("########.00"); 
	 /*       System.out.println(df.format(x_y_coord[0]));
	        System.out.println(df.format(x_y_coord[1]));
	        System.out.println(df.format(x_y_coord[2]));
	 */       
	        double len = nb_Position - 1 ; // s.length ;  // 1735 
	        double max_lon = x_y_coord[0] ; 
	        double min_lon = x_y_coord[0] ; 
	        double max_lat = x_y_coord[1] ; 
	        double min_lat = x_y_coord[1] ;  
	        
	       
	        for (int i=0;i<len;i++)
	        {
	        	x_y_z[i] = WGS84toOSBG36(Double.parseDouble(s [i][2]), Double.parseDouble(s [i][3]),29 ) ;
	        	
	        	if (x_y_z[i][0] > max_lon) { max_lon = x_y_z[i][0];}
	        	if (x_y_z[i][0] < min_lon) { min_lon = x_y_z[i][0];}
	        	if (x_y_z[i][1] > max_lat) { max_lat = x_y_z[i][1];}
	        	if (x_y_z[i][1] < min_lat) { min_lat = x_y_z[i][1];}
	        	
	        }
	   /*     System.out.println("max_lon = "+ max_lon); 
	        System.out.println("min_lon = "+ min_lon); 
	        System.out.println("max_lat = "+ max_lon); 
	        System.out.println("min_lat = "+ min_lat); 
		
		*/
		return x_y_z;
		}
	
	/**
     * @param args the command line arguments
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws InterruptedException, InvocationTargetException, FileNotFoundException {
        final Object lock = new Object();
        int count =1 ;  
        String [][] DataSet_v_1 = uploadDataSet("156", "2014-02-03") ; 
        int len_1 = nb_Position ; 
        double [][] x_y_z_v1 =  convDataSet2coor(DataSet_v_1); 
        
        String [][] DataSet_v_2 = uploadDataSet("169", "2014-02-03") ; 
        int len_2 = nb_Position ; 
        double [][] x_y_z_v2 =  convDataSet2coor(DataSet_v_2); 
        
        String [][] DataSet_v_3 = uploadDataSet("202", "2014-02-03") ; 
        int len_3 = nb_Position ; 
        double [][] x_y_z_v3 =  convDataSet2coor(DataSet_v_3); 
        
        
        String [][] DataSet_v_199 = uploadDataSet("199", "2014-02-03") ; 
        int len_199 = nb_Position ; 
        double [][] x_y_z_v199 =  convDataSet2coor(DataSet_v_199); 
        
    //    double [][] x_y_z = new double [1735][3]; 
        
      //  System.out.println( DataSet_v_1 [0][3]);
        
        // Appeler une fonction de projection !!! 
        
         
       
        
        //    x,y =  projection (DataSet_v_1 [0][2], DataSet_v_1 [0][3])
        
        final Tracker tracker = new Tracker(6, 6, 2, 10, 0.001, 0.01, 0.1);
        final List<Target> targets = new ArrayList<Target>();
        for (int i = 0; i < 4; i++) { // 4 OR N : Targets number
           // targets.add(new Target(i, x_y_z[0][0], x_y_z[0][1]));
            targets.add(new Target(i, 100, 380));
        }
        final List<Point2D> noiseMeasurements = new ArrayList<Point2D>();
        final List<Point2D> correctMeasurements = new ArrayList<Point2D>();

        final JFrame groundTruthFrame = new JFrame("Ground truth") {

            @Override
            public void paint(Graphics g) {
                synchronized (lock) {
                    g.clearRect(0, 0, 800, 800);
                    for (Target target : targets) {
                        g.fillOval((int) target.getX() - 2, (int) target.getY() - 2, 4, 4);
                    }
                }
                try {

                	 Image img = ImageIO.read(new File("index.png")); 
                     g.drawImage(img, 0, 0, this);

                    //Pour une image de fond

                    //g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);

                  }catch (IOException e) {

                      e.printStackTrace();

                    } 
               
            }
        };
        
        

         

            //Pour une image de fond

            //g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);

         
        
        
        groundTruthFrame.setSize(800, 800);
        
     //   groundTruthFrame.setBackground(Color.ORANGE);
        groundTruthFrame.setResizable(false);
        groundTruthFrame.setVisible(true);
        groundTruthFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JFrame measurementsFrame = new JFrame("Measurements") {

            @Override
            public void paint(Graphics g) {
                synchronized (lock) {
                    g.clearRect(0, 0, 800, 800);
                    g.setColor(Color.red);
                    for (Point2D point2D : noiseMeasurements) {
                        g.fillOval((int) point2D.getX() - 2, (int) point2D.getY() - 2, 4, 4);
                    }
                    g.setColor(Color.green);
                    for (Point2D point2D : correctMeasurements) {
                        g.fillOval((int) point2D.getX() - 2, (int) point2D.getY() - 2, 4, 4);
                    }
                }
            }
        };
        measurementsFrame.setSize(800, 800);
        measurementsFrame.setResizable(false);
        measurementsFrame.setVisible(true);
        measurementsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JFrame trackerFrame = new JFrame("Tracker") {

            @Override
            public void paint(Graphics g) {
                synchronized (lock) {
                    g.clearRect(0, 0,800, 800);
                    g.setColor(Color.red);
                    for (Fact fact : tracker.getBestHypothesis().getFacts().keySet()) {
                        TargetFact target = (TargetFact) fact;
                        g.fillRect((int) target.getX() - 4, (int) target.getY() - 4, 4, 8);
                    }
                    g.setColor(Color.green);
                    for (Target target : targets) {
                        g.fillOval((int) target.getX(), (int) target.getY() - 4, 4, 8);
                    }
                }
            }
        };
        trackerFrame.setSize(800, 800); // (400, 400);
        trackerFrame.setResizable(false);
        trackerFrame.setVisible(true);
        trackerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int j = 0 ; 
        while (count < 2000) {   //  while (true) {
            synchronized (lock) {
               j = 0 ; 
            	for (Target target : targets) {
                 if (j==0)
                 {         	// System.out.println("j= 0") ;      	
                	target.update(400 + x_y_z_v1[count][0], 380 + x_y_z_v1[count][1]);j++ ;  // +100 +380
                 }else if (j==1)
                 {
                	// System.out.println("j= 1") ; 
            	//	System.out.println( "x_y_z_v2["+count+"][0]" + x_y_z_v2[count][0] + ";"+ "x_y_z_v2["+count+"][1]" + x_y_z_v2[count][1]);
            		target.update(x_y_z_v2[count][0] - 400, 850+ x_y_z_v2[count][1]); j++ ; 
                 }else if (j==2)
                 {
                	// System.out.println("j= 2") ; 
               // 	System.out.println( "x_y_z_v3["+count+"][0]" + x_y_z_v3[count][0] + ";"+ "x_y_z_v3["+count+"][1]" + x_y_z_v3[count][1]);
            		target.update(x_y_z_v3[count][0] - 200, 650+ x_y_z_v3[count][1]);j++; 
                 }
                 else if (j==3)
                 {
                	// System.out.println("j= 3") ; 
                	System.out.println( "x_y_z_v199["+count+"][0]" + x_y_z_v199[count][0] + ";"+ "x_y_z_v199["+count+"][1]" + x_y_z_v199[count][1]);
            		target.update(x_y_z_v199[count][0] - 200, 700+ x_y_z_v199[count][1]); 
                 }
                //	target.update(200, 200);
                //   System.out.print("x, y = "+ x_y_z[count][0]+" " + x_y_z[count][1]); 
                    
                }
            	count ++; 
                noiseMeasurements.clear();
                correctMeasurements.clear();
                for (int i = 0; i < 80; i++) {
                    noiseMeasurements.add(new Point2D.Double(Math.random() * 400, Math.random() * 400));
                }
                for (Target target : targets) {
                    if (Math.random() < 0.95) {
                        correctMeasurements.add(new Point2D.Double(target.getX(), target.getY()));
                    }
                }
                List<Point2D> finalMeasurements = new ArrayList<Point2D>();
                finalMeasurements.addAll(noiseMeasurements);
                finalMeasurements.addAll(correctMeasurements);
                tracker.newScan(finalMeasurements);
            }

            EventQueue.invokeAndWait(new Runnable() {

                public void run() {
                    groundTruthFrame.repaint();
                    measurementsFrame.repaint();
                    trackerFrame.repaint();
                }
            });

            Thread.sleep(100);
        }

    }
}
