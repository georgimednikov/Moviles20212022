using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BoardManager : MonoBehaviour
{
    [SerializeField]
    GameObject tilePref;

    float topHeight, botHeight, refWidth;
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
        Vector2Int boardPos = new Vector2Int(Mathf.FloorToInt(pos.x + (map.Width) / 2.0f), Mathf.FloorToInt(pos.y + (map.Height) / 2.0f - transform.position.y));
        if (boardPos.x < 0 || boardPos.x >= map.Width || boardPos.y < 0 || boardPos.y >= map.Height) return;
        map.TouchedHere(boardPos);

        foreach (Vector2Int p in map.posToReset)
        {
            board[p.x, p.y].Reset();
        }
        map.posToReset.Clear();
        RenderFlow(map.touchingIndex);
        for (int i = 0; i < map.flowsToRender.Length; i++)
        {
            if (map.flowsToRender[i] && i != map.touchingIndex)
            {
                RenderFlow(i);
            }
        }
    }

    private void RenderFlow(int flowToRender)
    {
        Vector2Int[] flow = map.GetFlow(flowToRender);
        Vector2Int[] touchingFlow = map.GetFlow(map.touchingIndex);
        board[flow[0].x, flow[0].y].Reset();
        for (int i = 1; i < flow.Length; ++i)
        {
            Vector2Int pos = flow[i], prev = flow[i - 1];
            Tile tile = board[pos.x, pos.y], tilePrev = board[prev.x, prev.y];
            if (flowToRender != map.touchingIndex)
            {
                bool collWithTouching = false;
                for (int j = 1; j < touchingFlow.Length; j++)
                {
                    if (pos == touchingFlow[j])
                    {
                        collWithTouching = true;
                        break;
                    }
                }
                if (collWithTouching) break;
            }
            tile.Reset();
            tile.SetColor(GameManager.instance.skinPack.colors[flowToRender]);
            Direction opposite;
            Direction dir = Flow.VectorsToDir(pos, prev, out opposite);
            if (dir != Direction.NONE)
            {
                tile.SetConnectedDirections(dir);
                tilePrev.SetConnectedDirections(opposite);
            }
            tilePrev.SetColor(GameManager.instance.skinPack.colors[flowToRender]);
        }
    }

    public void StoppedTouching()
    {
        for (int i = 0; i < map.flowsToRender.Length; i++)
            if (map.flowsToRender[i]) {
                map.CommitFlow(i);
                map.flowsToRender[i]  = false;
            }

        map.StoppedTouching();
        if (map.IsSolved())
            Debug.LogError("HAS GANADO :M");
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
