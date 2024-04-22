package com.woodgrove.android.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.woodgrove.android.databinding.FragmentMyMusicBinding

class MyMusicFragment : Fragment() {
    private lateinit var _binding: FragmentMyMusicBinding
    private val binding get() = _binding

    companion object {
        private val TAG = MyMusicFragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyMusicBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }
}