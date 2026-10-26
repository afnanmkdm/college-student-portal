package com.college.studentportal;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private final int navy = Color.rgb(17, 35, 77);
    private final int blue = Color.rgb(45, 105, 220);
    private final int ink = Color.rgb(29, 42, 66);
    private final int muted = Color.rgb(112, 125, 148);
    private final int page = Color.rgb(246, 248, 252);

    private LinearLayout root;
    private LinearLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showDashboard();
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    private TextView label(String value, int size, int color,
                           boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(null, Typeface.BOLD);
        return t;
    }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    private void showDashboard() {
        root = column();
        root.setBackgroundColor(page);

        ScrollView scroll = new ScrollView(this);
        content = column();
        content.setPadding(20, 22, 20, 24);

        LinearLayout header = column();
        header.setPadding(22, 24, 22, 25);
        header.setBackground(shape(navy, 24));

        TextView brand = label("CAMPUS CONNECT", 12,
                Color.rgb(183, 207, 255), true);
        header.addView(brand);

        TextView title = label("Your college,\none place.", 29,
                Color.WHITE, true);
        title.setPadding(0, 16, 0, 8);
        header.addView(title);

        TextView sub = label(
                "Student services and certificate verification",
                13, Color.rgb(220, 229, 248), false);
        header.addView(sub);

        content.addView(header);

        TextView section = label("Quick access", 21, ink, true);
        section.setPadding(0, 25, 0, 14);
        content.addView(section);

        addFeature("01", "My Profile",
                "Student details and account", "profile");
        addFeature("02", "Certificates",
                "View and verify certificates", "certificates");
        addFeature("03", "QR Verification",
                "Certificate verification tools", "verify");
        addFeature("04", "Notices & Updates",
                "College announcements", "notices");
        addFeature("05", "Study Materials",
                "Notes and learning resources", "materials");
        addFeature("06", "Results & Attendance",
                "Academic information", "academics");

        TextView footer = label(
                "COLLEGE STUDENT PORTAL  •  VERSION 1.0",
                11, muted, true);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, 22, 0, 8);
        content.addView(footer);

        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1));

        LinearLayout nav = new LinearLayout(this);
        nav.setGravity(Gravity.CENTER);
        nav.setPadding(8, 10, 8, 10);
        nav.setBackgroundColor(Color.WHITE);

        addNav(nav, "Home", "home");
        addNav(nav, "Certificates", "certificates");
        addNav(nav, "Profile", "profile");

        root.addView(nav, new LinearLayout.LayoutParams(-1, -2));
        setContentView(root);
    }

    private void addFeature(String number, String title,
                            String description, String pageName) {
        LinearLayout card = column();
        card.setPadding(17, 16, 17, 16);
        card.setBackground(shape(Color.WHITE, 18));

        LinearLayout.LayoutParams cp =
                new LinearLayout.LayoutParams(-1, -2);
        cp.setMargins(0, 0, 0, 12);

        TextView badge = label(number, 12, blue, true);
        card.addView(badge);

        TextView heading = label(title, 17, ink, true);
        heading.setPadding(0, 8, 0, 4);
        card.addView(heading);

        TextView desc = label(description, 13, muted, false);
        card.addView(desc);

        card.setOnClickListener(v -> openSection(pageName));
        content.addView(card, cp);
    }

    private void addNav(LinearLayout nav, String title,
                        String destination) {
        Button b = new Button(this);
        b.setText(title);
        b.setAllCaps(false);
        b.setTextColor(blue);
        b.setTextSize(12);
        b.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(Color.WHITE));
        b.setOnClickListener(v -> openSection(destination));

        nav.addView(b, new LinearLayout.LayoutParams(0, -2, 1));
    }

    private void openSection(String destination) {
        if (destination.equals("home")) {
            showDashboard();
            return;
        }

        root = column();
        root.setBackgroundColor(page);
        root.setPadding(22, 28, 22, 22);

        Button back = new Button(this);
        back.setText("←  Back to Home");
        back.setAllCaps(false);
        back.setOnClickListener(v -> showDashboard());
        root.addView(back);

        String title;
        String description;

        switch (destination) {
            case "profile":
                title = "My Profile";
                description =
                        "Your student details and linked student ID " +
                        "will appear here after account setup.";
                break;
            case "certificates":
                title = "Certificates";
                description =
                        "Your verified certificate records will " +
                        "appear here when the student database is connected.";
                break;
            case "verify":
                title = "QR Verification";
                description =
                        "The QR scanner and certificate validation " +
                        "will be added with the verification service.";
                break;
            case "notices":
                title = "Notices & Updates";
                description =
                        "College notices and announcements will " +
                        "appear here once connected to the content service.";
                break;
            case "materials":
                title = "Study Materials";
                description =
                        "Learning resources and downloadable files " +
                        "will be available here.";
                break;
            default:
                title = "Results & Attendance";
                description =
                        "Academic records will appear here when " +
                        "the authorized student data source is connected.";
                break;
        }

        TextView heading = label(title, 27, navy, true);
        heading.setPadding(0, 30, 0, 15);
        root.addView(heading);

        TextView info = label(description, 16, ink, false);
        info.setLineSpacing(7, 1);
        root.addView(info);

        setContentView(root);
    }
}
