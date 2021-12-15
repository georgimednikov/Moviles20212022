using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Advertisements;

public class AdManager : MonoBehaviour
{
#if !UNITY_ADS // If the Ads service is not enabled
    public string gameId; // Set this value from the inspector
    public bool enableTestMode = true; // Enable this during development
#endif

    void InitializeAds() // Example of how to initialize the Unity Ads service
    {
#if !UNITY_ADS // If the Ads service is not enabled
        if (Advertisement.isSupported)
        { // If runtime platform is supported
            Advertisement.Initialize(gameId, enableTestMode); // Initialize
        }
#endif
    }

    void ShowAd() // Example of how to show an ad
    {
        if (Advertisement.isInitialized)
        { // If the ads are ready to be shown
            Advertisement.Show("Banner_Android"); // Show the default ad placement
        }
    }

    private void Start()
    {
        InitializeAds();
        ShowAd();
    }
}
