package umairayub.htmlgenerator;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.scrat.app.richtext.RichEditText;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    //Defining Variables
    FloatingActionButton fab;
    EditText  edt_heading;
    String htmltemplate, para, heading, h_size = "<h1>", h_size_ = "</h1>";
    RichEditText editor;
    Intent i;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing views
        fab = (FloatingActionButton) findViewById(R.id.fab);
        editor = (RichEditText) findViewById(R.id.rich_text);
        edt_heading = (EditText) findViewById(R.id.edt_heading);
        i = new Intent(MainActivity.this,HtmlActivity.class);

        NiceSpinner HeadingSizePicker = (NiceSpinner) findViewById(R.id.spinner);
        List<String> dataset = new LinkedList<>(Arrays.asList("H1", "H2", "H3", "H4", "H5","H6"));


        //// Initializing ADMOB ADS
        MobileAds.initialize(this,
                "");

        //// Creating a Admob Interstitial AD
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("??????????????????????????");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        // Setting Up Heading Size Picker
        HeadingSizePicker.attachDataSource(dataset);
        HeadingSizePicker.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        h_size = "<h1>";
                        h_size_ = "</h1>";
                     break;
                    case 1:
                        h_size = "<h2>";
                        h_size_ = "</h2>";
                        break;
                    case 2:
                        h_size = "<h3>";
                        h_size_ = "</h3>";

                        break;
                    case 3:
                        h_size = "<h4>";
                        h_size_ = "</h4>";

                        break;
                    case 4:
                        h_size = "<h5>";
                        h_size_ = "</h5>";

                        break;
                    case 5:
                        h_size = "<h6>";
                        h_size_ = "</h6>";

                        break;
                }
            }
        });

        /// this floating Action Button Generates the HtmlActivity with Generate method which takes two params 1 Heading 2 Paragraph
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                /// First of all we need to check that user have filled both fields or not
                if(edt_heading.getText().toString().isEmpty() || editor.getText().toString().isEmpty()){
                    // if any of these two is empty we won't let the user proceed by  showing them a Toast
                    Toast.makeText(MainActivity.this, "Fill both Fields!", Toast.LENGTH_SHORT).show();
                }else {

                    //check if ad is loaded if so then show it els log it
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                    }

                    //get get paragraph from edittor as html
                    para = editor.toHtml();

                    //get heading from edittext as plain text
                    heading = edt_heading.getText().toString();

                    //pass heading and paragraph to generate html code
                    GenerateHtml(heading, para);
                }
            }
        });


        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });


    }


    // this method generates html code
    public void GenerateHtml(String heading,String para) {
        // intializing/creating a intent
        i = new Intent(MainActivity.this, HtmlActivity.class);

        //as name says it the template of html we will put heading and paragraph to it and pass it to HtmlActivity Activity via Intent
        htmltemplate = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<title> Html Generated Using Html Generator </title>\n" +
                "</head>\n" +
                "<body>\n" + h_size + heading + h_size_ +"\n"+
                "<p>\n" + para + "\n</p>\n" +
                "</body>\n" +
                "</html>";

        i.putExtra("html", htmltemplate);
        startActivity(i);
    }

    ////////// MENU ////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                // do your code
                Intent intent = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intent);
                return true;
        }
    return super.onOptionsItemSelected(item);
    }
    ////////// MENU/////////


    // <Rich Edittor methods>These methods are for RickEdittor
    public void setBold(View v) {
        editor.bold(!editor.contains(editor.FORMAT_BOLD));
    }

    public void setItalic(View v) {
        editor.italic(!editor.contains(editor.FORMAT_ITALIC));
    }

    public void setUnderline(View v) {
        editor.underline(!editor.contains(editor.FORMAT_UNDERLINED));
    }

    public void setStrikethrough(View v) {
        editor.strikethrough(!editor.contains(editor.FORMAT_STRIKETHROUGH));
    }

    public void setBullet(View v) {
        editor.bullet(!editor.contains(editor.FORMAT_BULLET));
    }

    public void setQuote(View v) {
        editor.quote(!editor.contains(editor.FORMAT_QUOTE));
    }

    public void clear(View v){
        editor.getText().clear();
    }
    // </Rich Edittor methods>
}