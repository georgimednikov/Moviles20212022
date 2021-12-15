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
    [SerializeField] Text bestMovesText;
    [SerializeField] Text pipeText;
    [SerializeField] Text flowsText;
    [SerializeField] Text levelText;
    [SerializeField] Text sizeText;
    [SerializeField] Text hintsText;
    [SerializeField] Text youCompletedText;
    [SerializeField] Text levelCompleteText;
    [SerializeField] DeactivateButton nextLevelButton;
    [SerializeField] Image finishedStar;
    [SerializeField] Image finishedTick;
    [SerializeField] CompleteRect completeRect;
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

        nextLevelButton.SetLimit(GameManager.instance.nextPack.numLevels);

        int finished = PlayerPrefs.GetInt("finished_" + GameManager.instance.nextPack.name + "_" + currentLevel, 0);
        finishedStar.enabled = finished == 2;
        finishedTick.enabled = finished == 1;
        int bestMoves = PlayerPrefs.GetInt("moves_" + GameManager.instance.nextPack.name + "_" + currentLevel, 0);
        bestMovesText.text = "best: " + (bestMoves == 0 ? "-" : bestMoves.ToString());

        hintsText.text = GameManager.instance.hints + " x";

        return true;
    }

    public void ResetLevel()
    {
        BM.ResetLevel();
    }

    public void GameFinished(bool perfect, int moves)
    {
        string finishedstr = "finished_" + GameManager.instance.nextPack.name + "_" + currentLevel;
        string movesstr = "moves_" + GameManager.instance.nextPack.name + "_" + currentLevel;
        string ncompletedstr = "ncompleted_" + GameManager.instance.nextPack.name;

        int oldfinished = PlayerPrefs.GetInt(finishedstr, 0);
        if(oldfinished < 2) PlayerPrefs.SetInt(finishedstr, perfect ? 2 : 1);

        bestMovesText.text = "best: " + moves;
        int oldBest = PlayerPrefs.GetInt(movesstr, int.MaxValue);
        if(moves < oldBest) PlayerPrefs.SetInt(movesstr, moves);

        // Desbloquear el siguiente
        completeRect.Open();

        youCompletedText.text = "You completed the level in " + moves + " moves.";
        levelCompleteText.text = perfect ? "Perfect!" : "Level complete!";

        if(currentLevel < GameManager.instance.nextPack.numLevels - 1) PlayerPrefs.SetInt("locked_" + GameManager.instance.nextPack.name + "_" + (currentLevel + 1), 0);

        int packNumCompleted = PlayerPrefs.GetInt(ncompletedstr, 0);
        if (oldfinished < 1) packNumCompleted++;
        PlayerPrefs.SetInt(ncompletedstr, packNumCompleted);
    }

    public void UndoMove()
    {
        BM.UndoMove();
    }

    public void UpdateInfo(int moves, int percentage, int curFlows, int totalFlows)
    {
        movesText.text = "moves: " + moves;
        pipeText.text = "pipe: " + percentage + "%";
        flowsText.text = "flows: " + curFlows + "/" + totalFlows;
    }

    public void GiveHint()
    {
        if (GameManager.instance.hints > 0)
        {
            BM.GiveHint();
            GameManager.instance.hints--;
        }
            
        if(GameManager.instance.hints > 0) hintsText.text = GameManager.instance.hints + " x";
        else hintsText.text = "+";
    }
}
