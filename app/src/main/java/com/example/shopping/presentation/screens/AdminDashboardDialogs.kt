package com.example.shopping.presentation.screens

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.shopping.domain.models.BannerDataModels
import com.example.shopping.domain.models.CategoryDataModels
import com.example.shopping.domain.models.ProductDataModels
import com.example.shopping.presentation.screens.admin.AdminActionFeedbackDialog
import com.example.shopping.presentation.screens.admin.AdminDeleteBannerDialog
import com.example.shopping.presentation.screens.admin.AdminDeleteProductDialog
import com.example.shopping.presentation.screens.admin.AdminDuplicateWarningDialog
import com.example.shopping.presentation.screens.admin.AdminEditBannerDialog
import com.example.shopping.presentation.screens.admin.AdminEditCategoryDialog
import com.example.shopping.presentation.screens.admin.AdminEditProductDialog
import com.example.shopping.presentation.utils.CropAspectRatio
import com.example.shopping.presentation.utils.ProductImageCropDialog
import com.example.shopping.presentation.viewModels.ShoppingAppViewModel
import com.example.shopping.presentation.viewModels.addProduct
import com.example.shopping.presentation.viewModels.deleteBanner
import com.example.shopping.presentation.viewModels.deleteCategory
import com.example.shopping.presentation.viewModels.deleteProduct
import com.example.shopping.presentation.viewModels.updateBanner
import com.example.shopping.presentation.viewModels.updateCategory
import com.example.shopping.presentation.viewModels.updateProduct

@Composable
internal fun AdminDashboardDialogsHost(
    viewModel: ShoppingAppViewModel,
    actionDialogTitle: String?,
    actionDialogMessage: String?,
    isSuccessDialog: Boolean,
    onDismissActionDialog: () -> Unit,
    duplicateWarningMessage: String?,
    pendingDuplicateProduct: ProductDataModels?,
    onConfirmDuplicate: (ProductDataModels) -> Unit,
    onDismissDuplicate: () -> Unit,
    editingProduct: ProductDataModels?,
    editProductImage: String,
    onEditProductImageChange: (String) -> Unit,
    predefinedCategories: List<String>,
    predefinedGenders: List<String>,
    onRequestCropEditProduct: (String) -> Unit,
    onRequestPickAndCropEditProduct: () -> Unit,
    onDismissEditProduct: () -> Unit,
    deletingProductId: String?,
    onDismissDeleteProduct: () -> Unit,
    editingCategory: CategoryDataModels?,
    onDismissEditCategory: () -> Unit,
    editingBanner: BannerDataModels?,
    onDismissEditBanner: () -> Unit,
    deletingBanner: BannerDataModels?,
    onDismissDeleteBanner: () -> Unit,
    cropTarget: String?,
    cropImageUrl: String,
    cropBitmap: android.graphics.Bitmap?,
    cropDialogRatio: CropAspectRatio,
    cropDialogTitle: String,
    onDismissCrop: () -> Unit,
    onCropConfirmed: (String, String) -> Unit
) {
    val context = LocalContext.current

    AdminActionFeedbackDialog(
        title = actionDialogTitle,
        message = actionDialogMessage,
        isSuccess = isSuccessDialog,
        onDismiss = onDismissActionDialog
    )

    AdminDuplicateWarningDialog(
        message = duplicateWarningMessage,
        onConfirm = {
            if (pendingDuplicateProduct != null) {
                onConfirmDuplicate(pendingDuplicateProduct)
            }
        },
        onDismiss = onDismissDuplicate
    )

    AdminEditProductDialog(
        product = editingProduct,
        editImage = editProductImage,
        onEditImageChange = onEditProductImageChange,
        predefinedCategories = predefinedCategories,
        predefinedGenders = predefinedGenders,
        onRequestCrop = onRequestCropEditProduct,
        onRequestPickAndCrop = onRequestPickAndCropEditProduct,
        onDismiss = onDismissEditProduct,
        onSaveProduct = { updated ->
            viewModel.updateProduct(
                product = updated,
                onSuccess = {
                    Toast.makeText(context, "✅ Product Updated in Firestore!", Toast.LENGTH_SHORT).show()
                    onDismissEditProduct()
                },
                onError = { error ->
                    Toast.makeText(context, "❌ Update Error: $error", Toast.LENGTH_LONG).show()
                }
            )
        }
    )

    AdminDeleteProductDialog(
        productId = deletingProductId,
        onDismiss = onDismissDeleteProduct,
        onConfirm = { id ->
            onDismissDeleteProduct()
            viewModel.deleteProduct(
                productId = id,
                onSuccess = {
                    Toast.makeText(context, "🗑️ Product Deleted Successfully!", Toast.LENGTH_SHORT).show()
                },
                onError = { error ->
                    Toast.makeText(context, "❌ Delete Error: $error", Toast.LENGTH_LONG).show()
                }
            )
        }
    )

    AdminEditCategoryDialog(
        category = editingCategory,
        onDismiss = onDismissEditCategory,
        onSaveCategory = { updated ->
            viewModel.updateCategory(
                categoryId = updated.categoryId,
                category = updated,
                onSuccess = {
                    Toast.makeText(context, "✅ Category Updated in Firestore!", Toast.LENGTH_SHORT).show()
                    onDismissEditCategory()
                },
                onError = { error ->
                    Toast.makeText(context, "❌ Error: $error", Toast.LENGTH_LONG).show()
                }
            )
        }
    )

    AdminEditBannerDialog(
        banner = editingBanner,
        onDismiss = onDismissEditBanner,
        onSaveBanner = { updated ->
            viewModel.updateBanner(
                bannerId = updated.bannerId,
                banner = updated,
                onSuccess = {
                    Toast.makeText(context, "✅ Banner Updated in Firestore!", Toast.LENGTH_SHORT).show()
                    onDismissEditBanner()
                },
                onError = { error ->
                    Toast.makeText(context, "❌ Error: $error", Toast.LENGTH_LONG).show()
                }
            )
        }
    )

    AdminDeleteBannerDialog(
        banner = deletingBanner,
        onDismiss = onDismissDeleteBanner,
        onConfirm = { ban ->
            onDismissDeleteBanner()
            viewModel.deleteBanner(
                bannerId = ban.bannerId,
                onSuccess = {
                    Toast.makeText(context, "🗑️ Banner Deleted Successfully!", Toast.LENGTH_SHORT).show()
                },
                onError = { error ->
                    Toast.makeText(context, "❌ Delete Error: $error", Toast.LENGTH_LONG).show()
                }
            )
        }
    )

    if (cropTarget != null && (cropImageUrl.isNotBlank() || cropBitmap != null)) {
        ProductImageCropDialog(
            initialBitmap = cropBitmap,
            imageUrl = cropImageUrl,
            dialogTitle = cropDialogTitle,
            initialRatio = cropDialogRatio,
            onDismissRequest = onDismissCrop,
            onCropConfirmed = { _, base64Url ->
                onCropConfirmed(cropTarget, base64Url)
                Toast.makeText(context, "Framing & Crop Applied! 🎉", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
