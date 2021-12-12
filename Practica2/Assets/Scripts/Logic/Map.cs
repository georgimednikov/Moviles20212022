using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Map
{
    Flow[] flows;
    Flow touchingFlow;
    public int touchingIndex { get; private set; }
    public int movements { get; private set; }
    public int percentageFull { get; private set; }
    public int numFlowsComplete { get; private set; }
    Vector2Int[] flowEnds;
    int prevTouchingIndex, prevTouchFlowSize;

    public bool[] flowsToRender { get; private set; }
    public List<Vector2Int> posToReset { get; private set; }

    public int Width { get; set; }
    public int Height { get; set; }

    public Vector2Int[] GetFlow(int i) { return flows[i].GetPositions(); }

    public Map()
    {
        touchingIndex = -1;
        movements = 0;
        percentageFull = 0;
        prevTouchingIndex = -1;
        numFlowsComplete = 0;
    }

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
        flowsToRender = new bool[flows.Length];
        posToReset = new List<Vector2Int>();
        int i = 0;
        foreach (string f in flowStrings)
        {
            string[] pos = f.Split(','); //Dividimos el string del flow en "numeros"
            int[] absFlow = System.Array.ConvertAll(pos, s => int.Parse(s)); //Pasamos los "numeros" a numeros
            Vector2Int[] flow = System.Array.ConvertAll(absFlow, s => new Vector2Int(s / Height, s % Height)); //Pasamos los numeros a posiciones
            flows[i++] = new Flow(flow);
        }
    }

    public void TouchedHere(Vector2Int pos)
    {
        if (touchingFlow == null)
        {
            if (GetFlow(pos) && touchingFlow.IsEnd(pos))
            {
                AddToReset(touchingIndex, 0);
                if (touchingFlow.StartNewFlow(pos))
                    flowsToRender[touchingIndex] = true;
                prevTouchFlowSize = touchingFlow.GetPositions().Length;
            }
        }
        else
        {
            if (DifferentFlowEnd(pos)) return;
            CheckFlowCollision(pos);
            if (touchingFlow.AddFlow(pos))
                flowsToRender[touchingIndex] = true;
        }
    }

    public void StoppedTouching()
    {
        if (touchingIndex != prevTouchingIndex && touchingFlow.GetPositions().Length != prevTouchFlowSize) movements++;
        touchingFlow = null;
        prevTouchingIndex = touchingIndex;
        touchingIndex = -1;
        float sum = 0;
        int numComp = 0;
        foreach (var flow in flows)
        {
            sum += flow.GetNumPipes() - 2;
            if (flow.completed) numComp++;
        }

        numFlowsComplete = numComp;
        percentageFull = (int)(100 * (sum / ((Width * Height) - 2 * flows.Length)));
    }

    public int GetNumFlows()
    {
        return flows.Length;
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

    public void CommitFlow(int flowIndex)
    {
        //Si es el mismo flow que es el que estaba modificando no se actualiza el flow
        //porque no se ha podido romper con otro (se rompería el otro no este)
        if (flowIndex == touchingIndex) return;

        //Se comprueba para cada flow si colisiona con otro.
        //Si lo hace se guardan los cambios (el flow que se estaba tocando
        //invadió su camino y tiene que retroceder el invadido)
        for (int i = 1; i < flows[flowIndex].GetPositions().Length; ++i)
        {
            Vector2Int fpos = flows[flowIndex].GetPositions()[i];
            foreach (Vector2Int touchpos in flows[touchingIndex].GetPositions())
            {
                if (fpos == touchpos)
                {
                    flows[flowIndex].CommitChanges(i);
                }
            }
        }
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

    private bool DifferentFlowEnd(Vector2Int pos)
    {
        for (int i = 0; i < GetFlowEnds().Length; i++)
            if (pos == GetFlowEnds()[i] && touchingIndex != i / 2) return true;
        return false;
    }

    private void CheckFlowCollision(Vector2Int pos)
    {
        for (int i = 0; i < flows.Length; i++)
        {
            Flow f = flows[i];
            int coll = f.CollidesWithFlow(pos);
            if (coll != -1 && !(i == touchingIndex && pos == touchingFlow.GetLastPosition()))
            {
                // Si es otro flujo, hay que quitar un indice mas porque la posicion que se comprueba pasa a ser del touchingFlow
                // Si es el mismo flow no hace falta porque sigue perteneciendo a el
                if (touchingIndex != i) 
                    coll--;
                AddToReset(i, coll);
                return;
            }
        }
    }

    private void AddToReset(int flowIndex, int posIndex)
    {
        Flow f = flows[flowIndex];
        Vector2Int[] collFlow = f.GetPositions(posIndex);
        foreach (Vector2Int p in collFlow)
        {
            posToReset.Add(p);
        }
        // +1 porque no queremos perder posIndex, solo los posteriores ya que sigue perteneciendo al flow
        if (flowIndex == touchingIndex) f.RemovePositions(posIndex + 1);
        flowsToRender[flowIndex] = true;
    }
}
