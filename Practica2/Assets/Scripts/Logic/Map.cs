using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Map
{
    Flow[] flows;
    Flow touchingFlow;
    int touchingIndex;
    Vector2Int[] flowEnds;

    public int Width { get; set; }
    public int Height { get; set; }

    public bool IsComplete()
    {
        foreach (Flow f in flows)
        {
            if (!f.IsComplete()) return false;
        }
        return true;
    }
    // TODO: comprobacion de errores
    public void LoadMap(string[] flowStrings)
    {
        flows = new Flow[flowStrings.Length];

        int i = 0;
        foreach (string f in flowStrings)
        {
            string[] pos = f.Split(','); //Dividimos el string del flow en "numeros"
            int[] absFlow = System.Array.ConvertAll(pos, s => int.Parse(s)); //Pasamos los "numeros" a numeros
            Vector2Int[] flow = System.Array.ConvertAll(absFlow, s => new Vector2Int(s / Height, s % Height)); //Pasamos los numeros a posiciones
            flows[i++] = new Flow(flow);
        }
    }

    public List<Change> TouchedHere(Vector2Int pos)
    {
        Debug.Log(pos);
        if (touchingFlow == null)
        {
            GetFlow(pos);
            return touchingFlow.StartNewFlow(pos);
        }
        else
        {
            List<Change> changes = touchingFlow.AddFlow(pos);
            for (int i = 0; i < changes.Count; i++)
            {
                changes[i].index = touchingIndex;
            }
            return changes;
        }
    }

    public void Untouched()
    {
        touchingFlow = null;
    }

    private void GetFlow(Vector2Int pos)
    {
        int i = 0;
        foreach (var flow in flows)
        {
            if (flow.beingTouched(pos))
            {
                touchingFlow = flow;
                touchingIndex = i;
            }
            i++;
        }
    }

    public Vector2Int[] GetFlowEnds()
    {
        Vector2Int[] ends = new Vector2Int[flows.Length * 2];
        int i = 0;
        foreach (Flow f in flows)
        {
            ends[i++] = f.GetFirstEnd();
            ends[i++] = f.GetLastEnd();
        }
        return ends;
    }
}
