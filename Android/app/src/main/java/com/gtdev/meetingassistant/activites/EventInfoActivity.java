package com.gtdev.meetingassistant.activites;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gtdev.meetingassistant.R;
import com.gtdev.meetingassistant.model.EventInfo;
import com.gtdev.meetingassistant.utils.RestClientHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.skd.androidrecording.audio.AudioPlaybackManager;
import com.skd.androidrecording.audio.AudioRecordingHandler;
import com.skd.androidrecording.audio.AudioRecordingThread;
import com.skd.androidrecording.visualizer.VisualizerView;
import com.skd.androidrecording.visualizer.renderer.BarGraphRenderer;

import org.apache.http.Header;

import java.io.File;

import nl.changer.audiowife.AudioWife;

public class EventInfoActivity extends AppCompatActivity {
    private static String fileName = null;
    public static String FileNameArg = "arg_filename";

    private Button recordBtn, playBtn;
    private VisualizerView visualizerView;
    private AudioPlaybackManager playbackManager;

    private AudioRecordingThread recordingThread;
    private boolean startRecording = true;

    public static String getFileName(boolean isAudio) {
        String storageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        return String.format("%s/%s", storageDir, "AUDIO_FILE_NAME");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Meeting info");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventInfoActivity.this.onBackPressed();
            }
        });

        fileName = getFileName(true);

        recordBtn = (Button) findViewById(R.id.recordButton);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(EventInfoActivity.this);
                dialog.setContentView(R.layout.record_dialog);
                dialog.setTitle("Recording...");
                visualizerView = (VisualizerView) dialog.findViewById(R.id.visualizerView);
                dialog.findViewById(R.id.stopRecButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopRecording();
                        dialog.cancel();
                        final ProgressDialog pd = new ProgressDialog(EventInfoActivity.this);
                        pd.setMessage("Uploading audio...");
                        pd.show();
                        RestClientHelper.uploadAudio(EventInfoActivity.this, MainActivity.userEmail, new File(fileName), new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                pd.cancel();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                pd.cancel();
                            }
                        });
                    }
                });
                setupVisualizer(visualizerView);
                startRecording();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

        playBtn = (Button) findViewById(R.id.playButton);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AudioWife.getInstance().init(EventInfoActivity.this, Uri.fromFile(new File(fileName)))
                        .useDefaultUi((android.view.ViewGroup) findViewById(R.id.playerLayout), getLayoutInflater());
            }
        });
        initViews(MainActivity.eventInfos.get(getIntent().getIntExtra("event_id", 0)));
    }

    private void initViews(EventInfo eventInfo) {
        ((TextView) findViewById(R.id.title)).setText(eventInfo.title);
        ((TextView) findViewById(R.id.date)).setText(eventInfo.date);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.attendants);
        linearLayout.removeAllViews();
        for (int i = 0; i < eventInfo.attendants.size(); i++) {
            TextView textView = new TextView(linearLayout.getContext());
            textView.setText(eventInfo.attendants.get(i));
            textView.setTextColor(Color.BLACK);
            linearLayout.addView(textView);
        }
        if (eventInfo.recorded) {
            playBtn.setVisibility(View.VISIBLE);
            recordBtn.setVisibility(View.GONE);
        } else {
            recordBtn.setVisibility(View.VISIBLE);
            playBtn.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRecording();
    }

    @Override
    protected void onDestroy() {
        stopRecording();
        releaseVisualizer();
        super.onDestroy();
    }

    private void setupVisualizer(VisualizerView visualizerView) {
        Paint paint = new Paint();
        paint.setStrokeWidth(5f);
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(200, 227, 69, 53));
        BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(2, paint, false);
        visualizerView.addRenderer(barGraphRendererBottom);
    }

    private void releaseVisualizer() {
        if (visualizerView != null) {
            visualizerView.release();
            visualizerView = null;
        }
    }

    private void startRecording() {
        recordingThread = new AudioRecordingThread(fileName, new AudioRecordingHandler() {
            @Override
            public void onFftDataCapture(final byte[] bytes) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (visualizerView != null) {
                            visualizerView.updateVisualizerFFT(bytes);
                        }
                    }
                });
            }

            @Override
            public void onRecordSuccess() {}

            @Override
            public void onRecordingError() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        stopRecording();
                    }
                });
            }

            @Override
            public void onRecordSaveError() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        stopRecording();
                    }
                });
            }
        });
        recordingThread.start();
    }

    private void stopRecording() {
        if (recordingThread != null) {
            recordingThread.stopRecording();
            recordingThread = null;
        }
    }

}
