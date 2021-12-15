using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.Advertisements;

public class AdManager : MonoBehaviour, IUnityAdsLoadListener, IUnityAdsShowListener, IUnityAdsInitializationListener
{
    [SerializeField] string _androidAdUnitId = "Interstitial_Android";
    [SerializeField] string _iOsAdUnitId = "Interstitial_iOS";
    [SerializeField] string _androidGameId;
    [SerializeField] string _iOSGameId;
    [SerializeField] bool _testMode = true;
    [SerializeField] Button _showAdButton;
    private string _gameId;
    string _adUnitId;
    static bool showInit = false, loadInit = false, initInit = false;


    void Awake()
    {
        InitializeAds();

        _adUnitId = null; 
#if UNITY_IOS
		_adUnitId = _iOsAdUnitId;
        _gameId = _iOSGameId;
#elif UNITY_ANDROID
        _adUnitId = _androidAdUnitId;
        _gameId = _androidGameId;
#endif

        Advertisement.Banner.SetPosition(BannerPosition.BOTTOM_CENTER);
        LoadAd();
        LoadBanner();
        ShowBannerAd();
    }

    #region Initialize
    public void InitializeAds()
    {
        _gameId = (Application.platform == RuntimePlatform.IPhonePlayer)
            ? _iOSGameId
            : _androidGameId;
        if (!initInit)
            Advertisement.Initialize(_gameId, _testMode, this);
        else
            Advertisement.Initialize(_gameId, _testMode);
    }

    public void OnInitializationComplete()
    {
        initInit = true;
    }

    public void OnInitializationFailed(UnityAdsInitializationError error, string message)
    {
        Debug.Log($"Unity Ads Initialization Failed: {error.ToString()} - {message}");
    }
#endregion

    #region Load
    public void LoadAd()
    {
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
    #endregion

    #region Show
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
    #endregion

    #region Banner
    // Implement a method to call when the Load Banner button is clicked:
    public void LoadBanner()
    {
        // Set up options to notify the SDK of load events:
        BannerLoadOptions options = new BannerLoadOptions
        {
            loadCallback = OnBannerLoaded,
            errorCallback = OnBannerError
        };

        // Load the Ad Unit with banner content:
        Advertisement.Banner.Load(_adUnitId, options);
    }

    // Implement code to execute when the loadCallback event triggers:
    void OnBannerLoaded()
    {
        Debug.Log("Banner loaded");
    }

    // Implement code to execute when the load errorCallback event triggers:
    void OnBannerError(string message)
    {
        Debug.Log($"Banner Error: {message}");
        // Optionally execute additional code, such as attempting to load another ad.
    }

    // Implement a method to call when the Show Banner button is clicked:
    void ShowBannerAd()
    {
        // Set up options to notify the SDK of show events:
        BannerOptions options = new BannerOptions
        {
            clickCallback = OnBannerClicked,
            hideCallback = OnBannerHidden,
            showCallback = OnBannerShown
        };

        Advertisement.Banner.Show(_adUnitId, options);
    }

    void OnBannerClicked() { }
    void OnBannerShown() { }
    void OnBannerHidden() { }
    #endregion
}
