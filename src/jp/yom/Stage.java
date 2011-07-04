package jp.yom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import jp.yom.yglib.GameActivity;
import jp.yom.yglib.gl.YRendererList;
import jp.yom.yglib.node.YBoundary;
import jp.yom.yglib.node.YNode;
import jp.yom.yglib.vector.FLine;
import jp.yom.yglib.vector.FMatrix;
import jp.yom.yglib.vector.FPoint;
import jp.yom.yglib.vector.FSurface;
import jp.yom.yglib.vector.FVector;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;



/*****************************************************
 * 
 * 
 * ステージ（障害物等）
 * を構成する
 * 
 * @author Yomusu
 *
 */
public class Stage extends YNode implements YBoundary {
	
	float	width = 1000;
	float	height= 600;
	
	
	FPoint	pt[] = new FPoint[] {
			new FPoint(100,100), new FPoint(1000,100),
			new FPoint(100,600), new FPoint(1000,600)
	};
	
	FSurface	surfaces[] = null;
	
	final Paint	backPaint = new Paint();
	final Paint	normalPaint = new Paint();
	
	
	public Stage() {
		
		backPaint.setColor( Color.GREEN );
		normalPaint.setColor( Color.YELLOW );
		
		FPoint	pt2[] = new FPoint[] {
				new FPoint(pt[0]), new FPoint(pt[1]),
				new FPoint(pt[2]), new FPoint(pt[3]),
		};
		for( FPoint p : pt2 )
			p.z = 100;
		
		surfaces = new FSurface[] {
				new FSurface(pt[0],pt[1],pt2[0],pt2[1]),
				new FSurface(pt[1],pt[3],pt2[1],pt2[3]),
				new FSurface(pt[3],pt[2],pt2[3],pt2[2]),
				new FSurface(pt[2],pt[0],pt2[2],pt2[0]),
		};
		
		addChild( new Bar() );
	}

	@Override
	public Iterator<FSurface> getBoundsIter() {
		
		ArrayList<FSurface>	list = new ArrayList<FSurface>();
		
		list.addAll( Arrays.asList(surfaces) );
		
		Iterator<YNode>	it = childs();
		while( it.hasNext() ) {
			
			YNode	n = it.next();
			
		//	list.addAll( Arrays.asList( ((Bar)n).getCollisionBounds() ) );
			
		}
		
		return list.iterator();
	}
	
	/**********************************************
	 * 
	 * プロセス処理
	 * 
	 */
	@Override
	public void process(YNode parent, GameActivity h, YRendererList renderList ) {
		
		
		// 加速度
		float	gx = h.getGravityX();
		
		// 加速度センサ→重心位置→角加速度→角速度→角度（-0.5～0.5）
		
		
		// 角度
		float	th = (gx / 9.8f) * 0.5f;
		
		// 自前でチャイルド走査
		if( childList!=null ) {
			for( YNode c : childList ) {
				if( c instanceof Bar )
					((Bar) c).rotate( this, h, th );
				else
					c.process( this, h, renderList );
			}
		}
	}
	
	
	@Override
	public void paint(Canvas canvas) {
		
		for( FSurface s: surfaces ) {
			
			canvas.drawLine( s.p0.x, s.p0.y, s.p1.x, s.p1.y, backPaint );
			
			FVector	v = new FVector( s.p0, s.p1 );
			float	length = v.getScalar();
			v.normalize().scale( length / 2.0f );
			FPoint	np0 = new FPoint( s.p0 ).add( v );
			
			FPoint	np1 = new FPoint( np0 ).add( new FVector(s.normal).scale(30) );
			
			canvas.drawLine( np0.x, np0.y, np1.x, np1.y, normalPaint );
			
		}
		
		
		super.paint(canvas);
	}
	
	
}



class Bar extends YNode {
	
	/** 中心の座標 */
	FPoint	pos = new FPoint(500,400);
	
	public int	w = 500;
	public int	h = 50;
	
	/** 回転角 */
	public float	rotate = 0f;
	
	/** 頂点(ローカル座標系) */
	public FPoint[]	vertexies;
	
	/** 線データ(ワールド座標系、つまりアフィン変換後) */
	public FLine[]	lines;
	
	
	final Paint	backPaint = new Paint();
	
	Bar() {
		
		backPaint.setColor( Color.GREEN );
		
		float	x1 = -(w / 2);
		float	x2 = +(w / 2);
		float	y1 = -(h / 2);
		float	y2 = +(h / 2);
		
		vertexies = new FPoint[] {
			new FPoint(x1,y1), new FPoint(x2,y1),
			new FPoint(x1,y2), new FPoint(x2,y2),
		};
		
		lines = new FLine[]{
				new FLine( vertexies[0], vertexies[1] ),
				new FLine( vertexies[0], vertexies[2] ),
				new FLine( vertexies[1], vertexies[3] ),
				new FLine( vertexies[2], vertexies[3] ),
		};
	}
	
	
	public FLine[] getCollisionBounds() {
		
		return lines;
	}
	
	
	/**********************************
	 * 
	 * Ballを描画する
	 * @param canvas
	 */
	@Override
	public void paint( Canvas canvas ) {
		
		canvas.drawLine( vertexies[0].x, vertexies[0].y, vertexies[1].x, vertexies[1].y, backPaint );
		canvas.drawLine( vertexies[0].x, vertexies[0].y, vertexies[2].x, vertexies[2].y, backPaint );
		canvas.drawLine( vertexies[1].x, vertexies[1].y, vertexies[3].x, vertexies[3].y, backPaint );
		canvas.drawLine( vertexies[2].x, vertexies[2].y, vertexies[3].x, vertexies[3].y, backPaint );
	}
	
	
	/***************************************
	 * 
	 * 角度を設定する
	 * processの代わり
	 * 
	 * @param parent
	 * @param fh
	 */
	public void rotate( YNode parent, GameActivity fh, float th ) {
		
		this.rotate = th * (float)Math.PI;
		
		// アフィン変換
		FMatrix	mat = new FMatrix();
		
		mat.unit();
		mat.rotateZ( rotate );
		mat.translate( pos.x, pos.y, pos.z );
		
		
		
		float	x1 = -(w / 2);
		float	x2 = +(w / 2);
		float	y1 = -(h / 2);
		float	y2 = +(h / 2);
		float	z = 0f;
		
		mat.transform( x1,y1,z, vertexies[0] );
		mat.transform( x2,y1,z, vertexies[1] );
		mat.transform( x1,y2,z, vertexies[2] );
		mat.transform( x2,y2,z, vertexies[3] );
		
		
	//	YLog.info("bar",this.rotate);
	}

}
