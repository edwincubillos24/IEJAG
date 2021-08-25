package com.cubidevs.iejag

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.cubidevs.iejag.databinding.FragmentRegisterBinding
import com.cubidevs.iejag.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        val root: View = binding.root

        auth = Firebase.auth

        binding.registerButton.setOnClickListener {
            registerUser()
        }

        return root
    }

    private fun registerUser() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val reppassword = binding.repPasswordEditText.text.toString()

        if (password != reppassword){
            Toast.makeText(requireContext(), "Las contraseñas deben ser iguales", Toast.LENGTH_SHORT).show()
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        Log.d("register", "createUserWithEmail:success")
                        Toast.makeText(requireContext(), "Registro Exítoso",
                            Toast.LENGTH_SHORT).show()
                        createUser(email)
                    } else {
                        var msg=""
                        if (task.exception?.localizedMessage == "The email address is badly formatted.")
                            msg = "El correo está mal escrito"
                        else if (task.exception?.localizedMessage == "The given password is invalid. [ Password should be at least 6 characters ]")
                            msg = "La contraseña debe tener mínimo 6 caracteres"
                        else if (task.exception?.localizedMessage == "The email address is already in use by another account.")
                            msg = "Ya existe una cuenta con ese correo electrónico"
                        Log.w("register", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(requireContext(), msg,
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun createUser(email: String) {
        val id = auth.currentUser?.uid
        id?.let { id ->
            val user = User(id = id, email = email, role = "Profesor")
            val db = Firebase.firestore
            db.collection("users").document(id)
                .set(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("createInDB", "DocumentSnapshot added with ID: ${id}")
                    findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
                }
                .addOnFailureListener { e ->
                    Log.w("createInDB", "Error adding document", e)
                }
        }
    }
}