using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BoardManager : MonoBehaviour
{
    [SerializeField]
    GameObject tilePref;

    Tile[,] board;
    Map map;

    public void SetBoard(int sizeX, int sizeY)
    {
        sizeY = sizeY == 0 ? sizeX : sizeY;
        map.Width = sizeX;
        map.Height = sizeY;
        board = new Tile[sizeX, sizeY];
        for (int i = 0; i < board.GetLength(0); i++)
        {
            for (int j = 0; j < board.GetLength(1); j++)
            {
                GameObject tile = Instantiate(tilePref, new Vector2(i, j), Quaternion.identity, transform);
                board[i, j] = tile.GetComponent<Tile>();
            }
        }
    }

    public void LoadMap(string[] flows)
    {
        map.LoadMap(flows);
    }
}
