package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

public class DirectionInfo {
    public int greysCount = 0; // Cuantos grises hay en esta direccion
    public int numberCountAfterGreys = 0; // Cuantos azules hay tras el primer gris encontrado
    public boolean wouldBeTooMuch = false; // Si al llenar el primer gris se pasaria del numero de azules
    public int maxPossibleCount = 0; // Hasta cuantas celdas azules podria ver en esta direccion
    public int maxPossibleCountInOtherDirections = 0; // Hasta cuantas celdas azules podria ver en las direcciones que no son esta
    public int numberWhenFillingFirstGrey = 0; // Cuantas azules veria la celda si se rellena la primera gris
}
