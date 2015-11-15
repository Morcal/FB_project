package com.feibo.joke.view.module.mine.edit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import fbcore.log.LogUtil;

public class TimeManager {

	private static final int START_YEAR = 1970;

	private static TimeManager instance;
	private TimeData timeData;

	public static TimeManager getInstance() {
		if (instance == null) {
			instance = new TimeManager();
		}
		return instance;
	}

	public TimeData getTimeData() {
		if(timeData != null) {
			return timeData;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		int currentYear = calendar.get(Calendar.YEAR);
		int currentMonth = calendar.get(Calendar.MONTH) + 1;
		int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
		LogUtil.d("", "year->" + currentYear + ", month->" + currentMonth + ", day->" + currentDay);
		
		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] monthsBig = { "1", "3", "5", "7", "8", "10", "12" };
		String[] monthsLittle = { "4", "6", "9", "11" };

		final List<String> listBig = Arrays.asList(monthsBig);
		final List<String> listLittle = Arrays.asList(monthsLittle);
		
		TimeData timeData = new TimeData();
		timeData.yearList = new ArrayList<>();
		for(int i = currentYear; i >= START_YEAR; i--) {
			Year year = new Year();
			year.monthList = new ArrayList<>();
			year.name = String.valueOf(i + "年");
			
			for(int j=1; j < (i == currentYear ? (currentMonth + 1) : 13); j++) {
				Month month = new Month();
				month.name = String.valueOf(j + "月");
				month.dayList = new ArrayList<>();

				// 判断大小月及是否闰年,用来确定"日"的数据
				int maxDay = 0;
				if(listBig.contains(String.valueOf(j))) {
					maxDay = 31;
				} else if(listLittle.contains(String.valueOf(j))) {
					maxDay = 30;
				} else {
					// 闰年
					if ((i % 4 == 0 && i % 100 != 0) || i % 400 == 0)
						maxDay = 29;
					else
						maxDay = 28;
				}
				if(i == currentYear && j == currentMonth) {
					maxDay = currentDay;
				}
				for(int k=1; k <= maxDay; k++) {
					Day day = new Day();
					day.name = String.valueOf(k + "日");
					month.dayList.add(day);
				}
				year.monthList.add(month);
			}
			timeData.yearList.add(year);
		}
		this.timeData = timeData;
		return timeData;
	}

	public static class TimeData {
		public List<TimeNameBean> yearList;
	}

	public static class Year extends TimeNameBean {
		public List<TimeNameBean> monthList;
	}

	public static class Month extends TimeNameBean {
		public List<TimeNameBean> dayList;
	}

	public static class Day extends TimeNameBean {
	}

	public static class TimeNameBean {
		public String name;

		public TimeNameBean() {
		}

		public TimeNameBean(String name) {
			this.name = name;
		}
	}

}
