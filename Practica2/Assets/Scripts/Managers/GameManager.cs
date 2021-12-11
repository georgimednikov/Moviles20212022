using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public class GameManager : MonoBehaviour
{
    public static GameManager instance;

    [SerializeField] public SkinPack skinPack;
    [SerializeField] private LevelPack[] levelPacks;
    [SerializeField] private LevelManager LM;

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
        nextPack = levelPacks[0];
        nextLevel = nextPack.levelMap.text.Split('\n')[0];
#endif
        instance.LM.LoadLevel(instance.nextLevel);
    }

    /// <summary>
    /// Se le comunica al Game Manager de qué pack va a ser el siguiente nivel
    /// </summary>
    public void NextPack(int pack)
    {
        nextPack = levelPacks[pack];
        SceneManager.LoadScene("SelectLevelScene");
    }

    /// <summary>
    /// Se le comunica al Game Manager qué nivel se va a jugar
    /// </summary>
    public void NextLevel(int level)
    {
        nextLevel = nextPack.levelMap.text.Split('\n')[level];
        SceneManager.LoadScene("GameScene");
    }

    /// <summary>
    /// Se le comunica al Game Manager un nuevo skin pack que usar
    /// </summary>
    public void ChangeSkin(SkinPack skin)
    {
        skinPack = skin;
    }
}
