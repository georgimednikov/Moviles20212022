using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Flow
{
    List<Vector2Int> positions;
    LinkedList<Vector2Int> solution;
    bool hasBeenModified = false;
    bool completed = false;

    public Flow(Vector2Int[] sol)
    {
        positions = new List<Vector2Int>();
        solution = new LinkedList<Vector2Int>(sol);
    }
    public bool IsComplete()
    {
        if (!hasBeenModified) return completed;
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
            completed = false;
        else completed = true;
        hasBeenModified = false;
        return completed;
    }

    public List<Change> StartNewFlow(Vector2Int flow)
    {
        List<Change> changes = new List<Change>();
        hasBeenModified = true;
        if (positions.Count > 0)
        {

        }
        positions.Add(flow);
        return changes;
    }

    public List<Change> AddFlow(Vector2Int flow)
    {
        List<Change> changes = new List<Change>();
        hasBeenModified = true;
        for (int i = 0; i < positions.Count; i++)
        {
            if (positions[i] == flow)
            {
                for (int j = i; j < positions.Count - i; j++)
                {
                    Change resetChange = new Change();
                    resetChange.action = Change.ChangeType.RESET;
                    resetChange.pos = positions[j];
                }
                positions.RemoveRange(i, positions.Count - i);
                return changes;
            }
        }
        positions.Add(flow);
        Change change = new Change(), opposite = new Change();
        change.action = opposite.action = Change.ChangeType.ADD;
        change.pos = flow;
        opposite.pos = positions[positions.Count - 2];

        change.dir = VectorsToDir(change.pos, opposite.pos, ref opposite.dir);

        changes.Add(change);
        changes.Add(opposite);
        return changes;
    }

    public Vector2Int GetFirstEnd() { return solution.First.Value; }
    public Vector2Int GetLastEnd() { return solution.Last.Value; }

    public bool beingTouched(Vector2Int pos)
    {
        foreach (var p in positions)
        {
            if (p == pos) return true;
        }
        if (pos == GetFirstEnd()) return true;
        if (pos == GetLastEnd()) return true;

        return false;
    }

    Direction VectorsToDir(Vector2Int start, Vector2Int end, ref Direction opposite)
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
                return Direction.DOWN;
        }
    }
}
