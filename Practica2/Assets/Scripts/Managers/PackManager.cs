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
        foreach (LevelBundle bundle in bundles)
        {
            PackSelect sel = Instantiate(selectPrefab, packContent).GetComponent<PackSelect>();
            sel.SetBundle(bundle);
            sel.LoadPacks();
        }
    }
}
