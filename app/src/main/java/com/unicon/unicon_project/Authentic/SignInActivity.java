package com.unicon.unicon_project.Authentic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unicon.unicon_project.Classes.User;
import com.unicon.unicon_project.MainActivity;
import com.unicon.unicon_project.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.exception.KakaoException;

public class SignInActivity<mGoogleSignInClient> extends AppCompatActivity {
    LinearLayout text_register;
    TextView text_email, text_password;
    TextView btn_log_in;
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

        // ?????? ????????? ????????? ???????????? ??????????????? ????????? ????????? ????????????.
        // DEFAULT_SIGN_IN parameter??? ????????? ID??? ???????????? ????????? ????????? ??????????????? ????????????.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id1))
                .requestEmail() // email addresses??? ?????????
                .build();

        // ????????? ?????? GoogleSignInOptions??? ????????? GoogleSignInClient ????????? ??????
        mGoogleSignInClient = GoogleSignIn.getClient(SignInActivity.this, gso);
        mGoogleSignInClient.revokeAccess();

        firebaseAuth = FirebaseAuth.getInstance();


        //???????????? ???????????????
        if (Session.getCurrentSession().checkAndImplicitOpen()) {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onSuccess(MeV2Response result) {
                    goMainActivity();
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }
            });
        }//??????, ??? ????????? ???????????????
        else if (firebaseAuth.getCurrentUser() != null) {
            goMainActivity();
        }

        //????????????
        text_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);

            }
        });

        //??? ?????????
        btn_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = text_email.getText().toString().trim();
                String password = text_password.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignInActivity.this, "email ??? ???????????????", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignInActivity.this, "password ??? ???????????????", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            goMainActivity();
                        } else {
                            Log.e("###", "??? ????????? ??????");
                        }
                    }
                });
            }
        });

        //???????????? ?????????
        mSessionCallback = new SessionCallBack() {
            @Override
            public void onSessionOpened() {
                // ????????? ??????
                UserManagement.getInstance().me(new MeV2ResponseCallback() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        // ????????? ??????
                        Toast.makeText(SignInActivity.this, "???????????? ?????????????????????.. ?????? ??????????????????", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        // ????????? ????????? ??????
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        // ????????? ??????]
                        UserAccount kakaoAccount = result.getKakaoAccount();
                        String email = kakaoAccount.getEmail();
                        String pwd = String.valueOf(result.getId());
                        Profile profile = kakaoAccount.getProfile();
                        firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(
                                SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Log.e("###", "????????? ?????????????????? ?????? ??????");
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
                                            // ?????? ???????????? ????????? ??????
                                            Log.e("###", "????????? ?????????????????? ?????????");
                                            firebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        goMainActivity();
                                                    } else {
                                                        Toast.makeText(SignInActivity.this, "????????? ??????", Toast.LENGTH_SHORT).show();
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

        //?????? ?????????
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
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
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            newUser = new User();
                            newUser.setUsertoken(user.getUid());
                            newUser.setUsername("");
                            mstore.collection("User").document(newUser.getUsertoken())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()) {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                if(documentSnapshot.exists()){
                                                    Log.e("###", "?????? ???????????????");
                                                }
                                                else{
                                                    mstore.collection("User").document(newUser.getUsertoken())
                                                            .set(newUser)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                }
                                                            });
                                                }
                                            }
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

    // ?????? ?????????, ????????? ?????? ?????? X
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(mSessionCallback);
    }
}