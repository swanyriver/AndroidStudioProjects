public class EyeDraw{
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
