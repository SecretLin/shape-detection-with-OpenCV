package com.example.secret.opencvtest1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;

/*
画出轮廓中心
 */

public class MainActivity extends AppCompatActivity {

    private Button btn;
    private ImageView img;
    private static final String TAG = "OpenCV----";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.iv);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Center_of_Contour(R.mipmap.shapes_and_colors);
            }
        });

        findViewById(R.id.btnShape).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShapeDetection(R.mipmap.shapes_and_colors);
            }
        });

        findViewById(R.id.btnColor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorLabel(R.mipmap.shape);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //load OpenCV engine and init OpenCV library
        OpenCVLoader.initDebug();
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    //OpenCV库加载并初始化成功后的回调函数
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG, "成功加载");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i(TAG, "加载失败");
                    break;
            }
        }
    };

    /**
     * 画出轮廓中心
     */
    private void Center_of_Contour(int id) {


        //读入图片
        Bitmap srcBitmap;
        srcBitmap = BitmapFactory.decodeResource(getResources(), id);

        //建立几个Mat类型的对象
        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();
        Mat blur1 = new Mat();

        //将原始的bitmap转换为mat型.
        Utils.bitmapToMat(srcBitmap, rgbMat);
        //将图像转换为灰度
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_BGR2GRAY);
        //以两个不同的模糊半径对图像做模糊处理,前两个参数分别是输入和输出图像，第三个参数指定应用滤波器时所用核的尺寸，最后一个参数指定高斯函数中的标准差数值
        Imgproc.GaussianBlur(grayMat, blur1, new Size(5, 5), 0);
        //图片二值化
        Imgproc.threshold(blur1, blur1, 60, 255, Imgproc.THRESH_BINARY);
        //将处理后的图片显示出来
//        Bitmap grayBitmap;
//        grayBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.RGB_565);
//        Utils.matToBitmap(blur1, grayBitmap);
//        img.setImageBitmap(grayBitmap);

        //寻找图形的轮廓
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(blur1, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        //遍历每个图形的轮廓
        for (MatOfPoint c : contours) {
            //计算轮廓的中心
            Moments m = Imgproc.moments(c);
            int cx = (int) (m.m10 / m.m00);
            int cy = (int) (m.m01 / m.m00);

            //画轮廓
            Imgproc.drawContours(rgbMat, contours, -1, new Scalar(0, 255, 0), 2);
            //画中心的圆点
            Imgproc.circle(rgbMat, new Point(cx, cy), 7, new Scalar(255, 255, 255), -1);
            //在圆点旁边显示文字center
            Imgproc.putText(rgbMat, "center", new Point(cx - 20, cy - 20), Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255, 255, 255), 2);
            //再把mat转换为bitmap显示出来
            Bitmap grayBitmap;
            grayBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.RGB_565);
            Utils.matToBitmap(rgbMat, grayBitmap);
            img.setImageBitmap(grayBitmap);
        }


    }


    /**
     * 形状识别
     */
    private void ShapeDetection(int id) {
        //读入图片
        Bitmap srcBitmap;
        srcBitmap = BitmapFactory.decodeResource(getResources(), id);

        //建立几个Mat类型的对象
        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();
        Mat blur1 = new Mat();

        //将原始的bitmap转换为mat型.
        Utils.bitmapToMat(srcBitmap, rgbMat);
        //将图像转换为灰度
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_BGR2GRAY);
        //以两个不同的模糊半径对图像做模糊处理,前两个参数分别是输入和输出图像，第三个参数指定应用滤波器时所用核的尺寸，最后一个参数指定高斯函数中的标准差数值
        Imgproc.GaussianBlur(grayMat, blur1, new Size(5, 5), 0);
        //图片二值化
        Imgproc.threshold(blur1, blur1, 60, 255, Imgproc.THRESH_BINARY);

        //寻找图形的轮廓
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(blur1, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        //加载形状检测的类
        ShapeDetector sd = new ShapeDetector();
        for (MatOfPoint c : contours) {
            //计算轮廓的中心，并根据中心确定形状
            Moments m = Imgproc.moments(c);
            int cx = (int) (m.m10 / m.m00 );
            int cy = (int) (m.m01 / m.m00 );
            String shape = sd.detect(new MatOfPoint2f(c.toArray()));

            //画轮廓
            Imgproc.drawContours(rgbMat, contours, -1, new Scalar(0, 255, 0), 2);
            //在中心显示文字
            Imgproc.putText(rgbMat, shape, new Point(cx , cy ), Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255, 255, 255), 2);
            //将Mat转换为位图
            Bitmap grayBitmap= Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.RGB_565);
            Utils.matToBitmap(rgbMat, grayBitmap);
            img.setImageBitmap(grayBitmap);

        }


    }


    private void ColorLabel(int id){

        //读入图片
        Bitmap srcBitmap;
        srcBitmap = BitmapFactory.decodeResource(getResources(), id);

        //建立几个Mat类型的对象
        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();
        Mat blur1 = new Mat();

        //将原始的bitmap转换为mat型.
        Utils.bitmapToMat(srcBitmap, rgbMat);

        //以两个不同的模糊半径对图像做模糊处理,前两个参数分别是输入和输出图像，第三个参数指定应用滤波器时所用核的尺寸，最后一个参数指定高斯函数中的标准差数值
        Imgproc.GaussianBlur(rgbMat, blur1, new Size(5, 5), 0);
        //将图像转换为灰度
        Imgproc.cvtColor(blur1, grayMat, Imgproc.COLOR_BGR2GRAY);
        //将图像转换为lab
        Mat labMat = new Mat(blur1.size(),blur1.type());
        Imgproc.cvtColor(blur1,labMat,Imgproc.COLOR_RGB2Lab);
        //图片二值化
        Imgproc.threshold(grayMat, grayMat, 60, 255, Imgproc.THRESH_BINARY);

        //寻找图形的轮廓
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(grayMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        //加载颜色标签的类
        ColorLabeler cl = new ColorLabeler();

        for (MatOfPoint c : contours) {
            //计算轮廓的中心，并根据中心确定形状
            Moments m = Imgproc.moments(c);
            int cx = (int) (m.m10 / m.m00 );
            int cy = (int) (m.m01 / m.m00 );

            //传入图片和每个形状的轮廓
            String label = cl.label(labMat,c);

            //画轮廓
            Imgproc.drawContours(rgbMat, contours, -1, new Scalar(0, 255, 0), 2);
            //在中心显示文字
            Imgproc.putText(rgbMat, label, new Point(cx , cy ), Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255, 255, 255), 2);
            //将Mat转换为位图
            Bitmap grayBitmap= Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.RGB_565);
            Utils.matToBitmap(rgbMat, grayBitmap);
            img.setImageBitmap(grayBitmap);

        }

    }
}
