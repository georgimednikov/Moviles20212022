using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Map
{
    Flow[] flows;
    List<int> hintFlows;
    Flow touchingFlow;
    LogicTile tileToAnim;
    LogicTile[] lastMovedFlow;
    LogicTile[] flowEnds;
    int lastMovedIndex;
    int lastMovedMovements;
    public int touchingIndex { get; private set; }
    public int movements { get; private set; }
    public int percentageFull { get; private set; }
    public int numFlowsComplete { get; private set; }
    int prevTouchingIndex, startTouchFlowSize;

    public bool[] flowsToRender { get; private set; }
    public List<LogicTile> posToReset { get; private set; }

    public int Width { get; set; }
    public int Height { get; set; }

    public LogicTile[] GetFlow(int i) { return flows[i].GetPositions(); }

    public LogicTile[,] tileBoard;

    int emptyTiles = 0;

    public Map()
    {
        touchingIndex = -1;
        movements = 0;
        percentageFull = 0;
        prevTouchingIndex = -1;
        numFlowsComplete = 0;
    }

    public int GiveHint()
    {
        // TODO: no es solo random, coge al mas molesto, y ademas no coge flows solved
        if (hintFlows.Count == 0) return -1;

        int randomIndex = UnityEngine.Random.Range(0, hintFlows.Count - 1);
        flows[hintFlows[randomIndex]].Solve();

        //Se asignan los touching para que cuando se deje de hacer click
        //en la pista se procesa como si se hubiera realizado un movimiento
        touchingFlow = flows[hintFlows[randomIndex]];
        touchingIndex = hintFlows[randomIndex];

        for (int i = 0; i < flows.Length; ++i)
        {
            CommitFlow(i);
            flowsToRender[i] = true;
        }

        hintFlows.RemoveAt(randomIndex);
        lastMovedFlow = null;
        lastMovedMovements = movements;
        lastMovedIndex = -1;

        return touchingIndex;
    }

    public bool IsFlowSolved(int flow)
    {
        return flows[flow].IsSolved();
    }

    public bool IsGameSolved()
    {
        foreach (Flow f in flows)
        {
            if (!f.IsSolved())
                return false;
        }
        return true;
    }
    // TODO: comprobacion de errores
    public void LoadMap(string[] flowStrings, string[] levelInfo)
    {
        flows = new Flow[flowStrings.Length];
        hintFlows = new List<int>();
        for (int i = 0; i < flows.Length; ++i)
        {
            hintFlows.Add(i);
        }
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
            Vector2Int[] flow = System.Array.ConvertAll(absFlow, s => new Vector2Int(s % Width, s / Width)); //Pasamos los numeros a posiciones

            LogicTile[] tiles = new LogicTile[flow.Length];
            for (int j = 0; j < tiles.Length; j++)
                tiles[j] = tileBoard[flow[j].x, flow[j].y];
            flows[i] = new Flow(tiles);
        }

        // Se comprueba que parametros adicionales tiene el nivel y se gestionan.
        int a = 0, b;
        string[] aux;
        if (levelInfo.Length > 0)
        {
            if (levelInfo[0] != "")
            {
                aux = levelInfo[0].Split(':');
                foreach (var n in aux)
                {
                    a = int.Parse(n);
                    tileBoard[a % Width, a / Width].tileType = LogicTile.TileType.BRIDGE;
                }
            }
            if (levelInfo.Length > 1)
            {
                if (levelInfo[1] != "")
                {
                    aux = levelInfo[1].Split(':');
                    foreach (var n in aux)
                    {
                        a = int.Parse(n);
                        tileBoard[a % Width, a / Width].tileType = LogicTile.TileType.EMPTY;
                        emptyTiles++;
                    }
                }
                if (levelInfo.Length > 2)
                {

                    aux = levelInfo[2].Split(':', '|');
                    for (int j = 0; j < aux.Length;)
                    {
                        a = int.Parse(aux[j++]);
                        b = int.Parse(aux[j++]);
                        Vector2Int start = new Vector2Int(a % Width, a / Width);
                        Vector2Int end = new Vector2Int(b % Width, b / Width);
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
        tileToAnim = null; //Suponemos que no hay que animar ninguna tile
        if (touchingFlow == null)
        {
            if (GetFlow(pos))
            {
                lastMovedFlow = touchingFlow.GetPositions();
                lastMovedMovements = movements;
                lastMovedIndex = touchingIndex;
                if (touchingFlow.IsEnd(tile))
                {
                    tileToAnim = tileBoard[pos.x, pos.y]; //Si se da la condici�n se asigna
                    AddToReset(touchingIndex, 0);
                    if (touchingFlow.StartNewFlow(tile))
                        flowsToRender[touchingIndex] = true;
                }
                startTouchFlowSize = touchingFlow.GetPositions().Length;
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
        if (touchingFlow != null && touchingIndex != prevTouchingIndex && touchingFlow.GetPositions().Length != startTouchFlowSize) movements++;
        //Solo se reasigna si has tocado en un lugar v�lido
        if (touchingFlow != null) prevTouchingIndex = touchingIndex;
        touchingIndex = -1;
        float sum = 0;
        int numComp = 0;
        touchingFlow = null;
        foreach (var flow in flows)
        {
            sum += flow.GetNumPipes() - 2;
            if (flow.completed) numComp++;
        }

        numFlowsComplete = numComp;
        percentageFull = (int)(100 * (sum / ((Width * Height) - 2 * flows.Length - emptyTiles)));
    }

    public LogicTile TileToAnimate()
    {
        //Si hay que animar una tile se coge el extremo contrario al que se ha pulsado
        if (tileToAnim != null)
        {
            LogicTile[] ends = GetFlowEnds(touchingIndex);
            foreach (LogicTile t in ends)
                if (tileToAnim != t)
                    return t;
        }
        return null;
    }

    public int UndoMove()
    {
        if (lastMovedFlow == null) return -1;
        Flow flow = flows[lastMovedIndex];
        AddToReset(lastMovedIndex);
        flow.UndoMove(lastMovedFlow);
        movements = lastMovedMovements;
        touchingIndex = lastMovedIndex;
        return touchingIndex;
    }

    public int GetNumFlows()
    {
        return flows.Length;
    }

    public LogicTile[] GetFlowEnds(int flow)
    {
        if (flowEnds == null) GetFlowEnds();
        LogicTile[] tiles = new LogicTile[2];
        tiles[0] = flowEnds[flow * 2];
        tiles[1] = flowEnds[flow * 2 + 1];
        return tiles;
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
        //porque no se ha podido romper con otro (se romper�a el otro no este)
        if (flowIndex == touchingIndex) return;

        //Se comprueba para cada flow si colisiona con otro.
        //Si lo hace se guardan los cambios (el flow que se estaba tocando
        //invadi� su camino y tiene que retroceder el invadido)
        for (int i = 1; i < flows[flowIndex].GetPositions().Length; ++i)
        {
            LogicTile fpos = flows[flowIndex].GetPositions()[i];
            foreach (LogicTile touchpos in flows[touchingIndex].GetPositions())
            {
                if (fpos.pos == touchpos.pos)
                {
                    flows[flowIndex].CommitChanges(i);
                }
            }
        }
    }

    private bool GetFlow(Vector2Int p)
    {
        int i = 0;
        foreach (var flow in flows)
        {
            if (flow.BeingTouched(tileBoard[p.x, p.y]))
            {
                touchingFlow = flow;
                touchingIndex = i;
                return true;
            }
            i++;
        }
        return false;
    }

    private bool DifferentFlowEnd(LogicTile p)
    {
        for (int i = 0; i < GetFlowEnds().Length; i++)
            if (p == GetFlowEnds()[i] && touchingIndex != i / 2) return true;
        return false;
    }

    private void CheckFlowCollision(LogicTile p)
    {
        for (int i = 0; i < flows.Length; i++)
        {
            Flow f = flows[i];
            int coll = f.CollidesWithFlow(p);

            //Si hay colisi�n y no es con el flow que se est� modificando (no te cortas a ti mismo)
            if (coll != -1 && !(i == touchingIndex && p == touchingFlow.GetLastPosition()))
            {
                //Si se pasa sobre un puente y se recorre en direcciones distintas no hay colisi�n
                if (p.tileType == LogicTile.TileType.BRIDGE)
                {
                    LogicTile[] flowPositions = f.GetPositions();
                    Direction newBridgeDir = Flow.VectorsToDir(touchingFlow.GetLastPosition().pos, p.pos);
                    Direction prevBridgeDir1;
                    Direction prevBridgeDir2 = Flow.VectorsToDir(flowPositions[coll - 1].pos, flowPositions[coll].pos, out prevBridgeDir1);

                    //Comprobar que no vayan en el mismo eje = direcci�n perpendicular
                    if (newBridgeDir != prevBridgeDir1 && newBridgeDir != prevBridgeDir2) continue;
                }
                // Si es otro flujo, hay que quitar un indice mas porque la posicion que se comprueba pasa a ser del touchingFlow
                // Si es el mismo flow no hace falta porque sigue perteneciendo a el
                if (touchingIndex != i)
                    coll--;
                AddToReset(i, coll);
                return;
            }
        }
    }

    private void AddToReset(int flowIndex, int posIndex = 0)
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
