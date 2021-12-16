using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Serialization;


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

    public void SetConnectedDirections(params Direction[] dirs)
    {
        for (int i = 0; i < dirs.Length; i++)
        {
            flowDirections[(int)dirs[i]].SetActive(true);
        }
        corner.SetActive(true);
    }

    public void SetWalls(params int[] dirs)
    {
        for (int i = 0; i < dirs.Length; i++)
        {
            walls[dirs[i]].SetActive(true);
        }
    }

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

    public void SetFlowEnd()
    {
        flowEnd.SetActive(true);
    }

    public void SetLooseEnd(bool state)
    {
        looseEnd.SetActive(state);
    }

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
