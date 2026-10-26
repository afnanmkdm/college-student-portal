package com.college.studentportal;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;

public class MainActivity extends Activity {

    private final int navy = Color.rgb(18, 35, 75);
    private final int blue = Color.rgb(48, 105, 220);
    private final int page = Color.rgb(245, 247, 252);
    private final int ink = Color.rgb(30, 42, 65);
    private final int muted = Color.rgb(110, 124, 147);

    private SharedPreferences prefs;
    private String email = "";
    private LinearLayout root;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        prefs = getSharedPreferences("CampusData", MODE_PRIVATE);
        email = prefs.getString("session", "");

        if (email.isEmpty()) {
            showLogin();
        } else {
            showHome();
        }
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    private TextView text(String value, int size,
                          int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);

        if (bold) {
            t.setTypeface(null, Typeface.BOLD);
        }

        return t;
    }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    private EditText field(String hint, int type) {
        EditText e = new EditText(this);
        e.setSingleLine(true);
        e.setHint(hint);
        e.setTextSize(15);
        e.setInputType(type);
        e.setPadding(16, 12, 16, 12);
        e.setBackground(shape(Color.WHITE, 14));
        return e;
    }

    private void gap(LinearLayout box, int height) {
        View v = new View(this);
        box.addView(v,
                new LinearLayout.LayoutParams(1, height));
    }

    private void button(LinearLayout box, String title,
                        Runnable action) {
        Button b = new Button(this);
        b.setText(title);
        b.setAllCaps(false);
        b.setTextSize(14);
        b.setTextColor(Color.WHITE);
        b.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(blue));
        b.setOnClickListener(v -> action.run());

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 6, 0, 6);
        box.addView(b, p);
    }

    private void base(String title, String subtitle) {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(page);

        root = column();
        root.setPadding(22, 28, 22, 24);

        root.addView(text("CAMPUS CONNECT", 12, blue, true));

        TextView heading = text(title, 28, navy, true);
        heading.setPadding(0, 18, 0, 8);
        root.addView(heading);

        TextView sub = text(subtitle, 14, muted, false);
        sub.setPadding(0, 0, 0, 20);
        root.addView(sub);

        scroll.addView(root);
        setContentView(scroll);
    }

    private String hash(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(value.getBytes("UTF-8"));
            StringBuilder out = new StringBuilder();

            for (byte b : bytes) {
                out.append(String.format("%02x", b & 255));
            }

            return out.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    private void msg(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLogin() {
        base("Welcome back", "Sign in to your student portal");

        EditText user = field("Email address",
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        EditText pass = field("Password",
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);

        root.addView(user);
        gap(root, 12);
        root.addView(pass);
        gap(root, 12);

        button(root, "Sign In", () -> {
            String e = user.getText().toString()
                    .trim().toLowerCase();
            String p = pass.getText().toString();

            if (e.isEmpty() || p.isEmpty()) {
                msg("Enter email and password.");
                return;
            }

            try {
                JSONObject a = new JSONObject(
                        prefs.getString("account_" + e, ""));

                if (a.getString("password").equals(hash(p))) {
                    email = e;
                    prefs.edit().putString("session", email).apply();
                    showHome();
                } else {
                    msg("Incorrect email or password.");
                }
            } catch (Exception ex) {
                msg("Account not found. Register first.");
            }
        });

        button(root, "Create Student Account", this::showRegister);

        TextView note = text(
                "Google sign-in and SMS OTP require authentication setup.",
                12, muted, false);
        note.setPadding(0, 15, 0, 0);
        root.addView(note);
    }

    private void showRegister() {
        base("Create account", "Register for the student portal");

        EditText name = field("Full name",
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        EditText user = field("Email address",
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        EditText pass = field("Password (8 characters minimum)",
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);

        root.addView(name);
        gap(root, 10);
        root.addView(user);
        gap(root, 10);
        root.addView(pass);
        gap(root, 14);

        button(root, "Create Account", () -> {
            String n = name.getText().toString().trim();
            String e = user.getText().toString()
                    .trim().toLowerCase();
            String p = pass.getText().toString();

            if (n.isEmpty() || !e.contains("@") || p.length() < 8) {
                msg("Enter a valid name, email and password.");
                return;
            }

            if (!prefs.getString("account_" + e, "").isEmpty()) {
                msg("This email already has an account.");
                return;
            }

            try {
                JSONObject a = new JSONObject();
                a.put("name", n);
                a.put("password", hash(p));
                a.put("studentId", "");
                a.put("phone", "");
                a.put("course", "");
                a.put("year", "");

                prefs.edit()
                        .putString("account_" + e, a.toString())
                        .putString("session", e)
                        .apply();

                email = e;
                showProfile();
                msg("Account created on this device.");
            } catch (Exception ex) {
                msg("Could not create account.");
            }
        });

        button(root, "Back to Login", this::showLogin);
    }

    private JSONObject account() {
        try {
            return new JSONObject(
                    prefs.getString("account_" + email, "{}"));
        } catch (Exception ex) {
            return new JSONObject();
        }
    }

    private void saveAccount(JSONObject a) {
        prefs.edit()
                .putString("account_" + email, a.toString())
                .apply();
    }

    private void showHome() {
        JSONObject a = account();

        base("Hello, " + a.optString("name", "Student"),
                "Your college services in one place");

        LinearLayout banner = column();
        banner.setPadding(20, 22, 20, 22);
        banner.setBackground(shape(navy, 22));

        banner.addView(text("STUDENT PORTAL", 12,
                Color.rgb(180, 205, 255), true));
        gap(banner, 10);
        banner.addView(text("Learn. Manage. Verify.",
                23, Color.WHITE, true));
        gap(banner, 6);
        banner.addView(text("Your student services dashboard",
                13, Color.WHITE, false));

        root.addView(banner);
        gap(root, 20);
        root.addView(text("QUICK ACCESS", 13, muted, true));
        gap(root, 8);

        feature("My Profile", "Personal details and student ID",
                this::showProfile);
        feature("Student Records", "Manage student entries",
                () -> showRecords("student"));
        feature("Certificates", "Create and manage certificate records",
                () -> showRecords("cert"));
        feature("QR Verification", "Look up a certificate code",
                this::verifyCertificate);
        feature("Notices & Updates", "Create and read notices",
                () -> showRecords("notice"));
        feature("Study Materials", "Manage learning resources",
                () -> showRecords("material"));
        feature("Results & Attendance", "Manage academic entries",
                () -> showRecords("academic"));

        gap(root, 12);

        button(root, "Sign Out", () -> {
            prefs.edit().remove("session").apply();
            email = "";
            showLogin();
        });

        root.addView(text(
                "Local prototype: data is stored on this device.",
                12, muted, false));
    }

    private void feature(String title, String description,
                         Runnable action) {
        LinearLayout card = column();
        card.setPadding(17, 15, 17, 15);
        card.setBackground(shape(Color.WHITE, 16));

        card.addView(text(title, 17, ink, true));

        TextView d = text(description, 13, muted, false);
        d.setPadding(0, 5, 0, 0);
        card.addView(d);

        card.setOnClickListener(v -> action.run());

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 0, 0, 10);
        root.addView(card, p);
    }

    private void showProfile() {
        JSONObject a = account();

        base("My Profile", "Edit and save your student details");

        EditText name = field("Full name", InputType.TYPE_CLASS_TEXT);
        EditText phone = field("Phone number",
                InputType.TYPE_CLASS_PHONE);
        EditText id = field("Student ID", InputType.TYPE_CLASS_TEXT);
        EditText course = field("Course / Class",
                InputType.TYPE_CLASS_TEXT);
        EditText year = field("Academic year",
                InputType.TYPE_CLASS_TEXT);

        name.setText(a.optString("name", ""));
        phone.setText(a.optString("phone", ""));
        id.setText(a.optString("studentId", ""));
        course.setText(a.optString("course", ""));
        year.setText(a.optString("year", ""));

        root.addView(name);
        gap(root, 8);
        root.addView(phone);
        gap(root, 8);
        root.addView(id);
        gap(root, 8);
        root.addView(course);
        gap(root, 8);
        root.addView(year);
        gap(root, 12);

        button(root, "Save Profile", () -> {
            try {
                a.put("name", name.getText().toString().trim());
                a.put("phone", phone.getText().toString().trim());
                a.put("studentId", id.getText().toString().trim());
                a.put("course", course.getText().toString().trim());
                a.put("year", year.getText().toString().trim());

                saveAccount(a);
                msg("Profile saved.");
            } catch (Exception ex) {
                msg("Could not save profile.");
            }
        });

        button(root, "Back to Home", this::showHome);
    }

    private String dataKey(String type) {
        return "data_" + email + "_" + type;
    }

    private JSONArray records(String type) {
        try {
            return new JSONArray(
                    prefs.getString(dataKey(type), "[]"));
        } catch (Exception ex) {
            return new JSONArray();
        }
    }

    private void saveRecords(String type, JSONArray list) {
        prefs.edit()
                .putString(dataKey(type), list.toString())
                .apply();
    }

    private String typeTitle(String type) {
        switch (type) {
            case "student": return "Student Records";
            case "cert": return "Certificates";
            case "notice": return "Notices & Updates";
            case "material": return "Study Materials";
            default: return "Results & Attendance";
        }
    }

    private void showRecords(String type) {
        base(typeTitle(type), "Records saved on this device");

        button(root, "＋ Add New Record", () -> addRecord(type));

        JSONArray list = records(type);

        if (list.length() == 0) {
            TextView empty = text(
                    "No records yet. Tap Add New Record.",
                    14, muted, false);
            empty.setPadding(0, 20, 0, 20);
            root.addView(empty);
        }

        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.optJSONObject(i);
            if (item == null) continue;

            final int index = i;

            LinearLayout card = column();
            card.setPadding(16, 14, 16, 14);
            card.setBackground(shape(Color.WHITE, 15));

            card.addView(text(item.optString("title", "Record"),
                    17, ink, true));

            TextView detail = text(item.optString("detail", ""),
                    13, muted, false);
            detail.setPadding(0, 5, 0, 8);
            card.addView(detail);

            button(card, "Edit",
                    () -> editRecord(type, index));

            button(card, "Delete", () ->
                    new AlertDialog.Builder(this)
                            .setTitle("Delete record?")
                            .setMessage("This cannot be undone.")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Delete", (d, w) -> {
                                JSONArray current = records(type);
                                JSONArray updated = new JSONArray();

                                for (int j = 0; j < current.length(); j++) {
                                    if (j != index) {
                                        updated.put(current.optJSONObject(j));
                                    }
                                }

                                saveRecords(type, updated);
                                showRecords(type);
                            }).show());

            LinearLayout.LayoutParams p =
                    new LinearLayout.LayoutParams(-1, -2);
            p.setMargins(0, 0, 0, 12);
            root.addView(card, p);
        }

        button(root, "Back to Home", this::showHome);
    }

    private void addRecord(String type) {
        editRecordDialog(type, -1);
    }

    private void editRecord(String type, int index) {
        editRecordDialog(type, index);
    }

    private void editRecordDialog(String type, int index) {
        JSONObject old = new JSONObject();

        if (index >= 0) {
            old = records(type).optJSONObject(index);
            if (old == null) old = new JSONObject();
        }

        final JSONObject existing = old;

        LinearLayout box = column();
        box.setPadding(20, 8, 20, 0);

        EditText title = field("Title / Name", InputType.TYPE_CLASS_TEXT);
        EditText detail = field(
                type.equals("cert")
                        ? "Certificate code (unique)"
                        : "Details",
                InputType.TYPE_CLASS_TEXT);

        title.setText(existing.optString("title", ""));
        detail.setText(existing.optString("detail", ""));

        box.addView(title);
        gap(box, 10);
        box.addView(detail);

        new AlertDialog.Builder(this)
                .setTitle(index < 0 ? "Add " + typeTitle(type)
                        : "Edit " + typeTitle(type))
                .setView(box)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (dialog, which) -> {
                    String t = title.getText().toString().trim();
                    String d = detail.getText().toString().trim();

                    if (t.isEmpty() || d.isEmpty()) {
                        msg("Both fields are required.");
                        return;
                    }

                    JSONArray list = records(type);

                    if (type.equals("cert")) {
                        for (int i = 0; i < list.length(); i++) {
                            if (i == index) continue;

                            JSONObject record = list.optJSONObject(i);
                            if (record != null &&
                                    record.optString("detail")
                                            .equalsIgnoreCase(d)) {
                                msg("That certificate code already exists.");
                                return;
                            }
                        }
                    }

                    JSONObject item = new JSONObject();

                    try {
                        item.put("title", t);
                        item.put("detail", d);

                        if (index < 0) {
                            list.put(item);
                        } else {
                            JSONArray updated = new JSONArray();

                            for (int i = 0; i < list.length(); i++) {
                                updated.put(i == index
                                        ? item : list.optJSONObject(i));
                            }

                            list = updated;
                        }

                        saveRecords(type, list);
                        showRecords(type);
                        msg("Record saved.");
                    } catch (Exception ex) {
                        msg("Could not save record.");
                    }
                })
                .show();
    }

    private void verifyCertificate() {
        base("Certificate Verification",
                "Search locally saved certificate codes");

        EditText code = field("Enter certificate code",
                InputType.TYPE_CLASS_TEXT);
        root.addView(code);
        gap(root, 12);

        button(root, "Verify Code", () -> {
            String wanted = code.getText().toString().trim();

            if (wanted.isEmpty()) {
                msg("Enter a certificate code.");
                return;
            }

            JSONArray list = records("cert");

            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.optJSONObject(i);

                if (item != null &&
                        item.optString("detail")
                                .equalsIgnoreCase(wanted)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Record Found")
                            .setMessage(
                                    "Certificate: " +
                                    item.optString("title") +
                                    "\nCode: " +
                                    item.optString("detail") +
                                    "\n\nThis confirms a local record exists; " +
                                    "it does not prove official authenticity.")
                            .setPositiveButton("OK", null)
                            .show();
                    return;
                }
            }

            new AlertDialog.Builder(this)
                    .setTitle("No Matching Record")
                    .setMessage(
                            "No certificate with that code was found " +
                            "in this account's local records.")
                    .setPositiveButton("OK", null)
                    .show();
        });

        button(root, "Manage Certificates",
                () -> showRecords("cert"));
        button(root, "Back to Home", this::showHome);
    }
}
