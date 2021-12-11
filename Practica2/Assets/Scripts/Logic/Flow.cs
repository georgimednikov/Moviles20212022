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
    public void AddFlow(Vector2Int flow)
    {
        hasBeenModified = true;
        for (int i = 0; i < positions.Count; i++)
        {
            if (positions[i] == flow)
            {
                positions.RemoveRange(i + 1, positions.Count - (i + 1));
                return;
            }
        }
        positions.Add(flow);
    }

    public Vector2Int GetFirstEnd() { return solution.First.Value; }
    public Vector2Int GetLastEnd() { return solution.Last.Value; }
}
