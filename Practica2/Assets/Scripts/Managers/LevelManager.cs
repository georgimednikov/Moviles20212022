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
    [SerializeField] Text levelText;
    [SerializeField] Text sizeText;
    [SerializeField] Image completionAmount;
    [SerializeField] CanvasScaler canvasScaler;

    // TODO: comprobacion de errores
    public bool LoadLevel(string level)
    {
        int sizeX, sizeY = 0;
        string[] levelRows = level.Split(';');
        string[] levelInfo = levelRows[0].Split(',');

        string[] levelSize = levelInfo[0].Split(':');
        sizeX = int.Parse(levelSize[0]);
        // Si el mapa no es cuadrado, el levelSize tiene tamaño 2.
        sizeY = (levelSize.Length > 1) ? int.Parse(levelSize[1].Split('+')[0]) : 0; //Si es 0 es el valor inutil y no hay segunda dimension

        currentLevel = int.Parse(levelInfo[2]);

        levelText.text = "Level " + currentLevel;
        sizeText.text = sizeX + "x" + (sizeY == 0 ? sizeX : sizeY);
        // completionAmount.sprite = ; TODO

        BM.SetUIHeight(topRect.rect.height, botRect.rect.height, canvasScaler.referenceResolution.x);
        BM.SetBoard(sizeX, sizeY);
        BM.LoadMap(levelRows); // Quita el primer valor de levelRows que es la info del nivel y no un flow

        return true;
    }

    public void UpdateInfo(int moves, int percentage, int curFlows, int totalFlows)
    {
        movesText.text = "moves: " + moves;
        pipeText.text = "pipe: " + percentage + "%";
        flowsText.text = "flows: " + curFlows + "/" + totalFlows;
    }
}
