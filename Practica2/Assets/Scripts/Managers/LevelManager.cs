using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Linq;
using UnityEngine.UI;
using System;

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
    [SerializeField] Button nextLevelButton;
    [SerializeField] Button prevLevelButton;
    [SerializeField] Image finishedStar;
    [SerializeField] Image finishedTick;
    [SerializeField] CompleteRect completeRect;
    [SerializeField] CanvasScaler canvasScaler;

    public bool LoadLevel(string level, bool animate = true)
    {
        try {
            int sizeX, sizeY = 0;
            string[] levelRows = level.Split(';');
            string[] levelInfo = levelRows[0].Split(',');

            string[] levelSize = levelInfo[0].Split(':');
            sizeX = int.Parse(levelSize[0]);
            // Si el mapa no es cuadrado, el levelSize tiene tamaño 2.
            sizeY = (levelSize.Length > 1) ? int.Parse(levelSize[1].Split('+')[0]) : 0; //Si es 0 es el valor inutil y no hay segunda dimension

            currentLevel = int.Parse(levelInfo[2]) - 1;

            levelText.text = "Level " + (currentLevel + 1);
            levelText.color = GameManager.instance.nextBundle.bundleColor;
            sizeText.text = sizeX + "x" + (sizeY == 0 ? sizeX : sizeY);
            // completionAmount.sprite = ; TODO

            BM.SetUIHeight(topRect.rect.height, botRect.rect.height, canvasScaler.referenceResolution.x);
            BM.ChangeBoard(levelRows, sizeX, sizeY, animate);

            SetLevelButtonsInteractable();

            var levelsave = GameManager.instance.GetComponent<SaveManager>().RestoreLevel(GameManager.instance.nextPack.levelName, currentLevel);
            finishedStar.enabled = levelsave.completed == 2;
            finishedTick.enabled = levelsave.completed == 1;
            int bestMoves = levelsave.bestmoves;
            bestMovesText.text = "best: " + (bestMoves == int.MaxValue ? "-" : bestMoves.ToString());

            if (GameManager.instance.hints > 0) hintsText.text = GameManager.instance.hints + " x";
            else hintsText.text = "+";
            return true;
        }
        catch (Exception e)
        {
            Debug.LogError("Error al cargar el nivel");
            Debug.LogError(e);
            GameManager.GoToLevelSelect();
            return false;
        }
    }

    private void SetLevelButtonsInteractable()
    {
        if (currentLevel + 1 < GameManager.instance.nextPack.numLevels)
        {
            var nl = GameManager.instance.GetComponent<SaveManager>().RestoreLevel(GameManager.instance.nextPack.levelName, currentLevel + 1);
            nextLevelButton.interactable = !((nl.locked == 1));
        } else
        {
            nextLevelButton.interactable = false;
        }

        if (currentLevel - 1 >= 0)
        {
            var pl = GameManager.instance.GetComponent<SaveManager>().RestoreLevel(GameManager.instance.nextPack.levelName, currentLevel - 1);
            prevLevelButton.interactable = !((pl.locked == 1));
        } else
        {
            prevLevelButton.interactable = false;
        }
    }

    public void ResetLevel()
    {
        BM.ResetLevel();
    }

    public void GameFinished(bool perfect, int moves)
    {
        var SM = GameManager.instance.GetComponent<SaveManager>();
        var levelsaved = SM.RestoreLevel(GameManager.instance.nextPack.levelName, currentLevel);

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
            var nextLevel = SM.RestoreLevel(GameManager.instance.nextPack.levelName, currentLevel + 1);
            nextLevel.locked = 0; // No es una copia
        }


        int packNumCompleted = SM.RestoreNumCompleted(GameManager.instance.nextPack.levelName);
        if (oldfinished < 1) packNumCompleted++;
        SM.StoreNumCompleted(GameManager.instance.nextPack.levelName, packNumCompleted);
        SM.SaveToFile(GameManager.instance.GetComponent<SaveManager>().saveDirection);
        SetLevelButtonsInteractable();
    }

    public void UndoMove()
    {
        BM.UndoMove();
    }

    public void UpdateInfo(int moves, int curFlows, int totalFlows)
    {
        movesText.text = "moves: " + moves;
        flowsText.text = "flows: " + curFlows + "/" + totalFlows;
    }

    public void UpdateFlowInfo(int percentage)
    {
        pipeText.text = "pipe: " + percentage + "%";
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
