package com.brewery.dto;

import java.util.ArrayList;
import java.util.List;


public class Gauge {

	int minValue;
	int maxValue;
	List<PlotBand> plotBands;
	
    public Gauge() {
		super();
		minValue = 0;
		maxValue = 200;
		plotBands = new ArrayList<PlotBand>();
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


	public List<PlotBand> getPlotBands() {
		return plotBands;
	}

	public void addPlotBand( long from, long to, String color ) {
		PlotBand plotBand = new PlotBand( from, to, color );
		plotBands.add( plotBand );
	}
    
	@Override
	public String toString() {
		return "Gauge [minValue=" + minValue + ", maxValue=" + maxValue + ", plotBands=" + plotBands + "]";
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


}
