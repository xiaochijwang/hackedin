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

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ViewRequestActivity extends Activity {

	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewrequest);

		try {
			final ArrayList<String> requestNames = new ArrayList<String>();
			final ListView listChooseRequest = (ListView)findViewById(R.id.listChooseRequest);
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
			query.whereEqualTo("to_user_id", getIntent().getExtras().getString("user_id"));
			query.whereEqualTo("hackathon_id", getIntent().getExtras().getString("hackathon_id"));
			final List<ParseObject> requestList = query.find();
			for (ParseObject o : requestList) {
				ParseQuery<ParseObject> fromQuery = ParseQuery.getQuery("UserProfile");
				fromQuery.whereEqualTo("user_id", o.getString("from_user_id"));
				ParseObject from = fromQuery.getFirst();
				requestNames.add(from.getString("first_name") + " " + from.getString("last_name"));
			}
			ArrayAdapter<String> aa = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, requestNames);
			listChooseRequest.setAdapter(aa);
			listChooseRequest.setOnItemClickListener(new AdapterView.OnItemClickListener() {				
				public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
					Intent i = new Intent(context, ViewProfileActivity.class);
					Bundle b = new Bundle();
					b.putString("user_id", requestList.get(position).getString("to_user_id"));
					b.putString("view_user_id", requestList.get(position).getString("from_user_id"));
					b.putString("hackathon_id", requestList.get(position).getString("hackathon_id"));
					b.putString("team_id", requestList.get(position).getString("team_id"));
					i.putExtras(b);
					startActivity(i);
				}
			});
		}
		catch (ParseException e){
			alertMessage("Error", "Error retrieving requests", true);
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
