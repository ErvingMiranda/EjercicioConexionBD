package org.ezone.pae.ejercicioconexionbd.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.ezone.pae.ejercicioconexionbd.connection.DatabaseConnection;
import org.ezone.pae.ejercicioconexionbd.model.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LibroController {
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtAutor;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtStock;
    @FXML
    private ComboBox<String> cmbCategoria;
    @FXML
    private TableView<Libro> tblLibros;
    @FXML
    private TableColumn<Libro, Integer> colId;
    @FXML
    private TableColumn<Libro, String> colTitulo;
    @FXML
    private TableColumn<Libro, String> colAutor;
    @FXML
    private TableColumn<Libro, String> colCategoria;
    @FXML
    private TableColumn<Libro, Double> colPrecio;
    @FXML
    private TableColumn<Libro, Integer> colStock;

    private final ObservableList<Libro> listaLibros = FXCollections.observableArrayList
    ();

    @FXML
    public void initialize() {
        configurarTabla();
        configurarCombobox();
        cargarLibros();

        // Cada vez que se selecciona una fila del TableView, obtenemos ese objeto
        tblLibros.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        cargarLibroSeleccionado(newValue);
                    }
                });

    }

    //Encargado de mapear los datos en los TextFields
    private void cargarLibroSeleccionado(Libro libro) {
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        cmbCategoria.setValue(libro.getCategoria());
        txtPrecio.setText(String.valueOf(libro.getPrecio()));
        txtStock.setText(String.valueOf(libro.getStock()));
    }

    //Definir el tipo de dato que se escribe en cada celda del tableview
    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
    }

    //Método encargado de cargar datos por defecto del combobox
    private void configurarCombobox() {
        cmbCategoria.getItems().clear();
        cmbCategoria.getItems().addAll("Novela", "Novela corta", "Fantasía", "Cuento infantil", "Tecnología", "Programación",
                "Ciencia fición", "Misterio", "Otros");
    }

    @FXML
    private void cargarLibros() {
        listaLibros.clear();

        String sql = "SELECT * FROM libro";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                //Cada objeto de tipp Libro equivale a una fila de la tabla libros
                Libro libro = new Libro();
                libro.setId(resultSet.getInt("id"));
                libro.setTitulo(resultSet.getString("titulo"));
                libro.setAutor(resultSet.getString("autor"));
                libro.setCategoria(resultSet.getString("categoria"));
                libro.setPrecio(resultSet.getDouble("precio"));
                libro.setStock(resultSet.getInt("stock"));
                listaLibros.add(libro);
            }
            tblLibros.setItems(listaLibros);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @FXML
    private void guardarLibro() {
        if(!validarCampos()){
            return;
        }
        //Consulta SQL a ejecutar
        String sql = "INSERT INTO libro (titulo, autor, categoria, precio, stock) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setString(1, txtTitulo.getText());
            statement.setString(2, txtAutor.getText());
            statement.setString(3, cmbCategoria.getValue());
            statement.setDouble(4, Double.parseDouble(txtPrecio.getText()));
            statement.setInt(5, Integer.parseInt(txtStock.getText()));
            statement.executeUpdate();

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Registro almacenado",
                    "Libro registrado",
                    "El libro se ha almacenado exitosamente"
            );
        }

        catch (SQLException eX) {
            eX.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private boolean validarCampos() {
        if (txtTitulo.getText().isBlank() || txtAutor.getText().isBlank() || cmbCategoria.getValue() == null || txtPrecio.getText().isBlank() || txtStock.getText().isBlank()) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campos incompletos",
                    "Faltan datos",
                    "Debe completar todos los campos antes de guardar."
            );
            return false;
        }
        return true;
    }

    @FXML
    private void limpiarCampos() {
        txtTitulo.clear();
        txtAutor.clear();
        txtPrecio.clear();
        txtStock.clear();
        cmbCategoria.getSelectionModel().clearSelection();
    }

    @FXML
    private void eliminarLibro() {
        Libro libroSeleccionado = tblLibros.getSelectionModel().getSelectedItem();

        if(libroSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Selección requerida",
                    "No hay libro seleccionado",
                    "Seleccione un libro de la tabla"
            );
            return;
        }

        if(!validarCampos()) {
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de que desea eliminar el registro de libro?");

        if(confirmacion.showAndWait().isEmpty() || confirmacion.getResult() != ButtonType.OK) {
            return;
        }

        // Eliminación física - borrar la fila de la tabla
        String sql = "DELETE from libro WHERE id = ?";

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ){
            statement.setInt(1, libroSeleccionado.getId());

            int filasEliminadas = statement.executeUpdate();
            if(filasEliminadas > 0) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro eliminado",
                        "Eliminación completada",
                        "El libro fue eliminado exitosamente"
                );
                limpiarCampos();
                cargarLibros();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public void actualizarLibro() {
        Libro libroSeleccionado = tblLibros.getSelectionModel().getSelectedItem();

        if(libroSeleccionado == null){
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Selección rquerida",
                    "No hay un libro seleccionado",
                    "Seleccione un libro de la tabla"
            );
            return;
        }

        if(!validarCampos()) {
            return;
        }

        String sql = "UPDATE libro SET titulo = ?, autor = ?, categoria = ?, precio = ?, stock = ? WHERE id = ?";

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ){

            statement.setString(1, txtTitulo.getText());
            statement.setString(2, txtAutor.getText());
            statement.setString(3, cmbCategoria.getValue());
            statement.setDouble(4, Double.parseDouble(txtPrecio.getText()));
            statement.setInt(5, Integer.parseInt(txtStock.getText()));
            statement.setInt(6, libroSeleccionado.getId());

            int filasActualizadas = statement.executeUpdate();

            if(filasActualizadas > 0) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro actualizado",
                        "Actualización completada",
                        "El libro fue actualizado exitosamente"
                );
                limpiarCampos();
                cargarLibros();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

