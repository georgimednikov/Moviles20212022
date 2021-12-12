using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Linq;
using UnityEngine.UI;

public class LevelManager : MonoBehaviour
{
    public BoardManager BM;
    int currentLevel;

    [SerializeField] RectTransform topRect;
    [SerializeField] RectTransform botRect;
    [SerializeField] Text movesText;
    [SerializeField] Text pipeText;
    [SerializeField] Text flowsText;
    [SerializeField] Image completionAmount;
    [SerializeField] CanvasScaler canvasScaler;

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
        BM.SetUIHeight(topRect.rect.height, botRect.rect.height, canvasScaler.referenceResolution.x);
        BM.SetBoard(sizeX, sizeY);
        BM.LoadMap(levelRows.Skip(1).ToArray()); // Quita el primer valor de levelRows que es la info del nivel y no un flow
        return true;
    }

    public void UpdateInfo(int moves, int percentage, int curFlows, int totalFlows)
    {
        movesText.text = "moves: " + moves;
        pipeText.text = "pipe: " + percentage + "%";
        flowsText.text = "flows: " + curFlows + "/" + totalFlows;
    }
}
