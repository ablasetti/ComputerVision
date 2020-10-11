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


public class hand {
	private static void hand() {
		CanvasFrame canvas = new CanvasFrame("my img");
		CanvasFrame canvas2 = new CanvasFrame("new");
		
		IplImage img = cvLoadImage("hand2.jpg");
		////////////////////////////////////////////////////////////////////

		IplImage ctimg = cvCreateImage(cvGetSize(img), 8, 1);
		//IplImage myImage = cvCreateImage(cvGetSize(img), 8, 1);  	

		cvCvtColor(img, ctimg, CV_BGR2GRAY);   	
		
	  	//cvSmooth(ctimg, ctimg, CV_MEDIAN ,7);
    	cvCanny(ctimg,ctimg,100,100,3);
    	//cvDilate(ctimg, ctimg, null, 3);

		//countours
		CvMemStorage storage = CvMemStorage.create();
		CvMemStorage storage2 = CvMemStorage.create();
		CvMemStorage storage3 = CvMemStorage.create();
		
		CvSeq contour = new CvContour();
		cvFindContours(ctimg, storage, contour, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);

		while (contour != null && !contour.isNull()) {
			double area = cvContourArea(contour, CV_WHOLE_SEQ, 0);
			if (contour.elem_size() > 0 && area > 133) {
				
				//cvDrawContours(img, contour, CvScalar.RED, CvScalar.RED, -1, 1, CV_AA);

				CvMoments moments = new CvMoments();
				cvMoments(contour, moments, 1);

				// center of gravity
				double m00 = cvGetSpatialMoment(moments, 0, 0) ;			  
				double m10 = cvGetSpatialMoment(moments, 1, 0) ;
				double m01 = cvGetSpatialMoment(moments, 0, 1);

				if (m00 != 0) {   // calculate center
					int xCenter = (int) Math.round(m10/m00);
					int yCenter = (int) Math.round(m01/m00);		  
					CvPoint cogPt = new CvPoint(xCenter,yCenter);
					cvCircle(img, cogPt, 11,CV_RGB(255, 1, 1), 1, CV_AA, 0);
				}

				
				CvSeq hull = cvConvexHull2(contour, storage2, CV_CLOCKWISE, 0);	
				CvSeq defects = cvConvexityDefects(contour, hull, storage3);
				
				for (int i = 0; i < defects.total(); i++) {
				 Pointer pntr = cvGetSeqElem(defects, i);
				 CvConvexityDefect cdf = new CvConvexityDefect(pntr);
				 CvPoint startPt = cdf.start();
				 CvPoint endPt = cdf.end();
				 CvPoint depthPt = cdf.depth_point();
				 
				 cvLine(img, startPt, endPt, CV_RGB(0, 255, 0), 1, CV_AA, 0); 
				  // cvLine(img, startPt, depthPt, CvScalar.RED, 1, CV_AA, 0);
				  //cvLine(img, endPt, depthPt, CV_RGB(0, 255, 255), 1, CV_AA, 0);
				 cvCircle(img, endPt, 5,CV_RGB(255, 255, 0), 1, CV_AA, 0); 
				  cvCircle(img, depthPt, 5,CV_RGB(255, 100, 20), 1, CV_AA, 0); 
				}
								
			}
			contour = contour.h_next();
		}
		canvas.showImage(img);
		canvas2.showImage(ctimg);

	}


	public static void main(String[] args) {
		hand();
	}
}