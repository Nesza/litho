/*
 * Copyright (c) 2017-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.litho;

import static com.facebook.litho.Layout.create;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.robolectric.RuntimeEnvironment.application;

import android.view.View;
import com.facebook.litho.animation.AnimatedProperties;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.util.InlineLayoutSpec;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(ComponentsTestRunner.class)
public class CollectTransitionsTest {

  private static final InlineLayoutSpec WRAPPING_COMPONENT_WITH_TRANSITION =
      new InlineLayoutSpec() {

        @Override
        protected ComponentLayout onCreateLayout(ComponentContext c) {
          return create(c, COMPONENT_WITH_TRANSITION).build();
        }

        @Override
        protected Transition onCreateTransition(ComponentContext c) {
          return Transition.create("test").animate(AnimatedProperties.Y);
        }
      };

  private static final InlineLayoutSpec COMPONENT_WITH_TRANSITION =
      new InlineLayoutSpec() {

        @Override
        protected ComponentLayout onCreateLayout(ComponentContext c) {
          return Row.create(c)
              .child(Row.create(c).transitionKey("test"))
              .child(Row.create(c).transitionKey("test2"))
              .build();
        }

        @Override
        protected Transition onCreateTransition(ComponentContext c) {
          return Transition.create("test").animate(AnimatedProperties.X);
        }
      };

  @Rule public ExpectedException mExpectedException = ExpectedException.none();

  @Test
  public void testCollectsWrappingTransitions() {
    ComponentContext c = new ComponentContext(application);
    LayoutState layoutState =
        LayoutState.calculate(
            c,
            WRAPPING_COMPONENT_WITH_TRANSITION,
            ComponentTree.generateComponentTreeId(),
            View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY));
    assertThat(layoutState.getTransitionContext()).isNotNull();
    assertThat(layoutState.getTransitionContext().getTransitions()).hasSize(2);
  }
}