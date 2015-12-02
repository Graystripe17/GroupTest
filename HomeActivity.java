package gaga.minty.com.pocketcommunicate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class HomeActivity extends AppCompatActivity {
    public String nameOfChannel;
    public String myPhoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        myPhoneNumber = tMgr.getLine1Number().toString();
        EditText phoneNumber = (EditText)findViewById(R.id.phone);

        phoneNumber.setText(myPhoneNumber);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void joinChannel(View v) {
        EditText myET = (EditText) findViewById(R.id.channelSearchBox);
        EditText myENum = (EditText) findViewById(R.id.phone);
        nameOfChannel = myET.getText().toString();
        myPhoneNumber = myENum.getText().toString();

        String p1 = ((EditText)findViewById(R.id.P1)).getText().toString();
        String p2 = ((EditText)findViewById(R.id.P2)).getText().toString();
        String p3 = ((EditText)findViewById(R.id.P3)).getText().toString();
        String p4 = ((EditText)findViewById(R.id.P4)).getText().toString();
        String p5 = ((EditText)findViewById(R.id.P5)).getText().toString();




        Intent intentToChannel = new Intent(this, mChannel.class);
        intentToChannel.putExtra("nameOfChannel", nameOfChannel);
        intentToChannel.putExtra("phoneNumber", myPhoneNumber);
        intentToChannel.putExtra("p1", p1);
        intentToChannel.putExtra("p2", p2);
        intentToChannel.putExtra("p3", p3);
        intentToChannel.putExtra("p4", p4);
        intentToChannel.putExtra("p5", p5);
        startActivity(intentToChannel);

    }
}