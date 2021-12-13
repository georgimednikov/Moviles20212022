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
    LogicTile[] flowEnds;
    int prevTouchingIndex, prevTouchFlowSize;

    public bool[] flowsToRender { get; private set; }
    public List<LogicTile> posToReset { get; private set; }

    public int Width { get; set; }
    public int Height { get; set; }

    public LogicTile[] GetFlow(int i) { return flows[i].GetPositions(); }

    LogicTile[,] tileBoard;

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
    public void LoadMap(string[] flowStrings, string[] levelInfo)
    {
        flows = new Flow[flowStrings.Length];
        flowsToRender = new bool[flows.Length];
        posToReset = new List<LogicTile>();
        tileBoard = new LogicTile[Width, Height];
        for (int i = 0; i < Width; i++)
            for (int j = 0; j < Height; j++)
                tileBoard[i, j] = new LogicTile(new Vector2Int(i, j));

        for (int i = 0; i < flowStrings.Length; i++)
        {
            string[] pos = flowStrings[i].Split(','); //Dividimos el string del flow en "numeros"
            int[] absFlow = System.Array.ConvertAll(pos, s => int.Parse(s)); //Pasamos los "numeros" a numeros
            Vector2Int[] flow = System.Array.ConvertAll(absFlow, s => new Vector2Int(s / Height, s % Height)); //Pasamos los numeros a posiciones

            LogicTile[] tiles = new LogicTile[flow.Length];
            for (int j = 0; j < tiles.Length; j++)
                tiles[j] = tileBoard[flow[j].x, flow[j].y];
            flows[i] = new Flow(tiles);
        }

        // Se comprueba que parametros adicionales tiene el nivel y se gestionan.
        int a, b;
        string[] aux;
        if (levelInfo.Length > 0)
        {
            aux = levelInfo[0].Split(':');
            foreach (var n in aux)
            {
                a = int.Parse(n);
                tileBoard[a / Height, a % Height].tileType = LogicTile.TileType.BRIDGE;
            }
            if (levelInfo.Length > 1)
            {
                aux = levelInfo[1].Split(':');
                foreach (var n in aux)
                {
                    a = int.Parse(n);
                    tileBoard[a / Height, a % Height].tileType = LogicTile.TileType.EMPTY;
                }
                if (levelInfo.Length > 2)
                {
                    aux = levelInfo[2].Split(':', '|');
                    for (int j = 0; j < aux.Length;)
                    {
                        a = int.Parse(aux[j++]);
                        b = int.Parse(aux[j++]);
                        Vector2Int start = new Vector2Int(a / Height, a % Height);
                        Vector2Int end = new Vector2Int(b / Height, b % Height);
                        Direction endDir;
                        Direction startDir = Flow.VectorsToDir(start, end, out endDir);
                        tileBoard[start.x, start.y].walls[(int)startDir] = true;
                        tileBoard[end.x, end.y].walls[(int)endDir] = true;
                    }
                }
            }
        }
    }

    public void TouchedHere(Vector2Int pos)
    {
        LogicTile tile = tileBoard[pos.x, pos.y];
        if (touchingFlow == null)
        {
            if (GetFlow(pos) && touchingFlow.IsEnd(tile))
            {
                AddToReset(touchingIndex, 0);
                if (touchingFlow.StartNewFlow(tile))
                    flowsToRender[touchingIndex] = true;
                prevTouchFlowSize = touchingFlow.GetPositions().Length;
            }
        }
        else
        {
            if (DifferentFlowEnd(tile)) return;
            CheckFlowCollision(tile);
            if (touchingFlow.AddFlow(tile))
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

    public LogicTile[] GetFlowEnds()
    {
        if (flowEnds != null) return flowEnds;
        flowEnds = new LogicTile[flows.Length * 2];
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
            LogicTile fpos = flows[flowIndex].GetPositions()[i];
            foreach (LogicTile touchpos in flows[touchingIndex].GetPositions())
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
            if (flow.BeingTouched(tileBoard[pos.x, pos.y]))
            {
                touchingFlow = flow;
                touchingIndex = i;
                return true;
            }
            i++;
        }
        return false;
    }

    private bool DifferentFlowEnd(LogicTile pos)
    {
        for (int i = 0; i < GetFlowEnds().Length; i++)
            if (pos == GetFlowEnds()[i] && touchingIndex != i / 2) return true;
        return false;
    }

    private void CheckFlowCollision(LogicTile pos)
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
        LogicTile[] collFlow = f.GetPositions(posIndex);
        foreach (LogicTile p in collFlow)
        {
            posToReset.Add(p);
        }
        // +1 porque no queremos perder posIndex, solo los posteriores ya que sigue perteneciendo al flow
        if (flowIndex == touchingIndex) f.RemovePositions(posIndex + 1);
        flowsToRender[flowIndex] = true;
    }
}
