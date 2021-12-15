using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Linq;

public class BoardManager : MonoBehaviour
{
    public LevelManager LM;

    [SerializeField] GameObject tilePref;
    [SerializeField] GameObject cursor;
    [SerializeField] GameObject star;
    [SerializeField] GameObject tick;

    Tile[,] board;
    Map map;
    float topHeight, botHeight, refWidth;
    float tileSize;
    Vector2 camSize;
    Vector2 baseOffset;

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

    public void ResetLevel()
    {
        topHeight = 0;
        botHeight = 0;
        refWidth = 0;
        foreach (Tile t in board)
        {
            Destroy(t.gameObject);
        }
        board = null;
        map = null;
        camSize = Vector2.zero;
        tileSize = 0;
        baseOffset = Vector2.zero;
        transform.position = Vector3.zero;
        transform.localScale = Vector3.one;
    }

    public void UndoMove()
    {
        int i = map.UndoMove();
        if (i == -1) return;
        RenderReset();
        RenderFlow(i);
    }

    public void GiveHint()
    {
        int i = map.GiveHint();
        if (i == -1) return;
        RenderFlows();
        // Reducir en 1 las pistas TODO
    }

    public void TouchedHere(Vector3 pos)
    {
        int x = Mathf.FloorToInt((pos.x - baseOffset.x - transform.position.x) / tileSize),
            y = Mathf.FloorToInt((pos.y - baseOffset.y - transform.position.y) / tileSize);

        cursor.SetActive(true);
        cursor.transform.position = pos;
        cursor.transform.Translate(Vector3.forward * 11);
        // cursor.getComponent<Image>().setColor() TODO

        if (x < 0 || x >= map.Width || y < 0 || y >= map.Height) return;
        map.TouchedHere(new Vector2Int(x, y));

        RenderReset();
        map.posToReset.Clear();
        RenderFlows();
    }

    private void RenderFlows()
    {
        if (map.touchingIndex != -1)
            RenderFlow(map.touchingIndex);
        for (int i = 0; i < map.flowsToRender.Length; i++)
        {
            if (map.flowsToRender[i] && i != map.touchingIndex)
            {
                RenderFlow(i);
            }
        }
    }

    private void RenderReset()
    {
        foreach (LogicTile p in map.posToReset)
        {
            board[p.pos.x, p.pos.y].Reset();
        }
    }

    private void RenderFlow(int flowToRender)
    {
        LogicTile[] flow = map.GetFlow(flowToRender);
        //Si el flow no tiene longitud (pasa al hacerle undo a un flow con un único movimiento) no se renderiza
        if (flow.Length == 0) return;

        LogicTile[] touchingFlow = map.GetFlow(map.touchingIndex);
        board[flow[0].pos.x, flow[0].pos.y].Reset();
        for (int i = 1; i < flow.Length; ++i)
        {
            LogicTile p = flow[i], prev = flow[i - 1];
            Tile tile = board[p.pos.x, p.pos.y], tilePrev = board[prev.pos.x, prev.pos.y];
            if (flowToRender != map.touchingIndex)
            {
                bool collWithTouching = false;
                for (int j = 1; j < touchingFlow.Length; j++)
                {
                    if (p == touchingFlow[j])
                    {
                        collWithTouching = true;
                        break;
                    }
                }
                if (collWithTouching) break;
            }
            if (p.tileType != LogicTile.TileType.BRIDGE) tile.Reset();
            tile.SetColor(GameManager.instance.skinPack.colors[flowToRender]);
            Direction opposite;
            Direction dir = Flow.VectorsToDir(p.pos, prev.pos, out opposite);
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
        cursor.SetActive(false);
        for (int i = 0; i < map.flowsToRender.Length; i++)
            if (map.flowsToRender[i])
            {
                map.CommitFlow(i);
                map.flowsToRender[i] = false;
            }

        map.StoppedTouching();
        LM.UpdateInfo(map.movements, map.percentageFull, map.numFlowsComplete, map.GetNumFlows());
        if (map.IsSolved())
        {
            ToggleInput(false);
            FadeOutAnimation fadeOut = Instantiate(map.movements == map.GetNumFlows() ? star : tick).GetComponent<FadeOutAnimation>();
            fadeOut.AddListener(ShowEndScreen);
        }
    }

    public void ToggleInput(bool enabled)
    {
        GetComponent<InputManager>().inputEnabled = enabled;
    }

    public void ShowEndScreen()
    {
        LM.GameFinished(map.movements == map.GetNumFlows(), map.movements);
    }

    void ArrangeInScreen()
    {
        float boardScreenMaxHeight = Camera.main.orthographicSize * 2 * (1 - (topHeight + botHeight) / Camera.main.scaledPixelHeight * Camera.main.scaledPixelWidth / refWidth);
        float cameraWidthSize = Camera.main.orthographicSize * 2 * Camera.main.aspect;
        if (cameraWidthSize > boardScreenMaxHeight)//hay muchas tiles en vertical
        {
            // Hay que coger el height como valor limite de la pantalla
            transform.localScale = new Vector3(boardScreenMaxHeight / (map.Height), boardScreenMaxHeight / (map.Height), 1);
        }
        else
        {
            camSize = new Vector2(Camera.main.orthographicSize * Camera.main.aspect, Camera.main.orthographicSize);
            tileSize = camSize.x * 2.0f / map.Width;
            baseOffset = new Vector2(-tileSize * map.Width / 2.0f, -tileSize * map.Height / 2.0f);
            transform.localScale = new Vector3(cameraWidthSize / (map.Width), cameraWidthSize / (map.Width), 1);
        }
        //Se offsetea en Y en base a los márgenes del canvas
        transform.Translate(0, (botHeight - topHeight) / Camera.main.scaledPixelHeight * Camera.main.orthographicSize * Camera.main.scaledPixelWidth / refWidth, 0);
    }

    public void LoadMap(string[] flows)
    {
        // Nos saltamos 4 para quitar informacion inecesaria para Map
        map.LoadMap(flows.Skip(1).ToArray(), flows[0].Split(',').Skip(4).ToArray());
        LogicTile[] flowEnds = map.GetFlowEnds();
        int i = 0;
        Color32[] colorPool = GameManager.instance.skinPack.colors;
        foreach (LogicTile end in flowEnds)
        {
            Tile tile = board[end.pos.x, end.pos.y];
            tile.SetFlowEnd();
            //Módulo para que si no hay suficientes colores se repitan; i++ / 2 para pasar de color cada dos extremos
            tile.SetColor(colorPool[((i++) / 2) % colorPool.Length]);
        }

        foreach (var tile in map.tileBoard)
        {
            if (tile.tileType == LogicTile.TileType.EMPTY) board[tile.pos.x, tile.pos.y].Deactivate();
            for (int j = 0; j < tile.walls.Length; ++j)
            {
                if (tile.walls[j]) board[tile.pos.x, tile.pos.y].SetWalls(j);
            }
        }

        LM.UpdateInfo(map.movements, map.percentageFull, 0, map.GetNumFlows());
    }
}
