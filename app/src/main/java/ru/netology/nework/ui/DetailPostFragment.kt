package ru.netology.nework.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.CardPostBinding
import ru.netology.nework.databinding.FragmentNewPostBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.utils.AndroidUtils
import ru.netology.nework.utils.AndroidUtils.getFile
import ru.netology.nework.utils.MediaLifecycleObserver
import ru.netology.nework.utils.StringArg
import ru.netology.nework.utils.loadAttachmentView
import ru.netology.nework.viewmodel.PostViewModel
import java.io.FileNotFoundException
import java.io.IOException

@AndroidEntryPoint
class DetailPostFragment : Fragment() {


    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = CardPostBinding.inflate(
            inflater,
            container,
            false
        )



        return binding.root
    }
}