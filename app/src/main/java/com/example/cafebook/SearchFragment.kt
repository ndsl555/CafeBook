package com.example.cafebook

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RatingBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cafebook.Entity.CafeShopEntity
import com.example.cafebook.Extension.launchAndRepeatWithViewLifecycle
import com.example.cafebook.ViewModels.SearchViewModel
import com.example.cafebook.databinding.FragmentSearchBinding
import com.example.cafebook.ui.Adapter.CafeShopListAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()

    private val cafeShopListAdapter by lazy { CafeShopListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("FragmentCheck", "SFragment loaded")

        initView()
        initParam()
    }

    private fun initView() {
        setupRecyclerView()
        setupSearchView()
        setupFilters()
    }

    private fun initParam() {
        observeUiState()
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerView

        val dividerItemDecoration =
            DividerItemDecoration(
                recyclerView.context,
                DividerItemDecoration.VERTICAL,
            )
        recyclerView.addItemDecoration(dividerItemDecoration)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter =
            cafeShopListAdapter.apply {
                onCafeClick = ::onCafeClick
            }
    }

    private fun onCafeClick(cafe: CafeShopEntity) {
        showDetailDialog(cafe)
    }

    private fun showDetailDialog(cafe: CafeShopEntity) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(cafe.name)

        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_cafe_detail, null)
        builder.setView(dialogView)

        // RatingBars
        dialogView.findViewById<RatingBar>(R.id.ratingBarWifi).rating = cafe.wifi
        dialogView.findViewById<RatingBar>(R.id.ratingBarSeat).rating = cafe.seat
        dialogView.findViewById<RatingBar>(R.id.ratingBarQuiet).rating = cafe.quiet
        dialogView.findViewById<RatingBar>(R.id.ratingBarTasty).rating = cafe.tasty
        dialogView.findViewById<RatingBar>(R.id.ratingBarCheap).rating = cafe.cheap
        dialogView.findViewById<RatingBar>(R.id.ratingBarMusic).rating = cafe.music

        // TextViews
        dialogView.findViewById<TextView>(R.id.textViewAddress).text = cafe.address
        dialogView.findViewById<TextView>(R.id.textViewLimitedTime).text = cafe.limitedTime
        dialogView.findViewById<TextView>(R.id.textViewSocket).text = cafe.socket
        dialogView.findViewById<TextView>(R.id.textViewStandingDesk).text = cafe.standingDesk
        dialogView.findViewById<TextView>(R.id.textViewMrt).text = cafe.mrt
        dialogView.findViewById<TextView>(R.id.textViewOpenTime).text = cafe.openTime

        // 確定按鈕
        builder.setNeutralButton("確定") { dialog, _ ->
            dialog.dismiss()
        }

        // 加入口袋名單
        builder.setPositiveButton("加入口袋名單") { dialog, _ ->
            viewModel.addCafeToPocket(cafe)
            dialog.dismiss()
        }

        // 導航按鈕
        builder.setNegativeButton("導航") { _, _ ->
            val gmmIntentUri =
                Uri.parse("geo:${cafe.latitude},${cafe.longitude}?q=${Uri.encode(cafe.name)}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
            } else {
                Toast.makeText(requireContext(), "無法開啟地圖應用程式", Toast.LENGTH_SHORT).show()
            }
        }

        builder.create().show()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { viewModel.setSearchQuery(it) }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { viewModel.setSearchQuery(it) }
                    return true
                }
            },
        )
    }

    private fun setupFilters() {
        val cityNameMap =
            mapOf(
                "taipei" to "台北",
                "newtaipei" to "新北",
                "taoyuan" to "桃園",
                "hsinchu" to "新竹",
                "miaoli" to "苗栗",
                "taichung" to "台中",
                "changhua" to "彰化",
                "nantou" to "南投",
                "yunlin" to "雲林",
                "chiayi" to "嘉義",
                "tainan" to "台南",
                "kaohsiung" to "高雄",
                "pingtung" to "屏東",
                "yilan" to "宜蘭",
                "hualien" to "花蓮",
                "taitung" to "台東",
                "keelung" to "基隆",
                "penghu" to "澎湖",
                "kinmen" to "金門",
                "lienchiang" to "連江",
            )

        launchAndRepeatWithViewLifecycle {
            viewModel.uiState.collect { state ->
                _binding?.let { binding ->
                    // Spinner 城市
                    val originalCities = state.cities.sorted()
                    val cities = listOf(null) + originalCities
                    val displayCities = listOf("全部") + originalCities.map { cityNameMap[it] ?: it }

                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, displayCities)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.citySpinner.adapter = adapter

                    binding.citySpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long,
                            ) {
                                val selectedCity = cities[position] // null 或城市英文名
                                viewModel.setCity(selectedCity)
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }

                    // SeekBar
                    binding.seekBarWifi.setOnSeekBarChangeListener(seekBarListener { viewModel.setWifiThreshold(it) })
                    binding.seekBarSeat.setOnSeekBarChangeListener(seekBarListener { viewModel.setSeatThreshold(it) })
                    binding.seekBarQuiet.setOnSeekBarChangeListener(seekBarListener { viewModel.setQuietThreshold(it) })
                    binding.seekBarTasty.setOnSeekBarChangeListener(seekBarListener { viewModel.setTastyThreshold(it) })
                    binding.seekBarCheap.setOnSeekBarChangeListener(seekBarListener { viewModel.setCheapThreshold(it) })
                    binding.seekBarMusic.setOnSeekBarChangeListener(seekBarListener { viewModel.setMusicThreshold(it) })

                    // 篩選按鈕
                    binding.buttonApply.setOnClickListener {
                        binding.drawerLayout.closeDrawers()
                    }

                    // 清除按鈕
                    binding.buttonClear.setOnClickListener {
                        binding.seekBarWifi.progress = 0
                        binding.seekBarSeat.progress = 0
                        binding.seekBarQuiet.progress = 0
                        binding.seekBarTasty.progress = 0
                        binding.seekBarCheap.progress = 0
                        binding.seekBarMusic.progress = 0
                        binding.citySpinner.setSelection(0)
                        viewModel.setSearchQuery("")
                    }
                }
            }
        }
    }

    private fun seekBarListener(onChanged: (Int) -> Unit): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean,
            ) {
                onChanged(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
    }

    private fun observeUiState() {
        launchAndRepeatWithViewLifecycle {
            launch {
                viewModel.filteredCafes.collect { cafes ->
                    cafeShopListAdapter.cafes = cafes
                    binding.progressBar.visibility = View.GONE
                }
            }

            launch {
                viewModel.uiState.collect { state ->
                    binding.progressBar.visibility =
                        if (state.isLoading) View.VISIBLE else View.GONE
                    state.errorMessage?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
