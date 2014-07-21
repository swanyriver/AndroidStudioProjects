package EyeBalls;

import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Brandon on 12/12/13.
 */
public abstract class SmartEyeBrainwithIDs extends EyeBrain{

    private ArrayList<EyeBrainID> mEyeIDs = new ArrayList<EyeBrainID>();
    private int mIndexOfFocusedID;
    private int mBrainIDLastIssueNumber=0;


    public SmartEyeBrainwithIDs(EyeFocus eyeFocus) {
        super(eyeFocus);
    }

    @Override
    public EyeBrainID thinkFocusHere(int focusRequestX, int focusRequestY, int Index, EyeBrainID requestID) {


        if(requestID.equals(EyeBrainID.REQUEST_NEW_ID)){

            EyeBrainID issuedID = getNewIssuedID();
            thinkFocusHere(focusRequestX, focusRequestY, Index, issuedID);
            return issuedID;
        }

        return EyeBrainID.BLANK_ID;

    }

    @Override
    public EyeBrainID thinkFocusHere(int focusRequestX, int focusRequestY, EyeBrainID requestID) {

        //todo super. does not end function   localvariable = super. captures the return
        //todo if thinkRequest id, create id, add id to list, return id
        if(requestID.equals(EyeBrainID.REQUEST_NEW_ID)){

            EyeBrainID issuedID = getNewIssuedID();
            thinkFocusHere(focusRequestX, focusRequestY, issuedID);
            return issuedID;
        }
        return EyeBrainID.BLANK_ID;
    }

    @Override
    public EyeBrainID thinkFocusHere(View view, EyeBrainID requestID) {

        if(requestID.equals(EyeBrainID.ID_FOR_NEW_ISSUE)){
            //Log.d("smartWatch", "trying to get an id for a view focus");

            EyeBrainID issuedID = getNewIssuedID();
            //Log.d("smartWatch", "views id is " + issuedID.toString());
            thinkFocusHere(view, issuedID);
            return issuedID;
        }
        //setIsFocused(true); //todo  need a map with eyeIDs and focusStates  INSTEAD check if id is in map, return no longer focused
        new EyeWatchTimer(view,this,requestID);

        return EyeBrainID.BLANK_ID;
    }

    @Override
    public EyeBrainID thinkRequest(int requestCode, EyeBrainID requestID) {

        if(requestCode== EyeBrainID.REQUEST_NEW_ID||requestID.equals(EyeBrainID.ID_FOR_NEW_ISSUE)){
            return getNewIssuedID();
        }
        if(requestCode==EyeBall.PLEASE_LOOSE_FOCUS) pleaseLooseFocus(requestID);
        return EyeBrainID.BLANK_ID;
    }

    protected abstract void pleaseLooseFocus(EyeBrainID requestID);

    private EyeBrainID getNewIssuedID(){

        mBrainIDLastIssueNumber++;
        EyeBrainID issuedID=new EyeBrainID(mBrainIDLastIssueNumber,EyeBrainID.NEW_ID_GRANTED);
        mEyeIDs.add(issuedID);
        //Log.d("smartBrain", "new id issued " + issuedID.toString());
        onIDIssued(issuedID);
        return issuedID;

    }

    protected abstract void onIDIssued(EyeBrainID issuedID);


    @Override
    public void focusHere(int focusRequestX, int focusRequestY) {

        //do nothing
    }

    @Override
    public void request(int RequestCode) {
        //do nothing
    }

    @Override
    public void focusHere(View view) {
        //do nothing
    }

    @Override
    public void focusHere(int focusRequestX, int focusRequestY, int index) {
        //do nothing
    }

    public static abstract class SmartBrainWithIDTouchListener implements View.OnTouchListener{

        //private ArrayList<EyeBrainID> mEyeIDs = new ArrayList<EyeBrainID>();
        //private ArrayList<Integer> mPointerIDs = new ArrayList<Integer>();
        //private LinkedList<EyeBrainID> mEyeIDs = new LinkedList<EyeBrainID>();
        //private LinkedList<Integer> mPointerIDs = new LinkedList<Integer>();
        //todo should these be LinkedList,  what about a brain that doesnt want to do FILO
        //todo do i need the seperate lists with the map available
        //private Map<Integer, EyeBrainID> mFingerRegistry;
        private Hashtable<Integer, EyeBrainID> mFingerRegistry = new Hashtable<Integer, EyeBrainID>();

        protected EyeFocusBrainInterface mTouchEyeBrain;

        protected SmartBrainWithIDTouchListener(EyeFocusBrainInterface eyeBrain) {
            this.mTouchEyeBrain = eyeBrain;
        }

        protected EyeFocusBrainInterface sendTouchMessage(){
            return mTouchEyeBrain;
        }

        @Override
        public boolean onTouch(View v, MotionEvent ev) {
           // mFingerRegistry.get(event.getPointerId(0));

            final int action = ev.getAction();
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    onFirstFingerDown(ev);
                    break;
                }
                case MotionEvent.ACTION_POINTER_DOWN:{
                    onPointerDown(ev);
                }

