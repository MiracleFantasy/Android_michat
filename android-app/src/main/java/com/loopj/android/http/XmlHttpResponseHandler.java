package com.loopj.android.http;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XmlHttpResponseHandler extends TextHttpResponseHandler {

    private static final String TAG = XmlHttpResponseHandler.class.getSimpleName();

    // for overriding
    public void onStart() {}
    public void onSuccess(int statusCode, Header[] headers, Document response) {}
    public void onFailure(int statusCode, Header[] headers, Document errorResponse, Throwable error) {}
    public void onRetry() {}
    public void onProgress(int bytesWritten, int totalSize) {}
    public void onFinish() {}

    //
    // Pre-processing on messages (executes in background threadpool thread)
    //
    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseBody) {
        Object xmlResponse;
        if (statusCode != HttpStatus.SC_NO_CONTENT) {
            try {
                xmlResponse = parseResponse(responseBody);
                onSuccess(statusCode, headers, (Document) xmlResponse);
            } catch (Exception error) {
                onFailure(statusCode, headers, responseBody, error);
            }
        } else {
            try {
                xmlResponse = parseResponse(null);
                onSuccess(statusCode, headers, (Document) xmlResponse);
            } catch (Exception error) {
                onFailure(statusCode, headers, responseBody, error);
            }
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
        // Submit to Crashlytics to analyze the kinds of errors
        Crashlytics.log(Log.ERROR, TAG, error.getMessage());
        // ignore if responseBody is null or empty
        if (responseBody != null && !responseBody.isEmpty()) {
            Crashlytics.log(Log.ERROR, TAG, responseBody);
        }
    }

    /**
     * Returns Object of type {@link Document}
     *
     * @param responseBody response bytes to be assembled in String and parsed as JSON
     * @return Object parsedResponse
     * @throws ParserConfigurationException, SAXException, IOException exception if thrown while parsing XML
     */
    protected Object parseResponse(String responseBody) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc;
        if (responseBody != null) {
            InputStream is = new ByteArrayInputStream(responseBody.getBytes("UTF-8"));
            doc = db.parse(is);
        } else {
            doc = db.newDocument();
        }
        return doc;
    }
}
