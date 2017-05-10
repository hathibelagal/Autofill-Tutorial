package com.tutsplus.simplefill;

import android.app.assist.AssistStructure;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.service.autofill.AutoFillService;
import android.service.autofill.Dataset;
import android.service.autofill.FillCallback;
import android.service.autofill.FillResponse;
import android.service.autofill.SaveCallback;
import android.view.autofill.AutoFillValue;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

public class EmailAddressFiller extends AutoFillService {

    void identifyEmailFields(AssistStructure.ViewNode node, List<AssistStructure.ViewNode> emailFields) {
        if(node.getClassName().contains("EditText")) {
            String viewId = node.getIdEntry();
            if(viewId!=null && (viewId.contains("email") || viewId.contains("username"))) {
                emailFields.add(node);
                return;
            }
        }
        for(int i=0; i<node.getChildCount();i++) {
            identifyEmailFields(node.getChildAt(i), emailFields);
        }
    }

    @Override
    public void onFillRequest(AssistStructure assistStructure, Bundle bundle,
                              CancellationSignal cancellationSignal, FillCallback fillCallback) {
        List<AssistStructure.ViewNode> emailFields = new ArrayList<>();

        identifyEmailFields(assistStructure.getWindowNodeAt(0).getRootViewNode(), emailFields);

        // Do nothing if no email fields found
        if(emailFields.size() == 0)
            return;

        // Load the email addresses from preferences
        SharedPreferences sharedPreferences = getSharedPreferences("EMAIL_STORAGE", MODE_PRIVATE);
        String primaryEmail = sharedPreferences.getString("PRIMARY_EMAIL", "");
        String secondaryEmail = sharedPreferences.getString("SECONDARY_EMAIL", "");

        // Create remote views for both the email addresses
        RemoteViews rvPrimaryEmail = new RemoteViews(getPackageName(), R.layout.email_suggestion);
        rvPrimaryEmail.setTextViewText(R.id.email_suggestion_item, primaryEmail);
        RemoteViews rvSecondaryEmail = new RemoteViews(getPackageName(), R.layout.email_suggestion);
        rvSecondaryEmail.setTextViewText(R.id.email_suggestion_item, secondaryEmail);

        // Choose the first email field
        AssistStructure.ViewNode emailField = emailFields.get(0);

        // Create a dataset for the email addresses
        Dataset primaryEmailDataSet = new Dataset.Builder(rvPrimaryEmail)
                                            .setValue(
                                                    emailField.getAutoFillId(),
                                                    AutoFillValue.forText(primaryEmail)
                                            ).build();
        Dataset secondaryEmailDataSet = new Dataset.Builder(rvSecondaryEmail)
                .setValue(
                        emailField.getAutoFillId(),
                        AutoFillValue.forText(secondaryEmail)
                ).build();

        // Create and send response with both datasets
        FillResponse response = new FillResponse.Builder()
                                    .addDataset(primaryEmailDataSet)
                                    .addDataset(secondaryEmailDataSet)
                                    .build();
        fillCallback.onSuccess(response);
    }

    @Override
    public void onSaveRequest(AssistStructure assistStructure, Bundle bundle, SaveCallback saveCallback) {

    }
}
