using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[System.Serializable]
public class LevelPackSave
{
    public string name;
    public List<LevelSave> levelstates = new List<LevelSave>();
    public int numCompleted;
    public Vector2 scroll;
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
public class LevelContinueSave
{
    public bool toLoad;
    public int bundle;
    public int pack;
    public int level;
}

[System.Serializable]
public class SaveFile
{
    public string hash;
    public List<LevelPackSave> packSaves = new List<LevelPackSave>();
    public int hints = 3;
    public bool disabledAds;
    public int skinIndex;
    public LevelContinueSave levelContinue;
}
