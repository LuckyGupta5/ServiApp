package com.example.servivet.utils.network

 sealed  class NetworkStatus {
     object Available : NetworkStatus()
     object Unavailable : NetworkStatus()
}
