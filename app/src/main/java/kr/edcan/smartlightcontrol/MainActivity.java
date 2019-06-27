package kr.edcan.smartlightcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.listeners.ColorListener;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    private BluetoothSPP bt;

    LinearLayout colorView, connectBtn, colorBtn;

    SeekBar sk_r, sk_g, sk_b;
    TextView colorTxt, deviceTxt, titleTxt;

    Button deviceBtn;

    private int r = 255, g = 255, b = 255;

    private String colorString="R255G255B255";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        titleTxt = findViewById(R.id.title_txt);
//        Shader textShader = new LinearGradient(64, 0, 0, titleTxt.getPaint().getTextSize(),
//                new int[]{getResources().getColor(R.color.gradient1), getResources().getColor(R.color.gradient2)},
//                new float[]{0, 1}, Shader.TileMode.CLAMP);
//        titleTxt.getPaint().setShader(textShader);

        deviceTxt = findViewById(R.id.device_txt);
        deviceBtn = findViewById(R.id.device_dis_btn);
        deviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.disconnect();
            }
        });

        colorTxt = findViewById(R.id.color_txt);
        colorView = findViewById(R.id.colorView);

        sk_r = findViewById(R.id.seek_r);
        sk_g = findViewById(R.id.seek_g);
        sk_b = findViewById(R.id.seek_b);
        sk_r.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean bl) {
                r = i;
                setColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sk_g.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean bl) {
                g = i;
                setColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sk_b.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean bl) {
                b = i;
                setColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        bt = new BluetoothSPP(this);
        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "블루투스 사용이 불가능합니다."
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                deviceTxt.setText(name);
                deviceBtn.setVisibility(View.VISIBLE);
//                Toast.makeText(getApplicationContext()
//                        , "Connected to " + name + "\n" + address
//                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "연결이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                deviceTxt.setText("연결된 디바이스가 없습니다.");
                deviceBtn.setVisibility(View.GONE);
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        connectBtn = findViewById(R.id.connectBtn); //connect
        connectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            }
        });

        colorBtn = findViewById(R.id.colorBtn);
        colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Dialog);
                builder.setTitle("색상을 선택하세요");
                builder.setPositiveButton("확인", new ColorEnvelopeListener() {
                    @Override
                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                        r = Color.red(envelope.getColor());
                        g = Color.green(envelope.getColor());
                        b = Color.blue(envelope.getColor());
                        setColor();
                        setSeek();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    void setColor() {
        colorView.setBackgroundColor(Color.rgb(r, g, b));
        colorTxt.setText("R: " + r + "  G: " + g + "  B: " + b);
        colorString = "R" + r + "G" + g + "B" + b;

    }
    void setSeek(){
        sk_r.setProgress(r);
        sk_g.setProgress(g);
        sk_b.setProgress(b);
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }


    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
                setup();
            }
        }
    }

    public void setup() {
        Button btnSend = findViewById(R.id.send); //데이터 전송
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send(colorString, true);
                Log.d("디버그",colorString);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "블루투스가 활성화되지 않았습니다."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
