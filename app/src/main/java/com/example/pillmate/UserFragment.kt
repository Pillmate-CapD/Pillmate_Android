package com.example.pillmate

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class UserFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the TextView (or Button) for logout
        val logoutTextView: TextView = view.findViewById(R.id.tv_logout)

        // Set click listener on the logout TextView
        logoutTextView.setOnClickListener {
            // Show the logout confirmation dialog
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        // Create an AlertDialog for logout confirmation
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("필메이트에서 로그아웃하시겠어요?")
            .setPositiveButton("로그아웃") { dialog, id ->
                // User clicked Logout, perform logout action
                logout()
            }
            .setNegativeButton("취소") { dialog, id ->
                // User cancelled the dialog, just dismiss it
                dialog.dismiss()
            }

        // Show the dialog
        val dialog = builder.create()
        dialog.show()

        // Customize button text colors
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(android.R.color.holo_red_light))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(android.R.color.black))
    }

    private fun logout() {
        // Perform the logout logic and redirect to the LoginActivity
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}