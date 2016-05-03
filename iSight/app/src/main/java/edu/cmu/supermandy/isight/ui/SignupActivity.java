package edu.cmu.supermandy.isight.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.PreparedStatement;

import edu.cmu.supermandy.isight.exception.UserFormValidationFailedException;
import edu.cmu.supermandy.isight.model.User;
import edu.cmu.supermandy.isight.util.DBDAO;

/**
 * Created by Mandy on 4/4/16.
 */
public class SignupActivity extends Activity {
    private Button BackButton;
    private Button signUpButton;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText ageEditText;
    private EditText phoneNumEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        usernameEditText = (EditText) findViewById(R.id.sign_up_username);
        emailEditText = (EditText) findViewById(R.id.sign_up_email);
        passwordEditText = (EditText) findViewById(R.id.sign_up_password);
        confirmPasswordEditText = (EditText) findViewById(R.id.sign_up_confirmpassword);
        ageEditText = (EditText) findViewById(R.id.sign_up_userAge);
        phoneNumEditText = (EditText) findViewById(R.id.sign_up_userPhoneNumber);


        BackButton = (Button) findViewById(R.id.sign_up_back_button);
        signUpButton = (Button) findViewById(R.id.finish_sign_up_button);
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean signupSucceed=insertUser();
                if (signupSucceed) {
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                }
            }
        });

    }

    private boolean insertUser() {
        try {
            final DBDAO userdao = new DBDAO(this);
            int userId;
            String username = usernameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            String age = ageEditText.getText().toString();
            String phoneNum = phoneNumEditText.getText().toString();

            /** check required info has all been filled out*/
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || age.isEmpty()) {
                throw new UserFormValidationFailedException("Please fill all information out.\n ");
            }

            /** check email validation*/
            if (!email.contains("@")) {
                throw new UserFormValidationFailedException("The email address is invalid.\n ");
            }
            /** check password and confirmed password match*/
            if (!password.equals(confirmPassword)) {
                throw new UserFormValidationFailedException("Passwords don't match.\n ");
            }

            /** check username has already been used*/
            userId = userdao.checkDataExist("UserTable", "Username", "[" + username + "]");
            if (userId != -1) {
                throw new UserFormValidationFailedException("The username has already been used.\n ");
            }

            /** check email has already been used*/
            userId = userdao.checkDataExist("UserTable", "Email", "[" + email + "]");
            if (userId != -1) {
                throw new UserFormValidationFailedException("The email address has already been used.\n ");
            }

            User newUser = new User(username, email, password, Integer.parseInt(age), phoneNum);
            userdao.insertUser(newUser);
            return true;
        }
        catch (UserFormValidationFailedException e) {
            e.fix(SignupActivity.this);
            return false;
        }
    }
}
