package com.laboratorioBases.gasolineras.ingesta;

/**
 * Representa una fila unificada del CSV de carburantes.
 */
public class RegistroCsv {

    private String tipoServicio;       // TERRESTRE / MARITIMA
    private String provincia;
    private String municipio;
    private String localidad;
    private String codigoPostal;
    private String direccion;
    private String margen;
    private String longitud;
    private String latitud;
    private String tomaDatos;
    private String rotulo;
    private String tipoVenta;
    private String rem;
    private String horario;
    private String precioGasolina95E5;
    private String precioGasolina95E10;
    private String precioGasoleoA;
    private String precioGasoleoB;
    private String precioGasoleoMar;

    // Cabecera del CSV final
    public static final String CABECERA_CSV =
            "tipo_servicio;provincia;municipio;localidad;codigo_postal;direccion;" +
                    "margen;longitud;latitud;toma_datos;" +
                    "rotulo;tipo_venta;rem;horario;" +
                    "precio_gasolina_95_e5;precio_gasolina_95_e10;" +
                    "precio_gasoleo_a;precio_gasoleo_b;precio_gasoleo_mar";

    // ==== Getters y setters ====

    public String getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(String tipoServicio) { this.tipoServicio = tipoServicio; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }

    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getMargen() { return margen; }
    public void setMargen(String margen) { this.margen = margen; }

    public String getLongitud() { return longitud; }
    public void setLongitud(String longitud) { this.longitud = longitud; }

    public String getLatitud() { return latitud; }
    public void setLatitud(String latitud) { this.latitud = latitud; }

    public String getTomaDatos() { return tomaDatos; }
    public void setTomaDatos(String tomaDatos) { this.tomaDatos = tomaDatos; }

    public String getRotulo() { return rotulo; }
    public void setRotulo(String rotulo) { this.rotulo = rotulo; }

    public String getTipoVenta() { return tipoVenta; }
    public void setTipoVenta(String tipoVenta) { this.tipoVenta = tipoVenta; }

    public String getRem() { return rem; }
    public void setRem(String rem) { this.rem = rem; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getPrecioGasolina95E5() { return precioGasolina95E5; }
    public void setPrecioGasolina95E5(String precioGasolina95E5) { this.precioGasolina95E5 = precioGasolina95E5; }

    public String getPrecioGasolina95E10() { return precioGasolina95E10; }
    public void setPrecioGasolina95E10(String precioGasolina95E10) { this.precioGasolina95E10 = precioGasolina95E10; }

    public String getPrecioGasoleoA() { return precioGasoleoA; }
    public void setPrecioGasoleoA(String precioGasoleoA) { this.precioGasoleoA = precioGasoleoA; }

    public String getPrecioGasoleoB() { return precioGasoleoB; }
    public void setPrecioGasoleoB(String precioGasoleoB) { this.precioGasoleoB = precioGasoleoB; }

    public String getPrecioGasoleoMar() { return precioGasoleoMar; }
    public void setPrecioGasoleoMar(String precioGasoleoMar) { this.precioGasoleoMar = precioGasoleoMar; }

    /**
     * Devuelve la línea del CSV con todos los campos limpios y separados por ';'
     */
    public String toCsvLine() {
        return String.join(";",
                TextoUtil.limpiarCampoCsv(tipoServicio),
                TextoUtil.limpiarCampoCsv(provincia),
                TextoUtil.limpiarCampoCsv(municipio),
                TextoUtil.limpiarCampoCsv(localidad),
                TextoUtil.limpiarCampoCsv(codigoPostal),
                TextoUtil.limpiarCampoCsv(direccion),
                TextoUtil.limpiarCampoCsv(margen),
                TextoUtil.limpiarCampoCsv(longitud),
                TextoUtil.limpiarCampoCsv(latitud),
                TextoUtil.limpiarCampoCsv(tomaDatos),
                TextoUtil.limpiarCampoCsv(rotulo),
                TextoUtil.limpiarCampoCsv(tipoVenta),
                TextoUtil.limpiarCampoCsv(rem),
                TextoUtil.limpiarCampoCsv(horario),
                TextoUtil.limpiarCampoCsv(precioGasolina95E5),
                TextoUtil.limpiarCampoCsv(precioGasolina95E10),
                TextoUtil.limpiarCampoCsv(precioGasoleoA),
                TextoUtil.limpiarCampoCsv(precioGasoleoB),
                TextoUtil.limpiarCampoCsv(precioGasoleoMar)
        );
    }
}
