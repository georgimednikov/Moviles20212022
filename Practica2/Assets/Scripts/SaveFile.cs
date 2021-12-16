using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[System.Serializable]
public class LevelPackSave
{
    public string name;
    public List<LevelSave> levelstates = new List<LevelSave>();
    public int numCompleted;
}

[System.Serializable]
public class LevelSave
{
    public int id;
    public int completed; // 0 = no, 1 = si, 2 = perfect
    public int bestmoves = int.MaxValue;
    public int locked = -1; // -1 = por defecto (hay que poner el valor de verdad), 0 = no, 1 = si
}

[System.Serializable]
public class SaveFile
{
    public List<LevelPackSave> packSaves = new List<LevelPackSave>();
    public int hints;
    public bool disabledAds;
}
