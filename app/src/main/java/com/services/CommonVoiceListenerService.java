package com.services;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.savemeout.R;
import com.savemeout.utils.Constants;
import com.savemeout.ui.voicetotext.VoiceRecognitionActivity;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

/**
 * Created by binod on 3/1/18.
 */

public class CommonVoiceListenerService extends Service implements
        RecognitionListener {

    protected SpeechRecognizer mGoogleSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;
    private PackageManager packageManager;
    List<PackageInfo> packageList1;
    public static boolean IS_SERVICE_RUNNING = false;
    String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    //pocketSpinx
    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";
    private static final String FORECAST_SEARCH = "forecast";
    private static final String DIGITS_SEARCH = "digits";
    private static final String PHONE_SEARCH = "phones";
    private static final String MENU_SEARCH = "menu";

    /* Keyword we are looking for to activate menu */
    private static final String KEYPHRASE = "ok android";
    private edu.cmu.pocketsphinx.SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;
    private BroadcastReceiver receiver;
    Intent intentBroadCast;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initFeaturesUtils();
        showNotification();
        IS_SERVICE_RUNNING = true;
        captions = new HashMap<>();
        captions.put(KWS_SEARCH, R.string.kws_caption);
        captions.put(MENU_SEARCH, R.string.menu_caption);
        captions.put(DIGITS_SEARCH, R.string.digits_caption);
        captions.put(PHONE_SEARCH, R.string.phone_caption);
        captions.put(FORECAST_SEARCH, R.string.forecast_caption);
        intentBroadCast = new Intent();
        intentBroadCast.setAction(Constants.intentfilterfromservice);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        new SetupTask(this).execute();
        initGoogleSpeechRecognizer(this);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                startListeningGoogle();
            }
        };
        registerReceiver(receiver, new IntentFilter(Constants.intentfilterfromactivity));
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
        IS_SERVICE_RUNNING = false;
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();
        if (text.equals(KEYPHRASE))
            switchSearch(KWS_SEARCH);
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        //((TextView) findViewById(R.id.result_text)).setText("");
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();

            //makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            startListeningGoogle();
        }
    }

    void startListeningGoogle() {
        recognizer.stop();
        final Intent intent1 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent1.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        mGoogleSpeechRecognizer.startListening(intent1);
        intentBroadCast.putExtra("type", Constants.listening_started_ui);
        sendBroadcast(intentBroadCast);
    }

    void stopListeningGoogle() {
        // mGoogleSpeechRecognizer.cancel();
        switchSearch(KWS_SEARCH);
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(KWS_SEARCH))
            switchSearch(KWS_SEARCH);
    }

    @Override
    public void onError(Exception error) {
        //((TextView) findViewById(R.id.caption_text)).setText(error.getMessage());
    }

    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);
    }


    class GoogleSpeech implements android.speech.RecognitionListener {


        @Override
        public void onBeginningOfSpeech() {
            Log.d("Speech", "onBeginningOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.d("Speech", "onBufferReceived");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d("Speech", "onEndOfSpeech");

        }

        @Override
        public void onError(int error) {
            if (isNetworkAvailable(CommonVoiceListenerService.this)) {
                intentBroadCast.putExtra("type", Constants.listening_end);
                intentBroadCast.putExtra("text", "");
                sendBroadcast(intentBroadCast);
            }
            stopListeningGoogle();
            Log.d("Speech", "onError");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.d("Speech", "onEvent");
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.d("Speech", "onPartialResults");
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d("Speech", "onReadyForSpeech");
        }

        @Override
        public void onResults(Bundle results) {
            Log.d("Speech", "onResults");
            String resultText = "";
            ArrayList strlist = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (strlist.size() > 0) {
                Toast.makeText(CommonVoiceListenerService.this, "" + strlist.get(0), Toast.LENGTH_SHORT).show();
                for (int i = 0; i < packageList1.size(); i++) {
                    String appname = packageManager.getApplicationLabel(packageList1.get(i).applicationInfo).toString().toLowerCase();
                    if (strlist.get(0).toString().toLowerCase().contains("open " + appname)) {
                        final int finalI = i;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(packageList1.get(finalI).packageName);
                                startActivity(LaunchIntent);
                            }
                        }, 1200);
                        Toast.makeText(getApplicationContext(), "Opening " + appname, Toast.LENGTH_SHORT).show();

                        break;
                    }
                }
                if (strlist.get(0).toString().toLowerCase().equals("who is your creator") || strlist.get(0).toString().toLowerCase().equals("who created you") || strlist.get(0).toString().toLowerCase().equals("who is your father")) {
                    Toast.makeText(CommonVoiceListenerService.this, "I am created by Mr. Binod", Toast.LENGTH_SHORT).show();
                }

                resultText = strlist.get(0).toString();
            }

            intentBroadCast.putExtra("type", Constants.listening_end);
            intentBroadCast.putExtra("text", resultText);
            sendBroadcast(intentBroadCast);
            stopListeningGoogle();

        }

        @Override
        public void onRmsChanged(float rmsdB) {
            Log.d("Speech", "onRmsChanged");
        }

        public boolean isNetworkAvailable(Context context) {
            final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
            return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
        }


    }

    public Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/<id_here>"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/<user_name_here>"));
        }
    }


    private void initGoogleSpeechRecognizer(Context mContext) {

        mGoogleSpeechRecognizer = SpeechRecognizer
                .createSpeechRecognizer(mContext);

        mGoogleSpeechRecognizer.setRecognitionListener(new GoogleSpeech());

        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES, true);
    }

    private void initFeaturesUtils() {
        packageManager = getPackageManager();
        List<PackageInfo> packageList = packageManager
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);

        packageList1 = new ArrayList<PackageInfo>();
         /*To filter out System apps*/

        for (PackageInfo pi : packageList) {
            packageList1.add(pi);
            String appname = packageManager.getApplicationLabel(pi.applicationInfo).toString().toLowerCase();
            Log.e("app_name", appname);
        }
    }

    private class SetupTask extends AsyncTask<Void, Void, Exception> {
        WeakReference<CommonVoiceListenerService> activityReference;


        SetupTask(CommonVoiceListenerService activity) {
            this.activityReference = new WeakReference<>(activity);

        }

        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(activityReference.get());
                File assetDir = assets.syncAssets();
                activityReference.get().setupRecognizer(assetDir);
            } catch (IOException e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception result) {
            if (result != null) {
                /*((TextView) activityReference.get().findViewById(R.id.caption_text))
                        .setText("Failed to init recognizer " + result);*/
            } else {
                activityReference.get().switchSearch(KWS_SEARCH);
                Intent intentBroadCast = new Intent();
                intentBroadCast.setAction(Constants.intentfilterfromservice);
                intentBroadCast.putExtra("type", Constants.ps_initialization_complete);
                sendBroadcast(intentBroadCast);
            }
        }
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)

                .getRecognizer();
        recognizer.addListener(this);

        /* In your application you might not need to add all those searches.
          They are added here for demonstration. You can leave just one.
         */

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);

        // Create grammar-based search for selection between demos
        File menuGrammar = new File(assetsDir, "menu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);

        // Create grammar-based search for digit recognition
        File digitsGrammar = new File(assetsDir, "digits.gram");
        recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);

        // Create language model search
        File languageModel = new File(assetsDir, "weather.dmp");
        recognizer.addNgramSearch(FORECAST_SEARCH, languageModel);

        // Phonetic search
        File phoneticModel = new File(assetsDir, "en-phone.dmp");
        recognizer.addAllphoneSearch(PHONE_SEARCH, phoneticModel);
    }

    private void switchSearch(String searchName) {
        recognizer.stop();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH))
            recognizer.startListening(searchName);
        else
            recognizer.startListening(searchName, 10000);

        String caption = getResources().getString(captions.get(searchName));
        //((TextView) findViewById(R.id.caption_text)).setText(caption);

    }

    void showNotification() {
        Intent notificationIntent = new Intent(this, VoiceRecognitionActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("Voice Recognition")
                .setContentText("online")
                .setContentIntent(pendingIntent);

        //Yes intent
        /*Intent yesReceive = new Intent();
        yesReceive.setAction("close");
        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Close", pendingIntentYes);
        */
        startForeground(1337, mBuilder.build());
    }


}
