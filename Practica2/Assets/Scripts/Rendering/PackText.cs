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
    [SerializeField] Button arrowButton;

    int bundle, pack;

    int totalLevels;
    int completedLevels;

    /// <summary>
    /// Añade al click del boton la carga del pack dado
    /// </summary>
    public void SetButtonEvent(int bundle, int pack)
    {
        GameManager.OnClickBundle b = new GameManager.OnClickBundle();
        b.bundle = bundle; this.bundle = bundle;
        b.pack = pack; this.pack = pack;
        GetComponent<Button>().onClick.AddListener(() => { GameManager.NextPack(b); });

        int impLevel = GameManager.NextImperfectLevel(bundle, pack);
        if (impLevel == -1) arrowButton.interactable = false;
        else arrowButton.onClick.AddListener(() => { GameManager.LoadLevel(bundle, pack, impLevel); });
    }

    public void SetPackName(string name)
    {
        nameText.text = name;
    }

    public void SetColor(Color32 col)
    {
        nameText.color = col;
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
