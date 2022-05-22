package com.example.unicon_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
/*import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;*/

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    EditText mEmailText, mPasswordText, mPasswordCheckText, mName;
    Button mRegisterBtn;
    Button btn_cancel;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth =  FirebaseAuth.getInstance();

        mName = findViewById(R.id.text_name);
        mEmailText = findViewById(R.id.text_email);
        mPasswordText = findViewById(R.id.text_password);
        mPasswordCheckText = findViewById(R.id.text_password_check);
        mRegisterBtn = findViewById(R.id.btn_register);
        btn_cancel = findViewById(R.id.btn_cancel);

        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://www.example.com/finishSignUp?cartId=1234")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setIOSBundleId("com.example.ios")
                        .setAndroidPackageName(
                                "com.example.android",
                                true, /* installIfNotAvailable */
                                "12"    /* minimumVersion */)
                        .build();

        mRegisterBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                //가입 정보 가져오기
                final String email = mEmailText.getText().toString().trim();
                String pwd = mPasswordText.getText().toString().trim();
                String pwdCheck = mPasswordCheckText.getText().toString().trim();


                if(pwd.equals(pwdCheck)) {
                    Log.e("###", "등록 버튼 " + email + " , " + pwd);
                    final ProgressDialog mDialog = new ProgressDialog(SignUpActivity.this);
                    mDialog.setMessage("가입중입니다...");
                    mDialog.show();

                    //파이어베이스에 신규계정 등록하기
                    firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(
                            SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        mDialog.dismiss();

                                        FirebaseUser user = firebaseAuth.getCurrentUser();

                                        String email = user.getEmail();
                                        String uid = user.getUid();
                                        String name = mName.getText().toString().trim();

                                        //해쉬맵 테이블을 파이어베이스 데이터베이스에 저장
                                       /* HashMap<Object,String> hashMap = new HashMap<>();

                                        hashMap.put("uid",uid);
                                        hashMap.put("email",email);
                                        hashMap.put("name",name);

                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference reference = database.getReference("Users");
                                        reference.child(uid).setValue(hashMap);*/

                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        Toast.makeText(SignUpActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

                                    } else {
                                        mDialog.dismiss();
                                        Toast.makeText(SignUpActivity.this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                                        return;  //해당 메소드 진행을 멈추고 빠져나감.
                                    }
                                }
                            });
                }else{
                    Toast.makeText(SignUpActivity.this, "비밀번호가 틀렸습니다. 다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public boolean onSupportNavigateUp(){
        onBackPressed();; // 뒤로가기 버튼이 눌렸을시
        return super.onSupportNavigateUp(); // 뒤로가기 버튼
    }
}
