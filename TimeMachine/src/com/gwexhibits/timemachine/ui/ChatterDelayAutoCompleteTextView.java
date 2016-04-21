package com.gwexhibits.timemachine.ui;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;

import com.gwexhibits.timemachine.objects.pojo.ChatterMention;
import com.gwexhibits.timemachine.objects.pojo.ChatterSegment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by psyfu on 4/14/2016.
 */
public class ChatterDelayAutoCompleteTextView extends MultiAutoCompleteTextView {

    private static final Character TOKEN = '@';
    private static final String MENTIONS_REGEX = "(?:$|\\@)\\[(.*?)\\]+";
    private static final String MENTION_TMP = "@[%s]";

    private ProgressBar loadingIndicator;
    private AtTokenizer tokenizer;
    private Map<String, String> setOfMentions = new HashMap<>();

    public ChatterDelayAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        tokenizer = new AtTokenizer();
        setTokenizer(tokenizer);
    }

    public void setLoadingIndicator(ProgressBar progressBar) {
        loadingIndicator = progressBar;
    }


    @Override
    public boolean enoughToFilter() {
        Editable text = getText();

        int end = getSelectionEnd();
        if (end < 0 || tokenizer == null) {
            return false;
        }

        int start = tokenizer.findTokenStart(text, end);

        if (end - start >= getThreshold() && this.getText().toString().length() > end - start) {
            if (loadingIndicator != null) {
                loadingIndicator.setVisibility(View.VISIBLE);
            }            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void replaceText(CharSequence text) {
        super.replaceText(text);
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    protected CharSequence convertSelectionToString(Object selectedItem) {
        if (selectedItem instanceof ChatterMention){
            ChatterMention mention = (ChatterMention) selectedItem;
            setOfMentions.put(String.format(MENTION_TMP, mention.getName()), mention.getRecordId());
            return "[" + mention.getName().toString() + "]";
        } else {
            return selectedItem.toString();
        }
    }

    public void clear(){
        this.setOfMentions.clear();
        this.getText().clear();
    }

    public List<ChatterSegment> getSegments(){

        List<ChatterSegment> fragments = new ArrayList<>();
        Pattern pattern = Pattern.compile(MENTIONS_REGEX);
        String newText = " " + getText();
        Matcher matcher = pattern.matcher(newText);

        int start = 0;
        while (matcher.find()){
            fragments.add(new ChatterSegment().asText(newText.substring(start, matcher.start() - 1)));
            fragments.add(new ChatterSegment().asMention(setOfMentions.get(matcher.group())));
            start = matcher.end();
        }

        if (start < newText.length()){
            fragments.add(new ChatterSegment().asText(newText.substring(start)));
        }

        return fragments;

    }

    private class AtTokenizer implements MultiAutoCompleteTextView.Tokenizer {

        @Override
        public int findTokenStart(CharSequence text, int cursor) {
            int i = cursor;

            while (i > 0 && text.charAt(i - 1) != TOKEN) {
                i--;
            }
            while (i < cursor && text.charAt(i) == ' ') {
                i++;
            }

            return i;
        }

        @Override
        public int findTokenEnd(CharSequence text, int cursor) {
            int i = cursor;
            int len = text.length();

            while (i < len) {
                if (text.charAt(i) == TOKEN) {
                    return i;
                } else {
                    i++;
                }
            }

            return len;
        }

        @Override
        public CharSequence terminateToken(CharSequence text) {

            int i = text.length();

            while (i > 0 && text.charAt(i - 1) == ' ') {
                i--;
            }

            if (i > 0 && text.charAt(i - 1) == TOKEN) {
                return text;
            } else {
                if (text instanceof Spanned) {
                    SpannableString sp = new SpannableString(text + ", ");
                    TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
                            Object.class, sp, 0);
                    return sp;
                } else {
                    return text + ", ";
                }
            }
        }
    }

}
