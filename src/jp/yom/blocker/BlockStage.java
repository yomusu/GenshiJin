package jp.yom.blocker;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;

import jp.yom.yglib.GameActivity;
import jp.yom.yglib.gl.YGraphics;
import jp.yom.yglib.gl.YRenderer;
import jp.yom.yglib.gl.YRendererList;
import jp.yom.yglib.node.YNode;
import jp.yom.yglib.vector.FLine;
import jp.yom.yglib.vector.FMatrix;
import jp.yom.yglib.vector.FPoint;
import jp.yom.yglib.vector.FSurface;
import jp.yom.yglib.vector.FVector;


/******************************************************
 * 
 * 
 * ステージ
 * ・カメラ
 * ・背景
 * ・ブロックのデータ保持＆配置
 * 
 * 
 * 
 * @author matsumoto
 *
 */
public class BlockStage extends YNode {
	
	
	/** ブロックのリスト */
	ArrayList<Block>	blockList = new ArrayList<Block>();
	/** カメラのレンダラ */
	protected final CameraRender	cameraRender = new CameraRender();
	/** 背景のレンダラ */
	protected final BackGroundRender	bgRender = new BackGroundRender();
	
	
	
	/**********************************************************
	 * 
	 * 配置データ初期化
	 * ブロック定義データをこのメソッドを介してセットします
	 * 
	 */
	public void initialize() {
		
		//------------------------------
		// ブロックデータの読み込み
		FMatrix	mat = new FMatrix();
		
		
		// 試しに一個配置
		
		float	x = 0;
		float	y = 0;
		
		
		mat.unit();
		mat.translate( x,y,0f );
		
		
		Block	block = new Block();
		block.transform( mat );
	}
	
	
	/**********************************************************
	 * 
	 * ボールを通す
	 * 当たったブロックは倒れます
	 * ボールの跳ね返りはどいつがやるか
	 * メインスレッドで当たり判定は行うか？
	 * 
	 */
	public void putBall() {
		
		
	}
	
	/**********************************************************
	 * 
	 * 
	 * 
	 */
	@Override
	public void process(YNode parent, GameActivity app, YRendererList renderList) {
		
		// チャイルドの処理
		super.process(parent, app, renderList);
		
		
		// カメラのレンダラをセット
		renderList.add( 10001, cameraRender );
		
		// 背景のレンダラをセット
		//renderList.add( 10000, bgRender );
		
		// レンダラリスト登録
//		for( Block block : blockList ) {
//			float	z = 0f;
//			renderList.add( z, block );
//		}
	}
}


/****************************************************
 * 
 * 
 * カメラをレンダラ
 * 
 * 透視変換も行います
 * 
 * 
 */
class CameraRender implements YRenderer {
	
	/** カメラ位置 */
	FPoint	campos = new FPoint( 0, 50, -50 );
	/** 対象位置 */
	FPoint	objpos = new FPoint( 0, 0, 0 );		// 原点
	/** カメラ向き */
	FVector	camdir = new FVector( 0, 1, 0 );	// Yが上になるように
	
	public void setPosition( float x, float y ) {
	}
	
	@Override
	public void render(YGraphics g) {
		
		g.gl.glMatrixMode( GL10.GL_PROJECTION );
		g.gl.glLoadIdentity();
		
		
		// 透視変換…Zの向きが逆というウワサあり
		GLU.gluPerspective( g.gl, 120.0f, 1.0f, 1.0f, 100.0f );
		
		// カメラ位置…MODELVIEWというウワサあり
		GLU.gluLookAt( g.gl,
				campos.x, campos.y, campos.z,
				objpos.x, objpos.y, objpos.z,
				camdir.x, camdir.y, camdir.z
		);
		
		g.gl.glMatrixMode( GL10.GL_MODELVIEW );
		
		
		
		// テストデータ
		float	w = 10;
		float	h = 10;
		float	z = 30;
		
		float[]	vertices = new float[]{
				-w/2, h/2, z,
				 w/2, h/2, z,
				-w/2, -h/2, z,
				 w/2, -h/2, z,
		};
		float[]	colors = {
				1.0f, 1.0f, 1.0f, 1f,
				1.0f, 1.0f, 1.0f, 1f,
				1.0f, 1.0f, 1.0f, 1f,
				1.0f, 1.0f, 1.0f, 1f,
		};

		g.drawPoly4( vertices, colors );
	}
}


/****************************************************
 * 
 * 背景のレンダラ
 * 
 * @author Yomusu
 *
 */
class BackGroundRender implements YRenderer {
	
	float[]	v1 = new float[]{
			-320f, 300f,	320f, 300f,
			-320f, 0f,		320f, 0f,
	};
	
