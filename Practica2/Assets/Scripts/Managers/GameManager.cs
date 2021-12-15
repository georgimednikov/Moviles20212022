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
    public int hints { get { return _hints; } set { _hints = value; PlayerPrefs.SetInt("hints", _hints); } }
    public LevelPack nextPack;
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
            int h = PlayerPrefs.GetInt("hints", -1);
            if (h < 0) hints = 3; // TODO: mas seguro, en su propio manager?
            else hints = h;
            DontDestroyOnLoad(gameObject);
        }
        else
        {
            instance.LM = LM;
            Destroy(gameObject);
        }
#if UNITY_EDITOR
        nextPack = levelBundles[bundle].packs[pack];
        nextLevel = nextPack.levelMap.text.Split('\n')[level];
#endif
        instance.LM?.LoadLevel(instance.nextLevel);
    }

    /// <summary>
    /// Se le comunica al Game Manager de qu� pack va a ser el siguiente nivel
    /// </summary>
    public static void NextPack(OnClickBundle onClick)
    {
        instance.nextPack = instance.levelBundles[onClick.bundle].packs[onClick.pack];
        GoToLevelSelect();
    }

    public static void LoadNextLevel()
    {
        if (instance.levelIndex > instance.nextPack.numLevels) return; // TODO desactivar boton
        instance.nextLevel = instance.nextPack.levelMap.text.Split('\n')[++instance.levelIndex];
        Debug.Log("Cargando " + instance.levelIndex);
        instance.LM.ResetLevel();
        instance.LM.LoadLevel(instance.nextLevel);
    }
    public static void LoadPrevLevel()
    {
        if (instance.levelIndex < 0) return;
        instance.nextLevel = instance.nextPack.levelMap.text.Split('\n')[--instance.levelIndex];
        Debug.Log("Cargando " + instance.levelIndex);
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

    public static void GoToPackSelect()
    {
        SceneManager.LoadScene("PackSelect");
    }
}
