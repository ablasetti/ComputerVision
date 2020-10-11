import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;

import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MEDIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;

import static com.googlecode.javacv.cpp.opencv_objdetect.*;




import javax.swing.JPanel;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.VideoInputFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;



public class Face implements Runnable {
    final int INTERVAL = 100;// 1sec
    final int CAMERA_NUM = 0; // Default camera for this time

    /**
     * Correct the color range- it depends upon the object, camera quality,
     * environment.
     */
    static CvScalar rgba_min = cvScalar(0, 0, 130, 0);// RED wide dabur birko
    static CvScalar rgba_max = cvScalar(80, 80, 255, 0);
    

    
    IplImage image;
   // CanvasFrame canvas = new CanvasFrame("Web Cam Live");
  //  CanvasFrame path = new CanvasFrame("Detection");
  //  CanvasFrame canv2 = new CanvasFrame("thrimg");
  //  CanvasFrame canv3 = new CanvasFrame("contimg");
    CanvasFrame canv4 = new CanvasFrame("pyramid");
    
    int ii = 0;
    JPanel jp = new JPanel();

    public Face() {
      //  canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
  //      path.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
   //     path.setContentPane(jp);
    }

    @Override
    public void run() {
   
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(CAMERA_NUM);
        try {
            grabber.start();
            IplImage img;
  
            while (true) {
                img = grabber.grab();
                if (img != null) {
                    // show image on window
                  //  cvFlip(img, img, 1);// l-r = 90_degrees_steps_anti_clockwise
 
                    canv4.showImage(detectface(img));
 
                }
                 Thread.sleep(INTERVAL);
            }
        } catch (Exception e) {
        }
    }




 

  
    private IplImage detectface(IplImage orgImg) {
    	//IplImage ctimg = cvCreateImage(cvGetSize(orgImg), 8, 1);    	
    	IplImage grayImage = IplImage.create(orgImg.width(), orgImg.height(), IPL_DEPTH_8U, 1);

    	// We convert the original image to grayscale.
    	cvCvtColor(orgImg, grayImage, CV_BGR2GRAY);

    	CvMemStorage storage = CvMemStorage.create();
    	String CASCADE_FILE = "haarcascade_frontalface_alt.xml";
    	CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(CASCADE_FILE));
    	CvSeq faces = cvHaarDetectObjects(grayImage, cascade, storage, 1.1, 1, 0);
    	 
        //We iterate over the discovered faces and draw yellow rectangles around them.
        for (int i = 0; i < faces.total(); i++) {
          CvRect r = new CvRect(cvGetSeqElem(faces, i));
          cvRectangle(orgImg, cvPoint(r.x(), r.y()),
          cvPoint(r.x() + r.width(), r.y() + r.height()), CvScalar.YELLOW, 1, CV_AA, 0);
        }

    	return orgImg;
    }   
    

    public static void main(String[] args) {
        Face cot = new Face();
        Thread th = new Thread(cot);
        th.start();
    }


}