using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Linq;

public class BoardManager : MonoBehaviour
{
    public LevelManager LM;

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
                //Se offsetean las posiciones de las tiles para que la posici�n del gameObject padre cuadren con el centro del tablero
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

        foreach (LogicTile p in map.posToReset)
        {
            board[p.pos.x, p.pos.y].Reset();
        }
        map.posToReset.Clear();
        if(map.touchingIndex != -1) 
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
        LogicTile[] flow = map.GetFlow(flowToRender);
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
            tile.Reset();
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
        for (int i = 0; i < map.flowsToRender.Length; i++)
            if (map.flowsToRender[i]) {
                map.CommitFlow(i);
                map.flowsToRender[i] = false;
            }

        map.StoppedTouching();
        LM.UpdateInfo(map.movements, map.percentageFull, map.numFlowsComplete, map.GetNumFlows());
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
        //Se offsetea en Y en base a los m�rgenes del canvas
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
            //M�dulo para que si no hay suficientes colores se repitan; i++ / 2 para pasar de color cada dos extremos
            tile.SetColor(colorPool[((i++) % colorPool.Length) / 2]);
        }

        LM.UpdateInfo(map.movements, map.percentageFull, 0, map.GetNumFlows());
    }
}
