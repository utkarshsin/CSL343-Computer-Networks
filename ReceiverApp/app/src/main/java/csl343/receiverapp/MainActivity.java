package csl343.receiverapp;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static java.lang.StrictMath.abs;


public class MainActivity extends AppCompatActivity
{
    Recorder recorder;
    Button recordButton;
    CheckBox skipSilence;
    private Button pauseResumeButton;
    private static int SampleFreq = 8000;
    private static int frequencyOfSignal=500;
    private static int baudrate=2;
    private static int numchars=4;
    private static int numberofbits=numchars*7;
    char[] text;
    TextView Result;
    static String recv="";
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Wav Recorder");
        setupRecorder();
        skipSilence = (CheckBox) findViewById(R.id.skipSilence);
        skipSilence.setVisibility(CheckBox.INVISIBLE);
        Result=(TextView) findViewById(R.id.Result);
        Result.setVisibility(EditText.INVISIBLE);
        final EditText Password=(EditText)findViewById(R.id.password);
        final Button Errorbits=(Button)findViewById(R.id.errorbits);
        final TextView bitserror=(TextView)findViewById(R.id.Ber);
        bitserror.setVisibility(TextView.INVISIBLE);
        final TextView bits=(TextView)findViewById(R.id.Bits);
        bits.setVisibility(TextView.INVISIBLE);
        Errorbits.setEnabled(false);
        final Button decode=(Button)findViewById(R.id.Decode);
        decode.setEnabled(false);

        skipSilence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if (isChecked)
                {
                    setupNoiseRecorder();
                }
                else
                {
                    setupRecorder();
                }
            }
        });

        recordButton = (Button) findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
