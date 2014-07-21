package EyeBalls;


import android.view.View;

/**
 * Created by Brandon on 12/6/13.
 */
public interface EyeFocusBrainInterface extends EyeFocus {

    static class EyeBrainID{

        public static final int NO_RESPONSE = -4;
        public static final int REQUEST_NEW_ID = -1; //to be passed in as id or REQUESTCODE
        public static final int NEW_ID_GRANTED = -5; //sent back in .Respone with new ID
        public static final int REGISTERED_ID = -6;
        public static final int NO_LONGER_FOCUSED =-7; //sent back when ID is not in map
        public static final int NO_ID = -2;
        public static final int BLANK = -3;

        @Override
        public String toString() {

            String responseString="";

            if(Response ==NO_RESPONSE)responseString="NO_RESPONSE";
            if(Response ==NEW_ID_GRANTED)responseString="NEW_ID_GRANTED";
            if(Response ==REGISTERED_ID)responseString="REGISTERED_ID";
            if(Response ==NO_LONGER_FOCUSED)responseString="NO_LONGER_FOCUSED";



            return new String("EyeBrainID #:"+ ID + " Response:" + responseString);
        }

        public static final EyeBrainID BLANK_ID= new EyeBrainID(EyeBrainID.BLANK);
        public static final EyeBrainID ID_FOR_NEW_ISSUE= new EyeBrainID(EyeBrainID.REQUEST_NEW_ID);



        int ID;
        int Response;

        public EyeBrainID(int ID) {
            this.ID = ID;
            Response = NO_RESPONSE;
        }
        public EyeBrainID(int ID, int currentresponse) {
            this.ID = ID;
            Response = currentresponse;
        }

        public int getID(){
            return ID;
        }

        public boolean equals(EyeBrainID otherID){
            if(getID()==otherID.getID()) return true;
            else return false;
        }


    }

    static final int NO_THOUGHTS_INT= -1;
    static EyeBrainID NO_THOUGHTS = new EyeBrainID(NO_THOUGHTS_INT);
    static final String NO_THOUGHTS_LOG = "this brain  has no thoughts on this message,  message passed to child's EYE FOCUS methods";
    static final String TAG = "BRAIN";




    EyeBrainID thinkFocusHere(int focusRequestX, int focusRequestY, int Index, EyeBrainID requestID);
    EyeBrainID thinkFocusHere(int focusRequestX, int focusRequestY, EyeBrainID requestID);
    EyeBrainID thinkFocusHere(final View view, EyeBrainID requestID);
    //EyeBrainID thinkRequest(int requestCode, EyeBrain requestID);
    EyeBrainID thinkRequest(int requestCode, EyeBrainID requestID);



}
