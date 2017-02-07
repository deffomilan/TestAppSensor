package com.example.a13000.testappsensor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private GradientBackgroundPainter gradientBackgroundPainter;
    private TextView signUp, title, alreadyText, forgetPass;
    private Button signIn;
    private EditText email, password;
    private ViewGroup activity_main;
    private CheckBox rememberMe;
    private ImageView addall, google, facebook, twitter;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRef;

    private ProgressDialog progressDialog;

    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing you in ... \n\n ............");
        progressDialog.setCancelable(false);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseRef.keepSynced(true);

        checkFirstRun();

        View backgroundImage = findViewById(R.id.activity_main);
        signUp = (TextView) findViewById(R.id.signUp);
        signIn = (Button) findViewById(R.id.signIn);
        title = (TextView) findViewById(R.id.titleText);
        email = (EditText) findViewById(R.id.email);
        facebook = (ImageView) findViewById(R.id.facebook);
        google = (ImageView) findViewById(R.id.google);
        twitter = (ImageView) findViewById(R.id.twitter);
        addall = (ImageView) findViewById(R.id.addall);
        alreadyText = (TextView) findViewById(R.id.alreadyText);
        activity_main = (ViewGroup) findViewById(R.id.activity_main);
        password = (EditText) findViewById(R.id.password);
        forgetPass = (TextView) findViewById(R.id.forgotPass);
        rememberMe = (CheckBox) findViewById(R.id.rememberMe);

        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.milkshake);
        final Animation myAnimBounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        final Animation myAnimZoom = AnimationUtils.loadAnimation(this, R.anim.zoom_in);

        email.setFocusable(false);
        email.setFocusableInTouchMode(true);

        final int[] drawables = new int[3];
        drawables[0] = R.drawable.gradient_1;
        drawables[1] = R.drawable.gradient_2;
        drawables[2] = R.drawable.gradient_3;

        // Managing Visibility ...
        setVisible(signIn, signUp, email, password, alreadyText, forgetPass, rememberMe, google, twitter, facebook, addall);

        gradientBackgroundPainter = new GradientBackgroundPainter(backgroundImage, drawables);
        gradientBackgroundPainter.start();

        // Setting custom font ki text vannum ...
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/Airways_PERSONAL_USE_ONLY.ttf");
        signIn.setTypeface(custom_font);
        signUp.setTypeface(custom_font);
        title.setTypeface(custom_font1);
        password.setTypeface(custom_font);
        alreadyText.setTypeface(custom_font);
        email.setTypeface(custom_font);
        forgetPass.setTypeface(custom_font);
        rememberMe.setTypeface(custom_font);

        // For animation during enter
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                Fade fade = new Fade();
                fade.setDuration(5000);
                TransitionManager.beginDelayedTransition(activity_main, fade);
                toggleView(title, signUp, signIn, email, password, alreadyText, rememberMe, forgetPass, addall);
            }
        }, 100);

        // Configure Google Sign In ... Available in firebase docs ...
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                password.startAnimation(myAnimBounce);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp.startAnimation(myAnimZoom);
                startActivity(new Intent(MainActivity.this, Signup.class));
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailVal = email.getText().toString().trim();
                String passwordVal = password.getText().toString().trim();
                if (!TextUtils.isEmpty(emailVal) && !TextUtils.isEmpty(passwordVal)) {
                    checkLogin(emailVal, passwordVal);
                } else if (TextUtils.isEmpty(emailVal)) {
                    email.setError("You need to enter your e-mail here");
                } else if (TextUtils.isEmpty(passwordVal)) {
                    password.setError("You need a password to sign, obviously!");
                }
            }
        });

        addall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleView(google, facebook, twitter);
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(myAnim);
                // Add working code after here google sign in part vanna khojeko ...
                signIn();
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(myAnim);
                // Facebook ko affai le banauna parrxa so later to be done ...


            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(myAnim);
                        // Twitter ko lai ni affai le banaune parrxa so paxi garrney ...


                    }
                });
            }
        });

        // available in firebase docs ..
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this, "There is problem ...\n\n Please retry...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    // Available in firebase docs ...
    private void signIn() {
        progressDialog.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Available in firebase docs ...
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase ...
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                progressDialog.dismiss();
                Toast.makeText(this, "Please check your internet connection...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Later added for moving to setUpPage ... Rest available in firebase docs ...
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "This part is very much not under control now.", Toast.LENGTH_SHORT).show();
                            userExistOrNot();
                        }
                    }
                })
        ;
    }

    private void checkLogin(String emailVal, String passwordVal) {
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(emailVal, passwordVal)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    userExistOrNot();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this,
                            "Sorry! We cannot log you in. Please recheck you email and password",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // You method yeta vako right xa ... do not move ...
    private void userExistOrNot() {
        if (firebaseAuth.getCurrentUser() != null) {
            progressDialog.show();
            final String UID = firebaseAuth.getCurrentUser().getUid();
            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(UID)) {
                        progressDialog.dismiss();
                        Intent in = new Intent(MainActivity.this, FeedPage.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(in);
                    } else {
                        progressDialog.dismiss();
                        // Yo google bata login hunney bella mattra invoke hunnxa ...
                        Toast.makeText(MainActivity.this, "Sorry, You need to setup your account.", Toast.LENGTH_SHORT).show();
                        Intent newIntent = new Intent(MainActivity.this, SetupForNew.class);
                        startActivity(newIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Being safe here ... Avoiding infinite looping of progress bar I think ...
                    progressDialog.dismiss();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gradientBackgroundPainter.stop();
    }

    // App first ma install grrda aauney pop up alert dialog box...
    public void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
            String title = "Welcome to Complaint Board.\n";
            String message = "App under development for final year project.\n\n\n-By Milan Pandey";

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.show();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }

    // Animation hunna ko lai important xa ..
    private void toggleView(View... views) {
        for (View current : views) {
            if (current.getVisibility() == View.INVISIBLE) {
                current.setVisibility(View.VISIBLE);
            }
        }
    }


    // Animation koi lagi ho ...
    @SuppressLint("NewApi")
    public void setVisible(View... views) {
        for (View item : views) {
            item.setVisibility(View.INVISIBLE);
        }
    }
}
