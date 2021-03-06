package foo;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query.SortDirection;
import endpoints.repackaged.com.google.api.Http;
import io.swagger.annotations.ApiParam;

import java.util.Date;
import com.google.appengine.api.users.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;


@Api(name = "myApi",
        version = "v1",
        audiences = "1067622413243-kjhodo8vcomp32b0bpi84m47blnvkc1r.apps.googleusercontent.com",
        clientIds = "1067622413243-kjhodo8vcomp32b0bpi84m47blnvkc1r.apps.googleusercontent.com",
        namespace =
        @ApiNamespace(
                ownerDomain = "https://tinygram-1.ew.r.appspot.com/",
                ownerName = "https://tinygram-1.ew.r.appspot.com/",
                packagePath = "")
)

public class ProfileEndpoint {

    @ApiMethod(name = "createProfile", path = "profile/create", httpMethod = HttpMethod.POST)
    public Entity createProfile(User user) throws UnauthorizedException {
        Entity entityFound = null;
        if (user == null) {
            throw new UnauthorizedException("Invalid credentials");
        }
        Entity e = new Profile(user.getEmail()).createEntity();
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        try{
            entityFound = ds.get(e.getKey());
        }
        catch (EntityNotFoundException exc) {
            entityFound = null;
        }
        if (entityFound == null){
            ds.put(e);
            return e;
        }else{
            return entityFound;
        }
    }

    @ApiMethod(name = "getProfile", path = "profile/get/{profileName}",httpMethod = HttpMethod.GET)
    public Entity getProfile(User user, @Named("profileName") String profileName) throws EntityNotFoundException {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Key profileKey = new Entity("Profile", profileName).getKey();
        Entity profile = ds.get(profileKey);
        return profile;
    }

    @ApiMethod(name = "followProfile", path = "profile/{profileName}/follow", httpMethod = HttpMethod.POST)
    public Entity followProfile(User user, @Named("profileName") String profileName) throws UnauthorizedException, EntityNotFoundException {
        if (user == null){
            throw new UnauthorizedException("Invalid credentials");
        }

        String userName = user.getEmail().split("@")[0];

        if ( userName == profileName){
            throw new UnauthorizedException("Can't follow yourself");
        }

        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Entity profileToFollow = ds.get(new Entity("Profile",profileName).getKey());
        Entity profileFollowing = ds.get(new Entity("Profile", userName).getKey());

        //adding user to profile's followers
        Object items1 = profileToFollow.getProperty("followers");
        String toPut = (String)profileFollowing.getProperty("accountName");
        Collection<String> res1;
        if(items1 == null){
            res1 = new HashSet<String>();
        }else{
            res1 = (List<String>)items1;
        }
        if(res1.contains(toPut)){
            res1.remove(toPut);
        }else{
            res1.add(toPut);
        }

        profileToFollow.setProperty("followers", res1);

        //adding profile to user's follows
        Object items2 = profileFollowing.getProperty("follows");
        toPut = (String)profileToFollow.getProperty("accountName");
        Collection<String> res2;
        if(items2 == null){
            res2 = new HashSet<String>();
        }else{
            res2 = (List<String>)items2;
        }
        if(res2.contains(toPut)){
            res2.remove(toPut);
        }else{
            res2.add(toPut);
        }
        profileFollowing.setProperty("follows", res2);

        Transaction txn = ds.beginTransaction();
        ds.put(profileToFollow);
        ds.put(profileFollowing);
        txn.commit();

        return profileToFollow;
    }

    @ApiMethod(name = "updateProfile", path = "profile/{profileName}/update/", httpMethod = HttpMethod.PUT)
    public Entity updateProfile(User user, Profile updatedProfile) throws UnauthorizedException, EntityNotFoundException {
        if (user == null){
            throw new UnauthorizedException("Invalid credentials");
        }
        String userName = user.getEmail().split("@")[0];

        if ( userName == updatedProfile.accountName){
            throw new UnauthorizedException("Can only update yourself");
        }
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Key profileKey = KeyFactory.createKey("Profile",user.getEmail().split("@")[0]);
        Entity profile = ds.get(profileKey);

        if(!updatedProfile.url.equals("null")){
            profile.setProperty("url", updatedProfile.url);
        }

        if(!updatedProfile.description.equals("null")){
            profile.setProperty("description",updatedProfile.description);
        }

        if(!updatedProfile.name.equals("null")){
            profile.setProperty("name", updatedProfile.name);
        }

        Transaction txn = ds.beginTransaction();
        ds.put(profile);
        txn.commit();

        return profile;
    }

}
