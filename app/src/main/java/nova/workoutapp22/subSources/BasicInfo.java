package nova.workoutapp22.subSources;

/**
 * Created by jamsy on 2017-08-31.
 */

public class BasicInfo {

    //////////어싱크태스크 관련 상수들
    public static final Long RESULT_SUCCESS = Long.parseLong("0");
    public static final Long RESULT_FAIL = Long.parseLong("1");


    /////////////////////////////////카메라 관련 상수들
    public static final int CHOOSE_IMAGE = 300;
    public static final int PICK_FROM_ALBUM = 301;
    public static final int PICK_FROM_CAMERA = 302;
    public static final int CROP_FROM_IMAGE = 303;

    //========== 인텐트 부가정보 전달을 위한 키값 ==========//
    public static final String KEY_ADDMEMO_MODE = "MEMO_MODE";
    public static final String KEY_MEMO_TEXT = "MEMO_TEXT";
    public static final String KEY_ADDWO_MODE = "ADDWO_MODE";

    public static final String KEY_MEMO_ID = "MEMO_ID";
    public static final String KEY_MEMO_DATE = "MEMO_DATE";
    public static final String KEY_ID_PHOTO = "ID_PHOTO";
    public static final String KEY_URI_PHOTO = "URI_PHOTO";
    public static final String KEY_ID_VIDEO = "ID_VIDEO";
    public static final String KEY_URI_VIDEO = "URI_VIDEO";
    public static final String KEY_ID_VOICE = "ID_VOICE";
    public static final String KEY_URI_VOICE = "URI_VOICE";
    public static final String KEY_ID_HANDWRITING = "ID_HANDWRITING";
    public static final String KEY_URI_HANDWRITING = "URI_HANDWRITING";

    //========== 메모 모드 상수 ==========//
    public static final String MODE_ADD = "MODE_ADD";
    public static final String MODE_MODIFY = "MODE_MODIFY";
    public static final String MODE_VIEW = "MODE_VIEW";



    //========== 액티비티 요청 코드  ==========//
    public static final int REQ_MODIFY_MEMO = 1001;
    public static final int REQ_ADD_MEMO = 1002;

    public static final int REQ_ADD_WORKOUT = 2001;
    public static final int REQ_MODIFY_WORKOUT = 2002;

    public static final int REQ_PHOTO_CAPTURE_ACTIVITY = 1501;
    public static final int REQ_PHOTO_SELECTION_ACTIVITY = 1502;

    public static final int REQ_ZOOM = 3001;
    public static final int REQ_WATCH = 4001;


    /// 메뉴 세팅 관련 값들 ///

    public static final String MENU_WO_NORMAL = "WO_NORMAL";

    public static final String MENU_WO_MULT = "WO_MULT";

    public static final String MENU_ADDWO_NORMAL = "ADDWO_NORMAL";
    public static final String MENU_ADDWO_EDIT = "ADDWO_EDIT";
    public static final String MODE_ZOOM = "MODE_ZOOM" ;


    ///체크박스 관련 상수

    public static final boolean BOX_GONE = false;
    public static final boolean BOX_VISIBLE = true;

    // 어댑터 아이템 관련 상수 (겟뷰에서 사용)

    public static final int keyInt_imageView = 1;
    public static final int keyInt_checkBox = 2;

}
