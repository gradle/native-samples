package org.gradle.samples.fixtures

import org.commonmark.node.*
import org.commonmark.parser.Parser

import java.util.regex.Pattern
/**
 * Locates the documentation for a sample in the README.md
 *
 * The documentation for a sample should be formatted as:
 *
 * - A level 2 heading with '(<sample-name>)' at the end of the heading text
 * - Includes all content up to the next level 2 heading.
 * - Must contain at least one fenced code block containing the instructions for the sample.
 */
class Documentation {
    private final Map<String, SampleDocumentation> samples

    Documentation(File documentationFile) {
        samples = loadSamples(documentationFile)
    }

    Documentation() {
        this(new File(Samples.rootSampleDir, "README.md"))
    }

    SampleDocumentation getSample(String name) {
        assert samples.keySet().contains(name)
        return samples.get(name)
    }

    private static Map<String, SampleDocumentation> loadSamples(File readme) {
        def parser = Parser.builder().build()
        def root = parser.parse(readme.text)
        def visitor = new HeadingVisitor()
        root.accept(visitor)
        return visitor.samples
    }

    private static class HeadingVisitor extends AbstractVisitor {
        private static Map<String, SampleDocumentation> samples = [:]

        @Override
        void visit(Heading heading) {
            if (heading.level != 2) {
                return
            }
            StringBuilder result = new StringBuilder()
            heading.accept(new AbstractVisitor() {
                @Override
                void visit(Text text) {
                    result.append(text.literal)
                }
            })
            def text = result.toString()
            def matcher = Pattern.compile(".+\\s+\\((.+)\\)\\s*").matcher(text)
            if (!matcher.matches()) {
                return
            }
            def name = matcher.group(1)
            samples.put(name, new SampleDocumentation(name, heading))
        }
    }

    static class SampleDocumentation {
        final Heading heading
        final String name

        SampleDocumentation(String name, Heading heading) {
            this.name = name
            this.heading = heading
        }

        /**
         * Asserts that this sample has instructions.
         */
        void hasInstructions() {
            assert !getInstructions().isEmpty() : "Could not find any instructions in README.md for sample '${name}'"
        }

        List<FencedCodeBlock> getInstructions() {
            def instructions = []
            Node node = heading.next
            while (node != null) {
                if (node instanceof FencedCodeBlock) {
                    instructions.add(node)
                }
                if (node instanceof Heading && node.level <= 2) {
                    break
                }
                node = node.next
            }
            return instructions
        }
    }
}
