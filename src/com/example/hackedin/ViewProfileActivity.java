package com.example.hackedin;

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
import android.widget.TextView;

public class ViewProfileActivity extends Activity {

	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewprofile);
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("UserProfile");
		query.whereEqualTo("user_id", getIntent().getExtras().getString("view_user_id"));
		try {
			ParseObject profile = query.getFirst();
			((TextView)findViewById(R.id.textProfileName)).setText(profile.getString("first_name") + " " + profile.getString("last_name"));
			((TextView)findViewById(R.id.textProfileSchool)).setText("School: " + profile.getString("school"));
			ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("_User");
			ParseObject user = userQuery.get(getIntent().getExtras().getString("view_user_id"));
			((TextView)findViewById(R.id.textProfileEmail)).setText("Email: " + user.getString("email"));
			((TextView)findViewById(R.id.textProfilePhone)).setText("Phone: " + profile.getString("phone"));
			((TextView)findViewById(R.id.textProfileSkills)).setText("Skills: " + profile.getString("skills"));
			((TextView)findViewById(R.id.textProfileAbout)).setText("About: " + profile.getString("about"));
		}
		catch (ParseException e){
			alertMessage("Error", "Error retrieving user profile" + e, true);
		}
		
		if (getIntent().getExtras().getString("team_id") != null) {
			final Button buttonAcceptRequest = (Button)findViewById(R.id.buttonAcceptRequest);
			buttonAcceptRequest.setVisibility(View.VISIBLE);
			buttonAcceptRequest.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					try {
						ParseQuery<ParseObject> teamQuery = ParseQuery.getQuery("Team");
						ParseObject team = teamQuery.get(getIntent().getExtras().getString("team_id"));
						ParseQuery<ParseObject> requestQuery = ParseQuery.getQuery("Request");
						requestQuery.whereEqualTo("from_user_id", getIntent().getExtras().getString("view_user_id"));
						requestQuery.whereEqualTo("hackathon_id", getIntent().getExtras().getString("hackathon_id"));
						ParseObject.deleteAll(requestQuery.find());
						team.add("member_ids", getIntent().getExtras().getString("view_user_id"));
						team.saveInBackground(new SaveCallback() {
							public void done(ParseException e) {
								alertMessage("Success", "You have accepted this person as a new team member", true);
							}
						});
					}
					catch (ParseException e) {
						alertMessage("Error", "Error accepting request", false);
					}
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.onCreate(null);
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
