package com.pracify;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.pracify.authenticator.AccountAuthenticator;

public class MainActivity extends ActionBarActivity {

	private static final String STATE_DIALOG = "state_dialog";
	private static final String STATE_INVALIDATE = "state_invalidate";

	private String TAG = this.getClass().getSimpleName();
	private Account mAccount;
	private AccountManager mAccountManager;
	private AlertDialog mAlertDialog;
	private boolean mInvalidate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAccountManager = AccountManager.get(this);

		if (savedInstanceState != null) {
			boolean showDialog = savedInstanceState.getBoolean(STATE_DIALOG);
			boolean invalidate = savedInstanceState
					.getBoolean(STATE_INVALIDATE);
			if (showDialog) {
				showAccountPicker(LoginActivity.PARAM_AUTHTOKEN_TYPE,
						invalidate);
			}
		}

		Account[] allAccounts = mAccountManager
				.getAccountsByType(AccountAuthenticator.ACCOUNT_TYPE);

		if (allAccounts.length == 1) {

			Log.d(TAG, "Got one Account");
			mAccount = allAccounts[0];

			getExistingAccountAuthToken(mAccount,
					LoginActivity.PARAM_AUTHTOKEN_TYPE);
		} else {

			if (allAccounts.length >= 1) {

				for (Account account : allAccounts) {

					mAccountManager.removeAccount(account, null, null);
				}
			}

			addNewAccount(AccountAuthenticator.ACCOUNT_TYPE,
					LoginActivity.PARAM_AUTHTOKEN_TYPE);
		}
	}

	/**
	 * Get the auth token for an existing account on the AccountManager
	 * 
	 * @param account
	 * @param authTokenType
	 */
	private void getExistingAccountAuthToken(Account account,
			String authTokenType) {
		final AccountManagerFuture<Bundle> future = mAccountManager
				.getAuthToken(account, authTokenType, null, this, null, null);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Bundle bnd = future.getResult();

					final String authtoken = bnd
							.getString(AccountManager.KEY_AUTHTOKEN);
					if (authtoken != null) {
						startHomeActivity();
					} else {
						showMessage("Unable to Authenticate!!");
						finish();
					}
					Log.d(TAG, "GetToken Bundle is " + bnd);
				} catch (Exception e) {
					e.printStackTrace();
					showMessage(e.getMessage());
					finish();
				}
			}
		}).start();
	}

	private void startHomeActivity() {

		Intent intent = new Intent(this, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	/**
	 * Add new account to the account manager
	 * 
	 * @param accountType
	 * @param authTokenType
	 */
	private void addNewAccount(String accountType, String authTokenType) {
		final AccountManagerFuture<Bundle> future = mAccountManager.addAccount(
				accountType, authTokenType, null, null, this,
				new AccountManagerCallback<Bundle>() {
					@Override
					public void run(AccountManagerFuture<Bundle> future) {
						try {
							Bundle bnd = future.getResult();
							Log.d(TAG, "AddNewAccount Bundle is " + bnd);

							Account[] allAccounts = mAccountManager
									.getAccountsByType(AccountAuthenticator.ACCOUNT_TYPE);

							if (allAccounts.length == 1) {

								Log.d(TAG, "Got New Account");
								mAccount = allAccounts[0];

								getExistingAccountAuthToken(mAccount,
										LoginActivity.PARAM_AUTHTOKEN_TYPE);
							} else {

								showMessage("Some Error! Try Later....");
								finish();
							}

						} catch (Exception e) {
							e.printStackTrace();
							showMessage(e.getMessage());
							finish();
						}
					}
				}, null);
	}

	/**
	 * Show all the accounts registered on the account manager. Request an auth
	 * token upon user select.
	 * 
	 * @param authTokenType
	 */
	private void showAccountPicker(final String authTokenType,
			final boolean invalidate) {
		mInvalidate = invalidate;
		final Account availableAccounts[] = mAccountManager
				.getAccountsByType(AccountAuthenticator.ACCOUNT_TYPE);

		if (availableAccounts.length == 0) {
			Toast.makeText(this, "No accounts", Toast.LENGTH_SHORT).show();
		} else {
			String name[] = new String[availableAccounts.length];
			for (int i = 0; i < availableAccounts.length; i++) {
				name[i] = availableAccounts[i].name;
			}

			// Account picker
			mAlertDialog = new AlertDialog.Builder(this)
					.setTitle("Pick Account")
					.setAdapter(
							new ArrayAdapter<String>(getBaseContext(),
									android.R.layout.simple_list_item_1, name),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (invalidate)
										invalidateAuthToken(
												availableAccounts[which],
												authTokenType);
									else
										getExistingAccountAuthToken(
												availableAccounts[which],
												authTokenType);
								}
							}).create();
			mAlertDialog.show();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mAlertDialog != null && mAlertDialog.isShowing()) {
			outState.putBoolean(STATE_DIALOG, true);
			outState.putBoolean(STATE_INVALIDATE, mInvalidate);
		}
	}

	/**
	 * Invalidates the auth token for the account
	 * 
	 * @param account
	 * @param authTokenType
	 */
	private void invalidateAuthToken(final Account account, String authTokenType) {
		final AccountManagerFuture<Bundle> future = mAccountManager
				.getAuthToken(account, authTokenType, null, this, null, null);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Bundle bnd = future.getResult();

					final String authtoken = bnd
							.getString(AccountManager.KEY_AUTHTOKEN);
					mAccountManager
							.invalidateAuthToken(account.type, authtoken);
					showMessage(account.name + " invalidated");
				} catch (Exception e) {
					e.printStackTrace();
					showMessage(e.getMessage());
					finish();
				}
			}
		}).start();
	}

	private void showMessage(final String msg) {
		if (TextUtils.isEmpty(msg))
			return;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT)
						.show();
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
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
