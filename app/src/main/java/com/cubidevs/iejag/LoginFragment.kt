package com.cubidevs.iejag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cubidevs.iejag.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        val root: View = binding.root

        auth = Firebase.auth

        binding.loginButton.setOnClickListener {
            signIn()
        }

        binding.registerTextView.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        return root
    }

    private fun signIn() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Login", "signInWithEmail:success")
                    val user = auth.currentUser
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainFragment())
                } else {
                    var msg = ""
                    if (task.exception?.localizedMessage == "The email address is badly formatted.")
                        msg = "El correo está mal escrito"
                    else if (task.exception?.localizedMessage == "There is no user record corresponding to this identifier. The user may have been deleted.")
                        msg = "No existe una cuenta con ese correo electrónico"
                    else if (task.exception?.localizedMessage == "The password is invalid or the user does not have a password.")
                        msg = "Correo o contraseña invalida"
                    Log.w("Login", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(), msg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}