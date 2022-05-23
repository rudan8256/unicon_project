package com.example.unicon_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.unicon_project.Authentic.SessionCallBack;
import com.example.unicon_project.Authentic.SignInActivity;
import com.example.unicon_project.Pages.MapTest;
import com.example.unicon_project.Pages.PurchaseList;
import com.example.unicon_project.Pages.PurchasePage;
import com.example.unicon_project.Pages.SaleList;
import com.example.unicon_project.Pages.SalePage;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class MainActivity extends AppCompatActivity {

    Button ToSalePage, ToPurchasePage, ToMapTest, ToSaleList, ToPurchaseList,ToRecommendPage;

    FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button btn_log_out, btn_toChatting;
    FirebaseAuth firebaseAuth;
    private long clickTime = 0;
    private SessionCallBack mSessionCallback = new SessionCallBack();

    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToSalePage = findViewById(R.id.to_SalePage);
        ToPurchasePage = findViewById(R.id.to_PurchasePage);
        ToMapTest = findViewById(R.id.to_MapTest);
        ToSaleList = findViewById(R.id.to_SaleList);
        ToPurchaseList = findViewById(R.id.to_PurchaseList);
        ToRecommendPage = findViewById(R.id.to_RecommendePage);

        ToRecommendPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SalePage.class);
                startActivity(intent);
            }
        });

        ToSalePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SalePage.class);
                startActivity(intent);
            }
        });
        ToPurchasePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PurchasePage.class);
                startActivity(intent);
            }
        });
        ToPurchasePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PurchasePage.class);
                startActivity(intent);
            }
        });
        ToPurchasePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PurchasePage.class);
                startActivity(intent);
            }
        });
        ToPurchasePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PurchasePage.class);
                startActivity(intent);
            }
        });
        ToPurchasePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PurchasePage.class);
                startActivity(intent);
            }
        });
        ToPurchasePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PurchasePage.class);
                startActivity(intent);
            }
        });

        ToMapTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapTest.class);
                startActivity(intent);
            }
        });

        ToSaleList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SaleList.class);
                startActivity(intent);
            }
        });
        ToPurchaseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PurchaseList.class);
                startActivity(intent);
            }
        });
        ToPurchaseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PurchaseList.class);
                startActivity(intent);
            }
        });
        ToPurchaseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PurchaseList.class);
                startActivity(intent);
            }
        });
        ToPurchaseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PurchaseList.class);
                startActivity(intent);
            }
        });
        ToPurchaseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PurchaseList.class);
                startActivity(intent);
            }
        });
        ToPurchaseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PurchaseList.class);
                startActivity(intent);
            }
        });

        // 앱에 필요한 사용자 데이터를 요청하도록 로그인 옵션을 설정한다.
        // DEFAULT_SIGN_IN parameter는 유저의 ID와 기본적인 프로필 정보를 요청하는데 사용된다.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id1))
                .requestEmail() // email addresses도 요청함
                .build();

        // 위에서 만든 GoogleSignInOptions을 사용해 GoogleSignInClient 객체를 만듬
        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

        btn_log_out = findViewById(R.id.btn_log_out);
        firebaseAuth = FirebaseAuth.getInstance();
        btn_log_out.setOnClickListener(new View.OnClickListener() {
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
                                Log.e("###", "onCompleteLogout:logout ");
                                Session.getCurrentSession().removeCallback(mSessionCallback);
                                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        }
                    });
                }
                if (firebaseAuth.getCurrentUser() != null) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        Log.e("###", "user UID: " + user.getUid());
                        user.getIdToken(true)
                                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                                        if (task.isSuccessful()) {
                                            String idToken = task.getResult().getToken();
                                            // Send token to your backend via HTTPS
                                            // ...
                                            Log.e("###", "id token: " + idToken);
                                        } else {
                                            // Handle error -> task.getException();
                                        }
                                    }
                                });
                    }
                    Log.e("###", "getCurrentUser success " + firebaseAuth.getCurrentUser().getIdToken(true));
                    firebaseAuth.signOut();
                    mGoogleSignInClient.revokeAccess();

                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

        });

        btn_toChatting = findViewById(R.id.btn_toChatting);
        btn_toChatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChattingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // 뒤로가기 버튼 2번 누를 시에 앱 종료
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (SystemClock.elapsedRealtime() - clickTime < 2000) {
                finish();
                overridePendingTransition(0, 0);
                return true;
            }
            clickTime = SystemClock.elapsedRealtime();
            Toast.makeText(getApplication(), "한번 더 클릭하시면 앱을 종료합니다", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}