package com.github.mefi.jkuuza.app.db;

/**
 *
 * @author Marek Pilecky
 */
public enum DefaultDbParams {

	HOST {
		@Override
		 public String toString() {
			return "localhost";
		}
	},
	PORT {
		@Override
		 public String toString() {
			return "5984";
		}
	},
	DATABASE {
		@Override
		 public String toString() {
			return "jkuuza";
		}
	},
	USERNAME {
		@Override
		 public String toString() {
			return "";
		}
	},
	PASSWORD {
		@Override
		 public String toString() {
			return "";
		}
	},
}
