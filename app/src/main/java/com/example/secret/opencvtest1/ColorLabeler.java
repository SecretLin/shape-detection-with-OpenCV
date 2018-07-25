package com.example.secret.opencvtest1;

import org.opencv.android.Utils;
import org.opencv.core.Algorithm;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ColorLabeler {
    //定义一个颜色名称数组
    private String[] colorNames= {"blue","green","red"};
    //用mat存放rgb和lab
    private Mat[] rgbMat = new Mat[3];
    private Mat[] labMat = new Mat[3];

    public ColorLabeler(){
        //对应颜色数组的蓝色
        rgbMat[0] = new Mat(1,1,CvType.CV_8UC3,new Scalar(0,0,255));
        //绿色
        rgbMat[1] = new Mat(1,1,CvType.CV_8UC3,new Scalar(0,255,0));
        //红色
        rgbMat[2] = new Mat(1,1,CvType.CV_8UC3,new Scalar(255,0,0));
        //把rgb转换成lab
        for (int i=0;i<3;i++){
            labMat[i] = new Mat();
            Imgproc.cvtColor(rgbMat[i],labMat[i],Imgproc.COLOR_RGB2Lab);
        }

    }

    public String label(Mat image, MatOfPoint contour){
        //传入每个图形的轮廓
        //由于画轮廓的时候是需要一个list，因此这里新建一个list来存放每一个图形轮廓，然后再画出来
        List<MatOfPoint> contours = new ArrayList<>();
        contours.add(contour);

        String label = "unknown";
        //定义一个新的mat来为图形增添蒙版
        Mat mask = Mat.zeros(image.rows(),image.cols(),0);
        //根据蒙版来画轮廓
        Imgproc.drawContours(mask,contours,-1,new Scalar(255,255,255),-1);
        //腐蚀化图像的结构元素，默认采用3*3的正方形
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.erode(mask,mask,kernel,new Point(-1,-1),2);
        //计算lab和蒙版的平均值，返回一个scalar对象
        Scalar scalar = Core.mean(image,mask);
        //由于下面要计算scalar和lab的欧氏距离，所以需要创建一个与lab的大小和类型都一样的mat
        Mat mean = new Mat(1,1,CvType.CV_8UC3,scalar);

        //计算平均值跟各个颜色的lab的欧式距离
        double dis = Integer.MAX_VALUE;
        int min = 0;
        for (int i=0;i<3;i++){

            double d = Core.norm(labMat[i],mean);

            if (d<dis){
                dis = d;
                min = i;
            }
        }
        //得到的最小距离的颜色即为该形状的颜色
        label = colorNames[min];
        return label;
    }

}
