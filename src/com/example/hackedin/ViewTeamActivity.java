package com.example.hackedin;

import java.util.ArrayList;
import java.util.HashSet;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
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
			ParseObject team = query.get(getIntent().getExtras().getString("team_id"));
			((TextView)findViewById(R.id.textTeamName)).setText(team.getString("name"));
			ParseQuery<ParseObject> leaderQuery = ParseQuery.getQuery("UserProfile");
			leaderQuery.whereEqualTo("user_id", team.getString("leader_id"));
			ParseObject leaderProfile = leaderQuery.getFirst();
			((TextView)findViewById(R.id.textTeamLeader)).setText("Leader: " + leaderProfile.getString("first_name") + " " + leaderProfile.getString("last_name"));
			((TextView)findViewById(R.id.textTeamAbout)).setText("About: " + team.getString("about"));
			((TextView)findViewById(R.id.textTeamSize)).setText("Size: " + team.getList("member_ids").size() + "/" + team.getInt("max_members"));
			final ArrayList<String> memberNames = new ArrayList<String>();
			for (Object member_id : team.getList("member_ids")) {
				ParseQuery<ParseObject> memberQuery = ParseQuery.getQuery("UserProfile");
				memberQuery.whereEqualTo("user_id", (String)member_id);
				ParseObject memberProfile = memberQuery.getFirst();
				memberNames.add(memberProfile.getString("first_name") + " " + memberProfile.getString("last_name"));
			}
			final ListView listTeamMembers = (ListView)findViewById(R.id.listTeamMembers);
			ArrayAdapter<String> aa = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, memberNames);
			listTeamMembers.setAdapter(aa);
		}
		catch (ParseException e) {
			alertMessage("Error", "Error retrieving team information", true);
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
