package com.example.hackedin;

import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity {

	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		final Button buttonRegister = (Button)findViewById(R.id.buttonRegisterRegister);
		buttonRegister.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String error = "";
				
				final String firstName = ((EditText)findViewById(R.id.editRegisterFirst)).getText().toString().replaceAll("\\W", "");
				final String lastName = ((EditText)findViewById(R.id.editRegisterLast)).getText().toString().replaceAll("\\W", "");
				final String email = ((EditText)findViewById(R.id.editRegisterEmail)).getText().toString();
				final String password = ((EditText)findViewById(R.id.editRegisterPassword)).getText().toString();
				final String confirm = ((EditText)findViewById(R.id.editRegisterConfirm)).getText().toString();
				final String school = ((EditText)findViewById(R.id.editRegisterSchool)).getText().toString();
				final String phone = ((EditText)findViewById(R.id.editRegisterPhone)).getText().toString();
				
				if (firstName.length() == 0)
					error += "\nInvalid first name";
				if (lastName.length() == 0)
					error += "\nInvalid last name";
				if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
					error += "\nInvalid e-mail";
				if (password.length() < 8)
					error += "\nPassword too short";
				if (!password.equals(confirm))
					error += "\nPasswords don't match";
				if (school.replaceAll("\\W", "").length() == 0)
					error += "\nInvalid school";
				if (phone.length() > 0 && !android.util.Patterns.PHONE.matcher(phone).matches())
					error += "\nInvalid phone";
				
				if (error.length() > 0)
					alertMessage("Error", "Error creating profile:" + error);
				else {
					ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
					query.whereEqualTo("email", email);
					query.findInBackground(new FindCallback<ParseObject>() {
						public void done(List<ParseObject> userList, ParseException e) {
							if (e == null) {
								if (userList.size() == 0) {
									final ParseUser user = new ParseUser();
									user.setUsername(email);
									user.setPassword(password);
									user.setEmail(email);
									user.signUpInBackground(new SignUpCallback() {
										public void done(ParseException e) {
											if (e == null) {
												ParseObject userProfile = new ParseObject("UserProfile");
												userProfile.getRelation("user").add(user);
												userProfile.put("first_name", firstName);
												userProfile.put("last_name", lastName);
												userProfile.put("school", school);
												userProfile.put("phone", phone);
												userProfile.put("skills", "");
												userProfile.put("about", "");
												userProfile.saveInBackground(new SaveCallback() {
													public void done(ParseException e) {
														if (e == null)
															alertMessage("Success", "Your registration was successful!");
														else
															alertMessage("Error", "Error creating user profile");
													}
												});
											}
											else
												alertMessage("Error", "Error creating user");
										}
									});
								}
								else
									alertMessage("Error", "E-mail is already in use");
							}
							else
								alertMessage("Error", "Error querying users");
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
	
	public void alertMessage(String title, String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(message);
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}
