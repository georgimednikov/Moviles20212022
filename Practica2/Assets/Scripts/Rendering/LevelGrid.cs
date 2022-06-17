using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Clase encargada de cargar los botones de seleccion de nivel para la "pagina" dada
/// </summary>
public class LevelGrid : MonoBehaviour
{
    [SerializeField] LevelItem[] levels;
    [SerializeField] Text descriptionText;
    int ind;

    /// <summary>
    /// A partir de index, crea 30 botones y les asigna la accion de cargar el nivel (index + i), siendo i el numero del boton creado
    /// Coloca en los botones si se ha completado el nivel y si esta bloqueado
    /// Si es la primera vez que se carga el pack, se encarga de asignarle al nivel si debe estar bloqueado o no, segun si es el primero de los instanciados o no
    /// </summary>
    /// <param name="index">Index desde el que se crean 30 botones</param>
    public void SetIndexStart(RectTransform content, int index)
    {
        ind = index;
        for(int i = 0; i < levels.Length; ++i){
            levels[i].SetLevelIndex(i + 1);
            var levelsave = GameManager.instance.GetComponent<SaveManager>().RestoreLevel(GameManager.instance.nextPack.levelName, i + index);
            int finished = levelsave.completed;
            if (finished == 2) levels[i].SetStar(true);
            if (finished == 1) levels[i].SetTick(true);
            int locked = levelsave.locked;
            if (locked == 1 || locked == -1 && GameManager.instance.nextPack.locked && (i != 0 || index != 0))
            {
                levels[i].SetLevelLocked();
                levelsave.locked = 1; // Si es -1, hay que ponerle el valor de verdad
            }
            else
            {
                levels[i].SetButtonEvent(content, index + i);
                levelsave.locked = 0; // Si es -1, hay que ponerle el valor de verdad
            }
        }
    }

    /// <summary>
    /// Pone desc como descripcion del minipack que controla
    /// </summary>
    public void SetDescriptionText(string desc)
    {
        descriptionText.text = desc;
    }

    /// <summary>
    /// Pone el color col a los botones
    /// </summary>
    public void SetColor(Color32 col)
    {
        foreach (LevelItem i in levels)
        {
            i.SetColor(col);
        }
    }
}
