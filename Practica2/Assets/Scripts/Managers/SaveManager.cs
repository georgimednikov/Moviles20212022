using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;

public class SaveManager : MonoBehaviour
{
    SaveFile saveFile;

    private void Awake()
    {
        LoadFromFile("/save");
    }

    private void OnApplicationQuit()
    {
        SaveToFile("/save");
    }

    // Guarda en Application.persistentDataPath + fileString + ".json"
    public void SaveToFile(string fileString)
    {
        string json = JsonUtility.ToJson(saveFile);
        string destination = Application.persistentDataPath + fileString + ".json";
        StreamWriter file = new StreamWriter(destination);
        Debug.Log(json);
        file.Write(json);
        file.Flush();
    }

    public void LoadFromFile(string fileString)
    {
        string source = Application.persistentDataPath + fileString + ".json";
        if (File.Exists(source))
            saveFile = JsonUtility.FromJson<SaveFile>(string.Concat(File.ReadAllLines(source)));
        else saveFile = new SaveFile();
    }

    // TODO: hacer (Pablo)
    public void Hash()
    {
        throw new NotImplementedException();
    }

    public void StoreHint(int hint)
    {
        saveFile.hints = hint;
    }

    public void StoreNoAds(bool ads)
    {
        saveFile.disabledAds = ads;
    }

    public void StoreNumCompleted(string packName, int ncompleted)
    {
       saveFile.packSaves.Find(p => p.name.Equals(packName)).numCompleted = ncompleted;
    }

    public int RestoreHint()
    {
        return saveFile.hints;
    }

    // Devuelve la referencia al level (si se modifica, se modifica en el save)
    public LevelSave RestoreLevel(string packName, int level)
    {
        var a = saveFile.packSaves.Find(p => p.name.Equals(packName));
        if (a == null) // Esta comprobacion es para cuando se carga por primera vez el juego, para que se rellene solo el savefile
        {
            a = new LevelPackSave();
            a.name = packName;
            saveFile.packSaves.Add(a);
        }
        var b = a.levelstates.Find(l => l.id == level);
        if (b == null) // Esta comprobacion es para cuando se carga por primera vez el juego, para que se rellene solo el savefile
        {
            b = new LevelSave();
            b.id = level;
            a.levelstates.Add(b);
        }
        return b;
    }

    public bool RestoreNoAds()
    {
        return saveFile.disabledAds;
    }

    public int RestoreNumCompleted(string packName)
    {
        var a = saveFile.packSaves.Find(p => p.name.Equals(packName));
        if(a == null) // Esta comprobacion es para cuando se carga por primera vez el juego, para que se rellene solo el savefile
        {
            a = new LevelPackSave();
            a.name = packName;
            saveFile.packSaves.Add(a);
        }
        return a.numCompleted;
    }
}
