package com.loopeer.android.photodrama4android.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityTextInputBinding;

public class TextInputActivity extends PhotoDramaBaseActivity {

    private ActivityTextInputBinding mBinding;
    private String mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_text_input);
        mContent = getIntent().getStringExtra(Navigator.EXTRA_TEXT);
        mBinding.textInput.setText(mContent);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.menu_done) {
            if (TextUtils.isEmpty(mBinding.textInput.getText().toString())) return true;
            Intent intent = new Intent();
            mContent = mBinding.textInput.getText().toString();
            intent.putExtra(Navigator.EXTRA_TEXT, mContent);
            setResult(RESULT_OK, intent);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
