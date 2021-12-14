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
        if (positions.Count < 2) return 2;
        return positions.Count + 1;
    }

    public LogicTile[] GetPositions(int p = 0) {
        return positions.GetRange(p, positions.Count - p).ToArray(); 
    }
    public void RemovePositions(int p)
    {
        if (positions.Count == 0) return;
        if(positions.Count - p > 0) completed = false;
        positions.RemoveRange(p, positions.Count - p);
    }

    public LogicTile GetLastPosition() {
        if (positions.Count == 0) return new LogicTile(new Vector2Int(-1, -1));
        return positions[positions.Count - 1]; 
    }

    public void CommitChanges(int pos) {
        if (positions.Count - pos > 0) completed = false;
        positions.RemoveRange(pos, positions.Count - pos);
    }

    public bool IsSolved()
    {
        if (!completed) return false;
        if (!hasBeenModified) return solved;
        bool inversed = false;
        LinkedListNode<LogicTile> node;

        if (positions[0] == solution.Last.Value)
        {
            inversed = true;
            node = solution.Last;
        }
        else node = solution.First;
        bool end = false;
        for (int i = 1; i < positions.Count && !end; i++)
        {
            node = inversed ? node.Previous : node.Next;
            if (positions[i] != node.Value)
            {
                end = true;
                break;
            }
        }
        if (end)
            solved = false;
        else solved = true;
        hasBeenModified = false;
        return solved;
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
            !HasAdjacent(flow))) return false;
        hasBeenModified = true;
        positions.Add(flow);
        return true;
    }

    public LogicTile GetFirstEnd() { return solution.First.Value; }
    public LogicTile GetLastEnd() { return solution.Last.Value; }

    public bool BeingTouched(LogicTile pos)
    {
        if (IsEnd(pos)) return true;
        foreach (LogicTile p in positions)
        {
            if (p == pos) return true;
        }
        return false;
    }

    public int CollidesWithFlow(LogicTile pos)
    {
        for (int i = 0; i < positions.Count; i++)
        {
            if (positions[i] == pos) return i;
        }
        return -1;
    }

    public bool IsEnd(LogicTile pos)
    {
        return pos == GetFirstEnd() || pos == GetLastEnd();
    }

    bool HasAdjacent(LogicTile pos)
    {
        return (pos.pos - positions[positions.Count - 1].pos).magnitude == 1;
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
