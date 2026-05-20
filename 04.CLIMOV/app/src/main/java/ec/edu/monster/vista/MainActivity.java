package ec.edu.monster.vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import ec.edu.monster.R;
import ec.edu.monster.controller.SoapClient;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerCategoria, spinnerConversion;
    private TextInputEditText etValor;
    private TextView tvResultado, tvUnidad;
    private Button btnConvertir, btnCerrarSesion;
    private CardView cardResultado;
    private FloatingActionButton fabLimpiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerConversion = findViewById(R.id.spinnerConversion);
        etValor = findViewById(R.id.etValor);
        tvResultado = findViewById(R.id.tvResultado);
        tvUnidad = findViewById(R.id.tvUnidad);
        btnConvertir = findViewById(R.id.btnConvertir);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        cardResultado = findViewById(R.id.cardResultado);
        fabLimpiar = findViewById(R.id.fabLimpiar);

        // Configurar spinner de categorías
        String[] categorias = {"Seleccionar Categoría", "Longitud", "Temperatura", "Masa"};
        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(this, R.layout.spinner_item, categorias);
        adapterCat.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterCat);

        // Listener para cambio de categoría
        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizarConversiones(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Listener para botón convertir
        btnConvertir.setOnClickListener(v -> realizarConversion());
        
        // Listener para botón cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
        
        // Listener para botón flotante de limpiar
        fabLimpiar.setOnClickListener(v -> limpiarCampos());
    }
    
    private void cerrarSesion() {
        // Regresar al login
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
    }
    
    private void limpiarCampos() {
        // Limpiar el campo de valor
        etValor.setText("");
        
        // Ocultar el card de resultado
        cardResultado.setVisibility(View.GONE);
        
        // Limpiar los textos de resultado
        tvResultado.setText("");
        tvUnidad.setText("");
        
        // Mensaje de confirmación
        Toast.makeText(this, "Campos limpiados", Toast.LENGTH_SHORT).show();
    }

    private void actualizarConversiones(int categoria) {
        String[] conversiones;
        switch (categoria) {
            case 1: // Longitud
                conversiones = new String[]{"Seleccionar Conversión", "Centímetros a Pulgadas", "Pulgadas a Centímetros"};
                break;
            case 2: // Temperatura
                conversiones = new String[]{"Seleccionar Conversión", "Celsius a Fahrenheit", "Fahrenheit a Celsius"};
                break;
            case 3: // Masa
                conversiones = new String[]{"Seleccionar Conversión", "Kilogramos a Libras", "Libras a Kilogramos"};
                break;
            default:
                conversiones = new String[]{"Primero seleccione una categoría"};
                break;
        }
        ArrayAdapter<String> adapterConv = new ArrayAdapter<>(this, R.layout.spinner_item, conversiones);
        adapterConv.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerConversion.setAdapter(adapterConv);
    }

    private void realizarConversion() {
        String valor = etValor.getText().toString().trim();
        if (valor.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese un valor", Toast.LENGTH_SHORT).show();
            return;
        }

        int categoria = spinnerCategoria.getSelectedItemPosition();
        int conversion = spinnerConversion.getSelectedItemPosition();

        if (categoria == 0 || conversion == 0) {
            Toast.makeText(this, "Por favor seleccione categoría y conversión", Toast.LENGTH_SHORT).show();
            return;
        }

        String metodo = "";
        String parametro = "";
        String unidadResultado = "";

        // Determinar método SOAP según selección
        if (categoria == 1) { // Longitud
            if (conversion == 1) {
                metodo = "centimetrosAPulgadas";
                parametro = "centimetros";
                unidadResultado = "in";
            } else {
                metodo = "pulgadasACentimetros";
                parametro = "pulgadas";
                unidadResultado = "cm";
            }
        } else if (categoria == 2) { // Temperatura
            if (conversion == 1) {
                metodo = "celsiusAFahrenheit";
                parametro = "celsius";
                unidadResultado = "°F";
            } else {
                metodo = "fahrenheitACelsius";
                parametro = "fahrenheit";
                unidadResultado = "°C";
            }
        } else if (categoria == 3) { // Masa
            if (conversion == 1) {
                metodo = "kilogramosALibras";
                parametro = "kilogramos";
                unidadResultado = "lb";
            } else {
                metodo = "librasAKilogramos";
                parametro = "libras";
                unidadResultado = "kg";
            }
        }

        convertir(metodo, parametro, valor, unidadResultado);
    }

    private void convertir(String metodo, String parametro, String valorStr, String unidad) {
        new Thread(() -> {
            try {
                float valor = Float.parseFloat(valorStr);
                float resultado = SoapClient.callConversion(metodo, parametro, valor);
                runOnUiThread(() -> {
                    cardResultado.setVisibility(View.VISIBLE);
                    tvResultado.setText(String.format("%.2f", resultado));
                    tvUnidad.setText(unidad);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    cardResultado.setVisibility(View.VISIBLE);
                    tvResultado.setText("Error: " + e.getMessage());
                    tvUnidad.setText("");
                });
            }
        }).start();
    }
}
