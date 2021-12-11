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

    public bool IsSolved()
    {
        foreach (Flow f in flows)
        {
            if (!f.IsSolved()) return false;
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
        List<Change> changes = new List<Change>();
        if (touchingFlow == null)
        {
            if (GetFlow(pos)) return touchingFlow.StartNewFlow(pos);
            return changes;
        }
        else
        {
            for (int i = 0; i < GetFlowEnds().Length; i++)
                if (pos == GetFlowEnds()[i] && touchingIndex != i / 2) return changes;
            changes = touchingFlow.AddFlow(pos);
            for (int i = 0; i < changes.Count; i++)
            {
                changes[i].index = touchingIndex;
            }
            return changes;
        }
    }

    public void StoppedTouching()
    {
        touchingFlow = null;
    }

    private bool GetFlow(Vector2Int pos)
    {
        int i = 0;
        foreach (var flow in flows)
        {
            if (flow.BeingTouched(pos))
            {
                touchingFlow = flow;
                touchingIndex = i;
                return true;
            }
            i++;
        }
        return false;
    }

    public Vector2Int[] GetFlowEnds()
    {
        if (flowEnds != null) return flowEnds;
        flowEnds = new Vector2Int[flows.Length * 2];
        int i = 0;
        foreach (Flow f in flows)
        {
            flowEnds[i++] = f.GetFirstEnd();
            flowEnds[i++] = f.GetLastEnd();
        }
        return flowEnds;
    }
}
