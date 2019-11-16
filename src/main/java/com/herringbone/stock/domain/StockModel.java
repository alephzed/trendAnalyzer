package com.herringbone.stock.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockModel {

	private String symbol;

	private String last;
	private String date;
	private String time;
	private String change;
	private String open;
	private String high;
	private String low;
	private String volume;
	private String mktCap;
	private String previousClose;
	private String percentageChange;
	private String annRange;
	private String earns;
	private String pe;
	private String name;
}
