using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[CreateAssetMenu(fileName = "Level Bundle", menuName = "Flow/Level Bundle", order = 0)]
public class LevelBundle: ScriptableObject
{
    public string bundleName;
    public Color32 bundleColor;
    public Color32 wallsColor;
    public LevelPack[] packs;
}