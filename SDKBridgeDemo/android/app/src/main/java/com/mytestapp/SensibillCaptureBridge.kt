package com.mytestapp

import android.content.Intent
import com.facebook.react.bridge.*
import com.getsensibill.core.*
import com.getsensibill.oauthclient.OAuthSettings
import com.getsensibill.oauthclient.OauthSession
import com.getsensibill.rest.network.DefaultEnvironment
import com.getsensibill.sensibillauth.SensibillAuth
import com.getsensibill.sensibillauth.SensibillAuthBuilder
import com.getsensibill.tokenprovider.TokenProvider
import com.getsensibill.web.data.configuration.WebTheme
import com.getsensibill.web.data.models.Brand
import com.getsensibill.web.ui.WebUiActivity
import com.google.gson.Gson
import timber.log.Timber

class SensibillCaptureBridge internal constructor(context: ReactApplicationContext) : ReactContextBaseJavaModule(context) {

    // TODO: Either set a token, or username and password. If token is set here, it will be used
    val token = ""
    val username = ""
    val password = ""

    // TODO: Make sure to update the key, secret, credentials and environment based on what is provided from Sensibill
    val apiKey = "TO BE PROVIDED BY SENSIBILL"
    val apiSecret = "TO BE PROVIDED BY SENSIBILL"
    val credentialType = "TO BE PROVIDED BY SENSIBILL"
    // TODO Ensure set the environment to your applicable environment and change when moving to production.
    val environment = DefaultEnvironment.RECEIPTS_SANDBOX

    // Does not need to be updated or set for demo use
    val redirectURL = "https://testclient.com/redirect"

    /**
     * Starts the SDK by creating a Sensibill Auth object, and passing it into the SDK builder.
     * The SDK is then initialized. On success it will authenticate based on which ever version
     * you pick, token or username/password.
     * On successful authentication, it then starts the SDK, and you are ready to use it.
     */
    @ReactMethod
    fun startSDK() {
        if (SensibillSDK.getState() == CoreState.STARTED) {
            Timber.e("Make sure the SDK has not already been started.")
            Timber.e("Its important to release the sdk before trying to start it again.")
            return
        }

        // creates an auth settings based on the key, secret, credential type and a possible redirect
        val authSettings = OAuthSettings(apiKey, apiSecret, credentialType, redirectURL, false)

        // builds the SensibillAuth object using the auth settings and environment
        val sensibillAuth = SensibillAuthBuilder(reactApplicationContext, environment, authSettings)
                .build()

        // Creates the builder for the SDK
        val builder = InitializationBuilder(reactApplicationContext, environment)

        // Add Token Provider to SDK
        builder.authTokenProvider(sensibillAuth.getTokenProvider())

        // Initializes the SDK with the builder, then authenticates with token or username/password
        // Troubleshooting: check the environment configuration if you get invalid token and you believe the token is correct
        SensibillSDK.initialize(builder.build(), object : SDKInitializeListener {
            override fun onInitialized() {
                if(token.isNotBlank()) {
                    authenticateWithToken(token)
                } else {
                    authenticate(sensibillAuth, username, password)
                }
            }
            override fun onInitializationFailed() {
                Timber.e("Failed to initialize the SDK.")
            }
        })
    }

    /**
     * Stopping the SDK is as simple as calling the release() function. Make sure you have first
     * started or initialized the sdk.
     */
    @ReactMethod
    fun stopSDK() {
        if (SensibillSDK.getState() == CoreState.STARTED || SensibillSDK.getState() == CoreState.INITIALIZED) {
            // Releases the SDK, shutting down and releases all resources being used by the SDK
            SensibillSDK.release()
        } else {
            Timber.e("Make sure the SDK has been started or initialized before trying to release")
        }
    }

    /**
     * Launches the Spend Manager if the SDK was started prior. Make sure the SDK was started correctly
     * and is running, otherwise the Spend Manager will finish() and not be accessible.
     */
    @ReactMethod
    fun launchSpendManager(branding: String) {
        if (SensibillSDK.getState() != CoreState.STARTED) {
            Timber.e("Make sure to start the SDK before launching Spend Manager")
            return
        }

        // converts it to a brand
        val brand = Gson().fromJson(branding, Brand::class.java)

        // passes it into a theme
        val theme = WebTheme(brand)

        // Creates the intent and launches it.
        val intent = Intent(currentActivity, WebUiActivity::class.java).apply {
            putExtra(ARG_WEB_THEME_OVERRIDE, theme)
        }
        currentActivity?.startActivity(intent)
    }

    /**
     * Launches the Receipt Capture flow Activity
     */
    @ReactMethod
    fun launchReceiptCapture() {
        if (SensibillSDK.getState() != CoreState.STARTED) {
            Timber.e("Make sure to start the SDK before launching Receipt Capture")
            return
        }
        val intent = Intent(currentActivity, ReceiptCaptureActivity::class.java)
        currentActivity?.startActivity(intent)
    }

    /**
     * Will set the token. It will then call startSDK
     */
    private fun authenticateWithToken(token: String) {
        TokenProvider.fromLambda { _, _, _ -> token }
        startSDK("userIdentifierFromToken")
    }

    /**
     * Handles authentication using a username/password
     *
     * On success, it will call startSDK
     * On failure, will output to logs
     */
    private fun authenticate(auth: SensibillAuth, username: String, password: String) {
        val sessionListener = object : SensibillAuth.SessionListener {
            override fun onSuccess(session: OauthSession) {
                startSDK("userIdentifierFromSignIn")
            }
            override fun onFailure(error: SensibillAuth.AuthError) {
                Timber.e("Failed to authenticate: %s", error.shortDescription)
            }
        }
        auth.signIn(username, password, sessionListener)
    }

    /**
     * Starts the SDK.
     *
     * On started, will open the web ui activity, and start up spend manager
     * On failed, will output to logs
     */
    private fun startSDK(userIdentifier: String) {
        SensibillSDK.start(userIdentifier, object : SDKStartup {
            override fun onSDKStarted() {
                // Once started, we are able to access Spend Manager. See launchSpendManager()
            }
            override fun onSDKFailed(error: LoginError?, p1: String?) {
                Timber.e("Failed to start SDK: %s", error)
            }
        })
    }

    override fun getName(): String {
        return "SensibillSDKBridge"
    }

    companion object {
        const val ARG_WEB_THEME_OVERRIDE = "ARG_WEB_THEME_OVERRIDE"
    }
}
