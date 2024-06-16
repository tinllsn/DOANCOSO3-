package com.example.doan.fragments.settings

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.doan.data.User
import com.example.doan.databinding.FragmentUserAccountBinding
import com.example.doan.dialog.setupBottomSheetDialog
import com.example.doan.util.Resource
import com.example.doan.util.hideBottomNavigationView
import com.example.doan.viewmodel.UserAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class UserAccountFragment : Fragment() {
    private lateinit var binding: FragmentUserAccountBinding
    private val viewModel by viewModels<UserAccountViewModel>()
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        imageActivityResultLauncher is initialized to handle the result from an activity that allows the user to pick an image.
//        The result is processed using a lambda function that extracts the URI of the selected image and loads it into the ImageView using Glide.

//Cụ thể, imageActivityResultLauncher sẽ là một launcher mà bạn có thể sử dụng để khởi động một Intent và xử lý kết quả trả về từ hoạt động đó. Trong trường hợp này, khi bạn khởi động hoạt động để chọn một hình ảnh từ thư viện
// , kết quả trả về sẽ được xử lý trong khối lambda bạn đã cung cấp.
        imageActivityResultLauncher =
//            ActivityResultContracts.StartActivityForResult() là một hợp đồng (contract) cho biết chúng ta sẽ khởi động một hoạt động với
//            Intent và mong đợi một kết quả trả về.
//            Đăng ký một launcher cho kết quả từ hoạt động StartActivityForResult.
//            ActivityResultContracts.StartActivityForResult() là một hợp đồng (contract) cho biết chúng ta sẽ khởi động một hoạt động với Intent và mong đợi một kết quả trả về.
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                it là đối tượng ActivityResult. it.data là Intent chứa dữ liệu trả về từ hoạt động.
//                it.data?.data là URI của hình ảnh được chọn từ thư viện. Sử dụng toán tử ?. để kiểm tra null (an toàn null).
                imageUri = it.data?.data
                Glide.with(this).load(imageUri).into(binding.imageUser)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAccountBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageCloseUserAccount.setOnClickListener {
            findNavController().navigateUp()
        }


        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showUserLoading()
                    }
                    is Resource.Success -> {
                        hideUserLoading()
                        showUserInformation(it.data!!)
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.updateInfo.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonSave.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonSave.revertAnimation()
                        findNavController().navigateUp()
                    }
                    is Resource.Error -> {
                        binding.buttonSave.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.tvUpdatePassword.setOnClickListener {
            setupBottomSheetDialog {

            }
        }

        binding.buttonSave.setOnClickListener {
            binding.apply {
                val firstName = edFirstName.text.toString().trim()
                val lastName = edLastName.text.toString().trim()
                val email = edEmail.text.toString().trim()
                val user = User(firstName, lastName, email)
                viewModel.updateUser(user, imageUri)
            }
        }

        binding.imageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            "image/*": Điều này chỉ định rằng Intent chỉ nên xử lý các tệp có loại MIME là hình ảnh. Dấu * đại diện cho tất cả các định dạng hình ảnh (ví dụ: JPEG, PNG, GIF, ...).
            intent.type = "image/*"
            imageActivityResultLauncher.launch(intent)
        }

    }

    private fun showUserInformation(data: User) {
        binding.apply {
            Glide.with(this@UserAccountFragment).load(data.imagePath).error(ColorDrawable(Color.BLACK)).into(imageUser)
            edFirstName.setText(data.firstName)
            edLastName.setText(data.lastName)
            edEmail.setText(data.email)
        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.GONE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edLastName.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            buttonSave.visibility = View.VISIBLE
        }
    }

    private fun showUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            imageUser.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
            edFirstName.visibility = View.INVISIBLE
            edLastName.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            tvUpdatePassword.visibility = View.INVISIBLE
            buttonSave.visibility = View.INVISIBLE
        }
    }
}










