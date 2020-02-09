package org.hackku.fintech.services;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hackku.fintech.domains.Business;
import org.hackku.fintech.domains.DailyReport;
import org.hackku.fintech.util.PolyTrendLine;
import org.hackku.fintech.util.TrendLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PredictionService {
	
	@Autowired
	DailyReportService reportService;
	
	@Autowired
	ForecastService forecastService;

	public BigDecimal predictDay(LocalDate date, Business business) {
		List<DailyReport> reports = reportService.findByBusiness(business);
		List<Double> dayOfWeekIncomes = new ArrayList<>();
		for(DailyReport report : reports) {
			if(report.getCreated().getDayOfWeek().equals(date.getDayOfWeek())) {
				dayOfWeekIncomes.add(report.getIncome().doubleValue());
			}
		}
		double[] incomes = dayOfWeekIncomes.stream().mapToDouble(i -> i).toArray();
		double[] x = new double[365*3];
		for(int i =1; i <= x.length; i++) {x[i] = i;}
		TrendLine t = new PolyTrendLine(5);
		t.setValues(incomes, x);
		double predictionValue = t.predict(x.length+1);
		
		predictionValue = predictionValue*(temperatureMultiplier(forecastService.findOrSearch(business.getCity(), date).getMaxTemperature(),60,40))*(weatherMultiplier(forecastService.findOrSearch(business.getCity(), date).getWeatherIcon(),forecastService.findOrSearch(business.getCity(), date).getPrecipitationType(),0));
		BigDecimal endValue = new BigDecimal(predictionValue, MathContext.DECIMAL64);
		
		//BigDecimal x = BigDecimal.valueOf(5);
		//x = x.multiply(BigDecimal.valueOf(2));
		return endValue;
	}
	
	public double temperatureMultiplier(double temperature, double mean, double sigma) {
		double y = 1/(mean*Math.sqrt(2*Math.PI));
		y = y*(Math.pow(Math.E,(Math.pow(-(temperature-mean), 2)/Math.pow(2*sigma, 2))));
		return y;
	}
	
	public double weatherMultiplier(int weatherIcon, String precipitationType, double precipitationAmount) {
		double weatherTypeMultiplier = 0;
		double precipitationTypeMultiplier = 0;
		double precipiationAmountMultiplier = 0;
		
		
		if(weatherIcon >= 1 && weatherIcon <= 5) {
			weatherTypeMultiplier = 1;
		}
		else if(weatherIcon >= 6 && weatherIcon <= 11) {
			weatherTypeMultiplier = 0.9;
		}
		else if(weatherIcon >= 12 && weatherIcon <= 14) {
			weatherTypeMultiplier = 0.75;
		}
		else if(weatherIcon >= 15 && weatherIcon <= 17) {
			weatherTypeMultiplier = 0.3;
		}
		else if(weatherIcon >= 18 && weatherIcon <= 21) {
			weatherTypeMultiplier = 0.65;
		}
		else if(weatherIcon >= 22 && weatherIcon <= 23) {
			weatherTypeMultiplier = 0.85;
		}
		else if(weatherIcon >= 24 && weatherIcon <= 25) {
			weatherTypeMultiplier = 0.2;
		}
		else if(weatherIcon >= 26 && weatherIcon <= 29) {
			weatherTypeMultiplier = 0.6;
		}
		else if(weatherIcon >= 30 && weatherIcon <= 30) {
			weatherTypeMultiplier = 0.9;
		}
		else if(weatherIcon >= 31 && weatherIcon <= 31) {
			weatherTypeMultiplier = 0.8;
		}
		else if(weatherIcon >= 32 && weatherIcon <= 32) {
			weatherTypeMultiplier = 0.7;
		}
		else if(weatherIcon >= 33 && weatherIcon <= 36) {
			weatherTypeMultiplier = 0.9;
		}
		else if(weatherIcon >= 37 && weatherIcon <= 38) {
			weatherTypeMultiplier = 0.85;
		}
		else if(weatherIcon >= 39 && weatherIcon <= 42) {
			weatherTypeMultiplier = 0.1;
		}
		else if(weatherIcon >= 43 && weatherIcon <= 44) {
			weatherTypeMultiplier = 0.15;
		}
		
		
		if("rain".equals(precipitationType)) {
			precipitationTypeMultiplier = 0.8;
		}
		else if("snow".equals(precipitationType)) {
			precipitationTypeMultiplier = 0.7;
		}
		else if("ice".equals(precipitationType)) {
			precipitationTypeMultiplier = 0.4;
		}
		else if("mixed".equals(precipitationType)) {
			precipitationTypeMultiplier = 0.2;
		}
		else {
			precipitationTypeMultiplier = 1;
		}
			
		precipiationAmountMultiplier = temperatureMultiplier(precipitationAmount, 0, 3);
		
		return (weatherTypeMultiplier*precipitationTypeMultiplier*precipiationAmountMultiplier);
	} 
}
