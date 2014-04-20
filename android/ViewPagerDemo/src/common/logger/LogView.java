package common.logger;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class LogView extends TextView implements LogNode {
	
    LogNode mNext;

    public LogView(Context context) {
        super(context);
    }

    public LogView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LogView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void println(int priority, String tag, String msg, Throwable tr) {
        
        String priorityStr = priorityStr(priority);

        String exceptionStr = null;
        if (tr != null) {
            exceptionStr = android.util.Log.getStackTraceString(tr);
        }

        // Take the priority, tag, message, and exception, and concatenate as necessary
        // into one usable line of text.
        final StringBuilder outputBuilder = new StringBuilder();

        String delimiter = "\t";
        appendIfNotNull(outputBuilder, priorityStr, delimiter);
        appendIfNotNull(outputBuilder, tag, delimiter);
        appendIfNotNull(outputBuilder, msg, delimiter);
        appendIfNotNull(outputBuilder, exceptionStr, delimiter);

        // In case this was originally called from an AsyncTask or some other off-UI thread,
        // make sure the update occurs within the UI thread.
        ((Activity) getContext()).runOnUiThread(new Thread(new Runnable() {
            @Override
            public void run() {
                // Display the text we just generated within the LogView.
                appendToLog(outputBuilder.toString());
            }
        }));

        if (mNext != null) {
            mNext.println(priority, tag, msg, tr);
        }
    }
    
    private String priorityStr(int priority){
    	String str = null;
    	switch(priority) {
        case android.util.Log.VERBOSE:
        	str = "VERBOSE";
            break;
        case android.util.Log.DEBUG:
        	str = "DEBUG";
            break;
        case android.util.Log.INFO:
        	str = "INFO";
            break;
        case android.util.Log.WARN:
        	str = "WARN";
            break;
        case android.util.Log.ERROR:
        	str = "ERROR";
            break;
        case android.util.Log.ASSERT:
        	str = "ASSERT";
            break;
        default:
            break;
    	}
    	return str;
    }

    private StringBuilder appendIfNotNull(StringBuilder source, String addStr, String delimiter) {
        if (addStr != null) {
            if (addStr.length() == 0) {
                delimiter = "";
            }

            return source.append(addStr).append(delimiter);
        }
        return source;
    }

    /** Outputs the string as a new line of log data in the LogView. */
    public void appendToLog(String s) {
        append("\n" + s);
    }

    public LogNode getNext() {
        return mNext;
    }

    public void setNext(LogNode node) {
        mNext = node;
    }

}
