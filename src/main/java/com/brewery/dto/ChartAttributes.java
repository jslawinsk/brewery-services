package com.brewery.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ChartAttributes {

	int minValue;
	int maxValue;
	int startAngle;
	int endAngle;
	double valueNumber;
	String gaugeType;
	String title;
	String valueType;
	List<PlotBand> plotBands;
	List<ArrayList> stops;
	List<SeriesInfo> seriesInfos;
	SeriesInfo seriesInfo;
	
    public ChartAttributes() {
		super();
		
		minValue = 0;
		maxValue = 200;
		startAngle = -150;
		endAngle = 150;
		gaugeType = "gauge";
		title = "Measurement";
		valueType = "Number";
		valueNumber = 0.0;
		plotBands = new ArrayList<PlotBand>();
		stops = new ArrayList<ArrayList>();
		seriesInfos = new ArrayList<SeriesInfo>();
		seriesInfo = new SeriesInfo();
    }


	public int getMinValue() {
		return minValue;
	}


	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}


	public int getMaxValue() {
		return maxValue;
	}


	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public int getStartAngle() {
		return startAngle;
	}

	public void setStartAngle(int startAngle) {
		this.startAngle = startAngle;
	}

	public int getEndAngle() {
		return endAngle;
	}

	public void setEndAngle(int endAngle) {
		this.endAngle = endAngle;
	}

	public String getGaugeType() {
		return gaugeType;
	}

	public void setGaugeType(String gaugeType) {
		this.gaugeType = gaugeType;
	}
	
	public double getValueNumber() {
		return valueNumber;
	}

	public void setValueNumber(double valueNumber) {
		this.valueNumber = valueNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public List<PlotBand> getPlotBands() {
		return plotBands;
	}

	public void addPlotBand( long from, long to, String color ) {
		PlotBand plotBand = new PlotBand( from, to, color );
		plotBands.add( plotBand );
	}

	public List<SeriesInfo> getSeriesInfos() {
		return seriesInfos;
	}

	public void addSeriesInfo( SeriesInfo seriesInfo ) {
		seriesInfos.add( seriesInfo );
	}
	
	public List<ArrayList> getStops() {
		return stops;
	}

	public void addStop( double value, String color ) {
		// Stop stop = new Stop( value, color );
		ArrayList<Object> list = new ArrayList<>();
		list.add( value );
		list.add( color );
		stops.add( list );
	}

	public SeriesInfo getSeriesInfo() {
		return seriesInfo;
	}
	
	public void addSeriesData( long dateTime, double valueNumber ) {
		seriesInfo.addData(dateTime, valueNumber);
	}
	
	@Override
	public String toString() {
		return "Gauge [minValue=" + minValue 
				+ ", maxValue=" + maxValue 
				+ ", startAngle=" + startAngle 
				+ ", endAngle=" + endAngle 
				+ ", gaugeType=" + gaugeType 
				+ ", valueNumber=" + valueNumber 
				+ ", title=" + title 
				+ ", valueType=" + valueType 
				+ ", plotBands=" + plotBands 
				+ ", seriesInfo=" + seriesInfo 
				+ "]";
	}

	public class PlotBand{
    	private long from;
    	private long to;
    	private String color;
    	
    	public PlotBand() {
    	}
    	public PlotBand( long from, long to, String color ) {
    		this.from = from;
    		this.to = to;
    		this.color = color;
    	}
    	
		public long getFrom() {
			return from;
		}
		public void setFrom(long from) {
			this.from = from;
		}
		public long getTo() {
			return to;
		}
		public void setTo(long to) {
			this.to = to;
		}
		public String getColor() {
			return color;
		}
		public void setColor(String color) {
			this.color = color;
		}
		@Override
		public String toString() {
			return "PlotBand [from=" + from + ", to=" + to + ", color=" + color + "]";
		}
    	
    }

	public class SeriesInfo{
	    private String name;
		List<ArrayList> data;
		
		public SeriesInfo() {
			super();
			this.name = "test";
			data = new ArrayList<ArrayList>();
		}
		public SeriesInfo(String name) {
			super();
			this.name = name;
			data = new ArrayList<ArrayList>();
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<ArrayList> getData() {
			return data;
		}

		public void addData( long dateTime, double valueNumber ) {
			ArrayList<Object> list = new ArrayList<>();
			list.add( dateTime );
			list.add( valueNumber );
			data.add( list );
		}
		
		
		@Override
		public String toString() {
			return "SeriesData [Name=" + name + "]";
		}
		
	}
	
	public class Stop{
    	
    	private ArrayList<Object> list = new ArrayList<>();
    	
    	public Stop() {
        	list = new ArrayList<>();
    	}
    	public Stop( long value, String color ) {
    		list = new ArrayList<>();
    		list.add( value );
    		list.add( color );
    	}
    	
		public ArrayList<Object> getStop() {
			return list;
		}

		@Override
		public String toString() {
			return "Stop [ " + list + " ]";
		}
    	
    }

}
