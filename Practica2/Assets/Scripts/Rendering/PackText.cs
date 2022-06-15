using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// Clase encargada de controlar cada item de la lista de packs instanciada por PackSelect
/// </summary>
public class PackText : MonoBehaviour
{
    [SerializeField] Text nameText;
    [SerializeField] Text levelsText;
    [SerializeField] Button arrow;

    int totalLevels;
    int completedLevels;

    /// <summary>
    /// Añade al click del boton la carga del pack dado
    /// </summary>
    public void SetButtonEvent(int bundle, int pack)
    {
        GameManager.OnClickBundle b = new GameManager.OnClickBundle();
        b.bundle = bundle;
        b.pack = pack;
        GetComponent<Button>().onClick.AddListener(() => { GameManager.NextPack(b); });
    }

    public void SetPackName(string name)
    {
        nameText.text = name;
    }

    public void SetColor(Color32 col)
    {
        nameText.color = col;
    }

    public void SetArrowOnClick(int bundle, int pack, int level)
    {
        GameManager.OnClickBundle b = new GameManager.OnClickBundle();
        b.bundle = bundle;
        b.pack = pack;
        arrow.onClick.AddListener(() => { GameManager.NextLevelDirect(b, level); });
    }

    public void TogggleArrowInteract()
    {
        arrow.interactable = !arrow.interactable;
    }

    public void SetPackTotalLevels(int levels)
    {
        totalLevels = levels;
        levelsText.text = completedLevels + "/" + totalLevels;
    }

    public void SetPackCompletedLevels(int levels)
    {
        completedLevels = levels;
        levelsText.text = completedLevels + "/" + totalLevels;
    }
}
