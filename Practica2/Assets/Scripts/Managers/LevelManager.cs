using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class LevelManager : MonoBehaviour
{
    [SerializeField]
    BoardManager BM;
    int currentLevel;

    public bool LoadLevel(string level)
    {
        int sizeX, sizeY;
        string[] levelRows = level.Split(';');
        string[] levelInfo = levelRows[0].Split(',',':');
        sizeX = int.Parse(levelInfo[0]);
        sizeY = int.Parse(levelInfo[1]); //Si es 0 es el valor inutil y no hay segunda dimension
        currentLevel = int.Parse(levelInfo[levelInfo.Length - 2]);
        int numPipes = int.Parse(levelInfo[levelInfo.Length - 1]);
        BM.SetBoardSize(sizeX, sizeY);
        BM.SetNumPipes(numPipes);
        //BM.SetLevel();
        return true;
    }
}
