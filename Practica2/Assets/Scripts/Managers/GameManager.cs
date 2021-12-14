using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public class GameManager : MonoBehaviour
{
    public static GameManager instance;

    [SerializeField] public SkinPack skinPack;
    [SerializeField] public LevelBundle[] levelBundles;
    [SerializeField] public LevelManager LM;

    private LevelPack nextPack;
    private string nextLevel;

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
        nextPack = levelBundles[0].packs[0];
        nextLevel = nextPack.levelMap.text.Split('\n')[0];
#endif
        instance.LM?.LoadLevel(instance.nextLevel);
    }

    /// <summary>
    /// Se le comunica al Game Manager de qué pack va a ser el siguiente nivel
    /// </summary>
    public static void NextPack(int bundle, int pack)
    {
        instance.nextPack = instance.levelBundles[bundle].packs[pack];
        SceneManager.LoadScene("SelectLevelScene");
    }

    /// <summary>
    /// Se le comunica al Game Manager qué nivel se va a jugar
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
