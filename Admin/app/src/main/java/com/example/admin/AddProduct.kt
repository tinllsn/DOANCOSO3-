package com.example.admin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.admin.databinding.ActivityAddProductBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID

class AddProduct : AppCompatActivity() {
    private lateinit var dbRef: DatabaseReference
    private val binding by lazy { ActivityAddProductBinding.inflate(layoutInflater) }
    var selectedImages = mutableListOf<Uri>()
    val firestore = Firebase.firestore
    private val storage = Firebase.storage.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val selectImagesActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data

//                     xu ly hinh anh
                    //Multiple images selected
//                    Đoạn này kiểm tra xem clipData của Intent có khác null hay không.
//                    clipData chứa nhiều mục dữ liệu nếu người dùng đã chọn nhiều hình ảnh.

                    if (intent?.clipData != null) {
                        val count = intent.clipData?.itemCount ?: 0
                        (0 until count).forEach {
//                            Lấy URI của từng hình ảnh được
                            val imagesUri = intent.clipData?.getItemAt(it)?.uri
                            imagesUri?.let { selectedImages.add(it) }
                        }

                        //One images was selected

                    } else {
                        val imageUri = intent?.data
                        imageUri?.let { selectedImages.add(it) }
                    }
                    updateImages()
                }
            }
        //6
        /*-ACTION_GET_CONTENT. Hành động này được sử dụng để mở trình chọn nội dung (content chooser),
        cho phép người dùng chọn một mẩu dữ liệu (ví dụ: hình ảnh, âm thanh, tài liệu, v.v.) từ các ứng dụng cung cấp nội dung trên thiết bị
        */
        /*  -Đoạn này thêm một Extra vào Intent với khóa Intent.EXTRA_ALLOW_MULTIPLE và giá trị true.
            - Intent.EXTRA_ALLOW_MULTIPLE cho phép người dùng chọn nhiều mục (multiple items) cùng một lúc.
            Khi giá trị này được đặt thành true, trình chọn nội dung sẽ cho phép người dùng chọn nhiều hình ảnh thay vì chỉ một   */

