package com.example.hackedin;

import java.util.List;

import com.parse.FindCallback;
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

public class CreateTeamActivity extends Activity {

	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_createteam);
		
		final Button buttonTeamCreate = (Button)findViewById(R.id.buttonTeamCreate);
		buttonTeamCreate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String error = "";
				
				final String name = ((EditText)findViewById(R.id.editTeamName)).getText().toString();
				final int max = Integer.parseInt(((EditText)findViewById(R.id.editTeamMax)).getText().toString());
				final String about = ((EditText)findViewById(R.id.editTeamAbout)).getText().toString();
				
				if (name.length() == 0)
					error += "\nInvalid team name";
				if (max <= 1)
					error += "\nInvalid team size";
				
				if (error.length() > 0)
					alertMessage("Error", "Error creating team:" + error, false);
				else {
					ParseQuery<ParseObject> query = ParseQuery.getQuery("Team");
					query.whereEqualTo("name", name);
					query.findInBackground(new FindCallback<ParseObject>() {
						public void done(List<ParseObject> teamList, ParseException e) {
							if (e == null) {
								if (teamList.size() == 0) {
									ParseObject team = new ParseObject("Team");
									team.put("name", name);
									team.put("hackathon_id", getIntent().getExtras().get("hackathon_id"));
									team.put("max_members", max);
									team.put("about", about);
									team.put("leader_id", getIntent().getExtras().get("user_id"));
									team.add("member_ids", getIntent().getExtras().get("user_id"));
									team.saveInBackground(new SaveCallback() {
										public void done(ParseException e) {
											if (e == null)
												alertMessage("Success", "Your team was created!", true);
											else
												alertMessage("Error", "Error creating team", false);
										}
									});
								}
								else
									alertMessage("Error", "Team name is already in use", false);
							}
							else
								alertMessage("Error", "Error querying teams", false);
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
