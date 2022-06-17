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

    public SkinPack currSkin;
    public SkinPack[] skinPacks;
    public LevelBundle[] levelBundles;
    public LevelManager LM;

    int _hints = 0;
    public int hints { get { return _hints; } set { _hints = value; instance.GetComponent<SaveManager>().StoreHint(_hints);} }
    public LevelPack nextPack;
    public LevelBundle nextBundle;
    public string nextLevel;
    public int levelIndex = 0;

#if UNITY_EDITOR
    public int bundle, pack, level;
#endif

    SaveManager sm;

    void Awake()
    {
#if UNITY_EDITOR
        nextBundle = levelBundles[bundle];
        nextPack = nextBundle.packs[pack];
        nextLevel = nextPack.levelMap.text.Split('\n')[level];
#endif
        if (instance == null)
        {
            instance = this;
            currSkin = skinPacks[0];
            sm = GetComponent<SaveManager>();
            hints = sm.RestoreHint();
            DontDestroyOnLoad(gameObject);
            currSkin = skinPacks[sm.RestoreSkin()];
            LevelContinueSave levelSave = sm.LevelToContinue();
            if (levelSave != null)
                LoadLevel(levelSave);
        }
        else
        {
            instance.LM = LM;
            Destroy(gameObject);
        }
        instance.LM?.LoadLevel(instance.nextLevel, false);
    }

    internal static int NextImperfectLevel()
    {
        return instance.sm.NextImperfectLevel(instance.nextPack.levelName, instance.levelIndex + 1);
    }
    internal static int NextImperfectLevel(int bundle, int pack, int start = 0)
    {
        return instance.sm.NextImperfectLevel(instance.levelBundles[bundle].packs[pack].levelName, start);
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

    /// <summary>
    /// Se carga el siguiente nivel
    /// </summary>
    public static void LoadNextLevel()
    {
        var nl = instance.sm.RestoreLevel(instance.nextPack.levelName, instance.levelIndex);
        if (nl.locked == 1 || nl.locked == -1 && instance.nextPack.locked && instance.levelIndex + 1 != 0) return;
        if (instance.levelIndex + 1 >= instance.nextPack.numLevels)
        {
            GoToPackSelect();
            return;
        }
        instance.nextLevel = instance.nextPack.levelMap.text.Split('\n')[++instance.levelIndex];
        instance.LM.LoadLevel(instance.nextLevel);
    }

    public static void LoadNextLevel(int level)
    {
        instance.sm.RestoreLevel(instance.nextPack.levelName, instance.levelIndex);
        if (level == -1) return;

        instance.levelIndex = level;
        instance.nextLevel = instance.nextPack.levelMap.text.Split('\n')[level];
        instance.LM.LoadLevel(instance.nextLevel);
    }

    /// <summary>
    /// Se carga el anterior nivel
    /// </summary>
    public static void LoadPrevLevel()
    {
        var nl = instance.sm.RestoreLevel(instance.nextPack.levelName, instance.levelIndex);
        if (nl.locked == 1 || nl.locked == -1 && instance.nextPack.locked && instance.levelIndex - 1 != 0) return; // por si acaso
        if (instance.levelIndex < 1) return;
        instance.nextLevel = instance.nextPack.levelMap.text.Split('\n')[--instance.levelIndex];
        instance.LM.LoadLevel(instance.nextLevel);
    }

    public static void LoadLevel(int bundle, int pack, int level)
    {
        instance.sm.RestoreLevel(instance.nextPack.levelName, instance.levelIndex);
        instance.nextBundle = instance.levelBundles[bundle];
        instance.nextPack = instance.nextBundle.packs[pack];
        instance.levelIndex = level;
        instance.nextLevel = instance.nextPack.levelMap.text.Split('\n')[level];
        SceneManager.LoadScene("Level");
    }

    public static void ResetLevel()
    {
        instance.LM.LoadLevel(instance.nextLevel, false);
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

    public static void LoadLevel(LevelContinueSave save)
    {
        instance.nextBundle = instance.levelBundles[save.bundle];
        instance.nextPack = instance.nextBundle.packs[save.pack];
        instance.levelIndex = save.level;
        instance.nextLevel = instance.nextPack.levelMap.text.Split('\n')[save.level];
        SceneManager.LoadScene("Level");
    }

    public static void GoToLevelSelect()
    {
        SceneManager.LoadScene("LevelSelect");
    }

    /// <summary>
    /// Se le comunica al Game Manager un nuevo skin pack que usar
    /// </summary>
    public void ChangeSkin(int skin)
    {
        currSkin = skinPacks[skin];
        sm.StoreSkin(skin);
    }

    public static void GoToMainMenu()
    {
        SceneManager.LoadScene("MainMenu");
    }

    internal void AddHint()
    {
        hints++;
        sm.StoreHint(hints);
        LM.UpdateHints();
    }

    internal void StoreScroll(Vector2 scroll)
    {
        sm.StoreScroll(scroll);
    }
    internal Vector2 RestoreScroll(string packName)
    {
        return sm.SavedScroll();
    }

    public static void GoToPackSelect()
    {
        SceneManager.LoadScene("PackSelect");
    }

    public static int GetBundleIndex()
    {
        return Array.FindIndex(instance.levelBundles, p => p.name.Equals(instance.nextBundle.name));
    }

    public static int GetPackIndex(int bundle)
    {
        return Array.FindIndex(instance.levelBundles[bundle].packs, p => p.name.Equals(instance.nextPack.name));
    }

    public static bool InGame()
    {
        return instance.LM != null;
    }
}
