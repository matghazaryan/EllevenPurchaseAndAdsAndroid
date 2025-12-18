package com.elleven.purchaseandads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdsManager private constructor() {

    companion object {
        @Volatile
        private var instance: AdsManager? = null

        fun getInstance(): AdsManager {
            return instance ?: synchronized(this) {
                instance ?: AdsManager().also { instance = it }
            }
        }
    }
    
    // Cache ads
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    
    fun initialize(context: Context) {
        MobileAds.initialize(context) {}
    }
    
    // MARK: - Interstitial
    
    fun loadInterstitial(context: Context, adUnitId: String) {
        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                interstitialAd = null
            }

            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                setupInterstitialListener()
            }
        })
    }
    
    private fun setupInterstitialListener() {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null
            }
        }
    }
    
    fun showInterstitial(activity: Activity) {
        if (interstitialAd != null) {
            interstitialAd?.show(activity)
        } else {
            // Log or handle not ready
        }
    }
    
    // MARK: - Rewarded
    
    fun loadRewarded(context: Context, adUnitId: String) {
        val adRequest = AdRequest.Builder().build()
        
        RewardedAd.load(context, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                rewardedAd = null
            }
            
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                setupRewardedListener()
            }
        })
    }
    
    private fun setupRewardedListener() {
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
            }
            
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                rewardedAd = null
            }
        }
    }
    
    fun showRewarded(activity: Activity, onUserEarnedReward: (Int, String) -> Unit) {
        if (rewardedAd != null) {
            rewardedAd?.show(activity) { rewardItem ->
                onUserEarnedReward(rewardItem.amount, rewardItem.type)
            }
        } else {
            // Log or handle not ready
        }
    }
}
