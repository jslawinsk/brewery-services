package com.brewery.dto;

import java.util.ArrayList;
import java.util.List;


public class Gauge {

	int minValue;
	int maxValue;
	int startAngle;
	int endAngle;
	String gaugeType;
	List<PlotBand> plotBands;
	List<ArrayList> stops;
	
    public Gauge() {
		super();
		
		minValue = 0;
		maxValue = 200;
		startAngle = -150;
		endAngle = 150;
		gaugeType = "gauge";
		plotBands = new ArrayList<PlotBand>();
		stops = new ArrayList<ArrayList>();
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

	public List<PlotBand> getPlotBands() {
		return plotBands;
	}

	public void addPlotBand( long from, long to, String color ) {
		PlotBand plotBand = new PlotBand( from, to, color );
		plotBands.add( plotBand );
	}

	public List<ArrayList> getStops() {
		return stops;
	}

	public void addStop( long value, String color ) {
		// Stop stop = new Stop( value, color );
		ArrayList<Object> list = new ArrayList<>();
		list.add( value );
		list.add( color );
		stops.add( list );
	}
	
	@Override
	public String toString() {
		return "Gauge [minValue=" + minValue 
				+ ", maxValue=" + maxValue 
				+ ", startAngle=" + startAngle 
				+ ", endAngle=" + endAngle 
				+ ", gaugeType=" + gaugeType 
				+ ", plotBands=" + plotBands + "]";
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
