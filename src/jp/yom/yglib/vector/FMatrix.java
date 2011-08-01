package jp.yom.yglib.vector;

/***************************************************
 * 
 * 
 * マトリックス（アフィン変換）
 * 
 */
public class FMatrix {
	
	public float m00,m01,m02,m03;
	public float m10,m11,m12,m13;
	public float m20,m21,m22,m23;
	public float m30,m31,m32,m33;
	
	/********************************
	 * 
	 * 単位マトリックスを作成
	 * 
	 */
	public void unit() {
		//|  1  0  0  0|
		//|  0  1  0  0|
		//|  0  0  1  0|
		//|  0  0  0  1|
		
		m00 = 1;m01 = 0;m02 = 0;m03 = 0;
		m10 = 0;m11 = 1;m12 = 0;m13 = 0;
		m20 = 0;m21 = 0;m22 = 1;m23 = 0;
		m30 = 0;m31 = 0;m32 = 0;m33 = 1;
	}
	
	
	/**********************************
	 * 
	 * 行列の乗算
	 */
	public void mul( FMatrix m ) {
		
		float _00 = m00*m.m00 + m01*m.m10 + m02*m.m20 + m03*m.m30;
		float _01 = m00*m.m01 + m01*m.m11 + m02*m.m21 + m03*m.m31;
		float _02 = m00*m.m02 + m01*m.m12 + m02*m.m22 + m03*m.m32;
		float _03 = m00*m.m03 + m01*m.m13 + m02*m.m23 + m03*m.m33;
		
		float _10 = m10*m.m00 + m11*m.m10 + m12*m.m20 + m13*m.m30;
		float _11 = m10*m.m01 + m11*m.m11 + m12*m.m21 + m13*m.m31;
		float _12 = m10*m.m02 + m11*m.m12 + m12*m.m22 + m13*m.m32;
		float _13 = m10*m.m03 + m11*m.m13 + m12*m.m23 + m13*m.m33;
		
		float _20 = m20*m.m00 + m21*m.m10 + m22*m.m20 + m23*m.m30;
		float _21 = m20*m.m01 + m21*m.m11 + m22*m.m21 + m23*m.m31;
		float _22 = m20*m.m02 + m21*m.m12 + m22*m.m22 + m23*m.m32;
		float _23 = m20*m.m03 + m21*m.m13 + m22*m.m23 + m23*m.m33;
		
		float _30 = m30*m.m00 + m31*m.m10 + m32*m.m20 + m33*m.m30;
		float _31 = m30*m.m01 + m31*m.m11 + m32*m.m21 + m33*m.m31;
		float _32 = m30*m.m02 + m31*m.m12 + m32*m.m22 + m33*m.m32;
		float _33 = m30*m.m03 + m31*m.m13 + m32*m.m23 + m33*m.m33;
		
		m00 = _00; m01 = _01;m02 = _02;m03 = _03;
		m10 = _10; m11 = _11;m12 = _12;m13 = _13;
		m20 = _20; m21 = _21;m22 = _22;m23 = _23;
		m30 = _30; m31 = _31;m32 = _32;m33 = _33;
	}
	
	/*************************************
	 * 
	 * 拡大縮小
	 * 
	 */
	public void scale( float sx,float sy,float sz )
	{		
		//| sx  0  0  0|
		//|  0 sy  0  0|
		//|  0  0 sz  0|
		//|  0  0  0  1|
		FMatrix m = new FMatrix();
		m.m00 = sx;m.m01 =  0;m.m02 =  0;m.m03 = 0;
		m.m10 =  0;m.m11 = sy;m.m12 =  0;m.m13 = 0;
		m.m20 =  0;m.m21 =  0;m.m22 = sz;m.m23 = 0;
		m.m30 =  0;m.m31 =  0;m.m32 =  0;m.m33 = 1;
		//行列の合成
		mul(m);
	}
	
	/********************************************
	 * 
	 * 
	 * @param fv
	 */
	public void rotateXZY( FVector fv )
	{
		rotateX( fv.x );
		rotateZ( fv.z );
		rotateY( fv.y );
	}
	
