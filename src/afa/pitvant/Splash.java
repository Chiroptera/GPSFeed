package afa.pitvant;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Splash extends Activity implements OnClickListener{

	SharedPreferences prefs;
	String serverIP;
	int serverPORT;
	EditText etIP, etPORT;
	Button bStart, bDefault;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		initialize();
	}
	
	void initialize () {
		etIP = (EditText) findViewById(R.id.etIP);
		etPORT = (EditText) findViewById(R.id.etPort);
		bStart = (Button) findViewById(R.id.bStart);
		bDefault = (Button) findViewById(R.id.bDefault);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		serverIP=prefs.getString(serverIP, "0.0.0.0");
		serverPORT=Integer.parseInt(prefs.getString("serverPort", "0"));
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
			case R.id.bStart:
				//save IP and Port to preferences
				//call function to destroy this activity
				break;
			case R.id.bDefault:
				//call function to destroy this activity
				break;		
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		startActivity(new Intent(this, GPSLocFeed.class));
	}
	
	
}
