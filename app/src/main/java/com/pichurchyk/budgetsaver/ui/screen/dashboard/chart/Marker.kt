/*
 * Copyright 2025 by Patryk Goworowski and Patrick Michalik.
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

package com.pichurchyk.budgetsaver.ui.screen.dashboard.chart

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.LayeredComponent
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.patrykandpatrick.vico.core.common.shape.MarkerCorneredShape

@Composable
internal fun rememberMarker(
  valueFormatter: DefaultCartesianMarker.ValueFormatter =
    DefaultCartesianMarker.ValueFormatter.default(),
  showIndicator: Boolean = true,
): CartesianMarker {
  val labelBackgroundShape = MarkerCorneredShape(CorneredShape.Corner.Rounded)
  val labelBackground =
    rememberShapeComponent(
      fill = fill(MaterialTheme.colorScheme.background),
      shape = labelBackgroundShape,
      strokeFill = fill(MaterialTheme.colorScheme.outline),
      strokeThickness = 1.dp,
    )
  val label =
    rememberTextComponent(
//      style =
//        TextStyle(
//          color = MaterialTheme.colorScheme.onSurface,
//          textAlign = TextAlign.Center,
//          fontSize = 12.sp,
//        ),
      padding = Insets(8f, 4f),
      background = labelBackground,
      minWidth = TextComponent.MinWidth.fixed(40f),
    )
  val indicatorFrontComponent =
    rememberShapeComponent(fill(MaterialTheme.colorScheme.surface), CorneredShape.Pill)
  val guideline = rememberAxisGuidelineComponent()
  return rememberDefaultCartesianMarker(
    label = label,
    valueFormatter = valueFormatter,
    indicator =
      if (showIndicator) {
        { color ->
          LayeredComponent(
            back = ShapeComponent(Fill(color.copy(alpha = 0.15f).toArgb()), CorneredShape.Pill),
            front =
              LayeredComponent(
                back = ShapeComponent(fill = Fill(color.toArgb()), shape = CorneredShape.Pill),
                front = indicatorFrontComponent,
                padding = Insets(5f),
              ),
            padding = Insets(10f),
          )
        }
      } else {
        null
      },
    indicatorSize = 36.dp,
    guideline = guideline,
  )
}
