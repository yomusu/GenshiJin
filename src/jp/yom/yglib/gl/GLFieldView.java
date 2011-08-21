package jp.yom.yglib.gl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.yom.yglib.YSignal;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;


/***************************************************
 * 
 * 
 * OpenGLのSurfaceView
 * 
 * onTouchイベントをハンドルする必要がある
 * 
 * @author Yomusu
 *
 */
public class GLFieldView extends GLSurfaceView {
	
	
	//====================================================
	// 外部公開インターフェースの宣言
	//====================================================
	
	public interface TouchListener {
		
		/** タッチイベントを */
		public boolean onTouchEvent( MotionEvent event );
	}
	
	
	//====================================================
	// シグナルの宣言
	//====================================================
	
	/** サーフェースが作成されたシグナル */
	public final YSignal<Boolean>	surfaceReadySignal = new YSignal<Boolean>();
	
	//====================================================
	// メンバの宣言
	//====================================================
	
	/** タッチイベントを受ける外部リスナー */
	protected TouchListener	touchListener = null;
	
	
	/** サーフェースの実サイズ */
	int	surfaceWidth, surfaceHeight;
	
	/** スクリーン座標系での画面サイズ */
	int	screenWidth = 640;
	int	screenHeight= 400;
	
	/** テクスチャ等の管理 */
	private final YGraphics	graphics = new YGraphics();
	
	/** レンダリングリスト */
	protected YRendererList	renderList = null;
	
	/** 登録要求のテクスチャ */
	protected ArrayList<TextureEntry>	textureEntry = new ArrayList<TextureEntry>();
	
	
	//====================================================
	// 外部公開メソッドの宣言
	//====================================================
	
	public GLFieldView( Context context, AttributeSet attr ) {
		
		super(context,attr);
		
		//-----------------------------
		// レンダラーの設定
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		
		//-----------------------------
		// レンダラーの設定
		setRenderer( viewrenderer );
		
		//-----------------------------
		// ダブルバッファ切り替えタイミングを必要な時のみとする
		setRenderMode( RENDERMODE_WHEN_DIRTY );
		
		//----------------------------------
		// Touchイベントの要求
	//	setFocusable( true );
	}
	
	
	/**************************************************************
	 * 
	 * タッチイベント取得リスナーを張る
	 * 
	 * @param listener
	 */
	public void setTouchListener( TouchListener listener ) {
		
		this.touchListener = listener;
	}
	
	/**************************************************************
	 * 
	 * 使用するテクスチャを登録する
	 * 
	 * 実際にテクスチャが読み込まれてOpenGL的に登録されるのは
	 * invokeDraw()を行い、GLスレッドで処理した時です。
	 * 
	 * @param resID		R.drawableのリソースID
	 * @param texKey
	 */
	public void entryTextures( List<TextureEntry> tex ) {
		synchronized( textureEntry ) {
			textureEntry.addAll( tex );
		}
	}
	
	
	/****************************************************
	 * 
	 * ダブルバッファの切り替え
	 * 
	 * @param root
	 */
	public void invokeDraw( YRendererList list ) {
		
		this.renderList= list;
		requestRender();
	}
	
	
	
	//====================================================
	// 内部メソッドの宣言
	//====================================================
	
	/*************************************************************
	 * 
	 * 
	 * レンダラー
	 * 
	 */
	GLSurfaceView.Renderer	viewrenderer = new GLSurfaceView.Renderer(){
		
		@Override
		public void onDrawFrame(GL10 gl) {
			
			//----------------------------------
			// テクスチャ要求があればそれを消化
			if( textureEntry.size() > 0 ) {
				synchronized( textureEntry ) {
					graphics.gl = gl;
					for( TextureEntry tex : textureEntry )
						graphics.addTexture( tex );
					
					// 消化したのでクリア
					textureEntry.clear();
				}
				// 上で登録したテクスチャをGLに転送
				graphics.loadTexture();
			}
			
			//----------------------------------
			// Rootノードの描画
			if( renderList!=null ) {
				graphics.gl = gl;
				Iterator<YRenderer>	it = renderList.iterator();
				while( it.hasNext() ) {
					YRenderer	r = it.next();
					r.render( graphics );
				}
			}
		}
		
		/** GLSurfaceに変更があった */
		@Override
		public void onSurfaceChanged(GL10 gl, int w, int h) {
			
			surfaceWidth = w;
			surfaceHeight = h;
			
			//-------------------------------
			// テクスチャの復帰
			// 本当に必要だよね？
			// しかし復帰処理に、ビットマップの読み込みを含むのかどうかが疑問
			graphics.gl = gl;
			graphics.loadTexture();

			//-------------------------------
			// ビューポートの設定
			// スクリーンとゲーム画面サイズの縦横比を比較して、縦と横、どちらを基準にするか判断する
			float	scale = 1.0f;
			if( ((float)surfaceWidth / (float)surfaceHeight) < ((float)screenWidth / (float)screenHeight) ) {
				scale = (float)surfaceWidth / (float)screenWidth;
			} else {
				scale = (float)surfaceHeight / (float)screenHeight;
			}
			
			int	vw = (int)((float)screenWidth * scale);
			int	vh = (int)((float)screenHeight * scale);
			
			gl.glViewport( (surfaceWidth-vw)/2, (surfaceHeight-vh)/2, vw, vh );
			Log.v("App","GL:Render.surfaceChanged");
			
			// シグナル
			surfaceReadySignal.setSignal( Boolean.TRUE );
		}
		
		/** Resumeするたびに呼ばれる */
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig eglconfig) {
			Log.v("App","GL:Render.surfaceCreated");
			// テクスチャを削除
			graphics.deleteAllTexture();
		}
	};
	
	
	/*************************************************
	 * 
	 * タッチイベント
	 * 
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		// リスナーに転送
		if( touchListener!=null )
			return touchListener.onTouchEvent( event );
		
		return false;
	}
	
	/****************************************************
	 * 
	 * サーフェイスが作成された
	 * 
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		super.surfaceCreated(holder);
		// ログ出力
		Log.v("App","GL:surfaceCreated");
	}
	
	
	/****************************************************
	 * 
	 * サーフェイスが破棄された
	 * アプリ、停止時（pauseではなくonStop）に呼ばれます
	 * 
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		super.surfaceDestroyed(holder);
		
		// テクスチャを削除
		graphics.deleteAllTexture();
		// ログ出力
		Log.v("App","GL:surfaceDestroy");
	}
}
