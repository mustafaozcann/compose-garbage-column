package com.mzcnsoft.garbagecolumn.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mzcnsoft.garbagecolumn.R
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe
import de.charlex.compose.rememberRevealState
import kotlinx.coroutines.launch

@Immutable
data object ColumnDefaults {
	val primaryColor = Color(0xFF131842)
	val secondaryColor = Color(0xFFE68369)
	val textColor = Color(0xFFECCEAE)
	val buttonColor = Color(0xFF36BA98)
	val defaultTextStyle = TextStyle(
		color = textColor,
		fontFamily = FontFamily(Font(R.font.inter_semibold)),
		fontSize = 24.sp
	)
}

@Immutable
data class ItemUiModel(
	val id: Int,
	val title: String,
)

@Composable
fun GarbageColumn(
	modifier: Modifier = Modifier,
	paddingValues: PaddingValues = PaddingValues(16.dp),
	items: List<ItemUiModel>,
	onDeleteItem: (ItemUiModel) -> Unit,
) {

	val scope = rememberCoroutineScope()

	var isShowGarbageArea by remember {
		mutableStateOf(false)
	}

	var deletedItemId by remember {
		mutableIntStateOf(-1)
	}

	val rotationAnim = remember {
		Animatable(0f)
	}

	val transXAnim = remember {
		Animatable(0f)
	}

	val transYAnim = remember {
		Animatable(0f)
	}

	val alphaAnim = remember {
		Animatable(1f)
	}

	val scaleAnim = remember {
		Animatable(1f)
	}

	Row(
		modifier = modifier
			.fillMaxWidth()
			.background(ColumnDefaults.primaryColor)
			.padding(paddingValues)
			.fillMaxSize()
	) {
		Column(
			modifier = Modifier
				.graphicsLayer { clip = false }
				.weight(1f),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			items.forEachIndexed { index, item ->
				GarbageColumnItem(
					modifier = Modifier.then(
						if (item.id == deletedItemId) {
							Modifier
								.graphicsLayer {
									rotationZ = rotationAnim.value
									translationX = transXAnim.value
									translationY = transYAnim.value
									scaleX = scaleAnim.value
									alpha = alphaAnim.value
//									graphicsLayer.record(
//										density = Density(
//											density = density.density
//										),
//										layoutDirection = LayoutDirection.Ltr,
//										size = IntSize(1000,1000)
//									){
//										rotationZ = rotation
//										translationX = 550f
//									}
								}
						} else
							Modifier
					),
					uiModel = item,
					enableSwipe = !isShowGarbageArea,
					onDeleteClick = { deletedItem ->
//						onDeleteItem(deletedItem)
						isShowGarbageArea = true
						scope.launch {
							launch {
								scaleAnim.animateTo(0.5f, tween(1000))
								transXAnim.animateTo(800f, tween(4000))

								val alphaAnimResult = alphaAnim.animateTo(0.2f, tween(1000))
								if (alphaAnimResult.endReason == AnimationEndReason.Finished) {
									onDeleteItem(deletedItem)
									isShowGarbageArea = false
								}
							}

							launch {
								val rotationAnimResult = rotationAnim.animateTo(90f, tween(4000))
								if (rotationAnimResult.endReason == AnimationEndReason.Finished) {
									transYAnim.animateTo(1500f, tween(1000))
								}
							}
						}.invokeOnCompletion {
							scope.launch {
								rotationAnim.snapTo(0f)
								transXAnim.snapTo(0f)
								transYAnim.snapTo(0f)
								alphaAnim.snapTo(1f)
							}
						}
						deletedItemId = deletedItem.id
					}
				)
			}
		}
		AnimatedVisibility(
			visible = isShowGarbageArea,
		) {
			Box(
				modifier = Modifier
					.weight(1f)
					.padding(16.dp)
					.drawWithContent {
//						drawLayer(graphicsLayer)
					}
					.width(150.dp)
					.fillMaxHeight()
					.background(ColumnDefaults.primaryColor)
			)
		}
	}
}

@Composable
fun DeleteAction(
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier
			.wrapContentHeight()
	) {
		Box(
			modifier = Modifier
				.background(
					color = ColumnDefaults.buttonColor,
					shape = RoundedCornerShape(10.dp)
				)
				.padding(vertical = 16.dp, horizontal = 16.dp),
			contentAlignment = Alignment.Center
		) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Icon(
					modifier = Modifier.size(24.dp),
					painter = painterResource(id = R.drawable.ic_drag_garbage),
					contentDescription = "move to trash",
					tint = ColumnDefaults.textColor
				)
				Text(
					text = "Delete",
					style = ColumnDefaults.defaultTextStyle,
					color = ColumnDefaults.textColor
				)
			}
		}
	}
}

@Composable
fun GarbageColumnItem(
	modifier: Modifier = Modifier,
	enableSwipe: Boolean = true,
	uiModel: ItemUiModel,
	onDeleteClick: (ItemUiModel) -> Unit,
) {
	RevealSwipe(
		modifier = Modifier.padding(vertical = 5.dp),
		enableSwipe = enableSwipe,
		state = rememberRevealState(
			maxRevealDp = 130.dp,
			directions = setOf(RevealDirection.EndToStart)
		),
		backgroundCardEndColor = ColumnDefaults.buttonColor,
		backgroundStartActionLabel = "",
		backgroundEndActionLabel = "Delete entry",
		onBackgroundEndClick = {
			onDeleteClick(uiModel)
			true
		},
		hiddenContentEnd = {
			DeleteAction()
		}
	) {
		Box(
			modifier = modifier
				.fillMaxWidth()
				.wrapContentHeight()
				.background(
					ColumnDefaults.secondaryColor,
					shape = RoundedCornerShape(10.dp)
				)
				.height(100.dp),
			contentAlignment = Alignment.Center
		) {
			Text(
				text = uiModel.title,
				style = ColumnDefaults.defaultTextStyle
			)
		}
	}
}

@Preview
@Composable
private fun GarbageColumnPreview() {

	val items = remember {
		mutableStateListOf(
			ItemUiModel(
				id = 1,
				title = "Item 1"
			),
			ItemUiModel(
				id = 2,
				title = "Item 2"
			),
			ItemUiModel(
				id = 3,
				title = "Item 3"
			),
			ItemUiModel(
				id = 4,
				title = "Item 4"
			),
			ItemUiModel(
				id = 5,
				title = "Item 5"
			),
		)
	}

	GarbageColumn(items = items,
		onDeleteItem = { deletedItem ->
			items.remove(deletedItem)
		}
	)
}