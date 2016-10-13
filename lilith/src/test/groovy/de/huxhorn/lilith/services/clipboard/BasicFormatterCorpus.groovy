/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2016 Joern Huxhorn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.huxhorn.lilith.services.clipboard

import de.huxhorn.lilith.data.EventWrapperCorpus
import de.huxhorn.lilith.services.BasicFormatter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BasicFormatterCorpus {
	private static final Logger logger = LoggerFactory.getLogger(BasicFormatterCorpus)

	public static List<Object> createCorpus() {
		EventWrapperCorpus.createCorpus()
	}

	public static Set<Integer> isCompatible(BasicFormatter formatter) {
		Objects.requireNonNull(formatter, "formatter must not be null!")
		List<Object> corpus = createCorpus()

		Set<Integer> result = [] as TreeSet<Integer>
		for(int i=0;i<corpus.size();i++) {
			if(logger.isDebugEnabled()) logger.debug('Before isCompatible(corpus[{}])...', i)
			if(formatter.isCompatible(corpus[i])) {
				result << i
			}
		}
		return result
	}

	public static List<String> toString(BasicFormatter formatter) {
		toString(formatter, [])
	}

	public static List<String> toString(BasicFormatter formatter, Set<Integer> excluded) {
		def result = []
		List<Object> corpus = createCorpus()
		for(int i=0;i<corpus.size();i++) {
			if(excluded.contains(i)) {
				if(logger.isDebugEnabled()) logger.debug('Skipping excluded toString(corpus[{}])...', i)
				result << null
				continue;
			}
			if(logger.isDebugEnabled()) logger.debug('Before toString(corpus[{}])...', i)
			result << formatter.toString(corpus[i])
		}
		return result
	}
}
