package br.senai.sp.jandira.uploadds3t

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import br.senai.sp.jandira.uploadds3t.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity() {
    //Atributos
    //Representação da classe de manipulação de objetos de views das telas
    private lateinit var binding: ActivityMainBinding

    //Representação da classe de manipulação de endereço (local) de arquivos
    private var imageUri: Uri? = null

    //Referência para acesso e manipulação do Cloud Storage
    private lateinit var storageRef: StorageReference

    //Referência para acesso e manipulação do Cloud FireStore
    private lateinit var firebaseFireStore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initVars()
        registerClickEvents()

    }

    //Inicialização dos atributos do firebase
    private fun initVars() {
        storageRef = FirebaseStorage.getInstance().reference.child("images")
        firebaseFireStore = FirebaseFirestore.getInstance()
    }

    //Lançador de recursos externos da aplicacação
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        imageUri = it
        binding.imageView.setImageURI(it)
    }

    //Tratamento de eventos de click
    private fun registerClickEvents() {
        binding.imageView.setOnClickListener {
            resultLauncher.launch("image/*")
        }

        binding.uploadBtn.setOnClickListener {
            uploadImage()
        }
    }

    //Upload de imagens no firebase
    private fun uploadImage() {
        binding.progressBar.visibility = View.VISIBLE

        storageRef = storageRef.child(System.currentTimeMillis().toString())

//        ------------------ UPLOAD V1 BEGIN ------------------
//        imageUri?.let {
//
//            storageRef.putFile(it).addOnCompleteListener {
//
//                task ->
//                    if (task.isSuccessful){
//                        Toast.makeText(
//                            this,
//                            "Upload realizado com sucesso!",
//                            Toast.LENGTH_LONG).show()
//                    } else {
//                        Toast.makeText(
//                            this,
//                            "Houve um erro ao tentar realizar upload.",
//                            Toast.LENGTH_LONG).show()
//                    }
//
//                binding.progressBar.visibility = View.GONE
//
//            }
//
//        }
//        ------------------ UPLOAD V1 END ------------------


//        ------------------ UPLOAD V2 BEGIN ------------------

        imageUri?.let {
            storageRef.putFile(it).addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    storageRef.downloadUrl.addOnSuccessListener { uri ->

                        val map = HashMap<String, Any>()
                        map["pic"] = uri.toString()

                        firebaseFireStore.collection("images").add(map)
                            .addOnCompleteListener { firestoreTask ->

                                if (firestoreTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Upload realizado com sucesso",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else {
                                    Toast.makeText(
                                        this,
                                        "Erro ao tentar realizar o upload.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                                binding.progressBar.visibility = View.GONE
                                binding.imageView.setImageResource(R.drawable.upload)

                            }
                    }

                } else {

                    Toast.makeText(this, "Erro ao tentar realizar o upload.", Toast.LENGTH_SHORT).show()

                }

                //BARRA DE PROGRESSO DO UPLOAD
                binding.progressBar.visibility = View.GONE

                //TROCA A IMAGEM PARA A IMAGEM PADRÃO
                binding.imageView.setImageResource(R.drawable.upload)

            }
        }


//        ------------------ UPLOAD V2 END ------------------
    }

}