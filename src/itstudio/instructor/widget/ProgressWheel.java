package itstudio.instructor.widget;
import com.easemob.chatuidemo.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
* 
* @Description ProgressWheel

* @author MR.Wang

* @date 2014-12-5 上午9:54:32 

* @version V1.0
 */
public class ProgressWheel extends View {
	
	private Context context;
	//Sizes (with defaults)
	private int fullRadius = 100;
	private int circleRadius = 80;
	private int barLength = 60;
	private int barWidth = 20;
	private int rimWidth = 20;
	private int textSize = 20;
	
	//Padding (with defaults)
	private int paddingTop = 5;
	private int paddingBottom = 5;
	private int paddingLeft = 5;
	private int paddingRight = 5;
	
	//Colors (with defaults)
	private int barColor = 0xAA000000;
	private int circleColor = 0x00000000;
	private int rimColor = 0xAADDDDDD;
	private int textColor = 0xFF000000;

	//Paints
	private Paint barPaint = new Paint();
	private Paint circlePaint = new Paint();
	private Paint rimPaint = new Paint();
	private Paint textPaint = new Paint();
	
	//Rectangles
	@SuppressWarnings("unused")
	private RectF rectBounds = new RectF();
	private RectF circleBounds = new RectF();
	
	//Animation
	//The amount of pixels to move the bar by on each draw
	private int spinSpeed = 2;
	//The number of milliseconds to wait inbetween each draw
	private int delayMillis = 0;
	private Handler spinHandler = new Handler() {
		/**
		 * This is the code that will increment the progress variable
		 * and so spin the wheel
		 */
		@Override
		public void handleMessage(Message msg) {
			invalidate();
			if(isSpinning) {
				progress+=spinSpeed;
				if(progress>360) {
					progress = 0;
				}
				spinHandler.sendEmptyMessageDelayed(0, delayMillis);
			}
			//super.handleMessage(msg);
		}
	};
	int progress = 0;
	boolean isSpinning = false;
	
	//Other
	private String text = "";
	private String[] splitText = {};
	
