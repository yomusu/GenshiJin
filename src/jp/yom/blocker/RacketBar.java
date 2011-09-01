package jp.yom.blocker;

import jp.yom.yglib.AtariModel;
import jp.yom.yglib.GameActivity;
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


/**************************************************************
 * 
 * 
 * ラケットバー
 * 
 * 
 * @author Yomusu
 *
 */
public class RacketBar  extends YNode implements YRenderer {
	
	/** 当たり判定Model */
	AtariModel	atari = new AtariModel();;
	
	/** モデル */
	Model	model;
	
	
	public RacketBar( ) {

		// 幅
		int	w = 60;
		int	d = 20;
		int	h = 20;
		
		float	hw = w / 2f;
		float	hd = d / 2f;
		
		// 底面の４点
		FPoint	ptb[] = new FPoint[] {
				new FPoint(-hw,0,hd), new FPoint(hw,0,hd),
				new FPoint(-hw,0,-hd), new FPoint(hw,0,-hd)
		};
		// 底面を壁の高さまであげた4点
		FPoint	ptc[] = new FPoint[] {
				new FPoint(-hw,h,hd), new FPoint(hw,h,hd),
				new FPoint(-hw,h,-hd), new FPoint(hw,h,-hd)
		};
		
		atari.surfaces = new FSurface[] {
				// 前面
				new FSurface(ptc[2],ptc[3],ptb[2],ptb[3]),
				// 背面
				new FSurface(ptc[1],ptc[0],ptb[1],ptb[0]),
				// 向かって右側面
				new FSurface(ptc[3],ptc[1],ptb[3],ptb[1]),
				// 向かって左側面
				new FSurface(ptc[0],ptc[2],ptb[0],ptb[2]),
		};
		
		atari.lines = new FLine[] {
				new FLine( ptc[0], ptb[0] ),
				new FLine( ptc[1], ptb[1] ),
				new FLine( ptc[2], ptb[2] ),
				new FLine( ptc[3], ptb[3] ),
		};

		//------------------------------
		// モデルの作成
		model = new Model(atari.surfaces);

		// マテリアルの設定
		Material	mate = new Material();
		mate.setAmbientColor( 0.5f, 0.5f, 0.5f );
		mate.setDiffuseColor( 0.6f, 0.7f, 0.8f );
		mate.setSpecularColor( 0.9f, 0.9f, 0.9f );
		mate.setEmissionColor( 0.2f, 0.2f, 0.2f );
		mate.setShinness( 10f );

		model.material = mate;
		
		
		
		// 座標
		FMatrix	mat = new FMatrix();
		mat.unit();
		mat.translate( 0, 0, -150 );
		
		atari.transform( mat );
	}
	
	/******************************************
	 * 
	 * バーを進める
	 * 
	 */
	public void move( FVector slide ) {
		
		FMatrix	mat = new FMatrix();
		mat.unit();
		mat.translate( slide.x, slide.y, slide.z );
		// x座標の向きが異なるのを補正
		
		atari.transform( mat );
		
	//	Log.v( "App", "pos="+line.p1+" slide="+slide );
	}
	
	
	/******************************************
	 * 
	 * 毎フレームの処理
	 * 
	 */
	@Override
	public void process(YNode parent, GameActivity app, YRendererList renderList) {
		
		super.process(parent, app, renderList);
		
		renderList.add( 10, this );
	}
	
	
	@Override
	public void render(YGraphics g) {
		
	//	g.cullFace( true );
		
		// モデルの描画
		model.render( g );
		
	//	g.cullFace( false );
	}
	
	

}
