package group5.cs3750.trainingwheels;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;


public class JavaScriptDemo extends Activity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_java_script_demo);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    final WebView webView = (WebView) findViewById(R.id.webView);
    final EditText editText = (EditText) findViewById(R.id.editText);
    Button button = (Button) findViewById(R.id.button);

    webView.getSettings().setJavaScriptEnabled(true);

    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String websiteCode = String.format("<!DOCTYPE html>\n" +
            "<html>\n" +
            "<body>\n" +
            "\n" +
            "<h1>JavaScript in Body</h1>\n" +
            "\n" +
            "<p id=\"demo\">A Paragraph.</p>\n" +
            "\n" +
            "<button type=\"button\" onclick=\"myFunction()\">Try it</button>\n" +
            "\n" +
            "<script>\n" +
            "function myFunction() {\n" +
            "    document.getElementById(\"demo\").innerHTML = %s;\n" +
            "}\n" +
            "</script>\n" +
            "\n" +
            "</body>\n" +
            "</html> ", editText.getText().toString());
        webView.loadData(websiteCode, "text/html", null);
      }
    });
  }
}
