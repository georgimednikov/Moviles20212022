using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Linq;

public class LevelManager : MonoBehaviour
{
    [SerializeField]
    BoardManager BM;
    int currentLevel;

    // TODO: comprobacion de errores
    public bool LoadLevel(string level)
    {
        int sizeX, sizeY;
        string[] levelRows = level.Split(';');
        string[] levelInfo = levelRows[0].Split(',',':');
        sizeX = int.Parse(levelInfo[0]);
        sizeY = int.Parse(levelInfo[1]); //Si es 0 es el valor inutil y no hay segunda dimension
        currentLevel = int.Parse(levelInfo[levelInfo.Length - 2]);
        int numPipes = int.Parse(levelInfo[levelInfo.Length - 1]);
        BM.SetBoard(sizeX, sizeY);
        BM.LoadMap(levelRows.Skip(1).ToArray()); // Quita el primer valor de levelRows que es la info del nivel y no un flow
        return true;
    }
}
