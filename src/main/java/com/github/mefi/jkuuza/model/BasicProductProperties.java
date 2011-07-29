package com.github.mefi.jkuuza.model;

/**
 *
 * @author mefi
 */
public enum BasicProductProperties {

	NAME {
		@Override
		 public String toString() {
			return "name";
		}
	},
	DESCRIPTION {
		@Override
		 public String toString() {
			return "description";
		}
	},
	PRICE {
		@Override
		 public String toString() {
			return "price";
		}
	},
	PRICE_DPH {
		@Override
		 public String toString() {
			return "price_dph";
		}
	},
	TYPE {
		@Override
		 public String toString() {
			return "type";
		}
	},
	PRODUCER {
		@Override
		 public String toString() {
			return "producer";
		}
	},
	PARAMETER_NAME {
		@Override
		 public String toString() {
			return "parameter_name";
		}
	},
	PARAMETER_VALUE {
		@Override
		 public String toString() {
			return "parameter_value";
		}
	}
	
}
