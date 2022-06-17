using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Clase encargada de renderizar cada boton de nivel de la escena de seleccion de nivel
/// </summary>
public class LevelItem : MonoBehaviour
{
    [SerializeField] Image backgroundImg;
    [SerializeField] Text indexText;
    [SerializeField] Image star;
    [SerializeField] Image tick;
    [SerializeField] Image levelLock;
    [SerializeField] Image transparentImg;
    [SerializeField] Image[] transparentSides;

    /// <summary>
    /// Añade el evento que hace que se cargue el nivel level al ser pulsado el boton
    /// </summary>
    public void SetButtonEvent(RectTransform content, int level)
    {
        GetComponent<Button>().onClick.AddListener(() =>
        {
            GameManager.instance.StoreScroll(content.anchoredPosition);
            GameManager.NextLevel(level);
        });
    }

    public void SetColor(Color32 col)
    {
        backgroundImg.color = col;
        Color color = col;
        float alpha = transparentImg.color.a;
        color.a = alpha;
        transparentImg.color = color;
        foreach (var img in transparentSides)
        {
            img.color = color;
        }
    }

    public void SetStar(bool enabled)
    {
        star.enabled = enabled;
        backgroundImg.enabled = true;
    }

    public void SetTick(bool enabled)
    {
        tick.enabled = enabled;
        backgroundImg.enabled = true;
    }

    /// <summary>
    /// Pone el texto del boton a "index"
    /// </summary>
    public void SetLevelIndex(int index)
    {
        indexText.text = index.ToString();
    }

    /// <summary>
    /// Activa el candado del boton, y desactiva la imagen de color de fondo
    /// </summary>
    public void SetLevelLocked()
    {
        levelLock.enabled = true;
        backgroundImg.enabled = false;
    }
}
