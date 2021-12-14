using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.Serialization;

public class PackSelect : MonoBehaviour
{
    [Header("Pack")]
    [SerializeField] LevelBundle bundle;

    [Header("RenderItem")]
    [SerializeField] Text nameText;
    [SerializeField] Image foregroundImg;
    [SerializeField] GameObject packTextPrefab;
    
    public void SetBundle(LevelBundle bundle)
    {
        this.bundle = bundle;
    }

    public void LoadPacks()
    {
        nameText.text = bundle.bundleName;
        foregroundImg.color = bundle.bundleColor;
        foreach(var pack in bundle.packs)
        {
            PackUI ui = Instantiate(packTextPrefab, transform).GetComponent<PackUI>();
            ui.SetPackName(pack.levelName);
            ui.SetPackTotalLevels(pack.numLevels);
            ui.SetColor(bundle.bundleColor);
            //ui.SetPackCompletedLevels(); TODO
        }
    }
}
