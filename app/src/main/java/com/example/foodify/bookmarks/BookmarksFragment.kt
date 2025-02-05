package com.example.foodify.bookmarks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foodify.R
import com.example.foodify.databinding.FragmentBookmarksFargmentBinding


class BookmarksFragment : Fragment() {
    private lateinit var _binding: FragmentBookmarksFargmentBinding
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarksFargmentBinding.inflate(inflater, container, false)
        return binding.root
    }


}