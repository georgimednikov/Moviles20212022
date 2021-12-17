using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Flow
{
    List<LogicTile> positions;
    LinkedList<LogicTile> solution;
    bool hasBeenModified = false;
    bool solved = false;
    public bool completed { get; private set; }

    public Flow(LogicTile[] sol)
    {
        positions = new List<LogicTile>();
        solution = new LinkedList<LogicTile>(sol);
    }

    public int GetNumPipes()
    {
        if (completed) return positions.Count;
        //Los extremos siempre cuentan, aunque no se hayan metido todavía en el flow
        if (positions.Count < 2) return 2;
        //+1 por el extremo que no está todavía en el flow
        return positions.Count + 1;
    }

    public LogicTile GetFirstEnd() { return solution.First.Value; }
    public LogicTile GetLastEnd() { return solution.Last.Value; }

    public LogicTile GetPosition(int p)
    {
        return positions[p];
    }

    public LogicTile[] GetPositions(int p = 0) {
        return positions.GetRange(p, positions.Count - p).ToArray(); 
    }

    public LogicTile GetLastPosition() {
        if (positions.Count == 0) return null;
        return positions[positions.Count - 1]; 
    }

    public void RemovePositions(int p = 0)
    {
        if (positions.Count == 0) return;
        if(positions.Count - p > 0) completed = false;
        positions.RemoveRange(p, positions.Count - p);
    }

    public bool IsComplete()
    {
        return completed;
    }

    public bool IsSolved()
    {
        if (!completed) return false;
        if (!hasBeenModified) return solved;
        bool inversed = false;
        LinkedListNode<LogicTile> node;

        if (positions[0].pos == solution.Last.Value.pos)
        {
            inversed = true;
            node = solution.Last;
        }
        else node = solution.First;
        bool end = false;
        for (int i = 1; i < positions.Count && !end; i++)
        {
            node = inversed ? node.Previous : node.Next;
            if (positions[i].pos != node.Value.pos)
            {
                end = true;
                break;
            }
        }
        hasBeenModified = false;
        return solved = !end;
    }

    public bool IsEnd(LogicTile p)
    {
        return p.pos == GetFirstEnd().pos || p.pos == GetLastEnd().pos;
    }

    public LogicTile[] GetSolution()
    {
        LogicTile[] sol = new LogicTile[solution.Count];
        var node = solution.First;
        for (int i = 0; i < solution.Count; i++, node = node.Next)
            sol[i] = node.Value;
        return sol;
    }

    public void UndoMove(LogicTile[] p)
    {
        positions.Clear();
        foreach (LogicTile t in p)
        {
            AddFlow(t);
        }
    }
    public void CommitChanges(int p) {
        if (positions.Count - p > 0) completed = false;
        positions.RemoveRange(p, positions.Count - p);
    }

    public bool StartNewFlow(LogicTile flow)
    {
        int size = positions.Count;
        hasBeenModified = true;
        completed = false;
        positions.Clear();
        positions.Add(flow);
        return size > 0;
    }

    public bool AddFlow(LogicTile flow)
    {
        int coll = CollidesWithFlow(flow);
        //Se comprueba si está completo antes de la siguiente condición eliminatoria
        //para que si el valor es el mismo que el del frame anterior se actualice
        //aunque no se añada a la lista de posiciones.
        if (completed && coll == -1) return false;
        if (IsEnd(flow) && positions.Count > 1) completed = true;
        else completed = false;
        if (positions.Count > 0 &&
            (flow == positions[positions.Count - 1] ||
            !CanPlaceFlow(flow))) return false;
        hasBeenModified = true;
        positions.Add(flow);
        return true;
    }

    public bool BeingTouched(LogicTile p)
    {
        if (IsEnd(p)) return true;
        foreach (LogicTile po in positions)
        {
            if (po == p) return true;
        }
        return false;
    }

    //Devuelve el índice de la posición del flow donde ha habido una coincidencia; -1 si no hay
    public int CollidesWithFlow(LogicTile p)
    {
        for (int i = 0; i < positions.Count; i++)
        {
            if (positions[i] == p) return i;
        }
        return -1;
    }

    bool CanPlaceFlow(LogicTile p)
    {
        LogicTile prev = positions[positions.Count - 1];

        //Si la casilla está demasiado lejos o no se puede pone flujo sobre ella se devuelve false
        if (p.tileType == LogicTile.TileType.EMPTY ||
            (p.pos - prev.pos).magnitude != 1) return false;

        //Si hay una pared entre la nueva casilla p y la última ya establecida se devuelve false
        Direction prevToP;
        Direction pToPrev = VectorsToDir(p.pos, prev.pos, out prevToP);
        if (p.walls[(int)pToPrev]) return false;

        //Si la anterior es puente y la nueva tile intenta girar se devuelve false
        if (prev.tileType == LogicTile.TileType.BRIDGE)
        {
            LogicTile bridgeStart = positions[positions.Count - 2];
            Direction startToPrev = VectorsToDir(bridgeStart.pos, prev.pos);
            if (startToPrev != prevToP) return false;

            //                  p (Si está aquí está mal)
            //                ------
            // bridgeStart ->  prev  -> p (Si está aquí está bien)
            //                ------
        }

        //No se ha cumplido ninguna condición eliminatoria -> return true
        return true;
    }

    public static Direction VectorsToDir(Vector2Int start, Vector2Int end)
    {
        Direction d;
        return VectorsToDir(start, end, out d);
    }

    public static Direction VectorsToDir(Vector2Int start, Vector2Int end, out Direction opposite)
    {
        Vector2Int deltaPos = end - start;
        switch (deltaPos)
        {
            case { x: 0, y: 1 }:
                opposite = Direction.DOWN;
                return Direction.UP;
            case { x: 1, y: 0 }:
                opposite = Direction.LEFT;
                return Direction.RIGHT;
            case { x: -1, y: 0 }:
                opposite = Direction.RIGHT;
                return Direction.LEFT;
            case { x: 0, y: -1 }:
                opposite = Direction.UP;
                return Direction.DOWN;
            default:
                opposite = Direction.NONE;
                return Direction.NONE;
        }
    }
}
