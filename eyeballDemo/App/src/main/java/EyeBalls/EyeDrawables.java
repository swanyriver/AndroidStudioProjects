package EyeBalls;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by Brandon on 12/5/13.
 */
public class EyeDrawables {

    private static EyeDraw[] EyeDrawableList = new EyeDraw[]{

            new EyeDraw("bloodshot_cartoon_pupil_transparent","bloodshot_cartoon_pupil_transparent",.6f,.8f),
            new EyeDraw("terminator_eye_socket_trans","robot_pupil_trans",.6f,.6f,3),
            new EyeDraw("bloodshot_cartoon_pupil_transparent","bloodshot_cartoon_pupil_transparent",.6f,.8f,2),
            new EyeDraw("terminator_eye_socket_trans","robot_pupil_trans",.6f,.6f),
            new EyeDraw("terminator_eye_socket_trans","robot_pupil_trans",.5f,.25f,.5f),

            new EyeDraw("pupil"),
            new EyeDraw("eye_background","pupil",.75f,.5f),

    };



    public static int getNumDrawables(){
        return EyeDrawableList.length;
    }

    public static EyeDraw getEyeDraw(int index, Context context) {
        EyeDraw myEyedraw = EyeDrawables.EyeDrawableList[index];
        String packageName = context.getPackageName();

        myEyedraw.pupil_resource_id=context.getResources().getIdentifier(packageName+":drawable/" + myEyedraw.pupil_resource_name,null,null);
        myEyedraw.background_resource_id=context.getResources().getIdentifier(packageName+":drawable/" + myEyedraw.background_resource_name,null,null);

        //Log.d("EyeDraw", "" + packageName + ":drawable/" + myEyedraw.pupil_resource_name);
        //Log.d("EyeDraw", "" + packageName + ":drawable/" + myEyedraw.background_resource_name);
        //Log.d("EyeDraw", "IDs are:"+myEyedraw.pupil_resource_id + " " + myEyedraw.background_resource_id);

        return myEyedraw;
    }

    public static Bitmap drawEye(Context context, int width, int height, int index) {
        return drawEye(context, width, height, getEyeDraw(index,context));
    }


    public static Bitmap drawEye(Context context, int width, int height, EyeDraw eyeDraw) {


        //setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        int mHeight = width;
        int mWidth = height;
        //mEyeballCenterLocalX = mWidth / 2;
        //mEyeballCenterLocalY = mHeight/2;

        int eyeheight = (int) (mHeight * eyeDraw.percent_eye_white_vertical);
        int eyeWidth = (int) (mWidth * eyeDraw.percent_eye_white_horizantal);
        int mRadius;
        if(eyeWidth>eyeheight) mRadius=eyeheight/2;
        else mRadius=eyeWidth/2;


        float ovalOffsetVertical = (mHeight-eyeheight)/2;
        float ovalOffsetHorizantal = (mWidth-eyeWidth)/2;
        RectF ovalRect = new RectF(0+ovalOffsetHorizantal,0+ovalOffsetVertical,mWidth-ovalOffsetHorizantal,mHeight-ovalOffsetVertical);


        ////////////////////////////////
        ////////create pupil bitmap
        ///////////////////////////////
        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inMutable = true;
        //mPupilBM = BitmapFactory.decodeResource(getResources(),R.drawable.pupil, myOptions);
        Bitmap mPupilBM = BitmapFactory.decodeResource(context.getResources(),eyeDraw.pupil_resource_id, myOptions);
        //mPupilBM = Bitmap.createScaledBitmap(mPupilBM, (int) (mRadius * 2 / 8 * 7), (int) (mRadius * 2 / 8 * 7), false);
        mPupilBM = Bitmap.createScaledBitmap(mPupilBM, (int) (mRadius*2*eyeDraw.pupil_size), (int) (mRadius*2*eyeDraw.pupil_size), false);
        float mOffsetforPupil = (mPupilBM.getWidth()/2)*-1;


        ////////////////////////////////
        ////////draw black border (full oval)
        ///////////////////////////////
        myOptions.inMutable = true;
        myOptions.outHeight=mHeight;
        myOptions.outWidth=mWidth;
        //Bitmap circleBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Bitmap circleBitmap = BitmapFactory.decodeResource(context.getResources(),eyeDraw.background_resource_id, myOptions);
        circleBitmap = Bitmap.createScaledBitmap(circleBitmap, mWidth, mHeight, false);
        Canvas circleCanvas = new Canvas(circleBitmap);
        Paint circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setAntiAlias(true);

        Path mCirclePath = new Path();

        mCirclePath.addOval(ovalRect, Path.Direction.CW);
        mCirclePath.close();

        circleCanvas.drawPath(mCirclePath, circlePaint);



        ////////////////////////////////
        ////////draw white innercicle and create clippath
        ///////////////////////////////
        float margin = (float) (mRadius*.05);
        ovalRect.top+=margin;
        ovalRect.bottom-=margin;
        ovalRect.right-=margin;
        ovalRect.left+=margin;

        mCirclePath.reset();
        mCirclePath.addOval(ovalRect, Path.Direction.CW);
        mCirclePath.close();

        circlePaint.setColor(Color.WHITE);
        circleCanvas.drawPath(mCirclePath, circlePaint);


        //view.setLayerType(View.LAYER_TYPE_SOFTWARE, null); //will need to do this if i decide that pupil can be bigger than white


        int mPupilCenterX = mWidth/2;
        int mPupilCenterY = mHeight/2;



        circleCanvas.clipPath(mCirclePath);
        circleCanvas.drawBitmap(mPupilBM,mPupilCenterX+mOffsetforPupil,mPupilCenterY+mOffsetforPupil,new Paint());

        return circleBitmap;

    }



}
class EyeDraw{
    public String background_resource_name;
    public String pupil_resource_name;
    public float percent_eye_white_horizantal; //1 = equals all white no background,  .5 would be half eyewhite
    public float percent_eye_white_vertical; //1 = equals all white no background,  .5 would be half eyewhite
    public float pupil_size;

    static final float PUPIL_SIZE_DEFAULT=.875f;

    public int background_resource_id;
    public int pupil_resource_id;

    EyeDraw(String background_resource_name, String pupil_resource_name, float percent_eye_white_horizantal, float percent_eye_white_vertical) {
        this.background_resource_name = background_resource_name;
        this.pupil_resource_name = pupil_resource_name;
        this.percent_eye_white_horizantal = percent_eye_white_horizantal;
        this.percent_eye_white_vertical = percent_eye_white_vertical;
        this.pupil_size=PUPIL_SIZE_DEFAULT;

    }

    EyeDraw(String background_resource_name, String pupil_resource_name, float percent_eye_white_horizantal, float percent_eye_white_vertical, float pupilSize) {
        this.background_resource_name = background_resource_name;
        this.pupil_resource_name = pupil_resource_name;
        this.percent_eye_white_horizantal = percent_eye_white_horizantal;
        this.percent_eye_white_vertical = percent_eye_white_vertical;
        this.pupil_size=pupilSize;
    }

    EyeDraw(String pupil_resource_name) {
        this.background_resource_name = "no_background";
        this.pupil_resource_name = pupil_resource_name;
        this.percent_eye_white_horizantal = 1f;
        this.percent_eye_white_vertical = 1f;
        this.pupil_size=PUPIL_SIZE_DEFAULT;

    }



}


