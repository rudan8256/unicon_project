package com.unicon.unicon_project.Authentic;

import android.util.Log;

import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.exception.KakaoException;

public class SessionCallBack implements ISessionCallback {
/*
    private Context mContext = null;

    public SessionCallBack(Context context){
        super(context);
        this.mContext = context;
    }*/
    @Override
    public void onSessionOpened() {
        requestMe();
    }

    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
    }

    public void requestMe() {
        Log.e("###","requestMe");
        // 사용자정보 요청 결과에 대한 Callback
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e("SessionCallback :: ", "onSessionClosed : " + errorResult.getErrorMessage());
            }

            // 사용자 정보 요청 실패
            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.e("###", "onFailure : " + errorResult.getErrorMessage());
            }

            @Override
            public void onSuccess(MeV2Response result) {

                Log.e("###","여기까지 옴1");
                String id = String.valueOf(result.getId());
                Log.e("###", "id : "+id);
                UserAccount kakaoAccount = result.getKakaoAccount();
                Log.e("###","여기까지 옴2");
                if (kakaoAccount != null) {

                    // 이메일
                    String email = kakaoAccount.getEmail();
                    if (email != null) {
                        Log.e("SessionCallback :: ", "onSuccess:email "+email);
                    }

                    // 프로필
                    Profile _profile = kakaoAccount.getProfile();

                    if (_profile != null) {

                        Log.e("SessionCallback :: ", "nickname: " + _profile.getNickname());

                    }
                    Log.e("###","여기까지 옴3");
                }else{
                    Log.i("KAKAO_API", "onSuccess: kakaoAccount null");
                }
            }
        });
    }
}