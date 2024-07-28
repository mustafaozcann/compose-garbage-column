package com.mzcnsoft.garbagecolumn

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.mzcnsoft.garbagecolumn.ui.component.ColumnDefaults
import com.mzcnsoft.garbagecolumn.ui.component.GarbageColumn
import com.mzcnsoft.garbagecolumn.ui.component.ItemUiModel
import com.mzcnsoft.garbagecolumn.ui.theme.GarbageColumnTheme

class MainActivity : ComponentActivity() {

	@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
	@OptIn(ExperimentalMaterial3Api::class)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			GarbageColumnTheme {
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

				Scaffold(
					topBar = {
						CenterAlignedTopAppBar(
							title = {
								Text(
									text= "Garbage Column",
									color = ColumnDefaults.textColor,
									style = ColumnDefaults.defaultTextStyle,
									textAlign = TextAlign.Center
								)
							},
							colors = TopAppBarDefaults.topAppBarColors(
								containerColor = ColumnDefaults.primaryColor
							)
						)
					},
					content = { padding ->
						GarbageColumn(
							modifier = Modifier
								.imePadding()
								.statusBarsPadding()
								.navigationBarsPadding(),
							paddingValues = padding,
							items = items,
							onDeleteItem = { deletedItem ->
								items.remove(deletedItem)
							}
						)
					}
				)
			}
		}
	}
}