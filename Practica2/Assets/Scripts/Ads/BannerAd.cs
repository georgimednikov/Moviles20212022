using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.Advertisements;

public class BannerAd :  IUnityAdsLoadListener, IUnityAdsShowListener
{
    bool loadInit = false, showInit = false;
    string _adUnitId;
    // Update is called once per frame
    void Load(string adUnitId)
    {
        _adUnitId = adUnitId;
        if (!loadInit)
            Advertisement.Load(_adUnitId, this);
        else
            Advertisement.Load(_adUnitId);
    }

    public void OnUnityAdsAdLoaded(string adUnitId)
    {
        if (adUnitId.Equals(_adUnitId))
        {
            loadInit = true;
        }
    }

    public void OnUnityAdsFailedToLoad(string adUnitId, UnityAdsLoadError error, string message)
    {
    }

    public void ShowAd()
    {
        if (!showInit)
            Advertisement.Show(_adUnitId, this);
        else
            Advertisement.Show(_adUnitId);
    }

    public void OnUnityAdsShowFailure(string adUnitId, UnityAdsShowError error, string message)
    {
        Debug.Log($"Error showing Ad Unit {adUnitId}: {error.ToString()} - {message}");
        // Optionally execute code if the Ad Unit fails to show, such as loading another ad.
    }
    public void OnUnityAdsShowStart(string adUnitId) { GameManager.instance.LM.BM.ToggleInput(false); }
    public void OnUnityAdsShowClick(string adUnitId) { }
    public void OnUnityAdsShowComplete(string adUnitId, UnityAdsShowCompletionState showCompletionState)
    {
        if (adUnitId.Equals(_adUnitId) && showCompletionState.Equals(UnityAdsShowCompletionState.COMPLETED))
        {
            GameManager.instance.LM.BM.ToggleInput(true);
            GameManager.instance.addHint();
            showInit = true;
        }
    }
}
