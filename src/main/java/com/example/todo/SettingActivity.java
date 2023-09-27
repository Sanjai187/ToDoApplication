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

import com.example.todo.api.AuthenticationService;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingActivity extends AppCompatActivity {

    private boolean isFontFamily;
    private boolean isFontSize;
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
                if (isFontFamily) {
                    final int fontId = getFontFamily(parent.getItemAtPosition(position).toString());
                    final Typeface typeface = ResourcesCompat.getFont(SettingActivity.this,
                            fontId);

                    TypeFaceUtil.setSelectedTypeFace(typeface);
                    TypeFaceUtil.applyFontToView(getWindow().getDecorView().findViewById(android.R.id.content));
                    updateSystemSettings();
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
                    final float textSize = getFontSize(parent.getItemAtPosition(position).toString());

                    TypeFaceUtil.setSelectedFontSize(textSize);
                    TypeFaceUtil.applyTextSizeToView(getWindow().getDecorView().findViewById(android.R.id.content));
                    updateSystemSettings();
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
                final int selectedColor = getColorId(parent.getItemAtPosition(position).toString());

                TypeFaceUtil.setSelectedDefaultColor(selectedColor);
                updateSystemSettings();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        applyColorToComponent();
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
            final JSONObject responseJson = new JSONObject(responseBody);

            if (! responseJson.isNull(getString(R.string.data))) {
                final JSONObject data = responseJson.getJSONObject(getString(R.string.data));
                final String fontName = data.getString(getString(R.string.font_name));
                final String fontSize = data.getString(getString(R.string.size));
                final String color = data.getString(getString(R.string.font_color));

                applySystemSettings(fontName, fontSize, color);
            }
        } catch (JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void applySystemSettings(final String fontName, final String fontSize, final String color) {
        final int fontId = getFontFamily(fontName);
        final Typeface typeface = ResourcesCompat.getFont(SettingActivity.this, fontId);
        final String sizeName = String.valueOf(getFontSize(fontSize));

        TypeFaceUtil.setSelectedTypeFace(typeface);
        TypeFaceUtil.setSelectedFontSize(getFontSize(sizeName));
        TypeFaceUtil.setSelectedDefaultColor(getColorId(color));
    }

    private void updateSystemSettings() {
        final String selectedFontFamily = fontStyle.getSelectedItem().toString();
        final String selectedColor = defaultColor.getSelectedItem().toString();
        final int selectedFontSize = (int) getFontSize(fontSize.getSelectedItem().toString());

        sendSettings(selectedFontFamily, selectedColor, selectedFontSize);
    }

    private void sendSettings(final String selectedFontFamily, final String selectedColor,
                              final int fontSize) {
        final AuthenticationService authenticationService = new AuthenticationService(
                getString(R.string.base_url), token);

        authenticationService.updateSystemSetting(selectedFontFamily, fontSize,
                selectedColor, new AuthenticationService.ApiResponseCallBack() {
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

    private void showSnackBar(final String message) {
        final View parentLayout = findViewById(android.R.id.content);
        final Snackbar snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT);

        snackbar.show();
    }
}