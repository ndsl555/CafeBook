package com.example.cafebook

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RatingBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cafebook.Entity.CafeShopEntity
import com.example.cafebook.ViewModels.PocketViewModel
import com.example.cafebook.databinding.FragmentPocketBinding
import com.example.cafebook.ui.Adapter.CafeShopListAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PocketFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentPocketBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PocketViewModel by viewModel() // ✅ 使用 Koin 的方式

    private val pocketAdapter by lazy { CafeShopListAdapter() }

    // 城市中英文對照 Map
    private val cityNameMap =
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPocketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        Log.d("FragmentCheck", "PocketFragment loaded")
        // 設定 Toolbar 為 ActionBar
setHasOptionsMenu(true)
        viewModel.getuniCity()
        initView()
        initParam()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)  // 你的 menu 資源檔
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initView() {
        binding.fabClearAllData.setOnClickListener(this)
        binding.fabClearAllData.setImageResource(R.drawable.baseline_delete_24)
        binding.buttonApplyPocket.setOnClickListener(this)
        binding.buttonClearPocket.setOnClickListener(this)
        setHasOptionsMenu(true)

        setupRecyclerView()
        setupCitySpinner()
        setupSeekBars()
    }

    private fun initParam() {
        observeViewModel()
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerViewPocket

        val dividerItemDecoration =
            DividerItemDecoration(
                recyclerView.context,
                DividerItemDecoration.VERTICAL,
            )
        recyclerView.addItemDecoration(dividerItemDecoration)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter =
            pocketAdapter.apply {
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
        builder.setPositiveButton("從口袋名單移除") { dialog, _ ->
            viewModel.deleteCafeShop(cafe.name)
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

    private fun setupCitySpinner() {
        lifecycleScope.launch {
            viewModel.cityList.collect { cities ->
                val displayCities = listOf("全部") + cities.map { cityNameMap[it] ?: it }
                val adapter =
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        displayCities,
                    )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.citySpinnerPocket.adapter = adapter

                binding.citySpinnerPocket.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long,
                        ) {
                            val selectedCity =
                                if (position == 0) null else cities.getOrNull(position - 1)
                            viewModel.setCity(selectedCity)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
            }
        }
    }

    private fun setupSeekBars() {
        binding.seekBarWifiPocket.setOnSeekBarChangeListener(seekBarListener { viewModel.setWifi(it) })
        binding.seekBarSeatPocket.setOnSeekBarChangeListener(seekBarListener { viewModel.setSeat(it) })
        binding.seekBarQuietPocket.setOnSeekBarChangeListener(
            seekBarListener {
                viewModel.setQuiet(
                    it,
                )
            },
        )
        binding.seekBarTastyPocket.setOnSeekBarChangeListener(
            seekBarListener {
                viewModel.setTasty(
                    it,
                )
            },
        )
        binding.seekBarCheapPocket.setOnSeekBarChangeListener(
            seekBarListener {
                viewModel.setCheap(
                    it,
                )
            },
        )
        binding.seekBarMusicPocket.setOnSeekBarChangeListener(
            seekBarListener {
                viewModel.setMusic(
                    it,
                )
            },
        )
    }

    private fun seekBarListener(onChange: (Int) -> Unit): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean,
            ) {
                onChange(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.filteredCafes.collect { cafes ->
                pocketAdapter.cafes = cafes
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == R.id.menu_item_show_average) {
            showAverageDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAverageDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("名單中各項平均")
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.habbit, null)
        builder.setView(dialogView)

        val ratingBarWifi = dialogView.findViewById<RatingBar>(R.id.personalratingBarWifi)
        val ratingBarSeat = dialogView.findViewById<RatingBar>(R.id.personalratingBarSeat)
        val ratingBarQuiet = dialogView.findViewById<RatingBar>(R.id.personalratingBarQuiet)
        val ratingBarTasty = dialogView.findViewById<RatingBar>(R.id.personalratingBarTasty)
        val ratingBarCheap = dialogView.findViewById<RatingBar>(R.id.personalratingBarCheap)
        val ratingBarMusic = dialogView.findViewById<RatingBar>(R.id.personalratingBarMusic)

        lifecycleScope.launch {
            viewModel.bag.collect { averages ->
                ratingBarWifi.rating = averages.avgWifi.toFloat()
                ratingBarSeat.rating = averages.avgSeat.toFloat()
                ratingBarQuiet.rating = averages.avgQuiet.toFloat()
                ratingBarTasty.rating = averages.avgTasty.toFloat()
                ratingBarCheap.rating = averages.avgCheap.toFloat()
                ratingBarMusic.rating = averages.avgMusic.toFloat()
            }
        }

        builder.setNeutralButton("確定") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabClearAllData -> {
                clearDB()
            }

            R.id.buttonApplyPocket -> binding.drawerLayoutPocket.closeDrawers()
            R.id.buttonClearPocket -> reset()
        }
    }

    private fun clearDB() {
        AlertDialog.Builder(requireContext())
            .setMessage("確定清除所有資料?")
            .setPositiveButton("確定") { _, _ -> viewModel.deleteAllCafe() }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun reset() {
        binding.seekBarWifiPocket.progress = 0
        binding.seekBarSeatPocket.progress = 0
        binding.seekBarQuietPocket.progress = 0
        binding.seekBarTastyPocket.progress = 0
        binding.seekBarCheapPocket.progress = 0
        binding.seekBarMusicPocket.progress = 0
        binding.citySpinnerPocket.setSelection(0)
        viewModel.clearFilters()
        binding.drawerLayoutPocket.closeDrawers()
    }
}
