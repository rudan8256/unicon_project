package com.unicon.unicon_project.Authentic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.unicon.unicon_project.Classes.User;
import com.unicon.unicon_project.MainActivity;
import com.unicon.unicon_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
/*import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;*/

public class SignUpActivity extends AppCompatActivity {
    private User newUser;
    private static final String TAG = "RegisterActivity";
    private FirebaseFirestore mstore = FirebaseFirestore.getInstance();
    EditText mEmailText, mPasswordText, mPasswordCheckText, mName;
   TextView mRegisterBtn;
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


        mRegisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //?????? ?????? ????????????
                String nickName = mName.getText().toString().trim();
                String email = mEmailText.getText().toString().trim();
                String pwd = mPasswordText.getText().toString().trim();
                String pwdCheck = mPasswordCheckText.getText().toString().trim();

                if(email.equals("")){
                    Toast.makeText(SignUpActivity.this, "???????????? ????????? ????????????", Toast.LENGTH_SHORT).show();
                }
                else if(pwd.equals("")){
                    Toast.makeText(SignUpActivity.this, "??????????????? ????????? ????????????", Toast.LENGTH_SHORT).show();
                }
                else if(pwdCheck.equals("")){
                    Toast.makeText(SignUpActivity.this, "2??? ??????????????? ????????? ????????????", Toast.LENGTH_SHORT).show();
                }
                else if(pwd.equals(pwdCheck)) {
                    Log.e("###", "?????? ?????? " + email + " , " + pwd);
                    final ProgressDialog mDialog = new ProgressDialog(SignUpActivity.this);
                    mDialog.setMessage("??????????????????...");
                    mDialog.show();

                    //????????????????????? ???????????? ????????????
                    firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(
                            SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()){
                                        try {
                                            throw task.getException();
                                        }catch (FirebaseAuthWeakPasswordException e){
                                            Toast.makeText(SignUpActivity.this, "??????????????? ????????????.", Toast.LENGTH_SHORT).show();
                                        }catch (FirebaseAuthInvalidCredentialsException e){
                                            Toast.makeText(SignUpActivity.this, "email ????????? ?????? ?????????.", Toast.LENGTH_SHORT).show();
                                        }catch (FirebaseAuthUserCollisionException e){
                                            Toast.makeText(SignUpActivity.this, "?????? ???????????? email ?????????.", Toast.LENGTH_SHORT).show();
                                        }catch (Exception e){
                                            Toast.makeText(SignUpActivity.this, "?????? ????????? ?????????", Toast.LENGTH_SHORT).show();
                                        }
                                        mDialog.dismiss();

                                    }
                                    else  {
                                        mDialog.dismiss();

                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        newUser = new User();
                                        newUser.setUsertoken(user.getUid());
                                        newUser.setUsername(nickName);
                                        String email = user.getEmail();

                                        mstore.collection("User").document(newUser.getUsertoken())
                                                .set(newUser)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                    }
                                                });
                                        if(nickName.equals("")){
                                            Toast.makeText(SignUpActivity.this, "??????????????? ?????????????????????.\n????????????????????? ????????? ????????? ?????????", Toast.LENGTH_SHORT).show();
                                        }
                                        Toast.makeText(SignUpActivity.this, "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                }
                            });
                }else{
                    Toast.makeText(SignUpActivity.this, "??????????????? ???????????????. ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public boolean onSupportNavigateUp(){
        onBackPressed();; // ???????????? ????????? ????????????
        return super.onSupportNavigateUp(); // ???????????? ??????
    }
}
