package com.example.unicon_project.Authentic;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unicon_project.Classes.User;
import com.example.unicon_project.MainActivity;
import com.example.unicon_project.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.sdk.user.model.Account;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.exception.KakaoException;

public class SignInActivity<mGoogleSignInClient> extends AppCompatActivity {
   LinearLayout text_register;
    TextView text_email, text_password;
   TextView  btn_log_in;
    FirebaseAuth firebaseAuth;
    LinearLayout btn_google;

    private SessionCallBack mSessionCallback;
    private long clickTime = 0;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    private User newUser;
    //private SessionCallBack sessionCallback = new SessionCallBack();

    @Override
    protected void onStart() {
        super.onStart();
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

        //카카오톡 자동로그인
        if (Session.getCurrentSession().checkAndImplicitOpen()) {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onSuccess(MeV2Response result) {
                    Log.e("###", "카카오 자동 로그인 성공");
                    goMainActivity();
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }
            });
        }//구글, 앱 로그인 자동로그인
        else if (firebaseAuth.getCurrentUser() != null) {
            Log.e("###", "파이어베이스 자동로그인 성공");
            goMainActivity();
        }

        //회원가입
        text_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);

            }
        });

        //앱 로그인
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
                            Log.e("###", "앱 로그인 실패");
                        }
                    }
                });
            }
        });

        //카카오톡 로그인
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
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        // 로그인 성공]
                        UserAccount kakaoAccount = result.getKakaoAccount();
                        String email = kakaoAccount.getEmail();
                        String pwd = String.valueOf(result.getId());
                        Profile profile = kakaoAccount.getProfile();
                        Log.e("###", "email: "+email+"pwd: "+pwd+"name: "+profile.getNickname());
                        firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(
                                SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Log.e("###", "카카오 파이어베이스 유저 생성");
                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            newUser = new User();
                                            newUser.setUsertoken(user.getUid());
                                            newUser.setUsername(profile.getNickname());
                                            mstore.collection("User").document(newUser.getUsertoken())
                                                    .set(newUser)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                        }
                                                    });
                                            goMainActivity();
                                        } else {
                                            // 이미 존재하는 아이디 일때
                                            Log.e("###", "카카오 파이어베이스 로그인");
                                            firebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
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
                                    }
                                });
                    }
                });
            }

            @Override
            public void onSessionOpenFailed(KakaoException exception) {

            }
        };
        Session.getCurrentSession().addCallback(mSessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();

        //구글 로그인
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                Log.e("###", "google log in");
                Log.e("###", "진행 1");
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.e("###", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());

                Log.e("###", "진행 2");
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("###", "Google sign in failed", e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("kapi.kakao.com");

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("###", "signInWithCredential:success");
                            Log.e("###", "진행 3");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            newUser = new User();
                            newUser.setUsertoken(user.getUid());
                            newUser.setUsername("");
                            mstore.collection("User").document(newUser.getUsertoken())
                                    .set(newUser)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                        }
                                    });
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

    private void goMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
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
}