	/********************************************
	 * 
	 * X軸回転
	 * <pre>
	 * | 1     0    0 0|
	 * | 0  cosX sinX 0|
	 * | 0 -sinX cosX 0|
	 * | 0     0    0 1|
	 * </pre>
	 * 
	 * @param r 回転角（単位ラジアン）
	 */
	public void rotateX( float r ) {
		
		float sinx = (float)Math.sin(r);
		float cosx = (float)Math.cos(r);
		
		FMatrix m = new FMatrix();
		m.m00 = 1;m.m01 =    0;m.m02 =    0;m.m03 = 0;
		m.m10 = 0;m.m11 = cosx;m.m12 = sinx;m.m13 = 0;
		m.m20 = 0;m.m21 =-sinx;m.m22 = cosx;m.m23 = 0;
		m.m30 = 0;m.m31 =    0;m.m32 =    0;m.m33 = 1;
		//行列の合成
		mul(m);
	}
	
	/********************************************
	 * 
	 * Y軸回転
	 * 
	 * @param r 回転角（単位ラジアン）
	 */
	public void rotateY( float r ) {
		
		float siny = (float)Math.sin(r);
		float cosy = (float)Math.cos(r);
		//Y軸回転
		//| cosY 0 -sinY 0|
		//|    0 1     0 0|
		//| sinY 0  cosY 0|
		//|    0 0     0 1|
		FMatrix m = new FMatrix();
		m.m00 = cosy;m.m01 = 0;m.m02 =-siny;m.m03 = 0;
		m.m10 =    0;m.m11 = 1;m.m12 =    0;m.m13 = 0;
		m.m20 = siny;m.m21 = 0;m.m22 = cosy;m.m23 = 0;
		m.m30 =    0;m.m31 = 0;m.m32 =    0;m.m33 = 1;
		//行列の合成
		mul(m);
	}
	
	/********************************************
	 * 
	 * Z軸回転
	 * 
	 * @param r ...回転角（単位ラジアン）
	 * 
	 */
	public void rotateZ( float r ) {
		
		float sinz = (float)Math.sin(r);
		float cosz = (float)Math.cos(r);	
		//Z軸回転
		//|  cosZ sinZ 0 0|
		//| -sinZ cosZ 0 0|
		//|     0    0 1 0|
		//|     0    0 0 1|	
		FMatrix m = new FMatrix();
		m.m00 = cosz;m.m01 = sinz;m.m02 = 0;m.m03 = 0;
		m.m10 =-sinz;m.m11 = cosz;m.m12 = 0;m.m13 = 0;
		m.m20 =    0;m.m21 =    0;m.m22 = 1;m.m23 = 0;
		m.m30 =    0;m.m31 =    0;m.m32 = 0;m.m33 = 1;
		//行列の合成
		mul(m);
	}
	
	/*****************************************
	 * 
	 * 平行移動
	 */
	public void translate( FVector fv ) {
		translate( fv.x, fv.y, fv.z );
	}
	
	/*****************************************
	 * 
	 * 平行移動
	 * 
	 * @param dx
	 * @param dy
	 * @param dz
	 */
	public void translate( float dx,float dy,float dz ) {
		//|  1  0  0  0|
		//|  0  1  0  0|
		//|  0  0  1  0|
		//| dx dy dz  1|
	
		FMatrix m = new FMatrix();
		m.m00 =  1;m.m01 =  0;m.m02 =  0;m.m03 = 0;
		m.m10 =  0;m.m11 =  1;m.m12 =  0;m.m13 = 0;
		m.m20 =  0;m.m21 =  0;m.m22 =  1;m.m23 = 0;
		m.m30 = dx;m.m31 = dy;m.m32 = dz;m.m33 = 1;
		//行列の合成
		mul(m);
	}
	
	/****************************************
	 * 
	 * 
	 * 一次変換
	 * 
	 */
	public FPoint transform( float x, float y, float z, FPoint result ) {
		
		float	_x = (m00 * x) + (m10 * y) + (m20 * z) + m30;
		float	_y = (m01 * x) + (m11 * y) + (m21 * z) + m31;
		float	_z = (m02 * x) + (m12 * y) + (m22 * z) + m32;
		
		result.x = _x;
		result.y = _y;
		result.z = _z;
		
		return result;
	}
}
