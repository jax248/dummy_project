package com.pracify.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.pracify.LoginActivity;
import com.pracify.js.Login;

/*
 * Implement AbstractAccountAuthenticator and stub out all
 * of its methods
 */
public class AccountAuthenticator extends AbstractAccountAuthenticator {
	public static final String ACCOUNT_TYPE = "pracify.com";

	Context mContext;

	// Simple constructor
	public AccountAuthenticator(Context context) {
		super(context);
		mContext = context;
	}

	// Editing properties is not supported
	@Override
	public Bundle editProperties(AccountAuthenticatorResponse r, String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response,
			String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options)
			throws NetworkErrorException {

		final Bundle result;
		final Intent intent;

		intent = new Intent(this.mContext, LoginActivity.class);
		intent.putExtra(LoginActivity.PARAM_AUTHTOKEN_TYPE, authTokenType);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
				response);

		result = new Bundle();
		result.putParcelable(AccountManager.KEY_INTENT, intent);

		return result;
	}

	public Bundle confirmCredentials(AccountAuthenticatorResponse response,
			Account account, Bundle options) {
		if (options != null && options.containsKey(AccountManager.KEY_PASSWORD)) {
			final String password = options
					.getString(AccountManager.KEY_PASSWORD);
			final boolean verified = onlineConfirmPassword(account.name,
					password);
			final Bundle result = new Bundle();
			result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, verified);
			return result;
		}
		// Launch AuthenticatorActivity to confirm credentials
		final Intent intent = new Intent(mContext, LoginActivity.class);
		intent.putExtra(LoginActivity.PARAM_USERNAME, account.name);
		intent.putExtra(LoginActivity.PARAM_CONFIRMCREDENTIALS, true);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
				response);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle loginOptions)
			throws NetworkErrorException {
		if (!authTokenType.equals(LoginActivity.PARAM_AUTHTOKEN_TYPE)) {
			final Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ERROR_MESSAGE,
					"invalid authTokenType");
			return result;
		}
		final AccountManager am = AccountManager.get(mContext);
		String authToken = am.peekAuthToken(account, authTokenType);

		// Lets give another try to authenticate the user
		if (TextUtils.isEmpty(authToken)) {
			final String password = am.getPassword(account);
			if (password != null) {
				final boolean verified = onlineConfirmPassword(account.name,
						password);
				if (verified) {
					authToken = password;
				}
			}
		}

		// If we get an authToken - we return it
		if (!TextUtils.isEmpty(authToken)) {
			final Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
			result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
			result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
			return result;
		}
		// the password was missing or incorrect, return an Intent to an
		// Activity that will prompt the user for the password.
		final Intent intent = new Intent(mContext, LoginActivity.class);
		intent.putExtra(LoginActivity.PARAM_USERNAME, account.name);
		intent.putExtra(LoginActivity.PARAM_AUTHTOKEN_TYPE, authTokenType);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
				response);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	private boolean onlineConfirmPassword(String name, String password) {

		return Login.validateUser(name, password);
	}

	// Getting a label for the auth token is not supported
	@Override
	public String getAuthTokenLabel(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle loginOptions) {
		final Intent intent = new Intent(mContext, LoginActivity.class);
		intent.putExtra(LoginActivity.PARAM_USERNAME, account.name);
		intent.putExtra(LoginActivity.PARAM_AUTHTOKEN_TYPE, authTokenType);
		intent.putExtra(LoginActivity.PARAM_CONFIRMCREDENTIALS, false);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response,
			Account account, String[] features) {
		final Bundle result = new Bundle();
		result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
		return result;
	}
}