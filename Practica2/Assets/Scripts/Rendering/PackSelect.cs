using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.Serialization;

/// <summary>
/// Clase encargada de instanciar los botones de seleccion de pack para el bundle dado
/// </summary>
public class PackSelect : MonoBehaviour
{
    [Header("Pack")]
    [SerializeField] LevelBundle bundle;

    [Header("RenderItem")]
    [SerializeField] Text nameText;
    [SerializeField] Image foregroundImg;
    [SerializeField] GameObject packTextPrefab;

    int bundleIndex;

    public void SetBundle(int index)
    {
        bundleIndex = index;
        bundle = GameManager.instance.levelBundles[index];
    }

    /// <summary>
    /// Instancia los botones del pack, les coloca su color y pone el numero de completados de cada uno
    /// </summary>
    public void LoadPacks()
    {
        nameText.text = bundle.bundleName;
        foregroundImg.color = bundle.bundleColor;
        for(int i = 0; i < bundle.packs.Length; ++i)
        {
            PackText ui = Instantiate(packTextPrefab, transform).GetComponent<PackText>();
            ui.SetPackName(bundle.packs[i].levelName);
            ui.SetPackTotalLevels(bundle.packs[i].numLevels);
            ui.SetColor(bundle.bundleColor);
            ui.SetButtonEvent(bundleIndex, i);
            ui.SetPackCompletedLevels(GameManager.instance.GetComponent<SaveManager>().RestoreNumCompleted(bundle.packs[i].levelName));
            int nextimp = GameManager.instance.GetComponent<SaveManager>().NextImperfectLevel(bundle.packs[i].levelName, 0);
            if(nextimp == -1)
            {
                ui.TogggleArrowInteract();
            }else 
                ui.SetArrowOnClick(bundleIndex, i, nextimp);
        }
    }
}
