package com.mingmin.newtaipeipublicparking

import androidx.test.espresso.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables
import androidx.test.espresso.core.internal.deps.guava.collect.Iterators
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import androidx.test.espresso.util.TreeIterables
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

class RecyclerViewActionsExtension {
    companion object {
        fun actionOnChildView(childMatcher: Matcher<View>, viewAction: ViewAction): ViewAction {
            return ActionChildView(childMatcher, viewAction)
        }

        fun itemAtPosition(position: Int): RecyclerViewItemAssertion {
            return RecyclerViewItemAssertion(position)
        }

        private fun findChildView(childMatcher: Matcher<View>, root: View): View {
            val children = TreeIterables.breadthFirstViewTraversal(root)
            val matchedChildren = Iterables.filter(children) { childMatcher.matches(it) }.iterator()
            if (matchedChildren.hasNext()) {
                val matchedChild = matchedChildren.next()
                if (matchedChildren.hasNext()) {
                    throw AmbiguousViewMatcherException.Builder()
                        .withViewMatcher(childMatcher)
                        .withRootView(root)
                        .withView1(matchedChild)
                        .withView2(matchedChildren.next())
                        .withOtherAmbiguousViews(*Iterators.toArray<View>(matchedChildren, View::class.java))
                        .build()
                } else {
                    return matchedChild
                }
            } else {
                throw NoMatchingViewException.Builder()
                    .withViewMatcher(childMatcher)
                    .withRootView(root)
                    .build()
            }
        }
    }

    private class ActionChildView(private val childMatcher: Matcher<View>,
                                  private val viewAction: ViewAction) : ViewAction {
        override fun getDescription(): String {
            return "Perform action: ${viewAction.description} on child view matching: $childMatcher"
        }

        override fun getConstraints(): Matcher<View> {
            return allOf(isAssignableFrom(ViewGroup::class.java), isDisplayingAtLeast(90))
        }

        override fun perform(uiController: UiController?, view: View?) {
            val child = findChildView(childMatcher, view!!)
            viewAction.perform(uiController, child)
        }
    }

    class RecyclerViewItemAssertion(private val position: Int) : ViewAssertion {
        private lateinit var viewAssertion: ViewAssertion
        private var childMatcher: Matcher<View>? = null

        fun matches(viewMatcher: Matcher<View>): RecyclerViewItemAssertion {
            viewAssertion = ViewAssertions.matches(viewMatcher)
            return this
        }

        fun onChildView(childMatcher: Matcher<View>): RecyclerViewItemAssertion {
            this.childMatcher = childMatcher
            return this
        }

        fun doesNotExist(): RecyclerViewItemAssertion {
            this.viewAssertion = ViewAssertions.doesNotExist()
            return this
        }

        override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
            val recyclerView = view as RecyclerView
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
            var viewAtPosition: View? = null
            viewHolder?.let {
                val root = it.itemView
                viewAtPosition = if (childMatcher == null) root else findChildView(childMatcher!!, root)
            }
            viewAssertion.check(viewAtPosition, noViewFoundException)
        }
    }
}
