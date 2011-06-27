package jp.yom.yglib.node;

import java.util.ArrayList;
import java.util.Iterator;

import jp.yom.yglib.AppToolkit;
import jp.yom.yglib.gl.YRendererList;
import android.graphics.Canvas;
import android.view.MotionEvent;



/**************************************************
 * 
 * 
 * ノードの基本
 * 
 * @author Yomusu
 *
 */
public class YNode {
	
	
	/** オブジェクトリスト */
	protected ArrayList<YNode>	childList = null;
	
	
	/************************************
	 * 
	 * チャイルドを追加する
	 * 
	 * @param child
	 */
	public void addChild( YNode child ) {
		
		if( childList==null )
			childList = new ArrayList<YNode>();
		
		childList.add( child );
	}
	
	/************************************
	 * 
	 * チャイルドを削除する
	 * 
	 * @param child
	 */
	public void removeChild( YNode child ) {
		
		if( childList!=null )
			childList.remove( child );
	}
	
	
	/**************************************
	 * 
	 * チャイルドイテレーター
	 * 
	 * @return
	 */
	public Iterator<YNode> childs() {
		return childList.iterator();
	}
	
	
	/**************************************
	 * 
	 * 座標からそれに適合するノードを取得します
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public YNode getTouchableNodeAtPoint( float x, float y ) {
		
		// チャイルドを呼ぶ
		if( childList!=null ) {
			for( YNode c : childList ) {
				YNode	n = c.getTouchableNodeAtPoint(x,y);
				if( n!=null )
					return n;
			}
		}
		
		return null;
	}
	
	
	/**************************************
	 * 
	 * タッチイベント
	 */
	public void onTouch( MotionEvent e ) {
	}
	
	/************************************
	 * 
	 * 描画を行う
	 * @param canvas
	 */
	public void paint( Canvas canvas ) {
		
		// チャイルドを呼ぶ
		if( childList!=null ) {
			for( YNode c : childList )
				c.paint( canvas );
		}
	}
	
	
	/************************************
	 * 
	 * 移動等行う
	 * 
	 * @param h
	 */
	public void process( YNode parent, AppToolkit app, YRendererList renderList ) {
		
		// チャイルドを呼ぶ
		if( childList!=null ) {
			for( YNode c : childList )
				c.process( this, app, renderList );
		}
	}
}
