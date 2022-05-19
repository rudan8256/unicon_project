package com.example.unicon_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.exception.KakaoException;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class SignInActivity<mGoogleSignInClient> extends AppCompatActivity {
    TextView text_register;
    TextView text_email, text_password;
    Button btn_log_in;
    FirebaseAuth firebaseAuth;
    SignInButton btn_google;

    private SessionCallBack mSessionCallback;
    private long clickTime = 0;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    //private SessionCallBack sessionCallback = new SessionCallBack();

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
        if (currentUser != null) {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        }

        Log.e("###", "onStart");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Log.e("###", "onCreate");
        text_register = findViewById(R.id.text_register);
        text_email = findViewById(R.id.text_email);
        text_password = findViewById(R.id.text_password);
        btn_log_in = findViewById(R.id.btn_log_in);
        btn_google = findViewById(R.id.btn_google);

        // 앱에 필요한 사용자 데이터를 요청하도록 로그인 옵션을 설정한다.
        // DEFAULT_SIGN_IN parameter는 유저의 ID와 기본적인 프로필 정보를 요청하는데 사용된다.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id1))
                .requestEmail() // email addresses도 요청함
                .build();

        // 위에서 만든 GoogleSignInOptions을 사용해 GoogleSignInClient 객체를 만듬
        mGoogleSignInClient = GoogleSignIn.getClient(SignInActivity.this, gso);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            Log.e("###", "firebase automatic log in");
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        if (Session.getCurrentSession().checkAndImplicitOpen()) {
            // 사용자의 세션이 유효하므로 카카오 사용자 정보를 요청한다.
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                    @Override
                    public void onSuccess(MeV2Response result) {
                        Log.e("###", "kakao automatic log in");
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                    }
            });
        }

        text_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = text_email.getText().toString().trim();
                String password = text_password.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignInActivity.this, "email 을 입력하시오", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignInActivity.this, "password 을 입력하시오", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            goMainActivity();
                        } else {
                            Toast.makeText(SignInActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mSessionCallback = new SessionCallBack() {
            @Override
            public void onSessionOpened() {
                // 로그인 요청
                UserManagement.getInstance().me(new MeV2ResponseCallback() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        // 로그인 실패
                        Toast.makeText(SignInActivity.this, "로그인에 실패하였습니다.. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        // 로그인 세션이 닫힘
                        // Toast.makeText(KakaoLogin.this, "세션이 닫혔습니다.. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        // 로그인 성공
                        // result에 카카오 항목들의 정보가 담겨 있음
                        Intent intent=new Intent(SignInActivity.this,MainActivity.class);
                       /* intent.putExtra("name",result.getKakaoAccount().getProfile().getNickname());
                        intent.putExtra("profileImg",result.getKakaoAccount().getProfile().getProfileImageUrl());
                        intent.putExtra("state","on");*/
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onSessionOpenFailed(KakaoException exception) {

            }
        };
        Session.getCurrentSession().addCallback(mSessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();

        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                Log.e("###", "google log in");
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(Session.getCurrentSession().handleActivityResult(requestCode,resultCode,data)) {
            return;
            //super.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.e("###", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("###", "Google sign in failed", e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("###", "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                            goMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("###", "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    private void goMainActivity(){
        Intent intent = new Intent(SignInActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateUI(FirebaseUser user) {

    }
    // 세션 없애기, 로그인 계속 유지 X
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(mSessionCallback);
    }
    // 뒤로가기 버튼 2번 누를 시에 앱 종료
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(SystemClock.elapsedRealtime() - clickTime < 2000) {
                finish();
                overridePendingTransition(0,0);
                return true;
            }
            clickTime = SystemClock.elapsedRealtime();
            Toast.makeText(getApplication(),"한번 더 클릭하시면 앱을 종료합니다",Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}