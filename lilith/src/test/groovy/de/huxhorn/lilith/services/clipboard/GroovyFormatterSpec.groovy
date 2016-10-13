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

import de.huxhorn.sulky.junit.JUnitTools
import org.junit.Rule
import org.junit.rules.TemporaryFolder

class GroovyFormatterSpec extends AbstractClipboardFormatterSpec {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder()

	public File rootDirectory

	def setup() {
		rootDirectory = folder.newFolder()
	}

	@Override
	ClipboardFormatter createInstance() {
		File groovyFormatterFile = new File(rootDirectory, 'CopyMdcValue.groovy');
		JUnitTools.copyResourceToFile('/CopyMdcValue.txt', groovyFormatterFile)

		return new GroovyFormatter(groovyFormatterFile.absolutePath)
	}

	@Override
	Set<Integer> expectedIndices() {
		return [24, 68]
	}

	@Override
	List<String> expectedResults() {
		return [
				'mdcValue',
				'otherMdcValue'
		]
	}

	@Override
	boolean expectedNative() {
		return false
	}

	@Override
	boolean expectedAcceleratorAvailability() {
		return true
	}

	def "wrong type on corpus."() {
		setup:
		File groovyFormatterFile = new File(rootDirectory, 'WrongType.groovy');
		JUnitTools.copyResourceToFile('/WrongType.txt', groovyFormatterFile)

		def instance = new GroovyFormatter(groovyFormatterFile.absolutePath)

		when:
		Set<Integer> compatibleIndices = BasicFormatterCorpus.isCompatible(instance)
		List<String> results = BasicFormatterCorpus.toString(instance, [] as Set)

		then:
		compatibleIndices == [] as Set

		and:
		results.removeIf({ it == null })

		then:
		results == []
	}

	def "wrong type attributes."() {
		setup:
		File groovyFormatterFile = new File(rootDirectory, 'WrongType.groovy');
		JUnitTools.copyResourceToFile('/WrongType.txt', groovyFormatterFile)

		def instance = new GroovyFormatter(groovyFormatterFile.absolutePath)

		expect:
		instance.name == 'WrongType.groovy'
		instance.description == 'WrongType.groovy - Expected ClipboardFormatter but received WrongType!'
		instance.accelerator == null
	}

	def "syntax error attributes."() {
		setup:
		File groovyFormatterFile = new File(rootDirectory, 'SyntaxError.groovy');
		JUnitTools.copyResourceToFile('/SyntaxError.txt', groovyFormatterFile)

		def instance = new GroovyFormatter(groovyFormatterFile.absolutePath)

		expect:
		instance.name == 'SyntaxError.groovy'
		instance.description == 'SyntaxError.groovy - Exception while parsing class from \''+groovyFormatterFile.absolutePath+'\'!'
		instance.accelerator == null
	}

	def "working attributes."() {
		setup:
		def instance = createInstance()

		expect:
		instance.name == 'Copy mdcKey value'
		instance.description == 'Copy mdcKey value from MDC, if available.'
		instance.accelerator != null
	}

	def "missing file attributes."() {
		setup:
		File groovyFormatterFile = new File(rootDirectory, 'MissingFile.groovy');
		def instance = new GroovyFormatter(groovyFormatterFile.absolutePath)

		expect:
		instance.name == 'MissingFile.groovy'
		instance.description == 'MissingFile.groovy - \''+groovyFormatterFile.absolutePath+'\' is not a file!'
		instance.accelerator == null
	}

	def "null file attributes."() {
		setup:
		def instance = new GroovyFormatter()

		expect:
		instance.name == 'Missing file!'
		instance.description == 'Missing file! - groovyFileName must not be null!'
		instance.accelerator == null
	}
}
