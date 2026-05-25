package com.example.xiaonenghui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ServicesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_services, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchInput = view.findViewById<EditText>(R.id.input_search_services)
        val searchButton = view.findViewById<View>(R.id.button_search_services)
        val notificationButton = view.findViewById<View>(R.id.button_notifications)

        val chipAll = view.findViewById<TextView>(R.id.chip_all)
        val chipTutoring = view.findViewById<TextView>(R.id.chip_tutoring)
        val chipCreative = view.findViewById<TextView>(R.id.chip_creative)
        val chipErrands = view.findViewById<TextView>(R.id.chip_errands)
        val chipProgramming = view.findViewById<TextView>(R.id.chip_programming)

        val cardMath = view.findViewById<MaterialCardView>(R.id.card_service_math)
        val cardPpt = view.findViewById<MaterialCardView>(R.id.card_service_ppt)
        val cardDebug = view.findViewById<MaterialCardView>(R.id.card_service_debug)
        val cardDelivery = view.findViewById<MaterialCardView>(R.id.card_service_delivery)

        val bookMath = view.findViewById<MaterialButton>(R.id.button_book_math)
        val bookPpt = view.findViewById<MaterialButton>(R.id.button_book_ppt)
        val bookDebug = view.findViewById<MaterialButton>(R.id.button_book_debug)
        val bookDelivery = view.findViewById<MaterialButton>(R.id.button_book_delivery)

        val mathTitle = view.findViewById<TextView>(R.id.text_service_math_title).text.toString()
        val pptTitle = view.findViewById<TextView>(R.id.text_service_ppt_title).text.toString()
        val debugTitle = view.findViewById<TextView>(R.id.text_service_debug_title).text.toString()
        val deliveryTitle = view.findViewById<TextView>(R.id.text_service_delivery_title).text.toString()

        searchButton.setOnClickListener {
            submitSearch(searchInput.text.toString())
        }

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                submitSearch(searchInput.text.toString())
                true
            } else {
                false
            }
        }

        notificationButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.home_notifications))
                .setMessage(getString(R.string.home_no_notifications))
                .setPositiveButton(getString(R.string.home_close), null)
                .show()
        }

        chipAll.setOnClickListener { showFilterToast(getString(R.string.services_filter_all)) }
        chipTutoring.setOnClickListener { showFilterToast(getString(R.string.services_filter_tutoring)) }
        chipCreative.setOnClickListener { showFilterToast(getString(R.string.services_filter_creative)) }
        chipErrands.setOnClickListener { showFilterToast(getString(R.string.services_filter_errands)) }
        chipProgramming.setOnClickListener { showFilterToast(getString(R.string.services_filter_programming)) }

        cardMath.setOnClickListener { showCardToast(mathTitle) }
        cardPpt.setOnClickListener { showCardToast(pptTitle) }
        cardDebug.setOnClickListener { showCardToast(debugTitle) }
        cardDelivery.setOnClickListener { showCardToast(deliveryTitle) }

        bookMath.setOnClickListener { showBookToast(mathTitle) }
        bookPpt.setOnClickListener { showBookToast(pptTitle) }
        bookDebug.setOnClickListener { showBookToast(debugTitle) }
        bookDelivery.setOnClickListener { showBookToast(deliveryTitle) }
    }

    private fun submitSearch(rawKeyword: String) {
        val keyword = rawKeyword.trim()
        if (keyword.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.home_search_empty), Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(requireContext(), getString(R.string.services_search_toast, keyword), Toast.LENGTH_SHORT).show()
    }

    private fun showFilterToast(label: String) {
        Toast.makeText(requireContext(), getString(R.string.services_filter_toast, label), Toast.LENGTH_SHORT).show()
    }

    private fun showCardToast(title: String) {
        Toast.makeText(requireContext(), getString(R.string.services_card_toast, title), Toast.LENGTH_SHORT).show()
    }

    private fun showBookToast(title: String) {
        Toast.makeText(requireContext(), getString(R.string.services_book_toast, title), Toast.LENGTH_SHORT).show()
    }
}
