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
    [SerializeField] RectTransform infoRect;
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
    [SerializeField] GameObject animationStar;
    [SerializeField] GameObject animationTick;

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

        var levelsave = GameManager.instance.GetComponent<SaveManager>().RestoreLevel(GameManager.instance.nextPack.levelName, currentLevel);
        finishedStar.enabled = levelsave.completed == 2;
        finishedTick.enabled = levelsave.completed == 1;
        int bestMoves = levelsave.bestmoves;
        bestMovesText.text = "best: " + (bestMoves == int.MaxValue ? "-" : bestMoves.ToString());

        if (GameManager.instance.hints > 0) hintsText.text = GameManager.instance.hints + " x";
        else hintsText.text = "+";

        return true;
    }

    public void ResetLevel()
    {
        BM.ResetLevel();
    }

    public void setInfoRectHeight(float h)
    {
        infoRect.anchoredPosition = new Vector2(infoRect.anchoredPosition.x, h * canvasScaler.referenceResolution.y / 2);
    }

    public void GameFinished(bool perfect, int moves)
    {
        var levelsaved = GameManager.instance.GetComponent<SaveManager>().RestoreLevel(GameManager.instance.nextPack.levelName, currentLevel);

        int oldfinished = levelsaved.completed;
        if (oldfinished < 2) levelsaved.completed = perfect ? 2 : 1;

        bestMovesText.text = "best: " + moves;
        if(levelsaved.bestmoves > moves) levelsaved.bestmoves = moves;

        // Desbloquear el siguiente
        completeRect.Open();

        if (!finishedStar.enabled)
        {
            finishedStar.enabled = perfect;
            finishedTick.enabled = !perfect;
        }
        youCompletedText.text = "You completed the level in " + moves + " moves.";
        levelCompleteText.text = perfect ? "Perfect!" : "Level complete!";

        if (currentLevel < GameManager.instance.nextPack.numLevels - 1)
        {
            var nextLevel = GameManager.instance.GetComponent<SaveManager>().RestoreLevel(GameManager.instance.nextPack.levelName, currentLevel + 1);
            nextLevel.locked = 0; // No es una copia
        }


        int packNumCompleted = GameManager.instance.GetComponent<SaveManager>().RestoreNumCompleted(GameManager.instance.nextPack.levelName);
        if (oldfinished < 1) packNumCompleted++;
        GameManager.instance.GetComponent<SaveManager>().StoreNumCompleted(GameManager.instance.nextPack.levelName, packNumCompleted);
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

        if (GameManager.instance.hints > 0) hintsText.text = GameManager.instance.hints + " x";
        else hintsText.text = "+";
    }

    public void UpdateHints()
    {
        hintsText.text = GameManager.instance.hints + " x";
    }
}
