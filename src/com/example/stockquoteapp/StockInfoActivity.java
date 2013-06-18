package com.example.stockquoteapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class StockInfoActivity extends Activity {
	
	private static final String TAG = "STOCKQUOTE";
	
	// Define the TextViews I use in activity_stock_info.xml
	
	TextView companyNameTextView;
	TextView yearLowTextView;
	TextView yearHighTextView;
	TextView daysLowTextView;
	TextView daysHighTextView;
	TextView lastTradePriceOnlyTextView;
	TextView changeTextView;
	TextView daysRangeTextView;
	
	// XML node keys
	static final String KEY_ITEM = "quote"; // parent node
	static final String KEY_NAME = "Name";
	static final String KEY_YEAR_LOW = "YearLow";
	static final String KEY_YEAR_HIGH = "YearHigh";
	static final String KEY_DAYS_LOW = "DaysLow";
	static final String KEY_DAYS_HIGH = "DaysHigh";
	static final String KEY_LAST_TRADE_PRICE = "LastTradePriceOnly";
	static final String KEY_CHANGE = "Change";
	static final String KEY_DAYS_RANGE = "DaysRange";
	
	// XML Data to Retrieve
	String name = "";
	String yearLow = "";
	String yearHigh = "";
	String daysLow = "";
	String daysHigh = "";
	String lastTradePriceOnly = "";
	String change = "";
	String daysRange = "";
	
	// Used to make the URL to call for XML data
	String yahooURLFirst = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22";
	String yahooURLSecond = "%22)&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
	
	// NEW STUFF
	
	// Holds values pulled from the XML document using XmlPullParser
	String[][] xmlPullParserArray = {{"AverageDailyVolume", "0"}, {"Change", "0"}, {"DaysLow", "0"},
			{"DaysHigh", "0"}, {"YearLow", "0"}, {"YearHigh", "0"},
			{"MarketCapitalization", "0"}, {"LastTradePriceOnly", "0"}, {"DaysRange", "0"},
			{"Name", "0"}, {"Symbol", "0"}, {"Volume", "0"},
			{"StockExchange", "0"}};
			
	int parserArrayIncrement = 0;
			
	// END OF NEW STUFF
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_info);
		// Show the Up button in the action bar.
		
		Intent intent = getIntent();
		String stockSymbol = intent.getStringExtra(MainActivity.STOCK_SYMBOL);
		
		// Initialize TextViews
		companyNameTextView = (TextView) findViewById(R.id.companyNameTextView);
		yearLowTextView = (TextView) findViewById(R.id.yearLowTextView);
		yearHighTextView = (TextView) findViewById(R.id.yearHighTextView);
		daysLowTextView = (TextView) findViewById(R.id.daysLowTextView);
		daysHighTextView = (TextView) findViewById(R.id.daysHighTextView);
		lastTradePriceOnlyTextView = (TextView) findViewById(R.id.lastTradePriceOnlyTextView);
		changeTextView = (TextView) findViewById(R.id.changeTextView);
		daysRangeTextView = (TextView) findViewById(R.id.daysRangeTextView);
		
		// Sends a message to the LogCat
		Log.d(TAG, "Before URL Creation " + stockSymbol);
		
		// Build URL with the symbol
		final String yURL = yahooURLFirst + stockSymbol + yahooURLSecond;
		
		new MyAsyncTask().execute(yURL);
		
		setupActionBar();
	}
	
	private class MyAsyncTask extends AsyncTask<String, String, String>{

		@Override
		// return a string as quote detail
		protected String doInBackground(String... params) {
			try{
				URL url = new URL(params[0]);
				URLConnection connection;
				connection = url.openConnection();
				
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				int responseCode = httpConnection.getResponseCode();
				
				if (responseCode == HttpURLConnection.HTTP_OK){
					InputStream in = httpConnection.getInputStream();
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					
					Document dom = db.parse(in);
					
					Element docElt = dom.getDocumentElement();
					
					NodeList nl = docElt.getElementsByTagName("quote");
					
					if (nl != null && nl.getLength() > 0){
						for (int i=0; i < nl.getLength(); i++){
							StockInfo theStock = getStockInformation(docElt);
							name = theStock.getName();
							yearLow = theStock.getYearLow();
							yearHigh = theStock.getYearHigh();
							daysLow = theStock.getDaysLow();
							daysHigh = theStock.getDaysHigh();
							lastTradePriceOnly = theStock.getLastTradePriceOnly();
							change = theStock.getChange();
							daysRange = theStock.getDaysRange();
						}
					}
					
				}
		    } catch (MalformedURLException e) {
			      Log.d(TAG, "MalformedURLException", e);
			    } catch (IOException e) {
			      Log.d(TAG, "IOException", e);
			    } catch (ParserConfigurationException e) {
			      Log.d(TAG, "Parser Configuration Exception", e);
			    } catch (SAXException e) {
			      Log.d(TAG, "SAX Exception", e);
			    }
			
			finally {}
			return null;
		}
		
		private StockInfo getStockInformation(Element entry){
			String stockName = getTextValue(entry, "Name");
			String stockYearLow = getTextValue(entry, "YearLow");
			String stockYearHigh = getTextValue(entry, "YearHigh");
			String stockDaysLow = getTextValue(entry, "DaysLow");
			String stockDaysHigh = getTextValue(entry, "DaysHigh");
			String stocklastTradePriceOnlyTextView = getTextValue(entry, "LastTradePriceOnly");
			String stockChange = getTextValue(entry, "Change");
			String stockDaysRange = getTextValue(entry, "DaysRange");
			
			StockInfo theStock = new StockInfo(stockDaysLow, stockDaysHigh, stockYearLow,
					stockYearHigh, stockName, stocklastTradePriceOnlyTextView,
					stockChange, stockDaysRange);
				
		    return theStock;
			
		}
		
		private String getTextValue(Element entry, String tagName){
			String tagValueToReturn = null;
			
			NodeList nl = entry.getElementsByTagName(tagName);
			if (nl != null && nl.getLength() > 0){
				Element element = (Element) nl.item(0);
				tagValueToReturn = element.getFirstChild().getNodeValue();
			}
			
			return tagValueToReturn;
		}
		
		protected void onPostExecute(String result) {
			companyNameTextView.setText(name);
			yearLowTextView.setText("Year Low: " + yearLow);
			yearHighTextView.setText("Year High: " + yearHigh);
			daysLowTextView.setText("Days Low: " + daysLow);
			daysHighTextView.setText("Days High: " + daysHigh);
			lastTradePriceOnlyTextView.setText("Last Price: " + lastTradePriceOnly);
			changeTextView.setText("Change: " + change);
			daysRangeTextView.setText("Daily Price Range: " + daysRange);
			
		}
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stock_info, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
