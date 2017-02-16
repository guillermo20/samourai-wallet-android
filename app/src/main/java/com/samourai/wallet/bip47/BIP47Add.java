package com.samourai.wallet.bip47;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.samourai.wallet.SendActivity;
import com.samourai.wallet.access.AccessFactory;
import com.samourai.wallet.crypto.DecryptionException;
import com.samourai.wallet.payload.PayloadUtil;
import com.samourai.wallet.util.CharSequenceX;
import com.samourai.wallet.util.FormatsUtil;
import com.samourai.wallet.R;
import com.samourai.wallet.util.WebUtil;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bitcoinj.crypto.MnemonicException;
import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.util.encoders.DecoderException;

import java.io.IOException;

public class BIP47Add extends Activity {

    private EditText edLabel = null;
    private EditText edPCode = null;

    private TextWatcher twPCode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bip47_add);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        edLabel = (EditText)findViewById(R.id.label);
        edPCode = (EditText)findViewById(R.id.pcode);

        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.containsKey("pcode"))	{
            edPCode.setText(extras.getString("pcode"));
        }
        if(extras != null && extras.containsKey("label"))	{
            edLabel.setText(extras.getString("label"));
        }

        twPCode = new TextWatcher() {

            public void afterTextChanged(Editable s) {

                edPCode.removeTextChangedListener(this);

                final String userInput = edPCode.getText().toString();

                if(FormatsUtil.getInstance().isValidPaymentCode(userInput))    {
                    new PaymentCodeIOTask().execute(new String[] { userInput });
                }

                edPCode.addTextChangedListener(twPCode);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                ;
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ;
            }
        };

        edPCode.addTextChangedListener(twPCode);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bip47_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
        }
        else if(id == R.id.action_add) {

            View view = BIP47Add.this.getCurrentFocus();
            if(view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            String label = edLabel.getText().toString();
            final String pcode = edPCode.getText().toString();

            if(pcode == null || pcode.length() < 1 || !FormatsUtil.getInstance().isValidPaymentCode(pcode))    {
                Toast.makeText(BIP47Add.this, R.string.invalid_payment_code, Toast.LENGTH_SHORT).show();
            }
            else if(label == null || label.length() < 1)    {
                Toast.makeText(BIP47Add.this, R.string.bip47_no_label_error, Toast.LENGTH_SHORT).show();
            }
            else    {
                BIP47Meta.getInstance().setLabel(pcode, label);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();

                        try {
                            PayloadUtil.getInstance(BIP47Add.this).saveWalletToJSON(new CharSequenceX(AccessFactory.getInstance(BIP47Add.this).getGUID() + AccessFactory.getInstance().getPIN()));
                        }
                        catch(MnemonicException.MnemonicLengthException mle) {
                            mle.printStackTrace();
                            Toast.makeText(BIP47Add.this, R.string.decryption_error, Toast.LENGTH_SHORT).show();
                        }
                        catch(DecoderException de) {
                            de.printStackTrace();
                            Toast.makeText(BIP47Add.this, R.string.decryption_error, Toast.LENGTH_SHORT).show();
                        }
                        catch(JSONException je) {
                            je.printStackTrace();
                            Toast.makeText(BIP47Add.this, R.string.decryption_error, Toast.LENGTH_SHORT).show();
                        }
                        catch(IOException ioe) {
                            ioe.printStackTrace();
                            Toast.makeText(BIP47Add.this, R.string.decryption_error, Toast.LENGTH_SHORT).show();
                        }
                        catch(java.lang.NullPointerException npe) {
                            npe.printStackTrace();
                            Toast.makeText(BIP47Add.this, R.string.decryption_error, Toast.LENGTH_SHORT).show();
                        }
                        catch(DecryptionException de) {
                            de.printStackTrace();
                            Toast.makeText(BIP47Add.this, R.string.decryption_error, Toast.LENGTH_SHORT).show();
                        }
                        finally {
                            ;
                        }

                        Looper.loop();

                    }
                }).start();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("pcode", pcode);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();

            }

        }
        else {
            ;
        }

        return super.onOptionsItemSelected(item);
    }

    private class PaymentCodeIOTask extends AsyncTask<String, Void, String> {

        private ProgressDialog progress = null;
        private String title = null;

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(BIP47Add.this);
            progress.setCancelable(false);
            progress.setTitle(R.string.app_name);
            progress.setMessage(getString(R.string.please_wait));
            progress.show();
        }

        @Override
        protected String doInBackground(String... s) {

            String result = null;
            String url = WebUtil.PAYMENTCODE_IO_SEARCH + s[0];
            try {
                result = WebUtil.getInstance(BIP47Add.this).getURL(url);

                JSONObject obj = new JSONObject(result);
                if(obj.has("title"))    {
                    title = StringEscapeUtils.unescapeHtml4(obj.getString("title"));
                }

            }
            catch(Exception e) {
                ;
            }

            return "OK";
        }

        @Override
        protected void onPostExecute(String result) {

            if(progress != null && progress.isShowing())    {
                progress.dismiss();
            }

            if(title != null)    {
                edLabel.setText(title);
            }

        }

    }

}
