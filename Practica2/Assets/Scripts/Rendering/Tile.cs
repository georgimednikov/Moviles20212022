using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Serialization;

/// <summary>
/// Clase encargada del renderizado de las tiles que conforman el mapa de juego
/// </summary>
public class Tile : MonoBehaviour
{
    [SerializeField] GameObject flowEnd;
    [SerializeField] GameObject[] flowDirections;
    [SerializeField] GameObject[] walls;
    [SerializeField] GameObject looseEnd;
    [SerializeField] GameObject corner;
    [SerializeField] GameObject tick;

    bool hinted = false;

    private void Awake()
    {
        flowEnd.SetActive(false);
        for (int i = 0; i < flowDirections.Length; i++)
        {
            flowDirections[i].SetActive(false);
        }

        looseEnd.SetActive(false);
        corner.SetActive(false);
        tick.SetActive(false);
    }

    /// <summary>
    /// Pone una tuberia desde el centro de la tile hacia cada direccion de dirs.
    /// Activa ademas el circulo de esquina
    /// </summary>
    public void SetConnectedDirections(params Direction[] dirs)
    {
        for (int i = 0; i < dirs.Length; i++)
        {
            flowDirections[(int)dirs[i]].SetActive(true);
        }
        corner.SetActive(true);
    }

    /// <summary>
    /// Activa las paredes segun las direcciones dadas
    /// </summary>
    public void SetWalls(params int[] dirs)
    {
        for (int i = 0; i < dirs.Length; i++)
        {
            walls[dirs[i]].SetActive(true);
        }
    }

    /// <summary>
    /// Desactiva las tuberias segun las direcciones dadas.
    /// Si estan todas desactivadas, desactiva el circulo de esquina
    /// </summary>
    public void DisconnectDirections(params Direction[] dirs)
    {
        for (int i = 0; i < dirs.Length; i++)
        {
            flowDirections[(int)dirs[i]].SetActive(false);
        }
        bool allInactive = true;
        for (int i = 0; i < flowDirections.Length || allInactive; i++)
        {
            allInactive = !flowDirections[i].activeSelf;
        }
        corner.SetActive(!allInactive);
    }

    /// <summary>
    /// Desactiva las tuberias, el tick y los circulos centrales
    /// </summary>
    public void Reset()
    {
        looseEnd.SetActive(false);
        corner.SetActive(false);
        tick.SetActive(false);
        for (int i = 0; i < flowDirections.Length; i++)
        {
            flowDirections[i].SetActive(false);
        }
    }

    public void Deactivate()
    {
        gameObject.SetActive(false);
    }

    /// <summary>
    /// Activa el circulo grande
    /// </summary>
    public void SetFlowEnd()
    {
        flowEnd.SetActive(true);
    }

    /// <summary>
    /// Activa el circulo medio segun state
    /// </summary>
    public void SetLooseEnd(bool state)
    {
        looseEnd.SetActive(state);
    }

    /// <summary>
    /// Informa a la tile si deberia mostrar el tick cuando el flow esta completo
    /// </summary>
    public void SetTick()
    {
        hinted = true;
    }

    public void DrawTick()
    {
        if (hinted)
            tick.SetActive(true);
    }

    public void SetColor(Color32 col)
    {
        flowEnd.GetComponent<SpriteRenderer>().color = col;
        looseEnd.GetComponent<SpriteRenderer>().color = col;
        corner.GetComponent<SpriteRenderer>().color = col;
        for (int i = 0; i < flowDirections.Length; i++)
        {
            flowDirections[i].GetComponent<SpriteRenderer>().color = col;
        }
    }
}
