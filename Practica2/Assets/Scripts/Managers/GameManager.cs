using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public class GameManager : MonoBehaviour
{
    public static GameManager instance;

    [SerializeField]
    LevelManager LM;
    [SerializeField]
    LevelPack[] levelPacks;

    private LevelPack nextPack;
    private string nextLevel;

    void Awake()
    {
        if (instance == null)
        {
            instance = this;
            DontDestroyOnLoad(this.gameObject);
        }
        else
        {
            if (LM != null)
            {
                instance.LM = LM;
                LM.LoadLevel(instance.nextLevel);
            }
            Destroy(this.gameObject);
        }
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
}