                case MotionEvent.ACTION_MOVE: {

                    onFingerMove(ev);

                    break;
                }

                case MotionEvent.ACTION_UP: {
                   onAllFingersUp(ev);
                    break;
                }

                case MotionEvent.ACTION_CANCEL: {

                    onFingerCancel();

                    break;
                }

                case MotionEvent.ACTION_POINTER_UP: {
                    // Extract the index of the pointer that left the touch sensor
                    onPointerUp(ev);


                    break;
                }
            }


            return false;

        }




        public void onAllFingersUp(MotionEvent ev) {
            int currentFingerID = ev.getPointerId(ev.getActionIndex());
            EyeBrainID currentID = mFingerRegistry.get(currentFingerID);

                int x = (int) ev.getX(ev.getActionIndex());
                int y = (int) ev.getX(ev.getActionIndex());

                mFingerRegistry.clear();
                //mPointerIDs.clear();
                //mEyeIDs.clear();

                focusAllFingersUp(x, y, currentID);




        }



        public void onPointerUp(MotionEvent ev) {
            final int action = ev.getAction();
            final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                    >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
            final int pointerId = ev.getPointerId(pointerIndex);



            EyeBrainID currentID = mFingerRegistry.get(pointerId);
            if(currentID!=null){
                int x = (int) ev.getX(pointerIndex);
                int y = (int) ev.getX(pointerIndex);

                mFingerRegistry.remove(pointerId);
                //mPointerIDs.remove(pointerId);
                //mEyeIDs.remove(currentID);

                focusPointerUp(x,y,currentID);

            }
        }




        public void onFirstFingerDown(MotionEvent ev) {
            assignID(ev);


        }

        public void assignID(MotionEvent ev) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();

            int currentFingerID = ev.getPointerId(ev.getActionIndex());


            EyeBrainID issuedEyeId = sendTouchMessage().thinkRequest(EyeBrainID.REQUEST_NEW_ID, EyeBrainID.ID_FOR_NEW_ISSUE);
            if(issuedEyeId!=null){
                if(issuedEyeId.Response == EyeBrainID.NEW_ID_GRANTED){

                    //mPointerIDs.addLast(currentFingerID);
                    issuedEyeId.Response=EyeBrainID.REGISTERED_ID;
                   // mEyeIDs.addLast(issuedEyeId);
                    mFingerRegistry.put(currentFingerID, issuedEyeId);

                    fingerDownFocus(x, y, issuedEyeId);
                }
            }

            //Log.d("smart","map is " + mFingerRegistry.toString());
            //Log.d("smart","action down/pointerdown");
            //Log.d("smart","pointer id is" + currentFingerID);
            //Log.d("smart","pointers ids are "+ mPointerIDs.toString());
        }

        public void onPointerDown(MotionEvent ev){
            assignID(ev);

        }
        public void onFingerMove(MotionEvent ev) {
            for(int index=0; index<ev.getPointerCount();index++){
                //Log.d("smartBrain","checking for idnum");
                EyeBrainID currentID = mFingerRegistry.get(ev.getPointerId(index));
                if(currentID!=null){
                    int x = (int) ev.getX(index);
                    int y = (int) ev.getY(index);

                    //Log.d("smartBrain","finger moved with "+ currentID.toString());

                    fingerMoveFocus(x, y, currentID);

                }
            }
        }



        protected abstract void fingerDownFocus(int x, int y, EyeBrainID currentEyeID);
        protected abstract void fingerMoveFocus(int x, int y, EyeBrainID currentID);
        protected abstract void focusPointerUp(int x, int y, EyeBrainID currentID);
        protected abstract void focusAllFingersUp(int x, int y, EyeBrainID currentID);
        protected abstract void onFingerCancel();

        //todo for each motion event call these overidable methodcalls

    }

    public static class SmartBrainWithIDTouchListenerAdapter extends SmartBrainWithIDTouchListener{

        public SmartBrainWithIDTouchListenerAdapter(EyeFocusBrainInterface eyeBrain) {
            super(eyeBrain);
        }

    //todo write default indiscriminant passthrough for these methods

        @Override
        protected void fingerDownFocus(int x, int y, EyeBrainID currentEyeID) {

            sendTouchMessage().thinkFocusHere(x, y, currentEyeID);


        }

        @Override
        protected void fingerMoveFocus(int x, int y, EyeBrainID currentEyeID) {

            sendTouchMessage().thinkFocusHere(x, y, currentEyeID);

        }

        @Override
        protected void focusPointerUp(int x, int y, EyeBrainID currentEyeID) {

            sendTouchMessage().thinkRequest(EyeBall.PLEASE_LOOSE_FOCUS, currentEyeID);

        }

        @Override
        protected void focusAllFingersUp(int x, int y, EyeBrainID currentEyeID) {

            sendTouchMessage().request(EyeBall.PLEASE_LOOSE_FOCUS); //sends stupid message to all children   overide if you want better behavior
        }

        @Override
        protected void onFingerCancel() {
            sendTouchMessage().request(EyeBall.PLEASE_LOOSE_FOCUS); //sends stupid message to all children   overide if you want better behavior

        }
    }


}
