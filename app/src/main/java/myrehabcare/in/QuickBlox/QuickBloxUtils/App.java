package myrehabcare.in.QuickBlox.QuickBloxUtils;

import android.app.Application;
import android.content.Context;

import com.quickblox.auth.session.QBSettings;

import myrehabcare.in.R;

public class App extends Application {
    //App credentials
    private static final String APPLICATION_ID = "88141";
    private static final String AUTH_KEY = "mp3tQmnrYP9yEcr";
    private static final String AUTH_SECRET = "2OUFFUtTasx6mSH";
    private static final String ACCOUNT_KEY = "W93QvbuFnBhwNFQiNXqw";

    public static final String USER_DEFAULT_PASSWORD = "quickblox";

    private static App instance;
    private QBResRequestExecutor qbResRequestExecutor;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //initFabric();
        checkAppCredentials();
        initCredentials();
    }
/*
    private void initFabric() {
        if (!BuildConfig.DEBUG) {
            Fabr.with(this, new Crashlytics());
        }
    }*/

    private void checkAppCredentials() {
        if (APPLICATION_ID.isEmpty() || AUTH_KEY.isEmpty() || AUTH_SECRET.isEmpty() || ACCOUNT_KEY.isEmpty()) {
            throw new AssertionError(getString(R.string.error_credentials_empty));
        }
    }

    private void initCredentials() {
        QBSettings.getInstance().init(getApplicationContext(), APPLICATION_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

        // Uncomment and put your Api and Chat servers endpoints if you want to point the sample
        // against your own server.
        //
        // QBSettings.getInstance().setEndpoints("https://your_api_endpoint.com", "your_chat_endpoint", ServiceZone.PRODUCTION);
        // QBSettings.getInstance().setZone(ServiceZone.PRODUCTION);
    }

    public synchronized QBResRequestExecutor getQbResRequestExecutor(Context context) {
        return qbResRequestExecutor == null
                ? qbResRequestExecutor = new QBResRequestExecutor(context)
                : qbResRequestExecutor;
    }

    public static App getInstance() {
        return instance;
    }
}