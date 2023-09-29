package com.example.todo.service.impl;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.todo.R;
import com.example.todo.api.impl.AuthenticationService;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingActivity extends AppCompatActivity {

    private boolean isFontFamily;
    private boolean isFontSize;
    private boolean isDefaultColor;
    private String selectedFontFamily;
    private String selectedColor;
    private String selectedFontSize;
    private String token;
    private Spinner fontSize;
    private Spinner fontStyle;
    private Spinner defaultColor;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        final ImageView backButton = findViewById(R.id.backMenu);
        fontStyle = findViewById(R.id.fontStyle);
        fontSize = findViewById(R.id.fontSize);
        defaultColor = findViewById(R.id.colour);
        token = getIntent().getStringExtra(getString(R.string.token));

        getSystemSettings();
        backButton.setOnClickListener(view -> onBackPressed());
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.font_style, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontStyle.setAdapter(adapter);
        fontStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String fontName = parent.getItemAtPosition(position).toString();

                if (isFontFamily && ! fontName.equals(selectedFontFamily)) {
                    selectedFontFamily = fontName;
                    final int fontId = getFontFamily(fontName);
                    final Typeface typeface = ResourcesCompat.getFont(SettingActivity.this,
                            fontId);

                    TypeFaceUtil.setSelectedTypeface(typeface);
                    TypeFaceUtil.applyTypefaceToView(getWindow().getDecorView().findViewById(android.R.id.content));
                    updateFontFamily(fontName);
                } else {
                    isFontFamily = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        final ArrayAdapter<CharSequence> fontSizeAdapter = ArrayAdapter.createFromResource(
                this, R.array.font_size, android.R.layout.simple_spinner_item);

        fontSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSize.setAdapter(fontSizeAdapter);
        fontSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String fontSize = parent.getItemAtPosition(position).toString();

                if (isFontSize && ! fontSize.equals(selectedFontSize)) {
                    selectedFontSize = fontSize;
                    final float textSize = getFontSize(fontSize);

                    TypeFaceUtil.setSelectedTextSize(textSize);
                    TypeFaceUtil.applyTextSizeToView(getWindow().getDecorView().findViewById(android.R.id.content));
                    updateFontSize((int) getFontSize(parent.getSelectedItem().toString()));
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
                final String color = parent.getItemAtPosition(position).toString();

                if (isDefaultColor || color.equals(selectedColor)) {
                    selectedColor = color;
                    final int selectedColor = getColorId(color);

                    TypeFaceUtil.setSelectedDefaultColor(selectedColor);
                    applyColorToComponents(selectedColor);
                    updateColor(color);
                } else {
                    isDefaultColor = true;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateFontFamily(final String fontFamily) {
        final AuthenticationService authenticationService = new AuthenticationService(
                getString(R.string.base_url), token);

        authenticationService.updateFontFamily(fontFamily,
                new AuthenticationService.ApiResponseCallBack() {
                    @Override
                    public void onSuccess(final String responseBody) {
                        showSnackBar(getString(R.string.update_successfully));
                    }

                    @Override
                    public void onError(final String errorMessage) {
                        showSnackBar(errorMessage);
                    }
                });
    }

    private void updateFontSize(final int fontSize) {
        final AuthenticationService authenticationService = new AuthenticationService(
                getString(R.string.base_url), token);

        authenticationService.updateFontSize(fontSize, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {
                showSnackBar(getString(R.string.update_successfully));
            }

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void updateColor(final String color) {
        final AuthenticationService authenticationService = new AuthenticationService(
                getString(R.string.base_url), token);

        authenticationService.updateColor(color, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {
                showSnackBar(getString(R.string.update_successfully));
            }

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void getSystemSettings() {
        final AuthenticationService authenticationService = new AuthenticationService(
                getString(R.string.base_url), token);

        authenticationService.getSystemSetting(new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(String responseBody) {
                handleSystemSettings(responseBody);
            }

            @Override
            public void onError(String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void handleSystemSettings(final String responseBody) {
        try {
            final JSONObject jsonObject = new JSONObject(responseBody);

            if (! jsonObject.isNull(getString(R.string.data))) {
                final JSONObject data = jsonObject.getJSONObject(getString(R.string.data));
                final String fontName = data.getString(getString(R.string.font_name));
                final String fontSize = data.getString(getString(R.string.size));
                final String color = data.getString(getString(R.string.font_color));
                selectedFontFamily = fontName;
                selectedFontSize = String.valueOf(getFontSize(fontSize));
                selectedColor = color;

                updateFontFamilySpinner(fontName);
                updateFontSizeSpinner(selectedFontSize);
                updateColorSpinner(color);
                applySystemSettings(fontName, fontSize, color);
            }
        } catch (JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void updateColorSpinner(final String color) {
        final int position = getColorPosition(color);

        if (0 <= position) {
            defaultColor.setSelection(position);
        }
    }

    private void updateFontSizeSpinner(final String fontSizes) {
        final int position = getFontSizePosition(fontSizes);

        if (0 <= position) {
            fontSize.setSelection(position);
        }
    }

    private void updateFontFamilySpinner(final String fontName) {
        final int position = getFontFamilyPosition(fontName);

        if (0 <= position) {
            fontStyle.setSelection(position);
        }
    }

    private void applySystemSettings(final String fontName, final String fontSize, final String color) {
        final int fontId = getFontFamily(fontName);
        final Typeface typeface = ResourcesCompat.getFont(SettingActivity.this, fontId);
        final String sizeName = String.valueOf(getFontSize(fontSize));

        TypeFaceUtil.setSelectedTypeface(typeface);
        TypeFaceUtil.setSelectedTextSize(getFontSize(sizeName));
        TypeFaceUtil.setSelectedDefaultColor(getColorId(color));
    }

    private int getFontFamilyPosition(final String fontName) {
        final ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) fontStyle.getAdapter();

        return adapter.getPosition(fontName);
    }

    private int getFontSizePosition(final String fontSizes) {
        final ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) fontSize.getAdapter();

        return adapter.getPosition(fontSizes);
    }

    private int getColorPosition(final String color) {
        final ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) defaultColor.getAdapter();

        return adapter.getPosition(color);
    }

    private int getFontFamily(final String fontName) {
        switch (fontName) {
            case "akaya telivigala":
                return R.font.akaya_telivigala;
            case "akronim":
                return R.font.akronim;
            case "alegreya":
                return R.font.alegreya;
            case "antic didone":
                return R.font.antic_didone;
            default:
                return R.font.arbutus;
        }
    }

    private float getFontSize(final String sizeName) {
        switch (sizeName) {
            case "Small":
                return getResources().getDimension(R.dimen.text_small);
            case "Medium":
                return getResources().getDimension(R.dimen.text_medium);
            case "Large":
                return getResources().getDimension(R.dimen.text_large);
            default:
                return getResources().getDimension(R.dimen.text_default);
        }
    }

    private int getColorId(final String color) {
        switch (color) {
            case "default color":
                return R.color.default_color;
            case "violet":
                return R.color.Violet;
            case "blue":
                return R.color.blue;
            default:
                return R.color.green;
        }
    }

    public void applyColorToComponents(final int colorId) {
        final int selectedColor = ContextCompat.getColor(this, colorId);
        final LinearLayout relativeLayout = findViewById(R.id.settingsView);

        relativeLayout.setBackgroundColor(selectedColor);
    }

    private void showSnackBar(final String message) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);

        snackbar.show();
    }
}