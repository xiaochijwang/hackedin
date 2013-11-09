package com.example.hackedin;

import java.util.List;

import com.parse.FindCallback;
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

public class EditTeamActivity extends Activity {

	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editteam);

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Team");
		query.getInBackground(getIntent().getExtras().getString("team_id"), new GetCallback<ParseObject>() {
			public void done(ParseObject team, ParseException e) {
				((EditText)findViewById(R.id.editEditTeamName)).setText(team.getString("name"));
				((EditText)findViewById(R.id.editEditTeamMax)).setText("" + team.getInt("max_members"));
				((EditText)findViewById(R.id.editEditTeamAbout)).setText(team.getString("about"));
			}
		});
		
		final Button buttonTeamSave = (Button)findViewById(R.id.buttonTeamSave);
		buttonTeamSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String error = "";
				
				final String name = ((EditText)findViewById(R.id.editEditTeamName)).getText().toString();
				final int max = Integer.parseInt(((EditText)findViewById(R.id.editEditTeamMax)).getText().toString());
				final String about = ((EditText)findViewById(R.id.editEditTeamAbout)).getText().toString();
				
				if (name.length() == 0)
					error += "\nInvalid team name";
				if (max <= 1)
					error += "\nInvalid team size";
				
				if (error.length() > 0)
					alertMessage("Error", "Error editing team:" + error, false);
				else {
					ParseQuery<ParseObject> query = ParseQuery.getQuery("Team");
					query.whereEqualTo("name", name);
					query.findInBackground(new FindCallback<ParseObject>() {
						public void done(List<ParseObject> teamList, ParseException e) {
							if (e == null) {
								if (teamList.size() == 0) {
									ParseQuery<ParseObject> query = ParseQuery.getQuery("Team");
									try {
										ParseObject team = query.get(getIntent().getExtras().getString("team_id"));
										team.put("name", name);
										team.put("max_members", max);
										team.put("about", about);
										team.saveInBackground(new SaveCallback() {
											public void done(ParseException e) {
												if (e == null)
													alertMessage("Success", "Your edits were saved!", true);
												else
													alertMessage("Error", "Error editing team", false);
											}
										});
									}
									catch (ParseException pe) {
										alertMessage("Error", "Error retrieving team", false);
									}
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
		
		final Button buttonTeamDisband = (Button)findViewById(R.id.buttonTeamDisband);
		buttonTeamDisband.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				alertDialogBuilder.setTitle("Disband Team");
				alertDialogBuilder.setMessage("Are you sure you want to disband the team?");
				alertDialogBuilder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ParseQuery<ParseObject> team = new ParseQuery<ParseObject>("Team");
						team.getInBackground(getIntent().getExtras().getString("team_id"), new GetCallback<ParseObject>() {
							public void done(ParseObject team, ParseException e) {
								team.deleteInBackground();
								alertMessage("Disbanded", "Your team has been disbanded", true);
							}
						});
					}
				});
				alertDialogBuilder.setPositiveButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
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
