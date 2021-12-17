using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class ColorDropdown : MonoBehaviour
{
    float height;
    SkinPack[] skins;
    Dropdown dropdown;
    public Image tile;

    private void Start()
    {
        height = GetComponent<RectTransform>().rect.height;
        skins = GameManager.instance.skinPacks;
        dropdown = GetComponent<Dropdown>();

        List<string> names = new List<string>();
        foreach (SkinPack s in skins)
        {
            names.Add(s.name);
        }
        dropdown.AddOptions(names);
        dropdown.value = names.FindIndex(n => n == GameManager.instance.currSkin.name);

        dropdown.onValueChanged.AddListener(GameManager.instance.ChangeSkin);
    }
}
