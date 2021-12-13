using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Change
{
    public enum ChangeType
    {
        ADD,
        REMOVE,
        RESET
    }

    public ChangeType action;
    public Vector2Int pos;
    public Direction dir;
    public int index;
}