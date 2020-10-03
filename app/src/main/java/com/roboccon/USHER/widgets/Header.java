package com.roboccon.USHER.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.roboccon.USHER.R;

import org.w3c.dom.Text;

public class Header extends RelativeLayout {

    Context context;
    TextView pageTitle;
    TextView secondaryButton;
    TextView backTextview;
    String pageTitleStr;
    LinearLayout llSecondary;
    RelativeLayout backButtonContainer;
    LinearLayout tabsLayout;

    public static RelativeLayout tab1, tab2;


    public Header(Context context) {
        super(context);

        this.context = context;

        inflate(context);
    }

    public Header(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        inflate(context);
    }

    private void inflate(final Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_header, this);

        pageTitle = findViewById(R.id.page_title);
        backButtonContainer = findViewById(R.id.back_button_container);

    }

    public void setPageTitle(String pageTitleStr) {
        this.pageTitleStr = pageTitleStr;
        pageTitle.setText(pageTitleStr);

    }

}
