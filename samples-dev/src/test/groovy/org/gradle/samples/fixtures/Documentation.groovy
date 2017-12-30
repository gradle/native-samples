package org.gradle.samples.fixtures

import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Heading
import org.commonmark.node.Text
import org.commonmark.parser.Parser

class Documentation {
    private static HeadingVisitor headings

    static List<String> getHeadings() {
        if (headings == null) {
            def readme = new File(Samples.rootSampleDir, "README.md")
            def parser = Parser.builder().build()
            def root = parser.parse(readme.text)
            def visitor = new HeadingVisitor()
            root.accept(visitor)
            headings = visitor
        }
        return headings.headings
    }

    static class HeadingVisitor extends AbstractVisitor {
        List<String> headings = []

        @Override
        void visit(Heading heading) {
            StringBuilder result = new StringBuilder()
            heading.accept(new AbstractVisitor() {
                @Override
                void visit(Text text) {
                    result.append(text.literal)
                }
            })
            headings.add(result.toString())
        }
    }
}
