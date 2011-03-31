
package net.sf.openrock.model;

import java.util.logging.Logger;

public class CurlFactory {
	static final Logger logger = Logger.getLogger(CurlFactory.class.getName());

	public enum CurlType {
		NORMAL,
		STRAIGHT,
		SWINGY,
		REALLYSWINGY
	}

	public static CurlIF CreateCurlByType(CurlType curltype)
	{
		CurlIF curl = null;

		switch(curltype)
		{
			case NORMAL:
			logger.info("CurlNormal");
			curl = new Curl();
			break;

			case STRAIGHT:
			logger.info("CurlStraight");
			curl = new Curl(.02, .01, .45);
			break;

			case SWINGY:
			logger.info("CurlSwingy");
			curl = new Curl(.02, .01, .95);
			break;

			case REALLYSWINGY:
			logger.info("CurlReallySwingy");
			curl = new Curl(.02, .01, 1.3);
			break;
		}

		return curl;
	}
}

class Curl implements CurlIF
{
	private double A;
	private double B;
	private double C;


	Curl() {
		// default/Normal
		this.A = 0.02;
		this.B = 0.01;
		this.C = 0.7;
	}

	Curl(double A, double B, double C) {
		this.A = A;
		this.B = B;
		this.C = C;
	}

	public double getCurl(double da, double s, double sweep)
	{
		double curl = 0;

		double curlFriction = (1.0 - sweep) * A + 
			sweep * B;
		curl = -curlFriction * 
			Math.max(-1.0, Math.min(C*da/(s*s), 1.0));

		return curl;

	}
}

