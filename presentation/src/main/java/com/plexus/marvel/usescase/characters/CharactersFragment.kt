package com.plexus.marvel.usescase.characters

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.plexus.data.storage.database.LocalRepository
import com.plexus.domain.Character
import com.plexus.marvel.R
import com.plexus.marvel.application.App
import com.plexus.marvel.base.BaseFragment
import com.plexus.marvel.databinding.FragmentCharactersBinding
import com.plexus.marvel.usescase.characterdetail.CharacterDetailFragment
import com.plexus.marvel.usescase.home.HomeFragment
import com.plexus.marvel.utils.Constants.Companion.EXTRA_CHARACTER_ID
import kotlinx.coroutines.*

class CharactersFragment :
    BaseFragment<CharactersViewModel, FragmentCharactersBinding>(CharactersViewModel::class.java) {

    private lateinit var adapter: CharactersAdapter
    private var characters = arrayListOf<Character>()
    private var offset = 1

    override fun getLayoutRes(): Int {
        return R.layout.fragment_characters
    }

    override fun viewCreated(view: View?) {
        mBinding.viewModel = viewModel
        mBinding.action = this
        mBinding.lifecycleOwner = this
        initActions()
        initList()
        if (characters.isEmpty()) getFromDatabase()
    }

    private fun getFromDatabase() {
        CoroutineScope(Job()).launch(Dispatchers.IO) {
            characters.addAll(
                LocalRepository().getAllCharacters(App().getDatabase(requireContext()))
            )
            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }
    }


    private fun initActions() {
        viewModel.goToCharacterDetail = ::goToCharacterDetail
        viewModel.onCharactersLoaded = ::onCharactersLoaded
        viewModel.onErrorLoadingCharacters = ::onErrorLoadingCharacters
    }


    private fun initList() {
        adapter = CharactersAdapter(requireContext(), characters, viewModel)
        val layoutManager =
            GridLayoutManager(requireContext(), 1, GridLayoutManager.VERTICAL, false)
        mBinding.rvCharacters.layoutManager = layoutManager
        mBinding.rvCharacters.adapter = adapter
        setRecyclerListener()
    }

    private fun goToCharacterDetail(id: Int) {
        navigator.navigate(CharacterDetailFragment(), true, Bundle().apply {
            putInt(EXTRA_CHARACTER_ID, id)
        })
    }

    private fun onErrorLoadingCharacters() {
        viewModel.showErrorButton.value = true
        showErrorSnackBar(getString(R.string.splash_error_message))
    }

    private fun onCharactersLoaded(characters: ArrayList<Character>) {
        offset++
        this.characters.addAll(characters)
        adapter.notifyDataSetChanged()
    }

    private fun setRecyclerListener() {
        mBinding.rvCharacters.addOnScrollListener(object : PaginationListener(mBinding.rvCharacters.layoutManager as GridLayoutManager) {
            override fun loadMoreItems() {
                viewModel.getAllCharacters(offset)
            }
            override fun isLoading(): Boolean = viewModel.loading.value == true
        })
    }
}