// -Đoạn này đặt kiểu MIME (MIME type) của Intent là "image/*".
        // -"image/*" chỉ định rằng trình chọn nội dung chỉ nên hiển thị các loại tệp hình ảnh

        /*Đoạn này khởi chạy Intent đã được cấu hình thông qua một ActivityResultLauncher được gọi là selectImagesActivityResult.
        selectImagesActivityResult là một đối tượng ActivityResultLauncher<Intent> được đăng ký trước đó để nhận kết quả từ hoạt động chọn nội dung.
         launch(intent) sẽ khởi động Intent và hiển thị trình chọn nội dung cho người dùng. Khi người dùng chọn xong, kết quả sẽ được trả về và xử lý bởi ActivityResultLauncher.
             */
        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            Thêm một thông tin phụ vào Intent để cho phép người dùng chọn nhiều hình ảnh cùng một lúc (EXTRA_ALLOW_MULTIPLE với giá trị true).
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectImagesActivityResult.launch(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //1
        if (item.itemId == R.id.saveProduct) {
            val productValidation = validateInformation()
            if (!productValidation) {
                Toast.makeText(this, "Check your inputs", Toast.LENGTH_SHORT).show()
                return false
            }
            saveProducts() {
//                Log.d("test", it.toString())
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                binding.apply {
                    edName.setText("")
                    edCategory.setText("")
                    edPrice.setText("")
                    edDescription.setText("")
                    edOfferPercentage.setText("")
                    tvSelectedImages.setText("")
                }
                selectedImages.clear()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun validateInformation(): Boolean {
        if (selectedImages.isEmpty())
            return false
        if (binding.edName.text.toString().trim().isEmpty())
            return false
        if (binding.edCategory.text.toString().trim().isEmpty())
            return false
        if (binding.edPrice.text.toString().trim().isEmpty())
            return false

        return true
    }

    private fun saveProducts(state: (Boolean) -> Unit) {
//        val sizes = getSizesList(binding.edSizes.text.toString().trim())
        val imagesByteArrays = getImagesByteArrays() //7
        val name = binding.edName.text.toString().trim()
        val images = mutableListOf<String>()
        val category = binding.edCategory.text.toString().trim()
        val description = binding.edDescription.text.toString().trim()
        val price = binding.edPrice.text.toString().trim()
        val offerPercentage = binding.edOfferPercentage.text.toString().trim()

//        Sử dụng lifecycleScope.launch để khởi chạy một coroutine
        lifecycleScope.launch {
//            withContext(Dispatchers.Main) chuyển đổi ngữ cảnh sang main thread để hiển thị loading.
            withContext(Dispatchers.Main) {
                showLoading()
            }
//Tải lên hình ảnh lên Firebase Storage
            try {
                async {
                    Log.wtf("test1", "test")
                    imagesByteArrays.forEach {
//                        nó lặp qua imagesByteArrays, tạo một UUID cho mỗi hình ảnh và tải hình ảnh lên Firebase Storage.
                        val id = UUID.randomUUID().toString()
                        launch {
                            val imagesStorage = storage.child("products/images/$id")
                            val result = imagesStorage.putBytes(it).await()
                            val downloadUrl = result.storage.downloadUrl.await().toString()
                            images.add(downloadUrl)
                        }
                    }
                }.await()
            } catch (e: java.lang.Exception) {

                withContext(Dispatchers.Main) {
                    hideLoading()
                }
                state(false)
            }

            Log.d("test2", "test")
//Lưu thông tin sản phẩm vào Firestore
            val product = Product(
                UUID.randomUUID().toString(),
                name,
                category,
                price.toFloat(),
                if (offerPercentage.isEmpty()) null else offerPercentage.toFloat(),
                if (description.isEmpty()) null else description,
                null,
//                sizes,
                null,
                images
            )
//             luu vao reatime databae
            dbRef = FirebaseDatabase.getInstance().getReference("Products")
            dbRef.child(product.id).setValue(product).addOnSuccessListener {

                Log.wtf("reatime","success")
                state(true)
                hideLoading()
            }.addOnFailureListener {

                Log.wtf("test2", it.message.toString())
                state(false)
                hideLoading()
            }
//             =========================
            firestore.collection("Products").add(product).addOnSuccessListener {
                state(true)
                hideLoading()
            }.addOnFailureListener {
                Log.wtf("test2", it.message.toString())
                state(false)
                hideLoading()
            }
        }
    }

    private fun hideLoading() {
        binding.progressbar.visibility = View.INVISIBLE
    }

    private fun showLoading() {
        binding.progressbar.visibility = View.VISIBLE

    }

    private fun getImagesByteArrays(): List<ByteArray> {
        val imagesByteArray = mutableListOf<ByteArray>()
        selectedImages.forEach {
//            Tạo một đối tượng ByteArrayOutputStream để ghi dữ liệu hình ảnh vào một mảng byte.
            val stream = ByteArrayOutputStream()
//            Sử dụng MediaStore.Images.Media.getBitmap để lấy một đối tượng Bitmap từ URI của hình ảnh.
//            contentResolver được sử dụng để truy xuất nội dung của hình ảnh từ URI.
            val imageBmp = MediaStore.Images.Media.getBitmap(contentResolver, it)
//            Nén Bitmap thành định dạng JPEG và ghi dữ liệu nén vào stream. Bitmap.
//            CompressFormat.JPEG chỉ định định dạng nén. Tham số 85 chỉ định chất lượng nén (từ 0 đến 100, với 100 là chất lượng cao nhất).
//            Nếu việc nén thành công (compress trả về true), khối mã bên trong sẽ được thực thi.
            if (imageBmp.compress(Bitmap.CompressFormat.JPEG, 85, stream)) {
                val imageAsByteArray = stream.toByteArray()
                imagesByteArray.add(imageAsByteArray)
            }
        }
        return imagesByteArray
    }

    private fun updateImages() {
//        binding.tvSelectedImages.setText(selectedImages.size.toString())
        binding.tvSelectedImages.text = selectedImages.size.toString()
    }
}