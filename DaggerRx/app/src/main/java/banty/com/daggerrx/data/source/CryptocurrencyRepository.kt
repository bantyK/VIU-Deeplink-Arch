package banty.com.daggerrx.data.source

import android.util.Log
import banty.com.daggerrx.data.Cryptocurrency
import banty.com.daggerrx.data.source.local.CryptocurrencyDao
import banty.com.daggerrx.data.source.remote.ApiInterface
import io.reactivex.Observable
import io.reactivex.ObservableSource
import javax.inject.Inject

/**
 * Created by Banty on 16/06/18.
 */
class CryptocurrencyRepository @Inject constructor(val apiInterface: ApiInterface,
                                                   val cryptocurrencyDao: CryptocurrencyDao) {

    val TAG = "Repository"

    fun getCryptocurrencies(): Observable<List<Cryptocurrency>> {
        val apiObservable = getObservableFromApi()
        val dbObservable = getObservableFromDb()
        return Observable.concatArrayEager(apiObservable, dbObservable)
    }

    private fun getObservableFromApi(): Observable<List<Cryptocurrency>> {
        return apiInterface.getCryptocurrencies("0")
                .doOnNext {
                    Log.e(TAG, it.size.toString())
                    for (item in it) {
                        cryptocurrencyDao.insertCryptoCurrency(item)
                    }
                }
    }

    private fun getObservableFromDb(): ObservableSource<out List<Cryptocurrency>>? {
        return cryptocurrencyDao.queryCryptocrrencies()
                .toObservable()
                .doOnNext {
                    Log.e(TAG, it.size.toString())
                }

    }
}