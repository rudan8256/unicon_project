package com.unicon.unicon_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unicon.unicon_project.Authentic.SessionCallBack;
import com.unicon.unicon_project.Authentic.SignInActivity;
import com.unicon.unicon_project.Authentic.SignUpActivity;
import com.unicon.unicon_project.Classes.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.util.ArrayList;
import java.util.List;

public class MyPage extends AppCompatActivity {

    TextView name, email;
    LinearLayout login_on, login_off;
    Button btn_sign_up, btn_log_in, btn_my_sale, btn_contract;
    LinearLayout layout_liked, layout_notify, layout_ask, layout_logout, layout_delete;
    ImageView btn_back, edit_nickname;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private SessionCallBack mSessionCallback = new SessionCallBack();
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private List<User> mDatas = new ArrayList<>();

    private String token = "";
    private GoogleSignInClient mGoogleSignInClient;
    private Button btn_log_out;
    Dialog nickname_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        Construter();

        if (firebaseAuth.getCurrentUser() != null) {
            login_off.setVisibility(View.GONE);
            email.setText(firebaseAuth.getCurrentUser().getEmail());
            mstore.collection("User")
                    .whereEqualTo("usertoken", firebaseAuth.getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                User userdata = document.toObject(User.class);

                                if (userdata.getUsername().equals("")) {
                                    name.setText("no name");
                                } else {
                                    name.setText(userdata.getUsername());
                                }

                            }
                        }
                    });
        } else {
            login_on.setVisibility(View.GONE);
        }

        // 앱에 필요한 사용자 데이터를 요청하도록 로그인 옵션을 설정한다.
        // DEFAULT_SIGN_IN parameter는 유저의 ID와 기본적인 프로필 정보를 요청하는데 사용된다.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id1))
                .requestEmail() // email addresses도 요청함
                .build();

        // 위에서 만든 GoogleSignInOptions을 사용해 GoogleSignInClient 객체를 만듬
        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPage.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_my_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btn_contract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        layout_liked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        layout_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        layout_ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        layout_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Session.getCurrentSession().checkAndImplicitOpen()) {
                    UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                        @Override
                        public void onSessionClosed(ErrorResult errorResult) {
                            super.onSessionClosed(errorResult);
                            Log.e("###", "onSessionClosed: " + errorResult.getErrorMessage());

                        }

                        @Override
                        public void onCompleteLogout() {
                            if (mSessionCallback != null) {
                                Session.getCurrentSession().removeCallback(mSessionCallback);
                            }
                        }
                    });
                }
                if (firebaseAuth.getCurrentUser() != null) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        user.getIdToken(true)
                                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                                        if (task.isSuccessful()) {
                                            //성공시
                                        }
                                    }
                                });
                    }
                    firebaseAuth.signOut();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

        });
        layout_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mstore.collection("User").document(firebaseAuth.getCurrentUser().getUid())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if (Session.getCurrentSession().checkAndImplicitOpen()) {
                                    UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                                        @Override
                                        public void onSessionClosed(ErrorResult errorResult) {
                                            super.onSessionClosed(errorResult);
                                        }

                                        @Override
                                        public void onCompleteLogout() {
                                            if (mSessionCallback != null) {
                                                Session.getCurrentSession().removeCallback(mSessionCallback);
                                            }
                                        }
                                    });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("###", "회원탈퇴 실패");
                            }
                        });
                firebaseAuth.getCurrentUser()
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.e("###", "회원탈퇴 성공");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("###", " "+e);
                            }
                        });

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        nickname_dialog = new Dialog(this);
        nickname_dialog.setContentView(R.layout.dialog_edit_nickname);
        nickname_dialog.setCanceledOnTouchOutside(true);
        nickname_dialog.findViewById(R.id.complete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = (EditText) nickname_dialog.findViewById(R.id.text_nickname);
                String nickname = text.getText().toString();
                Log.e("###", firebaseAuth.getCurrentUser().getUid());
                Log.e("###", nickname);
                if (!nickname.equals("")) {
                    mstore.collection("User").document(firebaseAuth.getUid()).update("username", nickname);
                    name.setText(nickname);
                }
                nickname_dialog.dismiss();
            }
        });

        nickname_dialog.findViewById(R.id.dialog_cancelbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nickname_dialog.dismiss();
            }
        });
        edit_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nickname_dialog.show();
            }
        });
    }

    private void Construter() {
        login_off = findViewById(R.id.login_off);
        login_on = findViewById(R.id.login_on);
        btn_back = findViewById(R.id.back_acticity);
        btn_sign_up = findViewById(R.id.btn_signup);
        btn_log_in = findViewById(R.id.btn_login);
        btn_my_sale = findViewById(R.id.btn_my_sale);
        btn_contract = findViewById(R.id.btn_contract);

        layout_liked = findViewById(R.id.layout_liked);
        layout_notify = findViewById(R.id.layout_notify);
        layout_ask = findViewById(R.id.layout_ask);
        layout_logout = findViewById(R.id.layout_logout);
        layout_delete = findViewById(R.id.layout_delete);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);

        edit_nickname = findViewById(R.id.edit_nickname);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }
}