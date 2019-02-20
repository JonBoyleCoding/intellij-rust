/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.lang.core.completion

import org.intellij.lang.annotations.Language
import org.rust.ide.settings.RsCodeInsightSettings

class RsTraitMethodCompletionTest : RsCompletionTestBase() {

    fun `test auto import trait while method completion 1`() = doTest("""
        mod baz {
            pub trait Foo {
                fn foo(&self);
            }

            pub struct Bar;

            impl Foo for Bar {
                fn foo(&self) {}
            }
        }

        use baz::Bar;

        fn main() {
            Bar.fo/*caret*/
        }
    """, """
        mod baz {
            pub trait Foo {
                fn foo(&self);
            }

            pub struct Bar;

            impl Foo for Bar {
                fn foo(&self) {}
            }
        }

        use baz::Bar;
        use baz::Foo;

        fn main() {
            Bar.foo()/*caret*/
        }
    """)

    fun `test auto import trait while method completion 2`() = doTest("""
        mod baz {
            pub trait Foo {
                fn foo(&self, x: i32);
            }

            pub struct Bar;

            impl Foo for Bar {
                fn foo(&self, x: i32) {}
            }
        }

        use baz::Bar;

        fn main() {
            Bar.fo/*caret*/()
        }
    """, """
        mod baz {
            pub trait Foo {
                fn foo(&self, x: i32);
            }

            pub struct Bar;

            impl Foo for Bar {
                fn foo(&self, x: i32) {}
            }
        }

        use baz::Bar;
        use baz::Foo;

        fn main() {
            Bar.foo(/*caret*/)
        }
    """)

    fun `test do not insert trait import while method completion when trait in scope`() = doTest("""
        mod baz {
            pub trait Foo {
                fn foo(&self);
            }

            pub struct Bar;

            impl Foo for Bar {
                fn foo(&self) {}
            }
        }

        use baz::{Bar, Foo};

        fn main() {
            Bar.fo/*caret*/
        }
    """, """
        mod baz {
            pub trait Foo {
                fn foo(&self);
            }

            pub struct Bar;

            impl Foo for Bar {
                fn foo(&self) {}
            }
        }

        use baz::{Bar, Foo};

        fn main() {
            Bar.foo()/*caret*/
        }
    """)

    fun `test do not insert trait import while method completion when setting disabled`() = doTest("""
        mod baz {
            pub trait Foo {
                fn foo(&self, x: i32);
            }

            pub struct Bar;

            impl Foo for Bar {
                fn foo(&self, x: i32) {}
            }
        }

        use baz::Bar;

        fn main() {
            Bar.fo/*caret*/()
        }
    """, """
        mod baz {
            pub trait Foo {
                fn foo(&self, x: i32);
            }

            pub struct Bar;

            impl Foo for Bar {
                fn foo(&self, x: i32) {}
            }
        }

        use baz::Bar;

        fn main() {
            Bar.foo(/*caret*/)
        }
    """, addTraitImport = false)

    private fun doTest(
        @Language("Rust") before: String,
        @Language("Rust") after: String,
        addTraitImport: Boolean = true
    ) {
        val settings = RsCodeInsightSettings.getInstance()
        val initialValue = settings.addTraitImport
        settings.addTraitImport = addTraitImport
        try {
            doSingleCompletion(before, after)
        } finally {
            settings.addTraitImport = initialValue
        }
    }
}
