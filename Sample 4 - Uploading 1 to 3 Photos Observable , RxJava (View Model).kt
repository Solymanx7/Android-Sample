

class HelpViewModel : ViewModel() {

    var helpResult: MutableLiveData<String> = MutableLiveData()


    //region Help
    @SuppressLint("CheckResult")
    fun help(
        message: RequestBody,
        photos: ArrayList<MultipartBody.Part>?) {
        Log.d("HelpViewModel", "changeInfo: before GET")
        var observable: Observable<ResponseBody> = ProfileClient.INSTANCE!!.help(message, photos).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())


                observable.subscribe({ o ->
                                 run {
                                     Log.d("HelpViewModel", "help: \nSuccessfully Sent !")
                                     helpResult.value = "success"
                                 }
                             }, { e ->
                                 run {
                                     try {
                                         var jObjError = JSONObject((e as HttpException).response()?.errorBody()?.string())
                                         helpResult.value = jObjError.getString("message")
                                         Log.d("HelpViewModel", "help: \n ${helpResult.value}")
                                     } catch (o: TimeoutException) {
                                         Log.d("HelpViewModel", "help: TimeoutException ")
                                         helpResult.value = "Connection Timed out"
                                     } catch (o: Exception) {
                                         helpResult.value = "Unauthorized"
                                         Log.d("HelpViewModel", "help: Exception ${e.message}")
                                     }

                                 }
                             })
    }
    //endregion


}
