package SwansonLibrary;


import android.view.MotionEvent;
import android.view.View;



public abstract class oneFingerMoveListener implements View.OnTouchListener{


    public abstract void touchDown(float x, float y);
    public abstract void touchAt(float x, float y);
    public abstract void touchOver();

    private int mFinger1id;

    @Override
    public boolean onTouch(View view, MotionEvent event) {


        int indexDown = event.getActionIndex();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:

                mFinger1id = event.getPointerId(indexDown);
                touchDown(event.getX(),event.getY());



                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                //Log.d("touch","action pointer down");

                break;
            case MotionEvent.ACTION_POINTER_UP:





                break;
            case MotionEvent.ACTION_UP:

               touchOver();


                break;


            case MotionEvent.ACTION_CANCEL:
               touchOver();

                break;

            case MotionEvent.ACTION_MOVE:
                // Log.d("touch", "action move");




                for(int x=0;x<event.getPointerCount();x++)
                {

                    if(event.getPointerId(x)==mFinger1id){


                       touchAt(event.getX(x),event.getY(x));
                        //Log.d("touch", "action move x:"+ event.getX(x) + " Y:" +event.getY(x));

                    }

                }



                break;

            default:
                break;
        }



        return false;
    }


}