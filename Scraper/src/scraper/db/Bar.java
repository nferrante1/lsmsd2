package scraper.db;

import java.time.Instant;

public class Bar {
	public Instant t;
	public double o;
	public double h;
	public double l;
	public double c;
	public double v;
	
	public Bar(Instant t, double o, double h, double l, double c, double v ) {
		this.t = t;
		this.o = o;
		this.h = h;
		this.l = l;
		this.c = c;
		this.v = v;
	}
}