	float[]	v2 = new float[]{
			-320f, 0f,		320f, 0f,
			-320f, -100f,	320f, -100f,
	};
	
	float[]	c1 = new float[]{
			0f,0f,0f, 1f,
			0f,0f,0f, 1f,
			0f,0f,0f, 1f,
			0f,0f,0f, 1f,
	};
	
	float[]	c2 = new float[]{
			0.6f,0.4f,0.4f, 1f,
			0.6f,0.4f,0.4f, 1f,
			0.6f,0.4f,0.4f, 1f,
			0.6f,0.4f,0.4f, 1f,
	};
	
	@Override
	public void render(YGraphics g) {
		
		g.drawRect( v1, c1 );
		g.drawRect( v2, c2 );
	}
}


/***************************************
 * 
 * ブロックひとつ
 * 
 * @author matsumoto
 *
 */
class Block implements YRenderer {
	
	
	// 当たり面(World座標)
	FSurface[]	surfaces;
	
	
	public Block( ) {
		
		// 幅
		int	w = 50;	// X
		// 高さ(Z)
		int	h = 30;	// Z
		// 奥行き
		int	d = 20;	// Y
		
		int	hw = w / 2;	// X
		int	hd = d / 2;	// Y
		
		// 底面の４点
		FPoint	ptb[] = new FPoint[] {
				new FPoint(-hw,hd,0), new FPoint(hw,hd,0),
				new FPoint(-hw,-hd,0), new FPoint(hw,-hd,0)
		};
		
		// 天井の４点
		FPoint	ptc[] = new FPoint[] {
				new FPoint(-hw,hd,h), new FPoint(hw,hd,h),
				new FPoint(-hw,-hd,h), new FPoint(hw,-hd,h)
		};
		
		surfaces = new FSurface[] {
				// 底面
				new FSurface(ptb[1],ptb[0],ptb[3],ptb[2]),
				// 天井
				new FSurface(ptc[0],ptc[1],ptc[2],ptc[3]),
				// 前面
				new FSurface(ptc[2],ptc[3],ptb[2],ptb[3]),
				// 背面
				new FSurface(ptc[1],ptc[0],ptb[1],ptb[0]),
				// 向かって右側面
				new FSurface(ptc[3],ptc[1],ptb[3],ptb[1]),
				// 向かって左側面
				new FSurface(ptc[0],ptc[2],ptb[0],ptb[2]),
		};
	}
	
	
	/*************************************************
	 * 
	 * このブロックにアフィン変換を適用する
	 * 
	 * @param mat
	 */
	public void transform( FMatrix mat ) {
		
		// すべての面に対して…
		for( FSurface s : surfaces )
			s.transform( mat );
		
	}
	
	/*************************************************
	 * 
	 * 線分との当たり判定を行う
	 * 
	 * @param p1	終点
	 * @param p0	始点
	 * 
	 * @return	交点。当たらなかったらnull。
	 */
	public FPoint atariLine( FPoint p1, FPoint p0 ) {
		
		FLine	line = new FLine( p1, p0 );
		FVector	lineDir = line.toVector();
		
		// すべての面に対して…
		for( FSurface s : surfaces ) {
			
			// 背面ではなかったら
			if( s.isBack( lineDir )==false ) {
				
				// 交点計算を行う
				FPoint	p = s.getCrossPoint( line );
				
				if( p!=null )
					return p;
			}
		}
		
		return null;
	}
	
	/** レンダリング用頂点座標バッファ */
	float[]	vertices = new float[4*3];
	
	/** 頂点色データ */
	float[]	colors = {
			1.0f, 1.0f, 1.0f, 1f,
			1.0f, 1.0f, 1.0f, 1f,
			1.0f, 1.0f, 1.0f, 1f,
			1.0f, 1.0f, 1.0f, 1f,
	};
	
	
	/**************************************************
	 * 
	 * レンダリング
	 * 
	 */
	@Override
	public void render(YGraphics g) {
		
		// サーフェスから
		for( FSurface s : surfaces ) {
			
			vertices[0] = s.p0.x;
			vertices[1] = s.p0.y;
			vertices[2] = s.p0.z;
			
			vertices[3] = s.p1.x;
			vertices[4] = s.p1.y;
			vertices[5] = s.p1.z;
			
			vertices[6] = s.p2.x;
			vertices[7] = s.p2.y;
			vertices[8] = s.p2.z;
			
			vertices[9] = s.p3.x;
			vertices[10] = s.p3.y;
			vertices[11] = s.p3.z;
			
			g.drawPoly4( vertices, colors );
		}
	}
}
