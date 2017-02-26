package com.samourai.wallet;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by guillermo on 1/14/17.
 */

public class CreateWalletActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_passphrase);
    }
}
