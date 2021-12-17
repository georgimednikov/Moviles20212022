using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[CreateAssetMenu(fileName = "Skin Pack", menuName = "Flow/Skin Pack", order = 1)]
public class SkinPack : ScriptableObject
{
    public string skinName;
    public Color32[] colors;
}