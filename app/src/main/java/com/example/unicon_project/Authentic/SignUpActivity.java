package com.example.unicon_project.Authentic;

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

import com.example.unicon_project.Classes.User;
import com.example.unicon_project.MainActivity;
import com.example.unicon_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
/*import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;*/

public class SignUpActivity extends AppCompatActivity {
    private User newUser;
    private static final String TAG = "RegisterActivity";
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();
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

        mRegisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //가입 정보 가져오기
                String email = mEmailText.getText().toString().trim();
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
                                    if (!task.isSuccessful()){
                                        try {
                                            throw task.getException();
                                        }catch (FirebaseAuthWeakPasswordException e){
                                            Toast.makeText(SignUpActivity.this, "비밀번호가 간단해요.", Toast.LENGTH_SHORT).show();
                                        }catch (FirebaseAuthInvalidCredentialsException e){
                                            Toast.makeText(SignUpActivity.this, "email 형식이 맞지 않아요.", Toast.LENGTH_SHORT).show();
                                        }catch (FirebaseAuthUserCollisionException e){
                                            Toast.makeText(SignUpActivity.this, "이미 존재하는 email 입니다.", Toast.LENGTH_SHORT).show();
                                        }catch (Exception e){
                                            Toast.makeText(SignUpActivity.this, "다시 확인해 주세요", Toast.LENGTH_SHORT).show();
                                        }
                                        mDialog.dismiss();

                                    }
                                    else  {
                                        mDialog.dismiss();

                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        newUser = new User();
                                        newUser.setUsertoken(user.getUid());
                                        newUser.setUsername(mName.getText().toString().trim());
                                        String email = user.getEmail();

                                        mstore.collection("User").document(newUser.getUsertoken())
                                                .set(newUser)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                    }
                                                });
                                        Toast.makeText(SignUpActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();

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
