using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BoardManager : MonoBehaviour
{
    [SerializeField]
    GameObject tile;

    int[,] board;
    int numPipes;

    public void SetBoardSize(int sizeX, int sizeY)
    {
        board = new int[sizeY == 0 ? sizeX : sizeY, sizeX];
    }
    public void SetNumPipes(int n)
    {
        numPipes = n;
    }
}