	/**
	 * The constructor for the ProgressWheel
	 * @param context
	 * @param attrs
	 */
	public ProgressWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		parseAttributes(context.obtainStyledAttributes(attrs, 
				R.styleable.ProgressWheel));
	}
	
	//----------------------------------
	//Setting up stuff
	//----------------------------------
	
	/**
	 * Now we know the dimensions of the view, setup the bounds and paints
	 */
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		setupBounds();
		setupPaints();
		invalidate();
	}
	
	/**
	 * Set the properties of the paints we're using to 
	 * draw the progress wheel
	 */
	private void setupPaints() {
		barPaint.setColor(barColor);
        barPaint.setAntiAlias(true);
        barPaint.setStyle(Style.STROKE);
        barPaint.setStrokeWidth(barWidth);
        
        rimPaint.setColor(rimColor);
        rimPaint.setAntiAlias(true);
        rimPaint.setStyle(Style.STROKE);
        rimPaint.setStrokeWidth(rimWidth);
        
        circlePaint.setColor(circleColor);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Style.FILL);
        
        textPaint.setColor(textColor);
        textPaint.setStyle(Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
	}

	/**
	 * Set the bounds of the component
	 */
	private void setupBounds() {
		paddingTop = this.getPaddingTop();
	    paddingBottom = this.getPaddingBottom();
	    paddingLeft = this.getPaddingLeft();
	    paddingRight = this.getPaddingRight();
		
		rectBounds = new RectF(paddingLeft,
				paddingTop,
                this.getLayoutParams().width - paddingRight,
                this.getLayoutParams().height - paddingBottom);
		
		circleBounds = new RectF(paddingLeft + barWidth,
				paddingTop + barWidth,
                this.getLayoutParams().width - paddingRight - barWidth,
                this.getLayoutParams().height - paddingBottom - barWidth);
		
		fullRadius = (this.getLayoutParams().width - paddingRight - barWidth)/2;
	    circleRadius = (fullRadius - barWidth) + 1;
	}
	private int countSpeed( float dpValue)
	{
		 //DisplayMetrics dm = context.getResources().getDisplayMetrics();
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue / scale );
	}
	/**
	 * Parse the attributes passed to the view from the XML
	 * @param a the attributes to parse
	 */
	private void parseAttributes(TypedArray a) {
		barWidth = (int) a.getDimension(R.styleable.ProgressWheel_barWidth,
			barWidth);
		
		rimWidth = (int) a.getDimension(R.styleable.ProgressWheel_rimWidth,
			rimWidth);
		
		spinSpeed = countSpeed(a.getDimension(R.styleable.ProgressWheel_spinSpeed,
			spinSpeed));
		delayMillis = countSpeed( a.getInteger(R.styleable.ProgressWheel_delayMillis,
			delayMillis));
		if(delayMillis<=0) {
			delayMillis = 4;
		}
	    
	    barColor = a.getColor(R.styleable.ProgressWheel_barColor, barColor);
	    
	    barLength = countSpeed(a.getDimension(R.styleable.ProgressWheel_barLength,
	    	barLength));
	    
	    textSize = (int) a.getDimension(R.styleable.ProgressWheel_textSize,
	    	textSize);
	    
	    textColor = a.getColor(R.styleable.ProgressWheel_textColor,
	    	textColor);
	    
	    setText(a.getString(R.styleable.ProgressWheel_text));
	    
	    rimColor = a.getColor(R.styleable.ProgressWheel_rimColor,
	    	rimColor);
	    
	    circleColor = a.getColor(R.styleable.ProgressWheel_circleColor,
	    	circleColor);
	}

	//----------------------------------
	//Animation stuff
	//----------------------------------
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//Draw the rim
		canvas.drawArc(circleBounds, 360, 360, false, rimPaint);
		//Draw the bar
		if(isSpinning) {
			canvas.drawArc(circleBounds, progress - 90, barLength, false,
				barPaint);
		} else {
			canvas.drawArc(circleBounds, -90, progress, false, barPaint);
		}
		//Draw the inner circle
		canvas.drawCircle((circleBounds.width()/2) + rimWidth + paddingLeft, 
				(circleBounds.height()/2) + rimWidth + paddingTop, 
				circleRadius, 
				circlePaint);
		float offset = textPaint.measureText((int) (progress/3.6)+"%") / 2;
		canvas.drawText((int) (progress/3.6)+"%", this.getWidth() / 2 - offset, 
			this.getHeight() / 2+(textSize/2), textPaint);
	}

	/**
	 * Reset the count (in increment mode)
	 */
	public void resetCount() {
		progress = 0;
		setText("0%");
		invalidate();
	}

	/**
	 * Turn off spin mode
	 */
	public void stopSpinning() {
		isSpinning = false;
		progress = 0;
		spinHandler.removeMessages(0);
	}
	
	
	/**
	 * Puts the view on spin mode
	 */
	public void spin() {
		isSpinning = true;
		spinHandler.sendEmptyMessage(0);
	}
	
	/**
	 * Increment the progress by 1 (of 360)
	 */
	public void incrementProgress() {
		isSpinning = false;
		progress++;
		setText(Math.round(((float)progress/360)*100) + "%");
		spinHandler.sendEmptyMessage(0);
	}

	/**
	 * Set the progress to a specific value
	 */
	public void setProgress(int i) {
	    isSpinning = false;
	    progress=i;
	    spinHandler.sendEmptyMessage(0);
	}
	
	//----------------------------------
	//Getters + setters
	//----------------------------------
	
	/**
	 * Set the text in the progress bar
	 * Doesn't invalidate the view
	 * @param text the text to show ('\n' constitutes a new line)
	 */
	public void setText(String text) {
		this.text = text;
		splitText = this.text.split("\n");
	}
	
	public int getCircleRadius() {
		return circleRadius;
	}

	public void setCircleRadius(int circleRadius) {
		this.circleRadius = circleRadius;
	}

	public int getBarLength() {
		return barLength;
	}

	public void setBarLength(int barLength) {
		this.barLength = barLength;
	}

	public int getBarWidth() {
		return barWidth;
	}

	public void setBarWidth(int barWidth) {
		this.barWidth = barWidth;
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	@Override
	public int getPaddingTop() {
		return paddingTop;
	}

	public void setPaddingTop(int paddingTop) {
		this.paddingTop = paddingTop;
	}

	@Override
	public int getPaddingBottom() {
		return paddingBottom;
	}

	public void setPaddingBottom(int paddingBottom) {
		this.paddingBottom = paddingBottom;
	}

	@Override
	public int getPaddingLeft() {
		return paddingLeft;
	}

	public void setPaddingLeft(int paddingLeft) {
		this.paddingLeft = paddingLeft;
	}

	@Override
	public int getPaddingRight() {
		return paddingRight;
	}

	public void setPaddingRight(int paddingRight) {
		this.paddingRight = paddingRight;
	}

	public int getBarColor() {
		return barColor;
	}

	public void setBarColor(int barColor) {
		this.barColor = barColor;
	}

	public int getCircleColor() {
		return circleColor;
	}

	public void setCircleColor(int circleColor) {
		this.circleColor = circleColor;
	}

	public int getRimColor() {
		return rimColor;
	}

	public void setRimColor(int rimColor) {
		this.rimColor = rimColor;
	}
	
	
	public Shader getRimShader() {
		return rimPaint.getShader();
	}

	public void setRimShader(Shader shader) {
		this.rimPaint.setShader(shader);
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}
	
	public int getSpinSpeed() {
		return spinSpeed;
	}

	public void setSpinSpeed(int spinSpeed) {
		this.spinSpeed = spinSpeed;
	}
	
	public int getRimWidth() {
		return rimWidth;
	}

	public void setRimWidth(int rimWidth) {
		this.rimWidth = rimWidth;
	}
	
	public int getDelayMillis() {
		return delayMillis;
	}

	public void setDelayMillis(int delayMillis) {
		this.delayMillis = delayMillis;
	}
}