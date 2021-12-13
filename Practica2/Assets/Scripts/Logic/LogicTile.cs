using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class LogicTile
{
    public enum TileType
    {
        NORMAL,
        BRIDGE,
        EMPTY
    }

    public LogicTile(Vector2Int p)
    {
        pos = p;
        tileType = TileType.NORMAL;
        walls = new bool[4];
    }

    public Vector2Int pos;
    public TileType tileType;
    public bool[] walls;

}
