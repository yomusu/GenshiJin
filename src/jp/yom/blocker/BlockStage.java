package jp.yom.blocker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;

import jp.yom.yglib.GameActivity;
import jp.yom.yglib.gl.Camera;
import jp.yom.yglib.gl.Light;
import jp.yom.yglib.gl.Material;
import jp.yom.yglib.gl.Model;
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
public class BlockStage extends YNode implements YRenderer {
	
	
	/** ブロックのリスト */
	ArrayList<Block>	blockList = new ArrayList<Block>();
	/** カメラのレンダラ */
	protected final Camera	cameraRender = new Camera();
	/** 背景のレンダラ */
	protected final BackGroundRender	bgRender = new BackGroundRender();
	
	
	/** 平行光源 */
	Light.DirectionalLight	dirLight = new Light.DirectionalLight();
	
	/** フロアデータ */
	Floor		floor = new Floor();
	
	/** ボール */
	BlockerBall	ball = null;
	
	
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
		
		
		//------------------------------
		// 光源の設定
		dirLight.setGLLightNumber( GL10.GL_LIGHT0 );
		dirLight.setAmbientColor( 0.5f, 0.0f, 0.0f );
		dirLight.setDiffuseColor( 0.8f, 0.8f, 0.0f );
		dirLight.setSpecularColor( 0.1f, 0.1f, 0.1f );
		dirLight.setDirection( -1, -1, 0 );
		
		
		//------------------------------
		// カメラの初期設定
		cameraRender.campos.set( 0, 400, -400 );
		cameraRender.objpos.set( 0, 0, 0 );
		cameraRender.camdir.set( 0, 1, 0 );
		cameraRender.fovy = 60f;
		
		
		//------------------------------
		// ボール
		ball = new BlockerBall();
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
		
		
		// 試しに回転
		float	radZ = (float)((3.14/180.0) * 1);
		
		FMatrix	mat = new FMatrix();
		mat.unit();
		mat.rotateY( radZ );
		
		blockList.get(0).transform( mat );
		
		renderList.add( 100, this );
		
		
		
		//------------------------
		// ボール進行
		ball.forward();
		
		//------------------------
		// 当たり判定
		SurfaceIterator	it = new SurfaceIterator( floor.surfaces );
		while( it.hasNext() ) {
			
			if( it.nextAtari(ball.line,10) ) {
				
				// 続いて残りの移動距離を反射させる
				FVector	nokori = new FVector( it.cp, ball.line.p1 ).reflection( it.s.normal );
				
				// ボールの位置を交点に
				ball.line.p1.set(it.cp).add( nokori );
				
				ball.line.p0.set(it.cp);
				
				// 速度ベクトルを反射
				ball.speed.reflection( it.s.normal );
				
				// イテレーションしなおし
				if( nokori.getScalar() > 1f )
					it = new SurfaceIterator( floor.surfaces );
			}
		}
		
		ball.process( parent, app, renderList ); 
	}
	
	
	/**********************************************************
	 * 
	 * レンダリング
	 * 
	 */
	@Override
	public void render(YGraphics g) {
		
		// カメラのレンダラをセット
		cameraRender.render(g);
		
		// 背景のレンダラをセット
		//renderList.add( 10000, bgRender );
		
		//---------------------
		// 光源をセットする
		dirLight.render( g );
		
		//---------------------
		// ブロック
		
		g.depthTest( true );
		g.lighting( true );
		
		floor.model.render( g );
		
		g.cullFace( true );
		
		// モデル
		for( Block block : blockList )
			block.model.render(g);
		
		g.depthTest( false );
		g.cullFace( false );
		g.lighting( false );
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


/*****************************************
 * 
 * 
 * 床と壁
 * 
 * @author Yomusu
 *
 */
class Floor {
	
	/** 当たり面(World座標) */
	FSurface[]	surfaces;
	
	/** モデル */
	Model	model;
	
	
	public Floor( ) {
		
		// 幅
		int	w = 500;	// X
		// 奥行き
		int	d = 500;	// Z
		
		// 壁の高さ(Y)
		int	h = 30;	// Y
		
		int	hw = w / 2;	// X
		int	hd = d / 2;	// Y
		
		// 底面の４点
		FPoint	ptb[] = new FPoint[] {
				new FPoint(hw,0,hd), new FPoint(-hw,0,hd),
				new FPoint(hw,0,-hd), new FPoint(-hw,0,-hd)
		};
		// 底面を壁の高さまであげた4点
		FPoint	ptc[] = new FPoint[] {
				new FPoint(hw,h,hd), new FPoint(-hw,h,hd),
				new FPoint(hw,h,-hd), new FPoint(-hw,h,-hd)
		};
		
		
		surfaces = new FSurface[] {
				// 床
			//	new FSurface(ptb[0],ptb[1],ptb[2],ptb[3]),
				
				// 壁：手前
				new FSurface(ptc[3],ptc[2],ptb[3],ptb[2]),
				// 壁：奥
				new FSurface(ptc[0],ptc[1],ptb[0],ptb[1]),
				// 壁：向かって右側面
				new FSurface(ptc[1],ptc[3],ptb[1],ptb[3]),
				// 壁：向かって左側面
				new FSurface(ptc[2],ptc[0],ptb[2],ptb[0]),
		};
		
		
		//------------------------------
		// モデルの作成
		model = new Model(surfaces);
		
		// マテリアルの設定
		Material	mate = new Material();
		mate.setAmbientColor( 0.5f, 0.5f, 0.5f );
		mate.setDiffuseColor( 0.5f, 0.5f, 0.5f );
		mate.setSpecularColor( 0f, 0f, 0f );
		mate.setEmissionColor( 0f, 0f, 0f );
		mate.setShinness( 0f );
		
		model.material = mate;
	}
}

/****************************************
 * 
 * 当たり判定のための面イテレータ
 * 
 * @author Yomusu
 *
 */
class SurfaceIterator {
	
	Iterator<FSurface>	it;
	
	public FSurface	s;
	public FPoint		cp;
	
	
	public SurfaceIterator( FSurface[] a ) {
		this.it = Arrays.asList( a ).iterator();
	}
	
	public boolean hasNext() {
		return it.hasNext();
	}
	
	/** 次の面を当たり判定します */
	public boolean nextAtari( FLine ballLine, float thickness ) {
		
		s = it.next();
		
		// 背面ではなかったら
		if( s.isBack( ballLine.toVector() )==false ) {
			
			// 交点計算を行う
			cp = s.getCrossPoint( ballLine, thickness );
			
			if( cp!=null )
				return true;
			
		} else {
			cp = null;
		}
		
		return false;
	}
}

/***************************************
 * 
 * ブロックひとつ
 * 
 * @author matsumoto
 *
 */
class Block {
	
	
	/** 当たり面(World座標) */
	FSurface[]	surfaces;
	
	/** モデル */
	Model	model;
	
	
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
		
		
		//------------------------------
		// モデルの作成
		model = new Model(surfaces);
		
		// マテリアルの設定
		Material	mate = new Material();
		
		mate.setAmbientColor( 0.2f, 1.0f, -1.0f );
		mate.setDiffuseColor( 0.3f, 1.0f, -1.0f );
		mate.setSpecularColor( 0f, 0f, 0f );
		mate.setEmissionColor( 0f, 0f, 0f );
		mate.setShinness( 0.1f );
		
		model.material = mate;
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
}
