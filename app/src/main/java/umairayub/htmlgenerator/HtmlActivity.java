package umairayub.htmlgenerator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.adapters.Options;
import io.github.kbiakov.codeview.highlight.ColorTheme;
import spencerstudios.com.ezdialoglib.Animation;
import spencerstudios.com.ezdialoglib.EZDialog;
import spencerstudios.com.ezdialoglib.EZDialogListener;
import spencerstudios.com.ezdialoglib.Font;

public class HtmlActivity extends AppCompatActivity {


    /// Declaring Variables ///
    Button btn_preview, btn_save, btn_share,btn_d_save,btn_d_close;
    EditText editText;
    BottomSheetDialog bottomSheetDialog;
    CodeView codeView;
    String code;
    CoordinatorLayout ly;
    Snackbar snackbar;
    private int STORAGE_PERMISSION_CODE = 1;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);

        /// Initializing views and vars
        btn_preview = (Button) findViewById(R.id.btn_preview);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_share = (Button) findViewById(R.id.btn_share);
        codeView = (CodeView) findViewById(R.id.code_view);
        ly = (CoordinatorLayout) findViewById(R.id.crdly);
        Intent intent = getIntent();
        code = intent.getExtras().getString("html");


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        //// Initializing ADMOB ADS
        MobileAds.initialize(this,
                "????????????????????????");


        //// Creating a Admob Interstitial AD
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("??????????????????????????????");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        /// Loading the code on CodeView(Syntex Highlighter) this method will work faster then other!
        codeView.setOptions(Options.Default.get(this)
                .withLanguage("html")//sets language of highlighter to HTML
                .withCode(code) //sets the Code to Highlighter
                .withTheme(ColorTheme.SOLARIZED_LIGHT)); //sets the theme of Highlighter

        /// Handling Click event of Save Button ///
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = code;
                // Passing code as content for the file to be saved to method saveAsFile which accepts String as Parameter
                saveAsFile(content);

            }
        });

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        /// Handling Click event of Share Button ///
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, code);
                startActivity(Intent.createChooser(sharingIntent, "Share Code Via"));
            }
        });

        /// Handling Click event of Preview Button ///
        btn_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this Button will pass  the code with Intent to Preview Activity
                    Intent i = new Intent(HtmlActivity.this, PreviewActivity.class);
                    i.putExtra("code",code);
                    startActivity(i);
            }
        });
    }

    ////////<Saving File>\\\\\\\\\\
    private void saveAsFile(final String content) {

        //Before Saving we must check if user has Granted Permission to save or not, if user didn't we must ask for permission
        if (!checkAndRequest()) {
            new EZDialog.Builder(HtmlActivity.this)
                    .setTitle("Permission Required!")
                    .setMessage("Storage permission is needed to save and read HtmlActivity files")
                    .setPositiveBtnText("okay")
                    .setNegativeBtnText("close")
                    .setHeaderColor(Color.parseColor("#039be5"))
                    .setCancelableOnTouchOutside(false)
                    .setAnimation(Animation.UP)
                    .setFont(Font.COMFORTAA)
                    .setTitleTextColor(Color.WHITE)
                    .OnPositiveClicked(new EZDialogListener() {
                        @Override
                        public void OnClick() {
                            //todo
                            requestStoragePermission();


                        }
                    })
                    .OnNegativeClicked(new EZDialogListener() {
                        @Override
                        public void OnClick() {
                            //todo
                        }
                    })
                    .build();
        } else {
            // If they Did then we can save the file
            //Before saving we first check that the DIR exists or not if not then we create the DIR first
            final File dir = new File(Environment.getExternalStorageDirectory(), "Html Generator");
            if (!dir.exists()) {
                dir.mkdirs();
                Log.i("Dir", "DIR CREATED!");
            }

            /// Then will show a BottomSheetDialog to let user set a name for the file
             bottomSheetDialog = new BottomSheetDialog(HtmlActivity.this);
             bottomSheetDialog.setContentView(R.layout.save_dialog);
             btn_d_save = (Button) bottomSheetDialog.findViewById(R.id.btn_dialog_save);
             btn_d_close = (Button) bottomSheetDialog.findViewById(R.id.btn_dialog_close);
             editText = (EditText) bottomSheetDialog.findViewById(R.id.edt_dialog_filenaem);
             btn_d_close.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     bottomSheetDialog.dismiss();
                 }
             });
             btn_d_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (editText.getText().toString().isEmpty()) {
                        // if user didn't entered anything and clicked save we will show a toast that he/she can't save a file without name
                        Toast.makeText(HtmlActivity.this, "ENTER FILENAME", Toast.LENGTH_SHORT).show();
                    } else {
                        // if the user did entered a name we will add the ".html" extension to it and create the file in Storge/HtmlActivity Generator
                        bottomSheetDialog.dismiss();

                        //create file
                        File file = new File(dir, editText.getText().toString() + ".html");
                        int num = 0;
                        while (file.exists()) {
                            num++;
                            file = new File(dir, editText.getText().toString() + num + ".html");
                        }
                        //write to file
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(content.getBytes());
                            fos.close();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                        // will show a snackbar as the file get saved
                        snackbar = Snackbar.make(ly, "Saved!", Snackbar.LENGTH_LONG).setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.getView().setBackgroundColor(Color.parseColor("#039be5"));

                        snackbar.show();


                        //check if ad is loaded if so then show it els log it
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            Log.d("TAG", "The interstitial wasn't loaded yet.");
                        }
                    }
                }
            });
             bottomSheetDialog.show();

        }
    }
    ///////</Saving File>\\\\\\\\\\



//////////////////////////// Checking and Requesting Permission Starts HERE  \\\\\\\\\\\\\\\\\////////////////////////////////////
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            ActivityCompat.requestPermissions(HtmlActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();

                String content = code;
                saveAsFile(content);
            } else {
                Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();

            }
        }
    }
    public boolean checkAndRequest() {
        if (ContextCompat.checkSelfPermission(HtmlActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //Permission Granted
            Log.i("PERMISSION", "Permission Granted");
            return true;
        } else {
            Log.i("PERMISSION", "Permission not  Granted Showing Dialog");
            return false;

        }
    }

//////////////////////////// Checking and Requesting Permission Ends HERE    \\\\\\\\\\\\\\\\\////////////////////////////////////


    /////////// <MENU> \\\\\\\\\\\\\\\\
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    /////////// </MENU> \\\\\\\\\\\\\\\


}