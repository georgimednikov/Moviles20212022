using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;
using System.Security.Cryptography;
using System.Text;

public class SaveManager : MonoBehaviour
{
    SaveFile saveFile;

    public string saveDirection = "/save";
    string pimienta = "https://gl.wikipedia.org/wiki/Pementa";

    private void Awake()
    {
        if (!LoadFromFile(saveDirection))
        {
            Debug.LogError("Oh no datos corruptos");
            saveFile = new SaveFile();
        }
    }

    private void OnApplicationQuit()
    {
        SaveToFile(saveDirection);
    }
    /// <summary>
    /// Guarda la informacion de guardado en el archivo Application.persistentDataPath + fileString + ".json", utilizando sal y pimienta
    /// </summary>
    public void SaveToFile(string fileString) // TODO: permitir tener DLCs sin perder tus datos
    {
        string destination = Application.persistentDataPath + fileString + ".json";
        using (StreamWriter sw = new StreamWriter(destination))
        {
            saveFile.hash = "";
            string json = JsonUtility.ToJson(saveFile);
            saveFile.hash = Hash(pimienta.Substring(0, 16) + json + pimienta.Substring(15, 21));
            string jsonhash = JsonUtility.ToJson(saveFile);
            sw.Write(jsonhash);
        }
    }

    /// <summary>
    /// Carga la informacion del jugador guardada desde el archivo Application.persistentDataPath + fileString + ".json"
    /// </summary>
    /// <returns>true si no ha habido ningun problema de corrupcion o se ha creado un nuevo archivo ya que no existia, false si no</returns>
    public bool LoadFromFile(string fileString)
    {
        string source = Application.persistentDataPath + fileString + ".json";
        if (File.Exists(source))
            saveFile = JsonUtility.FromJson<SaveFile>(File.ReadAllText(source));
        else
        {
            saveFile = new SaveFile();
            return true;
        }

        string hash = saveFile.hash;
        saveFile.hash = "";
        string json = JsonUtility.ToJson(saveFile);
        string hashNew = Hash(pimienta.Substring(0, 16) + json + pimienta.Substring(15, 21));
        return hash == hashNew;
    }

    /// <summary>
    /// Utilizando SHA256, devuelve un hash en formato de string con el json recibido
    /// </summary>
    public string Hash(string json)
    {
        // Create a SHA256
        using (SHA256 sha256Hash = SHA256.Create())
        {
            // ComputeHash - returns byte array
            byte[] bytes = sha256Hash.ComputeHash(Encoding.UTF8.GetBytes(json));

            // Convert byte array to a string
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < bytes.Length; i++)
            {
                builder.Append(bytes[i].ToString("x2"));
            }
            return builder.ToString();
        }
    }

    /// <summary>
    /// Guarda el numero de hints dado
    /// </summary>
    public void StoreHint(int hint)
    {
        saveFile.hints = hint;
    }

    /// <summary>
    /// Guarda si se ha comprado la desactivacion de anuncios
    /// </summary>
    public void StoreNoAds(bool ads)
    {
        saveFile.disabledAds = ads;
    }

    /// <summary>
    /// Guarda el numero de niveles completados del pack dado
    /// </summary>
    public void StoreNumCompleted(string packName, int ncompleted)
    {
        saveFile.packSaves.Find(p => p.name.Equals(packName)).numCompleted = ncompleted;
    }

    /// <summary>
    /// Guarda el indice de la skin dada
    /// </summary>
    public void StoreSkin(int index)
    {
        saveFile.skinIndex = index;
    }

    /// <summary>
    /// Recupera el numero de hints guardadas
    /// </summary>
    public int RestoreHint()
    {
        return saveFile.hints;
    }

    /// <summary>
    /// Recupera el estado del nivel dado, devolviendolo como una referencia modificable. 
    /// Si no encuentra el nivel o el pack, lo añade a la lista de guardado
    /// </summary>
    /// <param name="packName">El pack al que pertenece</param>
    /// <param name="level">Indice del nivel dentro de ese pack</param>
    public LevelSave RestoreLevel(string packName, int level)
    {
        var a = saveFile.packSaves.Find(p => p.name.Equals(packName));
        if (a == null) // Esta comprobacion es para cuando se carga por primera vez el juego, para que se rellene solo el savefile
        {
            a = new LevelPackSave();
            a.name = packName;
            saveFile.packSaves.Add(a);
        }
        if (level < 0) return null;
        var b = a.levelstates.Find(l => l.id == level);
        if (b == null) // Esta comprobacion es para cuando se carga por primera vez el juego, para que se rellene solo el savefile
        {
            b = new LevelSave();
            b.id = level;
            a.levelstates.Add(b);
        }
        return b;
    }

    /// <summary>
    /// Recupera si se ha comprado la desactivacion de anuncios
    /// </summary>
    public bool RestoreNoAds()
    {
        return saveFile.disabledAds;
    }

    /// <summary>
    /// Recupera el numero de niveles completos del pack dado.
    /// Si no existe el pack, lo añade a la lista de guardado
    /// </summary>
    public int RestoreNumCompleted(string packName)
    {
        var a = saveFile.packSaves.Find(p => p.name.Equals(packName));
        if (a == null) // Esta comprobacion es para cuando se carga por primera vez el juego, para que se rellene solo el savefile
        {
            a = new LevelPackSave();
            a.name = packName;
            saveFile.packSaves.Add(a);
        }
        return a.numCompleted;
    }

    /// <summary>
    /// Recupera el indice de la skin seleccionada
    /// </summary>
    public int RestoreSkin()
    {
        return saveFile.skinIndex;
    }
}
