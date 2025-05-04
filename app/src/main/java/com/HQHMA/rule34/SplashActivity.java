package com.HQHMA.rule34;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.HQHMA.rule34.Models.AppVersion;
import com.HQHMA.rule34.Models.MyUser;
import com.HQHMA.rule34.Utilities.FireBase;
import com.HQHMA.rule34.Utilities.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class SplashActivity extends AppCompatActivity {

    Button tryAgainBtn;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;

    private int appVersion = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try{
            appVersion = (int) (getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES).getLongVersionCode());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        tryAgainBtn = findViewById(R.id.tryAgainBtn);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();


        checkForUserLogon();

        tryAgainBtn.setOnClickListener(view -> {
            tryAgainBtn.setVisibility(GONE);
            checkForUserLogon();
        });
    }

    private void checkForUserLogon() {
        progressBar.setProgress(2);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            loginAsGuest();
        }else {
            checkUserData(currentUser);
        }
    }

    private void loginAsGuest() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            checkUserData(user);
                        } else {
                            error("Network Error");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        error("Network Error");
                    }
                });
    }

    private void checkUserData(FirebaseUser currentUser) {
        FireBase.allUserCollectionReference().document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                MyUser newUser;
                if (task.isSuccessful()) {
                    newUser = task.getResult().toObject(MyUser.class);
                    if (newUser == null)
                        newUser = new MyUser(currentUser.getUid(), Timestamp.now(),Timestamp.now(),1,appVersion,0);
                    else {
                        newUser.setLoginCount(newUser.getLoginCount() + 1);
                        newUser.setLastOnlineTime(Timestamp.now());
                        newUser.setAppVersion(appVersion);
                    }

                    FireBase.allUserCollectionReference().document(currentUser.getUid()).set(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Toast.makeText(SplashActivity.this, "User Data Saved", Toast.LENGTH_SHORT).show();
                                loginSuccessful();
                            } else {
                                //Toast.makeText(SplashActivity.this, "User Data Not Saved", Toast.LENGTH_SHORT).show();
                                error("Network Error");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            error("Network Error");
                        }
                    });
                } else {
                    error("Network Error");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                error("Network Error");
            }
        });
    }

    private void error(String msg) {
        tryAgainBtn.setVisibility(VISIBLE);
        progressBar.setProgress(0);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void loginSuccessful() {
        progressBar.setProgress(7);

        checkAppDataFromServer();
    }

    private void checkAppDataFromServer() {
        FireBase.getAppData().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    AppVersion appData = task.getResult().toObject(AppVersion.class);
                    if (!appData.isAppActive()){
                        error("App server is not active try again later.");
                        return;
                    }
                    else if (appData.getVersion() > appVersion && appData.isForceUpdate()){
                        showAppUpdateDialog(appData.getUpdateText(),appData.getUpdateLink(),true);
                        error("Your app version is expired.");
                        return;
                    }
                    else if (appData.getVersion() > appVersion){
                        showAppUpdateDialog(appData.getUpdateText(),appData.getUpdateLink(),false);
                    }else {
                        startTheApp();
                    }
                } else {
                    error("Network Error");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                error("Network Error");
            }
        });

    }

    private void showAppUpdateDialog(String updateText, String updateLink, boolean forceUpdate) {
        Utilities.showAppUpdateDialog(updateText, forceUpdate, this, new Utilities.ShowAppUpdateDialogCallback() {
            @Override
            public void onUpdate() {
                try {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateLink));
                    startActivity(myIntent);
                    finish();
                } catch (ActivityNotFoundException e) {
                    error("No application can handle this request. Please install a WebBrowser.");
                    e.printStackTrace();
                }
            }
            @Override
            public void onSkip() {
                startTheApp();
            }
        });
    }

    private void startTheApp() {
        Intent i = new Intent(SplashActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }
}