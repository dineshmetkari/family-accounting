/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.achartengine.chartdemo.demo.chart;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

/**
 * Sales growth demo chart.
 */
public class SalesGrowthChart extends AbstractDemoChart {
	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */
	public String getName() {
		return "Sales growth";
	}

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	public String getDesc() {
		return "The sales growth across several years (time chart)";
	}

	/**
	 * Executes the chart demo.
	 * 
	 * @param context
	 *            the context
	 * @return the built intent
	 */
	public Intent execute(Context context) {
		String[] titles = new String[] { "Sales growth January 1995 to December 2000" };
		List<Date[]> dates = new ArrayList<Date[]>();
		List<double[]> values = new ArrayList<double[]>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		Date[] dateValues = null;
		try {
			dateValues = new Date[] { format.parse("1995.01.01"),
					format.parse("1995.03.01"), format.parse("1995.06.01"),
					format.parse("1995.09.01"), format.parse("1996.01.01"),
					format.parse("1996.03.01"), format.parse("1996.06.01"),
					format.parse("1996.09.01"), format.parse("1997.01.01"),
					format.parse("1997.03.01"), format.parse("1997.06.01"),
					format.parse("1997.09.01"), format.parse("1998.01.01"),
					format.parse("1998.03.01"), format.parse("1998.06.01"),
					format.parse("1998.09.01"), format.parse("1999.01.01"),
					format.parse("1999.03.01"), format.parse("1999.06.01"),
					format.parse("1999.09.01"), format.parse("2000.01.01"),
					format.parse("2000.03.01"), format.parse("2000.06.01"),
					format.parse("2000.09.01"), format.parse("2000.11.01") };
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dates.add(dateValues);

		values.add(new double[] { 4.9, 5.3, 3.2, 4.5, 6.5, 4.7, 5.8, 4.3, 4,
				2.3, -0.5, -2.9, 3.2, 5.5, 4.6, 9.4, 4.3, 1.2, 0, 0.4, 4.5,
				3.4, 4.5, 4.3, 4 });
		int[] colors = new int[] { Color.BLUE };
		PointStyle[] styles = new PointStyle[] { PointStyle.POINT };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		setChartSettings(renderer, "Sales growth", "Date", "%",
				dateValues[0].getTime(),
				dateValues[dateValues.length - 1].getTime(), -4, 11,
				Color.GRAY, Color.LTGRAY);
		renderer.setYLabels(10);
		return ChartFactory.getTimeChartIntent(context,
				buildDateDataset(titles, dates, values), renderer, "MMM yyyy");
	}

}
