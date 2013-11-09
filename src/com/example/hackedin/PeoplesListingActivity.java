package com.example.hackedin;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class PeoplesListingActivity extends Activity {

	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_peopleslisting);

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Team");
		query.whereEqualTo("hackathon_id", getIntent().getExtras().getString("hackathon_id"));
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(final List<ParseObject> teamList, ParseException e) {
				if (e == null) {
					final ArrayList<String> teamNames = new ArrayList<String>();
					final ListView listChooseTeam = (ListView)findViewById(R.id.listChooseTeam);
					for (ParseObject o : teamList)
						teamNames.add(o.getString("name") + " (" + o.getList("member_ids").size() + "/" + o.getInt("max_members")+ ")");
					ArrayAdapter<String> aa = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, teamNames);
					listChooseTeam.setAdapter(aa);
					listChooseTeam.setOnItemClickListener(new AdapterView.OnItemClickListener() {				
						public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
							Intent i = new Intent(context, ViewTeamActivity.class);
							Bundle b = new Bundle();
							b.putString("hackathon_id", getIntent().getExtras().getString("hackathon_id"));
							b.putString("user_id", getIntent().getExtras().getString("user_id"));
							b.putString("team_id", teamList.get(position).getObjectId());
							i.putExtras(b);
							startActivity(i);
						}
					});
				}
				else
					alertMessage("Error", "Error retrieving teams", true);
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
