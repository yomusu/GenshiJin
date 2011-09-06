package jp.yom.blocker;

import jp.yom.yglib.gl.Material;
import jp.yom.yglib.gl.Model;
import jp.yom.yglib.gl.YGraphics;
import jp.yom.yglib.gl.YRenderer;
import jp.yom.yglib.vector.AtariBall;
import jp.yom.yglib.vector.FMatrix;
import jp.yom.yglib.vector.FPoint;
import jp.yom.yglib.vector.FSurface;



/*********************************************************
 * 
 * 
 * ボール
 * 
 * @author Yomusu
 *
 */
public class BlockerBall extends AtariBall implements YRenderer {

	/** モデル */
	Model	model;
	
	
	public BlockerBall( ) {
		
		// 幅
		r = 10;

		// 底面の４点
		FPoint	ptb[] = new FPoint[] {
				new FPoint(-r,-r,r), new FPoint(r,-r,r),
				new FPoint(-r,-r,-r), new FPoint(r,-r,-r)
		};
		// 底面を壁の高さまであげた4点
		FPoint	ptc[] = new FPoint[] {
				new FPoint(-r,r,r), new FPoint(r,r,r),
				new FPoint(-r,r,-r), new FPoint(r,r,-r)
		};


		FSurface[]	surfaces = new FSurface[] {
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
		mate.setAmbientColor( 0.5f, 0.5f, 0.5f );
		mate.setDiffuseColor( 0.6f, 0.7f, 0.8f );
		mate.setSpecularColor( 0.9f, 0.9f, 0.9f );
		mate.setEmissionColor( 0.2f, 0.2f, 0.2f );
		mate.setShinness( 10f );

		model.material = mate;
		
		
		
		// 座標
		pos.set( -0, 5, -50 );
		p0.set( pos );
		
		// 速度
		speed.set( 0, 0, -6 );
	}
	
	
	
	@Override
	public void render(YGraphics g) {
		
		g.cullFace( true );
		
		
		FMatrix	mat = new FMatrix();
		mat.unit();
		mat.translate( pos.x, pos.y, pos.z );
		
		g.gl.glPushMatrix();
		
		// マトリックスのセット
		g.mulMatrix( mat );
		
		// モデルの描画
		model.render( g );
		
		g.gl.glPopMatrix();
		
		g.cullFace( false );
	}
	
	
}
