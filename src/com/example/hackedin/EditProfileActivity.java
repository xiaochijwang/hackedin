package com.example.hackedin;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditProfileActivity extends Activity {

	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editprofile);
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("UserProfile");
		query.whereEqualTo("user_id", getIntent().getExtras().getString("user_id"));
		query.getFirstInBackground(new GetCallback<ParseObject>() {
			public void done(ParseObject profile, ParseException e) {
				((EditText)findViewById(R.id.editEditProfileSchool)).setText(profile.getString("school"));
				((EditText)findViewById(R.id.editEditProfilePhone)).setText(profile.getString("phone"));
				((EditText)findViewById(R.id.editEditProfileSkills)).setText(profile.getString("skills"));
				((EditText)findViewById(R.id.editEditProfileAbout)).setText(profile.getString("about"));
			}
		});
		
		final Button buttonProfileSave = (Button)findViewById(R.id.buttonProfileSave);
		buttonProfileSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String error = "";
				
				final String school = ((EditText)findViewById(R.id.editEditProfileSchool)).getText().toString();
				final String phone = ((EditText)findViewById(R.id.editEditProfilePhone)).getText().toString();
				final String skills = ((EditText)findViewById(R.id.editEditProfileSkills)).getText().toString();
				final String about = ((EditText)findViewById(R.id.editEditProfileAbout)).getText().toString();

				if (school.replaceAll("\\W", "").length() == 0)
					error += "\nInvalid school";
				if (phone.length() > 0 && !android.util.Patterns.PHONE.matcher(phone).matches())
					error += "\nInvalid phone";
				
				if (error.length() > 0)
					alertMessage("Error", "Error editing profile:" + error, false);
				else {
					ParseQuery<ParseObject> query = ParseQuery.getQuery("UserProfile");
					query.whereEqualTo("user_id", getIntent().getExtras().getString("user_id"));
					query.getFirstInBackground(new GetCallback<ParseObject>() {
						public void done(ParseObject profile, ParseException e) {
							profile.put("school", school);
							profile.put("phone", phone);
							profile.put("skills", skills);
							profile.put("about", about);
							profile.saveInBackground(new SaveCallback() {
								public void done(ParseException e) {
									if (e == null)
										alertMessage("Success", "Your profile has been updated!", true);
									else
										alertMessage("Error", "Error updating user profile", false);
								}
							});
						}
					});
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void alertMessage(String title, String message, final boolean finish) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (finish)
					finish();
				else
					dialog.cancel();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}
