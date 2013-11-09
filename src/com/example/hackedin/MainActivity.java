package com.example.hackedin;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends Activity {

	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Parse.initialize(this, "1pmHxaqat375sbl4tPFnTjgbjDO6oLPKyTIROI8W", "vbgFSq9GcX4iT6ElDdEY6BgdoUgblSmPwu17qDhB");
		ParseAnalytics.trackAppOpened(getIntent());
		
		final Button buttonLogin = (Button)findViewById(R.id.buttonSigninLogin);
		buttonLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ParseUser.logInInBackground(((EditText)findViewById(R.id.editSigninEmail)).getText().toString(), ((EditText)findViewById(R.id.editSigninPassword)).getText().toString(), new LogInCallback() {
					public void done(ParseUser user, ParseException e) {
						if (user == null)
							alertMessage("Invalid Login", "Your e-mail or password is invalid. Please try again or register a new profile.");
						else
							alertMessage("Success", "Successful login");
					}
				});
			}
		});
		
		final Button buttonRegister = (Button)findViewById(R.id.buttonSigninRegister);
		buttonRegister.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(context, RegisterActivity.class);
			    startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void alertMessage(String title, String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(message);
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}
