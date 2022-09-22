/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Pada bagian digunakan untuk tempat game yang dimainkan berisi logika permainan.
 */
class GameFragment : Fragment() {

    // Binding objek akan memberikan member baru dengan akses pada tampilan di tata letak game_fragment.xml
    private lateinit var binding: GameFragmentBinding

    // Pada bagian ini untuk membuat ViewModel saat kondisi pertama kali fragmen dibuat.
    // Pada bagian ini apabila fragmen telah dibuat ulang, maka akan menerima member baru dengan GameViewModel yang telah dibuat sama.
    private val viewModel: GameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Pada bagian ini terjadi pengembangan file XML tata letak dan mengembalikan member pada objek yang mengikat.
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pada bagian ini mengatur viewModel untuk mengikat data dan mengakses tata letak.
        // Semua data akan menuju di ViewModel
        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS
        // Pada bagian ini untuk menentukan tampilan fragmen sebagai siklus hidup pengikatan.
        // Pada bagian ini untuk melakukan pengikatan dan mengamati pembaruan LiveData.
        binding.lifecycleOwner = viewLifecycleOwner

        // Untuk menampilkan tombol kirim dan lewati.
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }
    }

    /*
    * Pada bagian ini untuk memeriksa kata-kata pengguna dan memperbarui skor terkini.
    * Pada bagian ini untuk menampilkan kata acak berikutnya.
    * Pada bagian ini untuk menampilkan kata terakhir maka pengguna akan diperlihatkan dialog dengan skor terakhir.
    */
    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()

        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if (!viewModel.nextWord()) {
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }

    /*
     * Pada bagian ini untuk melewati kata saat ini tanpa pengubahan skor.
     * Untuk meningkatkan jumlah kata yang berjalan.
     * After the last word, the user is shown a Dialog with the final score.
     */
    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)
        } else {
            showFinalScoreDialog()
        }
    }

    /*
     * Pada bagian ini untuk membuat dan menampilkan hasil akhir.
     */
    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.congratulations))
                .setMessage(getString(R.string.you_scored, viewModel.score.value))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.exit)) { _, _ ->
                    exitGame()
                }
                .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                    restartGame()
                }
                .show()
    }

    /*
     * Pada bagian ini untuk menganalisa ulang data di ViewModel dan memperbarui tampilan dengan data baru.
     * Pada bagian ini untuk memulai permaianan baru.
     */
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }

    /*
     * Pada bagian ini terjadi untuk keluar dari permainan.
     */
    private fun exitGame() {
        activity?.finish()
    }

    /*
    *Pada bagian ini digunakan untuk mengatur ulang status kesalahan pada bidang teks.
    */
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }
}
