using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

[RequireComponent(typeof(Button))]
public class AdButton : MonoBehaviour
{
    [System.Serializable]
    public enum AdType{
        Reward,
        Intersicial
    }
    public AdType id;
    void Start()
    {
        AdManager AD = GameManager.instance.GetComponent<AdManager>();
        if (AdManager.adsDisabled && id == AdType.Reward) gameObject.SetActive(false);
        GetComponent<Button>().onClick.AddListener(() => { AD.ShowAd(AD._AdUnitId[(int)id]); });
    }
}