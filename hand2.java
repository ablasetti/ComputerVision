import java.awt.Point;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.CV_WHOLE_SEQ;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvDrawContours;
import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvInRangeS;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_calib3d.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

public class hand2 implements Runnable {

	final int INTERVAL = 1000;// 1sec
	final int CAMERA_NUM = 0; // Default camera for this time

	/**
	 * Correct the color range- it depends upon the object, camera quality,
	 * environment.
	 */

	IplImage image;
	CanvasFrame canvas = new CanvasFrame("hand");
	CanvasFrame canvas2 = new CanvasFrame("skin");

	// costruttore...
	private static void hand2() {


	}




	public void run() {

		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(CAMERA_NUM);
		try {
			grabber.start();
			IplImage img;

			while (true) {
				img = grabber.grab();
				if (img != null) {
					// show image on window
					cvFlip(img, img, 1);// l-r = 90_degrees_steps_anti_clockwise

					IplImage skin = skin_detect(img);
					
					canvas2.showImage(skin);
					canvas.showImage(gethand(img,skin));

				}
			}
		} catch (Exception e) {
		}


	}



	private IplImage skin_detect(IplImage img) {   
		 //CvScalar rgba_min = cvScalar(0, 0, 130, 0);// RED wide dabur birko
		 //CvScalar rgba_max = cvScalar(80, 80, 255, 0);

		 CvScalar rgba_min = cvScalar(0, 0, 0, 0);// RED wide dabur birko
		 CvScalar rgba_max = cvScalar(80, 80, 220, 0);
		 
		IplImage img2 = cvCreateImage(cvGetSize(img), 8, 1);
		
		//cvCvtColor(img, img2, CV_BGR2GRAY);   	
		//cvSmooth(img2, img2, CV_MEDIAN ,7);
		//cvCanny(img2,img2,100,100,3);
		//cvDilate(ctimg, ctimg, null, 3);
		
		cvInRangeS(img, rgba_min, rgba_max, img2);// red
		cvMorphologyEx(img2, img2, null, null, CV_MOP_OPEN, 1);

		return img2;
	}		


	/*
	 private CvSeq findBiggestContour(IplImage imgThreshed)
	  // return the largest contour in the threshold image
	  { 
	    CvSeq bigContour = null;
	    CvMemStorage contourStorage = CvMemStorage.create();
	    // generate all the contours in the threshold image as a list
	    CvSeq contours = new CvSeq(null);
	    cvFindContours(imgThreshed, contourStorage, contours, Loader.sizeof(CvContour.class),
	                                                       CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);

	    // find the largest contour in the list based on bounded box size
	    float maxArea = 600.0f;
	    CvBox2D maxBox = null;
	    while (contours != null && !contours.isNull()) {
	      if (contours.elem_size() > 0) {
	       CvBox2D box = cvMinAreaRect2(contours, contourStorage);
	       if (box != null) {
	          CvSize2D32f size = box.size();
	          float area = size.width() * size.height();
	    	  //float area = (float)cvContourArea(contours, CV_WHOLE_SEQ, 0);
	    	  
	          if (area > maxArea) {
	            maxArea = area;
	            bigContour = contours;
	          }
	        }
	      }
	      contours = contours.h_next();
	    }
	    return bigContour;
	  }  // end of findBiggestContour()	
	
*/

	private IplImage gethand(IplImage img,IplImage ctimg) {              
		//countours
		CvMemStorage storage = CvMemStorage.create();
		CvMemStorage storage2 = CvMemStorage.create();
		CvMemStorage storage3 = CvMemStorage.create();

	    CvSeq contour = new CvContour();
		//CvSeq contour = findBiggestContour(ctimg);
	    cvFindContours(ctimg, storage, contour, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);

		while (contour != null && !contour.isNull()) {
			double area = cvContourArea(contour, CV_WHOLE_SEQ, 0);
			if (contour.elem_size() > 0 && area > 1000) {
				CvPoint cogPt=new CvPoint(0,0);;
				cvDrawContours(img, contour, CvScalar.RED, CvScalar.RED, -1, 1, CV_AA);

				CvMoments moments = new CvMoments();
				cvMoments(contour, moments, 1);
				// center of gravity
				double m00 = cvGetSpatialMoment(moments, 0, 0) ;			  
				double m10 = cvGetSpatialMoment(moments, 1, 0) ;
				double m01 = cvGetSpatialMoment(moments, 0, 1);

				if (m00 != 0) {   // calculate center
					int xCenter = (int) Math.round(m10/m00);
					int yCenter = (int) Math.round(m01/m00);		  
					cogPt = new CvPoint(xCenter,yCenter);
					cvCircle(img, cogPt, 11,CV_RGB(255, 1, 1), 1, CV_AA, 0);
				}


				CvSeq hull = cvConvexHull2(contour, storage2, CV_CLOCKWISE, 0);	
				CvSeq defects = cvConvexityDefects(contour, hull, storage3);

				//NOTA
				// I difetti e l'hull sono di tipo Cvseq questo significa che si possono usare gli
				// algo di matching come cvMatchShapes e i cont tree.
				
				for (int i = 0; i < defects.total(); i++) {
					Pointer pntr = cvGetSeqElem(defects, i);
					CvConvexityDefect cdf = new CvConvexityDefect(pntr);
					CvPoint startPt = cdf.start();
					CvPoint endPt = cdf.end();
					CvPoint depthPt = cdf.depth_point();

					//cvLine(img, startPt, endPt, CV_RGB(0, 255, 0), 1, CV_AA, 0); 
					cvLine(img, startPt, cogPt, CV_RGB(0, 255, 0), 1, CV_AA, 0); 
					// cvLine(img, startPt, depthPt, CvScalar.RED, 1, CV_AA, 0);
					//cvLine(img, endPt, depthPt, CV_RGB(0, 255, 255), 1, CV_AA, 0);
					cvCircle(img, startPt, 5,CV_RGB(100, 156, 143), 1, CV_AA, 0);
					//cvCircle(img, endPt, 5,CV_RGB(255, 255, 0), 1, CV_AA, 0); 
					//cvCircle(img, depthPt, 5,CV_RGB(255, 100, 20), 1, CV_AA, 0); 
				}

			}
			contour = contour.h_next();
		}
		return img;
	}
	
	

	public static void main(String[] args) {
		hand2 hd = new hand2();
		Thread th = new Thread(hd);
		th.start();
	}
}