using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Serialization;


public class Tile : MonoBehaviour
{
    [SerializeField] GameObject flowEnd;
    [SerializeField] GameObject[] flowDirections;
    [SerializeField] GameObject looseEnd;
    [SerializeField] GameObject corner;
    [SerializeField] GameObject tick;

    Color32 color;

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
        //if (!flowEnd.activeSelf && dirs.Length == 1)
        //{
        //    looseEnd.SetActive(true);
        //} else
        //{
        //    looseEnd.SetActive(false);
        //}
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

    public void SetLooseEnd()
    {
        looseEnd.SetActive(true);
    }

    public void SetTick()
    {
        tick.SetActive(true);
    }

    public void SetColor(Color32 col)
    {
        color = col;
        flowEnd.GetComponent<SpriteRenderer>().color = col;
        looseEnd.GetComponent<SpriteRenderer>().color = col;
        corner.GetComponent<SpriteRenderer>().color = col;
        for (int i = 0; i < flowDirections.Length; i++)
        {
            flowDirections[i].GetComponent<SpriteRenderer>().color = col;
        }
    }
}
