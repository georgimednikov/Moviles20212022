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

    public LevelPack nextPack;
    public string nextLevel;
#if UNITY_EDITOR
    public int bundle, pack, level;
#endif

    void Awake()
    {
        if (instance == null)
        {
            instance = this;
            DontDestroyOnLoad(gameObject);
        }
        else
        {
            if (LM != null)
            {
                instance.LM = LM;
            }
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
        SceneManager.LoadScene("LevelSelect");
    }


    /// <summary>
    /// Se le comunica al Game Manager qu� nivel se va a jugar
    /// </summary>
    public static void NextLevel(int level)
    {
        instance.nextLevel = instance.nextPack.levelMap.text.Split('\n')[level];
        SceneManager.LoadScene("GameScene");
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
