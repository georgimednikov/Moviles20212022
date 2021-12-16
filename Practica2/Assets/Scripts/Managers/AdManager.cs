using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.Advertisements;
using UnityEngine.SceneManagement;




public class AdManager : MonoBehaviour, IUnityAdsLoadListener, IUnityAdsShowListener, IUnityAdsInitializationListener
{
    [SerializeField] string _intersicialAdIdAndroid = "Interstitial_Android";
    [SerializeField] string _intersicialAdIdIOS = "Interstitial_iOS";
    [SerializeField] string _rewardAdIdAndroid = "Rewarded_Android";
    [SerializeField] string _rewardAdIdIOS = "Rewarded_iOS";
    [SerializeField] string _bannerAdIdAndroid = "Banner_Android";
    [SerializeField] string _bannerAdIdIOS = "Banner_iOS";
    [SerializeField] string _androidGameId;
    [SerializeField] string _iOSGameId;
    [SerializeField] bool _testMode = true;
    private string _gameId;
    public AdId[] _AdUnitId;
    string _bannerAdUnitId;
    bool initInit = false, adsDisabled = false;
    public struct AdId
    {
        public string id;
        public bool init;
    }

    void Awake()
    {
        InitializeAds();
        SceneManager.sceneLoaded += OnLevelFinishedLoading;
        _AdUnitId = new AdId[2];
#if UNITY_IOS
		_adUnitId = _rewardAdIdIOS;
        _gameId = _iOSGameId;
        _nextLeveladUnitId = _intersicialAdIdIOS;
        _bannerAdUnitId = _bannerAdIdIOS;
#elif UNITY_ANDROID
        _AdUnitId[0].id = _rewardAdIdAndroid;
        _gameId = _androidGameId;
        _AdUnitId[1].id = _intersicialAdIdAndroid;
        _bannerAdUnitId = _bannerAdIdAndroid;
#endif
    }

    void OnLevelFinishedLoading(Scene scene, LoadSceneMode mode)
    {
        if (!adsDisabled)
        {
            Advertisement.Banner.SetPosition(BannerPosition.BOTTOM_CENTER);
            LoadAd(_AdUnitId[0]);
            LoadAd(_AdUnitId[1]);
            LoadBanner();
            ShowBannerAd();
        }
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
    public void LoadAd(AdId AdUnitId)
    {
        if (!adsDisabled)
        {
            if (!AdUnitId.init)
                Advertisement.Load(AdUnitId.id, this);
            else
                Advertisement.Load(AdUnitId.id);
        }
    }

    public void OnUnityAdsAdLoaded(string adUnitId)
    {
        if (adUnitId.Equals(_AdUnitId[0].id))
        {
            _AdUnitId[0].init = true;
        }
        else if (adUnitId.Equals(_AdUnitId[1].id))
        {
            _AdUnitId[1].init = true;
        }

    }

    public void OnUnityAdsFailedToLoad(string adUnitId, UnityAdsLoadError error, string message) { }
    #endregion

    #region Show
    public void ShowAd(AdId adId)
    {
        if (!adsDisabled)
        {
            if (!adId.init)
                Advertisement.Show(adId.id, this);
            else
                Advertisement.Show(adId.id);
        }
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
        if (showCompletionState.Equals(UnityAdsShowCompletionState.COMPLETED))
        {
            GameManager.instance.LM.BM.ToggleInput(true);
            if (adUnitId.Equals(_AdUnitId[0].id))
            {
                _AdUnitId[0].init = true;
                GameManager.instance.addHint();
            }
            else if (adUnitId.Equals(_AdUnitId[1].id))
            {
                _AdUnitId[1].init = true;
            }
        }
    }
    #endregion

    #region Banner
    // Implement a method to call when the Load Banner button is clicked:
    public void LoadBanner()
    {
        if (!adsDisabled)
        {
            // Set up options to notify the SDK of load events:
            BannerLoadOptions options = new BannerLoadOptions
            {
                loadCallback = OnBannerLoaded,
                errorCallback = OnBannerError
            };

            // Load the Ad Unit with banner content:
            Advertisement.Banner.Load(_bannerAdUnitId, options);
        }
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
        if (!adsDisabled)
        {
            // Set up options to notify the SDK of show events:
            BannerOptions options = new BannerOptions
            {
                clickCallback = OnBannerClicked,
                hideCallback = OnBannerHidden,
                showCallback = OnBannerShown
            };

            Advertisement.Banner.Show(_bannerAdUnitId, options);
        }
    }

    void OnBannerClicked() { }
    void OnBannerShown() { }
    void OnBannerHidden() { }
    #endregion
}
