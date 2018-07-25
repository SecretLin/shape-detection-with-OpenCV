package com.example.secret.opencvtest1;

import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class ShapeDetector {

    public String detect(MatOfPoint2f c){

        String shape = "unknown";
        //计算轮廓的周长
        double peri = Imgproc.arcLength(c,true);
        MatOfPoint2f approx = new MatOfPoint2f();
        //得到大概值
        Imgproc.approxPolyDP(c,approx,0.04 * peri,true);

        //如果是三角形形状，则有三个顶点
        if (approx.toList().size()==3){
            shape = "triangle";
        }

        //如果有四个顶点，则是正方形或者长方形
        else if (approx.toList().size()==4){

            //计算轮廓的边界框并使用边界框来计算宽高比
            Rect rect = new Rect();
            rect = Imgproc.boundingRect(new MatOfPoint(approx.toArray()));
            float ar = rect.width/(float)rect.height;

            //正方形的宽高比接近为1，除此之外就为长方形
            if (ar>=0.95 && ar<=1.05){
                shape = "square";
            }else {
                shape = "rectangle";
            }
        }

        //如果是五角形，则有五个顶点
        else if (approx.toList().size()==5){
            shape = "pentagon";
        }

        //除了以上情况之外，我们假设为圆形
        else {
            shape = "circle";
        }

        return shape;
    }

}
