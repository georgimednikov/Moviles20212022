using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public class GameManager : MonoBehaviour
{
    public struct OnClickBundle
    {
        public int bundle, pack;
    }

    public static GameManager instance;

    [SerializeField] public SkinPack skinPack;
    [SerializeField] public LevelBundle[] levelBundles;
    [SerializeField] public LevelManager LM;

    int _hints = 0;
    public int hints { get { return _hints; } set { _hints = value; instance.GetComponent<SaveManager>().StoreHint(_hints);} }
    public LevelPack nextPack;
    public LevelBundle nextBundle;
    public string nextLevel;
    public int levelIndex = 0;
#if UNITY_EDITOR
    public int bundle, pack, level;
#endif

    void Awake()
    {
        if (instance == null)
        {
            instance = this;
            hints = instance.GetComponent<SaveManager>().RestoreHint(); // TODO: comprobar si no existia antes
            DontDestroyOnLoad(gameObject);
        }
        else
        {
            instance.LM = LM;
            Destroy(gameObject);
        }
#if UNITY_EDITOR
        nextBundle = levelBundles[bundle];
        nextPack = nextBundle.packs[pack];
        nextLevel = nextPack.levelMap.text.Split('\n')[level];
#endif
        instance.LM?.LoadLevel(instance.nextLevel);
    }

    /// <summary>
    /// Se le comunica al Game Manager de qu� pack va a ser el siguiente nivel
    /// </summary>
    public static void NextPack(OnClickBundle onClick)
    {
        instance.nextBundle = instance.levelBundles[onClick.bundle];
        instance.nextPack = instance.nextBundle.packs[onClick.pack];
        GoToLevelSelect();
    }

    public static void LoadNextLevel()
    {
        var nl = instance.GetComponent<SaveManager>().RestoreLevel(instance.nextPack.levelName, instance.levelIndex);
        if (nl.locked == 1 || nl.locked == -1 && instance.nextPack.locked && instance.levelIndex + 1 != 0) return;
        if (instance.levelIndex + 1 >= instance.nextPack.numLevels)
        {
            GoToPackSelect();
            return;
        }
        instance.nextLevel = instance.nextPack.levelMap.text.Split('\n')[++instance.levelIndex];
        instance.LM.ResetLevel();
        instance.LM.LoadLevel(instance.nextLevel);
    }
    public static void LoadPrevLevel()
    {
        var nl = instance.GetComponent<SaveManager>().RestoreLevel(instance.nextPack.levelName, instance.levelIndex);
        if (nl.locked == 1 || nl.locked == -1 && instance.nextPack.locked && instance.levelIndex - 1 != 0) return; // por si acaso
        if (instance.levelIndex < 1) return;
        instance.nextLevel = instance.nextPack.levelMap.text.Split('\n')[--instance.levelIndex];
        instance.LM.ResetLevel();
        instance.LM.LoadLevel(instance.nextLevel);
    }

    public static void ResetLevel()
    {
        instance.LM.ResetLevel();
        instance.LM.LoadLevel(instance.nextLevel);
    }

    /// <summary>
    /// Se le comunica al Game Manager qu� nivel se va a jugar
    /// </summary>
    public static void NextLevel(int level)
    {
        instance.levelIndex = level;
        instance.nextLevel = instance.nextPack.levelMap.text.Split('\n')[level];
        SceneManager.LoadScene("Level");
    }

    public static void GoToLevelSelect()
    {
        SceneManager.LoadScene("LevelSelect");
    }

    /// <summary>
    /// Se le comunica al Game Manager un nuevo skin pack que usar
    /// </summary>
    public void ChangeSkin(SkinPack skin)
    {
        skinPack = skin;
    }

    public static void GoToMainMenu()
    {
        SceneManager.LoadScene("MainMenu");
    }

    internal void addHint()
    {
        hints++;
        instance.GetComponent<SaveManager>().StoreHint(hints);
        LM.UpdateHints();
    }

    public static void GoToPackSelect()
    {
        SceneManager.LoadScene("PackSelect");
    }
}