//                File file = new File(Environment.getExternalStorageDirectory()
//                        .getAbsolutePath() + File.separator + "utkarsh.wav");
//                boolean deleted = file.delete();
                recorder.startRecording();
                skipSilence.setEnabled(false);
            }
        });
        findViewById(R.id.stopButton).setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
                recorder.stopRecording();
                skipSilence.setEnabled(true);
                recordButton.post(new Runnable()
                {
                    @Override public void run()
                    {
                        animateVoice(0);
                    }
                });
                decode.setEnabled(true);
                Errorbits.setEnabled(true);
            }
        });

        findViewById(R.id.Decode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                File f = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + File.separator + "utkarsh.wav");
                if(f==null)
                {
                    Toast.makeText(MainActivity.this, "File is not present for decode",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    String s = f.toString();
                    double[] array = openWav(s);
                    String password = data(array);
                    text=password.toCharArray();
                    Result.setText(password);

                    Result.setVisibility(TextView.VISIBLE);
                }
            }
        });

        Errorbits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String passwordtext=Password.getText().toString();
                    if(passwordtext.matches(""))
                    {
                        Toast.makeText(MainActivity.this, "Enter the Original Password",
                                Toast.LENGTH_SHORT).show();
                    }
                    else if((passwordtext.toString().toCharArray().length!=numchars))
                    {
                        Toast.makeText(MainActivity.this, "Length of password is not correct",
                                Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,passwordtext,
                                Toast.LENGTH_SHORT).show();
                        char[] array=passwordtext.toCharArray();
                        int count=0;
                        for(int i=0;i<array.length;i++)
                        {
                            if(array[i]!=text[i])
                            {
                                count++;
                            }
                        }
                        String s=Integer.toString(count);
                        bitserror.setText(s);
                        bitserror.setVisibility(TextView.VISIBLE);
                        bits.setText(recv);
                        bits.setVisibility(TextView.VISIBLE);
                    }
            }
        });
    }

    private void setupNoiseRecorder() {

        recorder = Test_Recorder.wav(
                new PullTransport.Noise(mic(), new PullTransport.OnAudioChunkPulledListener() {
                    @Override public void onAudioChunkPulled(AudioChunk audioChunk) {
                        animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                    }
                }, new WriteAction.Default(), new Recorder.OnSilenceListener() {
                    @Override public void onSilence(long silenceTime) {
                        Log.e("silenceTime", String.valueOf(silenceTime));
                        Toast.makeText(MainActivity.this, "silence of " + silenceTime + " detected",
                                Toast.LENGTH_SHORT).show();
                    }
                }, 200), file());
    }

    private void setupRecorder()
    {
        recorder = Test_Recorder.wav(
                new PullTransport.Default(mic(), new PullTransport.OnAudioChunkPulledListener() {
                    @Override public void onAudioChunkPulled(AudioChunk audioChunk) {
                        animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                    }
                }), file());
    }

    private void animateVoice(final float maxPeak) {
        recordButton.animate().scaleX(1 + maxPeak).scaleY(1 + maxPeak).setDuration(10).start();
    }

    private AudioSource mic() {
        return new AudioSource.Smart(MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                AudioFormat.CHANNEL_IN_MONO, SampleFreq);
    }

    @NonNull
    private File file() {
        return new File(Environment.getExternalStorageDirectory(), "utkarsh.wav");
    }


    private static byte[] demodulate(String filename) {
        File file = new File(filename);
        try {

            InputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer, 0, buffer.length);
            fis.close();
            return buffer;
        } catch (Exception e) {

        }
        return null;
    }

    private static String data(double[] array)
    {
        int[] result = new int[numberofbits];
        for (int i = 0, index = 0; i < numberofbits; i++) {
            int count = 0;
            for (int j = 0; j < (int) (array.length / numberofbits) - 1; j++) {
                if (array[index] * array[index + 1] <= 0) {
                    count++;
                }
                index++;
            }
            result[i] = count;
        }

        int low = result[0];
        int high = result[0];
        for (int i = 0; i < result.length; i++) {
            if (result[i] < low) {
                low = result[i];
            }
            if (result[i] > high) {
                high = result[i];
            }
        }
        String s = "";
        for (int i = 0; i < result.length; i++) {
            if (abs(result[i] - low) > abs(result[i] - high)) {
                s = s + "1";
                result[i] = 1;
            } else {
                s = s + "0";
                result[i] = 0;
            }
        }
        recv=recv+s;
        String password="";
        int index=0;
        while(index<result.length) {
            String temp = "";
            for (int i = 0; i < 7; i++) {
                temp = temp + result[index++];
            }
            int foo = Integer.parseInt(temp, 2);

            password=password+(char)foo;
        }

        return password;

    }

    public static double bytesToDouble(byte firstByte, byte secondByte) {
        // convert two bytes to one short (little endian)
        int s = (secondByte << 8) | firstByte;
        // convert to range from -1 to (just below) 1
        return s / 32768.0;
    }

    // Returns left and right double arrays. 'right' will be null if sound is mono.
    public static double[] openWav(String filename)
    {
        byte[] wav =demodulate(filename);

        // Determine if mono or stereo
        int channels = wav[22];     // Forget byte 23 as 99.999% of WAVs are 1 or 2 channels

        // Get past all the other sub chunks to get to the data subchunk:
        int pos = 12;   // First Subchunk ID from 12 to 16

        // Keep iterating until we find the data chunk (i.e. 64 61 74 61 ...... (i.e. 100 97 116 97 in decimal))
        while(!(wav[pos]==100 && wav[pos+1]==97 && wav[pos+2]==116 && wav[pos+3]==97)) {
            pos += 4;
            int chunkSize = wav[pos] + wav[pos + 1] * 256 + wav[pos + 2] * 65536 + wav[pos + 3] * 16777216;
            pos += 4 + chunkSize;
        }
        pos += 8;

        // Pos is now positioned to start of actual sound data.
        int samples = (wav.length - pos)/2;     // 2 bytes per sample (16 bit sound mono)
        if (channels == 2) samples /= 2;        // 4 bytes per sample (16 bit stereo)

        // Allocate memory (right will be null if only mono sound)
        double[] left = new double[samples];
        double[] right;
        if (channels == 2)
            right = new double[samples];
        else right = null;

        // Write to double array/s:
        int i=0;
        while (pos < wav.length) {
            left[i] = bytesToDouble(wav[pos], wav[pos + 1]);
            pos += 2;
            if (channels == 2) {
                right[i] = bytesToDouble(wav[pos], wav[pos + 1]);
                pos += 2;
            }
            i++;
        }

        return left;
    }
}

