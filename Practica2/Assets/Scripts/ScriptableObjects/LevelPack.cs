using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[CreateAssetMenu(fileName = "Level Pack", menuName = "Flow/Level Pack", order = 0)]
public class LevelPack : ScriptableObject
{
    public string levelName;
    public TextAsset levelMap;
}