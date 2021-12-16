using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class AdButton : MonoBehaviour
{
    [System.Serializable]
    public enum AdType{
        Reward,
        Intersicial
    }
    public AdType id;
    // Start is called before the first frame update
    void Start()
    {
        AdManager AD = GameManager.instance.GetComponent<AdManager>();
        GetComponent<Button>().onClick.AddListener(() => { AD.ShowAd(AD._AdUnitId[(int)id]); });
    }
}
