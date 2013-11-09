package com.example.hackedin;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class HackathonInfoActivity extends Activity {

	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hackathoninfo);
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Hackathon");
		query.getInBackground(getIntent().getExtras().getString("hackathon_id"), new GetCallback<ParseObject>() {
			public void done(ParseObject hackathon, ParseException e) {
				if (e == null) {
					((TextView)findViewById(R.id.textHackathonName)).setText(hackathon.getString("name"));
					((TextView)findViewById(R.id.textHackathonDate)).setText(dateString(hackathon.getDate("start_date")) + " - " + dateString(hackathon.getDate("end_date")));
					((TextView)findViewById(R.id.textHackathonLocation)).setText(hackathon.getString("location"));
					((TextView)findViewById(R.id.textHackathonWebsite)).setText(hackathon.getString("website"));
					
					ParseQuery<ParseObject> query = ParseQuery.getQuery("Team");
					query.whereEqualTo("hackathon_id", hackathon.getObjectId());
					query.whereContainsAll("member_ids", Arrays.asList(getIntent().getExtras().getString("user_id")));
					query.findInBackground(new FindCallback<ParseObject>() {
						public void done(final List<ParseObject> teamList, ParseException e) {
							if (e == null) {
								if (teamList.size() == 0) {
									((Button)findViewById(R.id.buttonCreateTeam)).setVisibility(View.VISIBLE);
								}
								else {
									((Button)findViewById(R.id.buttonViewTeam)).setVisibility(View.VISIBLE);

									final Button buttonViewTeam = (Button)findViewById(R.id.buttonViewTeam);
									buttonViewTeam.setOnClickListener(new View.OnClickListener() {
										public void onClick(View v) {
											Intent i = new Intent(context, ViewTeamActivity.class);
											Bundle b = new Bundle();
											b.putString("hackathon_id", getIntent().getExtras().getString("hackathon_id"));
											b.putString("user_id", getIntent().getExtras().getString("user_id"));
											b.putString("team_id", teamList.get(0).getObjectId());
											i.putExtras(b);
											startActivity(i);
										}
									});
								}
							}
							else
								alertMessage("Error", "Error retrieving team", true);
						}						
					});
				}
				else
					alertMessage("Error", "Error retrieving hackathon", true);
			}
		});
		
		final Button buttonCreateTeam = (Button)findViewById(R.id.buttonCreateTeam);
		buttonCreateTeam.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(context, CreateTeamActivity.class);
				Bundle b = new Bundle();
				b.putString("hackathon_id", getIntent().getExtras().getString("hackathon_id"));
				b.putString("user_id", getIntent().getExtras().getString("user_id"));
				i.putExtras(b);
				startActivity(i);
			}
		});
		
		final Button buttonLookupTeam = (Button)findViewById(R.id.buttonLookupTeam);
		buttonLookupTeam.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(context, LookupTeamActivity.class);
				Bundle b = new Bundle();
				b.putString("hackathon_id", getIntent().getExtras().getString("hackathon_id"));
				b.putString("user_id", getIntent().getExtras().getString("user_id"));
				i.putExtras(b);
				startActivity(i);
			}
		});
		
		final Button buttonViewRequests = (Button)findViewById(R.id.buttonViewRequests);
		buttonViewRequests.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(context, ViewRequestActivity.class);
				Bundle b = new Bundle();
				b.putString("hackathon_id", getIntent().getExtras().getString("hackathon_id"));
				b.putString("user_id", getIntent().getExtras().getString("user_id"));
				i.putExtras(b);
				startActivity(i);
			}
		});
		
		final Button buttonEditProfile = (Button)findViewById(R.id.buttonEditProfile);
		buttonEditProfile.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(context, EditProfileActivity.class);
				Bundle b = new Bundle();
				b.putString("user_id", getIntent().getExtras().getString("user_id"));
				i.putExtras(b);
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
	
	public String dateString(Date date) {
		return ((1 + date.getMonth()) + "/" + date.getDate() + "/" + (1900 + date.getYear()));
	}
}
