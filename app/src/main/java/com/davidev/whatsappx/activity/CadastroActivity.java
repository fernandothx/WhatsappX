package com.davidev.whatsappx.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.davidev.whatsappx.R;
import com.davidev.whatsappx.config.ConfiguracaoFirebase;
import com.davidev.whatsappx.helper.Base64Custom;
import com.davidev.whatsappx.helper.UsuarioFirebase;
import com.davidev.whatsappx.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoNome, campoEmail, campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome  = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);

    }

    // Após validado, uma tarefa tenta executar o cadastro no FirebaseAuth.
    public void cadastrarUsuario(final Usuario usuario){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, task -> {

            if ( task.isSuccessful() ){

                Toast.makeText(CadastroActivity.this,
                        "Sucesso ao cadastrar usuário!",
                        Toast.LENGTH_SHORT).show();
                UsuarioFirebase.atualizarNomeUsuario( usuario.getNome() );
                finish();

                try {

                    String identificadorUsuario = Base64Custom.codificarBase64( usuario.getEmail() );
                    usuario.setId( identificadorUsuario );
                    usuario.salvar();

                }catch (Exception e){
                    e.printStackTrace();
                }

            }else {

                String excecao = "";
                try {
                    throw Objects.requireNonNull(task.getException());
                }catch ( FirebaseAuthWeakPasswordException e){
                    excecao = "Digite uma senha mais forte!";
                }catch ( FirebaseAuthInvalidCredentialsException e){
                    excecao = "Por favor, digite um e-mail válido";
                }catch ( FirebaseAuthUserCollisionException e){
                    excecao = "Este conta já foi cadastrada";
                }catch (Exception e){
                    excecao = "Erro ao cadastrar usuário: "  + e.getMessage();
                    e.printStackTrace();
                }

                Toast.makeText(CadastroActivity.this,
                        excecao,
                        Toast.LENGTH_SHORT).show();

            }

        });

    }

    // Valida se o usuario preencheu o formulario adequadamente para criar conta.
    public void validarCadastroUsuario(View view){

        //Recuperar textos dos campos
        String textoNome  = Objects.requireNonNull(campoNome.getText()).toString();
        String textoEmail = Objects.requireNonNull(campoEmail.getText()).toString();
        String textoSenha = Objects.requireNonNull(campoSenha.getText()).toString();

        if( !textoNome.isEmpty() ){//verifica nome
            if( !textoEmail.isEmpty() ){//verifica e-mail
                if ( !textoSenha.isEmpty() ){

                    Usuario usuario = new Usuario();
                    usuario.setNome( textoNome );
                    usuario.setEmail( textoEmail );
                    usuario.setSenha( textoSenha );

                    cadastrarUsuario( usuario );

                }else {
                    Toast.makeText(CadastroActivity.this,
                            "Preencha a senha!",
                            Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(CadastroActivity.this,
                        "Preencha o email!",
                        Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(CadastroActivity.this,
                    "Preencha o nome!",
                    Toast.LENGTH_SHORT).show();
        }

    }
}

