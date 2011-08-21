/*
 *   Copyright 2011 Marek Pilecky
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
