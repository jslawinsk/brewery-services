package com.brewery.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ChartAttributesTest {

	@Test
	void testPlotBand() {
		ChartAttributes chartAttributes = new ChartAttributes();
		ChartAttributes.PlotBand plotBand =  chartAttributes.new PlotBand( );
		plotBand.setFrom( 0 );
		plotBand.setTo( 12 );
		plotBand.setColor( "#e0790b" );
		assertEquals( "#e0790b", plotBand.getColor() );
	}

	@Test
	void testSeriesInfo() {
		ChartAttributes chartAttributes = new ChartAttributes();
		ChartAttributes.SeriesInfo seriesInfo =  chartAttributes.new SeriesInfo( );
		seriesInfo.setName( "Test" );
		assertEquals( "Test", seriesInfo.getName() );
	}
	
	@Test
	void testStop() {
		ChartAttributes chartAttributes = new ChartAttributes();
		ChartAttributes.Stop stop =  chartAttributes.new Stop( );
		assertEquals( 0, stop.getStop().size() );
		
		stop =  chartAttributes.new Stop( 3, "#e0790b" );
		assertTrue( stop.toString().startsWith( "Stop [ " ) );
	}
}
