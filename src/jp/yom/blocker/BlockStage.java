package jp.yom.blocker;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import jp.yom.yglib.GameActivity;
import jp.yom.yglib.gl.Camera3D;
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
 * やること
 * ・透視変換＆カメラ位置
 * ・Blockクラス
 * ・Blockのレンダリング
 * ・ライティング…環境光
 * ・床
 * ・ボールの配置
 * ・ボールを動かす＆外壁に当たり判定
 * ・ブロックとボールの当たり判定
 * ・バー配置
 * ・バー移動
 * ・バーとボールの当たり判定
 * ・ブロックの破壊
 * ・ゲームクリア判定
 * 
 * 
 * @author matsumoto
 *
 */
public class BlockStage extends YNode {
	
	
	/** ブロックのリスト */
	ArrayList<Block>	blockList = new ArrayList<Block>();
	/** カメラのレンダラ */
	protected final Camera3D	cameraRender = new Camera3D();
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
		
		blockList.add( block );
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
	
	
	float	radZ = 0f;
	
	/**********************************************************
	 * 
	 * 
	 * 
	 */
	@Override
	public void process(YNode parent, GameActivity app, YRendererList renderList) {
		
		// チャイルドの処理
		super.process(parent, app, renderList);
		
		
		// 試しに回転
		radZ = (float)((3.14/180.0) * 1);
		
		FMatrix	mat = new FMatrix();
		mat.unit();
		mat.rotateY( radZ );
		
		blockList.get(0).transform( mat );
		
		
		// カメラのレンダラをセット
		renderList.add( 10001, cameraRender );
		
		// 背景のレンダラをセット
		//renderList.add( 10000, bgRender );
		
		// レンダラリスト登録
		for( Block block : blockList ) {
			float	z = 0f;
			renderList.add( z, block );
		}
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
		int	w = 20;	// X
		// 高さ(Y)
		int	h = 30;	// Y
		// 奥行き
		int	d = 20;	// Z
		
		int	hw = w / 2;	// X
		int	hd = d / 2;	// Y
		
		// 底面の４点
		FPoint	ptb[] = new FPoint[] {
				new FPoint(-hw,0,hd), new FPoint(hw,0,hd),
				new FPoint(-hw,0,-hd), new FPoint(hw,0,-hd)
		};
		
		// 天井の４点
		FPoint	ptc[] = new FPoint[] {
				new FPoint(-hw,h,hd), new FPoint(hw,h,hd),
				new FPoint(-hw,h,-hd), new FPoint(hw,h,-hd)
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
	float[]	normals  = new float[4*3];
	
	/** 頂点色データ */
	float[]	colors = {
			1.0f, 1.0f, 0.0f, 1f,
			1.0f, 1.0f, 0.0f, 1f,
			1.0f, 1.0f, 0.0f, 1f,
			1.0f, 1.0f, 0.0f, 1f,
	};
	
	
	/**************************************************
	 * 
	 * レンダリング
	 * 
	 */
	@Override
	public void render(YGraphics g) {
		
		g.depthTest( true );
		g.cullFace( true );
		
		float[]	lit_amb = new float[]{1.0f, 0.0f, 1.0f, 0.0f};
		float[]	lit_dif = new float[]{1.0f, 1.0f, 1.0f, 0.0f};
		float[]	lit_spc = new float[]{1.0f, 1.0f, 1.0f, 0.0f};
		float[]	lit_pos = new float[]{1.0f, 1.0f, 1.0f, 0.0f};
		
		float[]	mat_amb = new float[]{0.2f, 0.2f, 0.2f, 0.0f};
		float[]	mat_dif = new float[]{0.6f, 0.6f, 0.6f, 0.0f};
		float[]	mat_spc = new float[]{0.2f, 0.2f, 0.2f, 0.0f};
		float[]	mat_emi = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
		float[]	mat_shi = new float[]{30.0f};
		
		// ライティング
		g.gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lit_amb,0);
		g.gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lit_dif,0);
		g.gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, lit_spc,0);
		g.gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lit_pos,0);
		
		g.gl.glEnable(GL10.GL_LIGHT0);
		g.gl.glEnable(GL10.GL_LIGHTING);

		g.gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_AMBIENT, mat_amb,0);
		g.gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_DIFFUSE, mat_dif,0);
		g.gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_SPECULAR, mat_spc,0);
		g.gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_SHININESS, mat_shi,0);
		g.gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_EMISSION, mat_emi,0);
		
		// サーフェスから
		for( FSurface s : surfaces ) {
			
			normals[0] = s.normal.x;
			normals[1] = s.normal.y;
			normals[2] = s.normal.z;
			
			vertices[0] = s.p0.x;
			vertices[1] = s.p0.y;
			vertices[2] = s.p0.z;
			
			normals[3] = s.normal.x;
			normals[4] = s.normal.y;
			normals[5] = s.normal.z;
			
			vertices[3] = s.p1.x;
			vertices[4] = s.p1.y;
			vertices[5] = s.p1.z;
			
			normals[6] = s.normal.x;
			normals[7] = s.normal.y;
			normals[8] = s.normal.z;
			
			vertices[6] = s.p2.x;
			vertices[7] = s.p2.y;
			vertices[8] = s.p2.z;
			
			normals[9] = s.normal.x;
			normals[10] = s.normal.y;
			normals[11] = s.normal.z;
			
			vertices[9] = s.p3.x;
			vertices[10] = s.p3.y;
			vertices[11] = s.p3.z;
			
			g.drawPoly4( vertices, normals, colors );
		}
		
		
		g.cullFace( false );
		g.depthTest( false );
	}
}
