CUSTOM NETWORK LIBRARY
 
 
 * Step 1. Add the JitPack repository to your build file

     dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
   
* Step 2. Add the dependency

    dependencies {
	        implementation 'com.github.Ajitshaw002:NetworkManager:1.1'
	}


SYNTEX 

  Http.RequestCall(Http.GET)
                .url(YOUR_URL)
                .makeRequest(object : ResponseListener {
                    override fun onResponse(res: JSONObject?) {
                        //Add response code here
                    }
                    override fun onFailure(e: Exception?) {
                        //Add failure code here
                    }
                })
