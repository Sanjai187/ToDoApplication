package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class SettingActivity extends AppCompatActivity {

    private boolean isFontFamily;
    private boolean isFontSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getIntent().getStringExtra(getString(R.string.settingview));
        final ImageView backButton = findViewById(R.id.backMenu);
        final Spinner fontStyle = findViewById(R.id.fontStyle);
        final Spinner fontSize = findViewById(R.id.fontSize);
        final Spinner defaultColor = findViewById(R.id.colour);

        backButton.setOnClickListener(view -> onBackPressed());
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.font_style, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontStyle.setAdapter(adapter);
        fontStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFontFamily) {
                    final int fontId = getFontFamily(position);
                    final Typeface typeface = ResourcesCompat.getFont(SettingActivity.this,
                            fontId);

                    TypeFaceUtil.setSelectedTypeFace(typeface);
                    TypeFaceUtil.applyFontToView(getWindow().getDecorView().findViewById(android.R.id.content));
                } else {
                    isFontFamily = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final ArrayAdapter<CharSequence> fontSizeAdapter = ArrayAdapter.createFromResource(
                this, R.array.font_size, android.R.layout.simple_spinner_item);

        fontSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSize.setAdapter(fontSizeAdapter);
        fontSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFontSize) {
                    final float textSize = getFontSize(position);

                    TypeFaceUtil.setSelectedFontSize(textSize);
                    TypeFaceUtil.applyTextSizeToView(getWindow().getDecorView().findViewById(android.R.id.content));
                } else {
                    isFontSize = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        final ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this,
                R.array.font_colour, android.R.layout.simple_spinner_item);

        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        defaultColor.setAdapter(colorAdapter);
        defaultColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final int selectedColor = getColorId(position);

                TypeFaceUtil.setSelectedDefaultColor(selectedColor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        applyColorToComponent();
    }

    private int getFontFamily(final int position) {
        switch (position) {
            case 0:
                return R.font.akaya_telivigala;
            case 1:
                return R.font.akronim;
            case 2:
                return R.font.alegreya;
            case 3:
                return R.font.antic_didone;
            default:
                return R.font.arbutus;
        }
    }

    private float getFontSize(final int position) {
        switch (position) {
            case 0:
                return getResources().getDimension(R.dimen.text_small);
            case 1:
                return getResources().getDimension(R.dimen.text_medium);
            case 2:
                return getResources().getDimension(R.dimen.text_large);
            default:
                return getResources().getDimension(R.dimen.text_default);
        }
    }

    private int getColorId(final int position) {
        switch (position) {
            case 0:
                return R.color.default_color;
            case 1:
                return R.color.Violet;
            case 2:
                return R.color.blue;
            default:
                return R.color.green;
        }
    }

    private void applyColorToComponent() {
        final int defaultColor = TypeFaceUtil.getSelectedDefaultColor();
        final LinearLayout layout = findViewById(R.id.settingsView);

        if (defaultColor == R.color.green) {
            layout.setBackgroundColor(getResources().getColor(R.color.green));
        } else if (defaultColor == R.color.blue) {
            layout.setBackgroundColor(getResources().getColor(R.color.blue));
        } else if (defaultColor == R.color.Violet) {
            layout.setBackgroundColor(getResources().getColor(R.color.Violet));
        }
    }
}