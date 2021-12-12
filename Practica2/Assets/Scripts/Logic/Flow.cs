using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Flow
{
    List<Vector2Int> positions;
    LinkedList<Vector2Int> solution;
    bool hasBeenModified = false;
    bool solved = false;
    bool completed = false;

    public Flow(Vector2Int[] sol)
    {
        positions = new List<Vector2Int>();
        solution = new LinkedList<Vector2Int>(sol);
    }

    public Vector2Int[] GetPositions(int p = 0) {
        return positions.GetRange(p, positions.Count - p).ToArray(); 
    }
    public void RemovePositions(int p)
    {
        if (positions.Count == 0) return;
        positions.RemoveRange(p, positions.Count - p);
    }

    public Vector2Int GetLastPosition() {
        if (positions.Count == 0) return new Vector2Int(-1, -1);
        return positions[positions.Count - 1]; 
    }

    public void CommitChanges(int pos) {
        positions.RemoveRange(pos, positions.Count - pos);
    }

    public bool IsSolved()
    {
        if (!completed) return false;
        if (!hasBeenModified) return solved;
        bool inversed = false;
        LinkedListNode<Vector2Int> node;

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

    public bool StartNewFlow(Vector2Int flow)
    {
        int size = positions.Count;
        hasBeenModified = true;
        completed = false;
        positions.Clear();
        positions.Add(flow);
        return size > 0;
    }

    public bool AddFlow(Vector2Int flow)
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

    public Vector2Int GetFirstEnd() { return solution.First.Value; }
    public Vector2Int GetLastEnd() { return solution.Last.Value; }

    public bool BeingTouched(Vector2Int pos)
    {
        if (IsEnd(pos)) return true;
        foreach (var p in positions)
        {
            if (p == pos) return true;
        }
        return false;
    }

    public int CollidesWithFlow(Vector2Int pos)
    {
        for (int i = 0; i < positions.Count; i++)
        {
            if (positions[i] == pos) return i;
        }
        return -1;
    }

    public bool IsEnd(Vector2Int pos)
    {
        return pos == GetFirstEnd() || pos == GetLastEnd();
    }

    bool HasAdjacent(Vector2Int pos)
    {
        return (pos - positions[positions.Count - 1]).magnitude == 1;
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
