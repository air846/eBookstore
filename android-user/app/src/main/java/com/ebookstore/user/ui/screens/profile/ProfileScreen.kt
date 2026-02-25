package com.ebookstore.user.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToReader: (Int, Int, Int) -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.cacheDir, "avatar_${System.currentTimeMillis()}.jpg")
                inputStream?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }

                viewModel.uploadAvatar(
                    file = file,
                    onSuccess = {
                        scope.launch {
                            snackbarHostState.showSnackbar("头像上传成功")
                        }
                        file.delete()
                    },
                    onError = { error ->
                        scope.launch {
                            snackbarHostState.showSnackbar("上传失败: $error")
                        }
                        file.delete()
                    }
                )
            } catch (e: Exception) {
                scope.launch {
                    snackbarHostState.showSnackbar("读取图片失败: ${e.message}")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("我的")
                        Text(
                            text = "管理资料、通知与阅读记录",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material.CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // User Info
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "个人信息",
                            style = MaterialTheme.typography.titleMedium
                        )

                        // Avatar section
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .clickable { imagePickerLauncher.launch("image/*") }
                            ) {
                                AsyncImage(
                                    model = uiState.avatar.ifEmpty { "https://via.placeholder.com/150" },
                                    contentDescription = "头像",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                if (uiState.isUploading) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        androidx.compose.material.CircularProgressIndicator(
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }
                            }

                            Column {
                                Text(
                                    text = "点击头像上传",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                TextButton(
                                    onClick = { imagePickerLauncher.launch("image/*") },
                                    enabled = !uiState.isUploading
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("选择图片")
                                }
                            }
                        }

                        OutlinedTextField(
                            value = uiState.user?.username ?: "",
                            onValueChange = {},
                            label = { Text("用户名") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false
                        )

                        OutlinedTextField(
                            value = uiState.nickname,
                            onValueChange = viewModel::onNicknameChange,
                            label = { Text("昵称") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = viewModel::onEmailChange,
                            label = { Text("邮箱") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                viewModel.updateProfile {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("个人资料已更新")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isSaving
                        ) {
                            if (uiState.isSaving) {
                                androidx.compose.material.CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("保存资料")
                            }
                        }
                    }
                }

                // Password
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "账户安全",
                            style = MaterialTheme.typography.titleMedium
                        )

                        OutlinedTextField(
                            value = uiState.oldPassword,
                            onValueChange = viewModel::onOldPasswordChange,
                            label = { Text("原密码") },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation()
                        )

                        OutlinedTextField(
                            value = uiState.newPassword,
                            onValueChange = viewModel::onNewPasswordChange,
                            label = { Text("新密码") },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation()
                        )

                        OutlinedTextField(
                            value = uiState.confirmPassword,
                            onValueChange = viewModel::onConfirmPasswordChange,
                            label = { Text("确认新密码") },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation()
                        )

                        Button(
                            onClick = {
                                viewModel.updatePassword {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("密码修改成功")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isSaving
                        ) {
                            Text("修改密码")
                        }
                    }
                }

                // Notices
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "评论通知",
                                style = MaterialTheme.typography.titleMedium
                            )
                            TextButton(onClick = { viewModel.markAllNoticesRead() }) {
                                Text("全部已读")
                            }
                        }

                        if (uiState.notices.isEmpty()) {
                            Text("暂无通知", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            uiState.notices.forEach { notice ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (notice.type == 1) "有人点赞了你的评论" else "有人点踩了你的评论",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.weight(1f)
                                        )
                                        TextButton(
                                            onClick = { viewModel.markNoticeRead(notice.id) }
                                        ) {
                                            Text(if (notice.readFlag == 1) "已读" else "标记")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Stats
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "阅读统计",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("收藏: ${uiState.favorites.size} 本")
                        Text("历史: ${uiState.history.size} 条")
                    }
                }

                // Logout
                OutlinedButton(
                    onClick = { viewModel.logout(onLogout) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("退出登录")
                }
            }
        }
    }
}
