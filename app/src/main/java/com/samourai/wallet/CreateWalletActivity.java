package com.samourai.wallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by guillermo on 1/14/17.
 */

public class CreateWalletActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this;
        getActionBar().hide();
        setContentView(R.layout.activity_create_wallet);
        TextView newWallet=(TextView) findViewById(R.id.new_wallet_button);
        newWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InitPassphraseActivity.class);
                context.startActivity(intent);
            }
        });
    }

    static public class InitPassphraseActivity extends Activity{

        EditText passphrase;
        TextView nextButton;

        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            getActionBar().hide();
            setContentView(R.layout.activity_passphrase);
            passphrase = (EditText) findViewById(R.id.passphrase);
            nextButton = (TextView) findViewById(R.id.next_button);
            final Context context = this;
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String passphrase39 = passphrase.getText().toString();

                    if (passphrase39 != null && passphrase39.length() > 0) {

                        Intent intent = new Intent(context, PinEntryActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("create", true);
                        intent.putExtra("passphrase", passphrase39 == null ? "" : passphrase39);
                        startActivity(intent);

                    } else {
                        Toast.makeText(context, R.string.bip39_must, Toast.LENGTH_SHORT).show();
                        //AppUtil.getInstance(this).restartApp();
                    }
                }
            });
        }
    }


    static public class MnemonicWordsActivity extends Activity{

        private LinearLayout hView;

        private String seed;

        private String vSeed[];

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getActionBar().hide();
            setContentView(R.layout.activity_seedwords);
            seed = getIntent().getStringExtra("seed");
            hView = (LinearLayout) findViewById(R.id.mnemotic_list_view);
            vSeed  = seed.split(" ");
            String vNumWord="word";
            for (int i = 0;i<vSeed.length;i++){
                final String auxSeed = vSeed[i];
                String word = vNumWord + "" +i;
                final TextView textView = new TextView(this);
                textView.setText(word);
                textView.setBackgroundColor(getResources().getColor(R.color.grey));
                textView.setGravity(Gravity.CENTER);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                LinearLayout.LayoutParams lastTxtParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lastTxtParams.setMargins((int)getResources().getDimension(R.dimen.text_horizontal_margin), 0, (int)getResources().getDimension(R.dimen.text_horizontal_margin), 0);

                lastTxtParams.height = 160;
                lastTxtParams.width = 360;
                textView.setLayoutParams(lastTxtParams);

                textView.setOnLongClickListener(new View.OnLongClickListener() {
                    String seed=auxSeed;

                    @Override
                    public boolean onLongClick(View v) {
                        textView.setBackgroundColor(getResources().getColor(R.color.red));
                        textView.setText(seed);
                        return true;
                    }
                });

                hView.addView(textView);
            }
        }
    }

}
