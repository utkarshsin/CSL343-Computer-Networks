package csl343.sendapp;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
{
    private static int SampleFreq = 8000;
    private static int frequencyOfSignal=500;
    private static int baudrate=2;
    private static int numchars=4;
    private static int numberofbits=numchars*7;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sound=(Button)findViewById(R.id.button);
        final EditText Password=(EditText)findViewById(R.id.Password);
        final TextView Result=(TextView) findViewById(R.id.result);
        findViewById(R.id.checkBox).setVisibility(CheckBox.INVISIBLE);
        Result.setVisibility(TextView.INVISIBLE);

        sound.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String passwordtext=Password.getText().toString();
                if (passwordtext.matches(""))
                {
                    Toast.makeText(MainActivity.this, "You did not enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(passwordtext.toString().toCharArray().length!=numchars) {
                    Toast.makeText(MainActivity.this, "Please enter "+numchars+" length password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    char[] words=passwordtext.toCharArray();
                    String binary="";
                    for(int i=0;i<words.length;i++)
                    {
                        int a=words[i];
                        String temp=Integer.toBinaryString(a);
                        binary+=temp;
                    }
                    char[] result=binary.toCharArray();
                    int[] arr=new int[result.length];
                    for(int i=0;i<result.length;i++)
                    {
                        arr[i]=result[i]-'0';
                    }
                    Result.setVisibility(TextView.VISIBLE);
                    Result.setText(binary);

                    Toast.makeText(MainActivity.this, binary, Toast.LENGTH_SHORT).show();
                    modulation(arr);
                }

            }
        });
    }
    public void modulation(int[] arr)
    {

        int mBufferSize = AudioTrack.getMinBufferSize(SampleFreq, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);

        AudioTrack mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SampleFreq,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                mBufferSize, AudioTrack.MODE_STREAM);

        byte[] sound=generateSineWavefreq(arr);


        mAudioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
        mAudioTrack.play();

        mAudioTrack.write(sound, 0, sound.length);
        mAudioTrack.stop();
        mAudioTrack.release();
    }

    private static byte[] generateSineWavefreq(int[] input)
    {
        byte[] data = new byte[baudrate*SampleFreq*numberofbits];

        for(int i=0,index=0;i<numberofbits;i++)
        {
            for(int j=0;j<baudrate*SampleFreq;j++)
            {
                if(input[i]==1)
                {
                    data[index++] = (byte) (input[i] * Math.sin((2.0 * Math.PI * 3*j * frequencyOfSignal) / SampleFreq) * 127);
                }
                else
                {
                    data[index++] = (byte) ( Math.sin((2.0 * Math.PI * j * frequencyOfSignal) / SampleFreq) * 127);
                }
            }
        }

        return data;
    }

}
