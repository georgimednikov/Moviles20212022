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
    public GameObject test;
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
        // Se crean los structs de los 2 tipos de anuncios
        _AdUnitId = new AdId[2];
#if UNITY_IOS
		_AdUnitId[0].id = _rewardAdIdIOS;
        _gameId = _iOSGameId;
        __AdUnitId[1].id = _intersicialAdIdIOS;
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
        Debug.LogError($"Unity Ads Initialization Failed: {error.ToString()} - {message}");
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
        if (!adsDisabled && Advertisement.isInitialized && (adId.id == _AdUnitId[0].id || (adId.id == _AdUnitId[1].id && Random.Range(0.0f, 1.0f) < 0.3333)))
        {
            // Pedro Pablo: en el editor parece que funciona de manera diferente a en el movil, añadiendo todo el rato a los listeners en el primero, y en el segundo quitandolo cuando acaba.
            // Lo hemos puesto asi ya que creemos que es un error de la version 4.0.0 de la APi de ads
#if (!UNITY_ANDROID && !UNITY_IOS) || UNITY_EDITOR
            if (!adId.init)
#endif
            Advertisement.Show(adId.id, this);
#if !UNITY_ANDROID && !UNITY_IOS || UNITY_EDITOR
            else
                Advertisement.Show(adId.id);
#endif
        }
    }

    public void OnUnityAdsShowFailure(string adUnitId, UnityAdsShowError error, string message)
    {
        Debug.LogError($"Error showing Ad Unit {adUnitId}: {error.ToString()} - {message}");
    }
    public void OnUnityAdsShowStart(string adUnitId) { Debug.Log("Espero que sea legible"); GameManager.instance.LM.BM.ToggleInput(false); }
    public void OnUnityAdsShowClick(string adUnitId) { }
    public void OnUnityAdsShowComplete(string adUnitId, UnityAdsShowCompletionState showCompletionState)
    {
        Debug.Log("Espero que sea legible");
        test?.SetActive(false);
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
    public void LoadBanner()
    {
        if (!adsDisabled)
        {
            BannerLoadOptions options = new BannerLoadOptions
            {
                loadCallback = OnBannerLoaded,
                errorCallback = OnBannerError
            };
            Advertisement.Banner.Load(_bannerAdUnitId, options);
        }
    }

    void OnBannerLoaded()
    {
        //Debug.Log("Banner loaded");
    }

    void OnBannerError(string message)
    {
        Debug.LogError($"Banner Error: {message}");
    }

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
