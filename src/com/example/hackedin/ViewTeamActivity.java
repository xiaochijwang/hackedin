package com.example.hackedin;

import java.util.ArrayList;
import java.util.Arrays;
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
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ViewTeamActivity extends Activity {

	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewteam);
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Team");
		try {
			final ParseObject team = query.get(getIntent().getExtras().getString("team_id"));
			((TextView)findViewById(R.id.textTeamName)).setText(team.getString("name"));
			ParseQuery<ParseObject> leaderQuery = ParseQuery.getQuery("UserProfile");
			leaderQuery.whereEqualTo("user_id", team.getString("leader_id"));
			ParseObject leaderProfile = leaderQuery.getFirst();
			((TextView)findViewById(R.id.textTeamLeader)).setText("Leader: " + leaderProfile.getString("first_name") + " " + leaderProfile.getString("last_name"));
			((TextView)findViewById(R.id.textTeamAbout)).setText("About: " + team.getString("about"));
			((TextView)findViewById(R.id.textTeamSize)).setText("Size: " + team.getList("member_ids").size() + "/" + team.getInt("max_members"));
			final ArrayList<String> memberNames = new ArrayList<String>();
			if (team.getList("member_ids") != null)
				for (Object member_id : team.getList("member_ids")) {
					ParseQuery<ParseObject> memberQuery = ParseQuery.getQuery("UserProfile");
					memberQuery.whereEqualTo("user_id", (String)member_id);
					ParseObject memberProfile = memberQuery.getFirst();
					memberNames.add(memberProfile.getString("first_name") + " " + memberProfile.getString("last_name"));
				}
			final ListView listTeamMembers = (ListView)findViewById(R.id.listTeamMembers);
			ArrayAdapter<String> aa = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, memberNames);
			listTeamMembers.setAdapter(aa);
			listTeamMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {				
				public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
					Intent i = new Intent(context, ViewProfileActivity.class);
					Bundle b = new Bundle();
					b.putString("view_user_id", team.getList("member_ids").get(position).toString());
					i.putExtras(b);
					startActivity(i);
				}
			});
			
			if (team.getString("leader_id").equals(getIntent().getExtras().getString("user_id"))) {
				((Button)findViewById(R.id.buttonEditTeam)).setVisibility(View.VISIBLE);
				
				final Button buttonEditTeam = (Button)findViewById(R.id.buttonEditTeam);
				buttonEditTeam.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Intent i = new Intent(context, EditTeamActivity.class);
						Bundle b = new Bundle();
						b.putString("hackathon_id", getIntent().getExtras().getString("hackathon_id"));
						b.putString("user_id", getIntent().getExtras().getString("user_id"));
						b.putString("team_id", getIntent().getExtras().getString("team_id"));
						i.putExtras(b);
						startActivity(i);
					}
				});
			}
		}
		catch (ParseException e) {
			finish();
		}
		
		ParseQuery<ParseObject> teamQuery = ParseQuery.getQuery("Team");
		teamQuery.whereEqualTo("hackathon_id", getIntent().getExtras().getString("hackathon_id"));
		teamQuery.whereContainsAll("member_ids", Arrays.asList(getIntent().getExtras().getString("user_id")));
		teamQuery.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> teamList, ParseException e) {
				if (e == null) {
					if (teamList.size() == 0) {
						final Button buttonRequestJoin = (Button)findViewById(R.id.buttonRequestJoin);
						buttonRequestJoin.setVisibility(View.VISIBLE);
					}
					else if (findViewById(R.id.buttonEditTeam).getVisibility() == View.INVISIBLE && teamList.get(0).getObjectId().equals(getIntent().getExtras().getString("team_id"))) {
						final Button buttonLeaveTeam = (Button)findViewById(R.id.buttonLeaveTeam);
						buttonLeaveTeam.setVisibility(View.VISIBLE);
						buttonLeaveTeam.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
								alertDialogBuilder.setTitle("Leave Team");
								alertDialogBuilder.setMessage("Are you sure you want to leave the team?");
								alertDialogBuilder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										ParseQuery<ParseObject> team = new ParseQuery<ParseObject>("Team");
										team.getInBackground(getIntent().getExtras().getString("team_id"), new GetCallback<ParseObject>() {
											public void done(ParseObject team, ParseException e) {
												List<String> newTeam = team.getList("member_ids");
												newTeam.remove(getIntent().getExtras().getString("user_id"));
												team.put("member_ids", newTeam);
												team.saveInBackground(new SaveCallback() {
													public void done(ParseException e) {
														alertMessage("Disbanded", "You have left the team", true);
													}
												});
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
				}
				else
					alertMessage("Error", "Error retrieving team", true);
			}						
		});
		
		final Button buttonRequestJoin = (Button)findViewById(R.id.buttonRequestJoin);
		buttonRequestJoin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					ParseQuery<ParseObject> teamQuery = ParseQuery.getQuery("Team");
					final ParseObject team = teamQuery.get(getIntent().getExtras().getString("team_id"));
					ParseQuery<ParseObject> requestQuery = ParseQuery.getQuery("Request");
					requestQuery.whereEqualTo("from_user_id", getIntent().getExtras().getString("user_id"));
					requestQuery.whereEqualTo("to_user_id", team.getString("leader_id"));
					requestQuery.whereEqualTo("hackathon_id", getIntent().getExtras().getString("hackathon_id"));
					requestQuery.whereEqualTo("team_id", getIntent().getExtras().getString("team_id"));
					requestQuery.findInBackground(new FindCallback<ParseObject>() {
						public void done(List<ParseObject> requests, ParseException e) {
							if (requests.size() == 0) {
								ParseObject request = new ParseObject("Request");
								request.put("from_user_id", getIntent().getExtras().getString("user_id"));
								request.put("to_user_id", team.getString("leader_id"));
								request.put("hackathon_id", getIntent().getExtras().getString("hackathon_id"));
								request.put("team_id", getIntent().getExtras().getString("team_id"));
								request.saveInBackground(new SaveCallback() {
									public void done(ParseException e) {
										alertMessage("Success", "A request has been sent to join the team", true);
									}
								});
							}
							else
								alertMessage("Error", "You have already send a request for this team", true);
						}
					});
				}
				catch (ParseException e) {
					alertMessage("Error", "Error sending request", true);
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
