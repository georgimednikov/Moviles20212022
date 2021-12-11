using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BoardManager : MonoBehaviour
{
    [SerializeField]
    GameObject tilePref;

    float topHeight, botHeight, refWidth;
    Vector2Int gridPos;
    Tile[,] board;
    Map map;

    public void SetUIHeight(float topHeight, float botHeight, float refWidth)
    {
        this.topHeight = topHeight;
        this.botHeight = botHeight;
        this.refWidth = refWidth;
    }

    public void SetBoard(int sizeX, int sizeY)
    {
        sizeY = sizeY == 0 ? sizeX : sizeY;
        map = new Map();
        map.Width = sizeX;
        map.Height = sizeY;
        board = new Tile[sizeX, sizeY];
        for (int i = 0; i < board.GetLength(0); i++)
        {
            for (int j = 0; j < board.GetLength(1); j++)
            {
                //Se offsetean las posiciones de las tiles para que la posición del gameObject padre cuadren con el centro del tablero
                GameObject tile = Instantiate(tilePref, new Vector2(i - (map.Width - 1) / 2.0f, j - (map.Height - 1) / 2.0f), Quaternion.identity, transform);
                board[i, j] = tile.GetComponent<Tile>();
            }
        }
        ArrangeInScreen();
    }

    public void TouchedHere(Vector3 pos)
    {
        Vector2Int boardPos = new Vector2Int(Mathf.FloorToInt(pos.x + (map.Width) / 2.0f), Mathf.FloorToInt(pos.y + (map.Height) / 2.0f));
        Debug.Log(boardPos);
        if (boardPos.x < 0 || boardPos.x >= map.Width || boardPos.y < 0 || boardPos.y >= map.Height) return;
        var changes = map.TouchedHere(boardPos);// + transform.position.y)));

        //Debug.Log(changes);
        if (changes == null) return;
        foreach (var change in changes)
        {
            Tile tile = board[change.pos.x, change.pos.y];
            switch (change.action)
            {
                case Change.ChangeType.ADD:
                    tile.SetColor(GameManager.instance.skinPack.colors[change.index]);
                    tile.SetConnectedDirections(change.dir);
                    break;
                case Change.ChangeType.REMOVE:
                    tile.DisconnectDirections(change.dir);
                    break;
                case Change.ChangeType.RESET:
                    tile.Reset();
                    break;
                default:
                    break;
            }
        }
    }

    public void StoppedTouching()
    {
        map.StoppedTouching();
        if (map.IsSolved())
            ;
    }

    void ArrangeInScreen()
    {
        float boardScreenMaxHeight = Camera.main.orthographicSize * 2 * (1 - (topHeight + botHeight) / Camera.main.scaledPixelHeight * Camera.main.scaledPixelWidth / refWidth);
        float cameraWidthSize = Camera.main.orthographicSize * 2 * Camera.main.aspect;
        if (cameraWidthSize > boardScreenMaxHeight)//hay muchas tiles en vertical
        {
            transform.localScale = new Vector3(boardScreenMaxHeight / (map.Height), boardScreenMaxHeight / (map.Height), 1);
            // Hay que coger el height como valor limite de la pantalla
        }
        else
        {
            transform.localScale = new Vector3(cameraWidthSize / (map.Width), cameraWidthSize / (map.Width), 1);
        }
        //Se offsetea en Y en base a los márgenes del canvas
        transform.Translate(0, (botHeight - topHeight) / Camera.main.scaledPixelHeight * Camera.main.orthographicSize * Camera.main.scaledPixelWidth / refWidth, 0);
    }

    public void LoadMap(string[] flows)
    {
        map.LoadMap(flows);
        Vector2Int[] flowEnds = map.GetFlowEnds();
        int i = 0;
        Color32[] colorPool = GameManager.instance.skinPack.colors;
        foreach (Vector2Int end in flowEnds)
        {
            Tile tile = board[end.x, end.y];
            tile.SetFlowEnd();
            //Módulo para que si no hay suficientes colores se repitan; i++ / 2 para pasar de color cada dos extremos
            tile.SetColor(colorPool[((i++) % colorPool.Length) / 2]);
        }
    }
}
