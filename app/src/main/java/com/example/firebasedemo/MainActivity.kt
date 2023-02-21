package com.example.firebasedemo

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        auth.signOut()  // sign out current user

        btnRegister.setOnClickListener{
            registerUser()
        }

        btnLogin.setOnClickListener{
            LoginUser()
                val intent = Intent(this, FirstActivity::class.java)
                startActivity(intent)

        }

        btnUpdate.setOnClickListener{
            updateProfile()
        }

    }
//
//    override fun onStart() {  // if the user restart app user email id, password  logged in firebase
//        super.onStart()
//        notRegisterandLogin()
//    }

    private fun registerUser(){
        val email = etEmailRegister.text.toString()
        val password = etPasswordRegister.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty())
        {
            CoroutineScope(Dispatchers.IO).launch {

                // IO -> Fetching data from the database is an IO operation, which is done on the IO thread.
                try {
                    auth.createUserWithEmailAndPassword(email,password).await()  // create the email and password and await until when our registraion will be completed
                    withContext(Dispatchers.Main){ // update the login state to user
                        checkRegisterdInState()
                    }
                }
                catch (e: Exception){ // catch will throw the exception or wrong thing like when we register
                    withContext(Dispatchers.Main){

                        //withContext -> switch the context

                        // main -> // main -> It is mostly used when we need to perform the UI operations
                        // within the coroutine,as UI can only be changed from the main thread(also called the UI thread).

                        Toast.makeText(this@MainActivity, e.message,Toast.LENGTH_SHORT).show()
                    }  //  e.message -> show error message

                }
            }


        }
    }

    private fun LoginUser(){
        val email = etEmailLogin.text.toString()
        val password = etPasswordLogin.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty())
        {
            CoroutineScope(Dispatchers.IO).launch {

                // IO -> Fetching data from the database is an IO operation, which is done on the IO thread.
                try {
                    auth.signInWithEmailAndPassword(email,password).await()  // sign up the email and password and await until when our Login will be completed
                    withContext(Dispatchers.Main){ // update the login state to user
                        checkLoggedInState()
                    }
                }
                catch (e: Exception){ // catch will throw the exception or wrong thing like when we register
                    withContext(Dispatchers.Main){
                        // main -> // main -> It is mostly used when we need to perform the UI operations
                        // within the coroutine,as UI can only be changed from the main thread(also called the UI thread).

                        Toast.makeText(this@MainActivity, e.message,Toast.LENGTH_SHORT).show()
                    }  //  e.message -> show error message

                }
            }


        }
    }

    private fun checkRegisterdInState() {
        if (auth.currentUser == null)
        {
            tvLoggedIn.text = "You are not Registerd In"
        }
        else
        {
            tvLoggedIn.text = "You are Registerd In!"
            Toast.makeText(this@MainActivity, "You are Registerd In!",Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLoggedInState() {
        val user = auth.currentUser
        if (user == null)

        {
            tvLoggedIn.text = "You are not logged In"
        }
        else
        {
            tvLoggedIn.text = "You are logged In!"
            Toast.makeText(this@MainActivity, "You are logged In!",Toast.LENGTH_SHORT).show()
            etUsername.setText(user.displayName)
            ivProfilePicture.setImageURI(user.photoUrl)
        }
    }

//    private fun notRegisterandLogin() {
//
//            tvLoggedIn.text = "Not Registerd and logged In"
//
//        Toast.makeText(this@MainActivity, "Not Registerd and logged In",Toast.LENGTH_SHORT).show()
//    }

    private fun updateProfile(){

        auth.currentUser?.let {user ->
            val username = etUsername.text.toString()
            val photoURI = Uri.parse("android.resource://$packageName/${R.drawable.shazam_logo}")
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(photoURI)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(profileUpdates).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                        Toast.makeText(this@MainActivity,"Successfully profile Updated",Toast.LENGTH_SHORT).show()
                    }
                }catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }



}