using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Map
{
    Flow[] flows;

    public int Width { get; set; }
    public int Height { get; set; }

    public bool IsComplete()
    {
        foreach(Flow f in flows)
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
