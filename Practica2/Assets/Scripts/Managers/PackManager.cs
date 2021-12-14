using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PackManager : MonoBehaviour
{
    [SerializeField] PackSelect selectPrefab;
    [SerializeField] Transform packContent;

    private void Start()
    {
        LoadBundles();
    }

    public void LoadBundles()
    {
        LevelBundle[] bundles = GameManager.instance.levelBundles;
        for (int i = 0; i < bundles.Length; ++i)
        {
            PackSelect sel = Instantiate(selectPrefab, packContent).GetComponent<PackSelect>();
            sel.SetBundle(i);
            sel.LoadPacks();
        }
    }
}
