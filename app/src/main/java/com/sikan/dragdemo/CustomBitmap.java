package com.sikan.dragdemo;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;

import java.io.File;

public class CustomBitmap {
	//Matrix.MSKEW_X 控制X坐标的线性倾斜系数
	//Matrix.MSKEW_Y 控制Y坐标的线性倾斜系数
	//Matrix.MTRANS_X//左上顶点X坐标
	//Matrix.MTRANS_Y//左上顶点Y坐标
	//Matrix.MSCALE_X//宽度缩放倍数
	//Matrix.MSCALE_Y//高度缩放位数
	private int id;//唯一标识，实际项目中可替换为url
	public float startDis;// 开始距离
	public PointF midPoint;// 中间点
	public int scaleW;//缩放后的宽度
	public int scaleH;//缩放后的高度
	public float oldRotation = 0;
	public float rotation = 0;
	public PointF startPoint = new PointF();
	public Matrix matrix = new Matrix();
	public Bitmap bitmap;
	public String name;
	public int x;//顶点坐标
	public int y;
	public float scale;//缩放

	public File file;
	public String url;
	public int imgId;
	
	public CustomBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMatrix(Matrix matrix) {
		this.matrix = matrix;
	}

}
