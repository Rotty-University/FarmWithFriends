package edu.brown.cs.student.farm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.brown.cs.student.proxy.FarmProxy;

public class DataBaseTest {
  @Before
  public void setUp() throws Exception {
    FarmProxy.setUpDataBase("data/testdb.sqlite3");
  }

  @After
  public void tearDown() throws Exception {
    FarmProxy.setConnection(null);
  }

  @Test
  public void testInsertingAndGettingUserName() throws Exception {
    setUp();
    FarmProxy.insertUserInfoIntoDatabase("test", "hash", "salt", "test@gmail.com", 1, "false");
    String username = FarmProxy.getUserNameFromDataBase("test");
    assertEquals(username, "test");
    String empty = FarmProxy.getUserNameFromDataBase("nonexistent");
    assertNull(empty);
    tearDown();
  }

  @Test
  public void testInsertingAndGettingUserInfo() throws Exception {
    setUp();
    FarmProxy.insertUserInfoIntoDatabase("test", "hash", "salt", "test@gmail.com", 1, "false");
    String[] userinfo = FarmProxy.getUserInfoFromDataBaseForLogIn("test");
    assertEquals(userinfo[0], "test");
    assertEquals(userinfo[1], "hash");
    assertEquals(userinfo[2], "salt");
    assertEquals(userinfo[3], "test@gmail.com");
    String userName = FarmProxy.getEmailFromDataBase("test@gmail.com");
    assertEquals(userName, "test");
    String[] empty = FarmProxy.getUserInfoFromDataBaseForLogIn("nonexistent");
    assertNull(empty);
    String emptyEmail = FarmProxy.getEmailFromDataBase("nonexistent");
    assertNull(emptyEmail);
    tearDown();
  }

  @Test
  public void testAddingAndGettingFromFriendsList() throws Exception {
    setUp();
    FarmProxy.insertUserInfoIntoDatabase("test", "hash", "salt", "test@gmail.com", 1, "false");
    String friendList = FarmProxy.getFriendsList("test");
    assertEquals(friendList, "");
    FarmProxy.updateFriendsList("test", "kobe");
    String friendUpdate = FarmProxy.getFriendsList("test");
    assertEquals(friendUpdate, "kobe,");
    FarmProxy.updateFriendsList("test", "bryant");
    String updated = FarmProxy.getFriendsList("test");
    assertEquals(updated, "kobe,bryant,");
    tearDown();
  }

  @Test
  public void testAddingAndGettingFromPendingList() throws Exception {
    setUp();
    FarmProxy.insertUserInfoIntoDatabase("test", "hash", "salt", "test@gmail.com", 1, "false");
    String friendListPending = FarmProxy.getFriendsListPending("test");
    assertEquals(friendListPending, "");
    FarmProxy.updateFriendsPending("kobe", "test");
    String friendUpdatePending = FarmProxy.getFriendsListPending("test");
    assertEquals(friendUpdatePending, "kobe,");
    FarmProxy.updateFriendsPending("bryant", "test");
    String updated = FarmProxy.getFriendsListPending("test");
    assertEquals(updated, "kobe,bryant,");
    FarmProxy.updateFriendsPendingAfterAdding("", "test");
    assertEquals(FarmProxy.getFriendsListPending("test"), "");
    tearDown();
  }

  @Test
  public void testInsertingAndGettingMap() throws Exception {
    setUp();
    String[] empty = FarmProxy.getDataFromMostRecentMap();
    assertNull(empty);
    FarmProxy.insertMapIntoDataBase(1, "THEMAP", 24);
    String[] mostRecentMap = FarmProxy.getDataFromMostRecentMap();
    assertEquals(mostRecentMap[0], "1");
    assertEquals(mostRecentMap[1], "THEMAP");
    assertEquals(mostRecentMap[2], "24");
    FarmProxy.insertMapIntoDataBase(2, "SECONDMAP", 8);
    String[] updatedMap = FarmProxy.getDataFromMostRecentMap();
    assertEquals(updatedMap[0], "2");
    int freespaceMap1 = FarmProxy.getFreeSpaceFromMap(1);
    assertEquals(freespaceMap1, 24);
    assertEquals(FarmProxy.getFreeSpaceFromMap(2), 8);
    tearDown();
  }

  @Test
  public void testUpdatingTheMapAndInteractionWithUser() throws Exception {
    setUp();
    assertNull(FarmProxy.getMapFromDataBase(2));
    FarmProxy.insertMapIntoDataBase(1, "THEMAP", 24);
    String[] mostRecentMap = FarmProxy.getDataFromMostRecentMap();
    assertEquals(mostRecentMap[0], "1");
    assertEquals(mostRecentMap[1], "THEMAP");
    assertEquals(mostRecentMap[2], "24");
    FarmProxy.updateTheMapData(1, "THISISUPDATEDMAP");
    String mapdata = FarmProxy.getMapFromDataBase(1);
    assertEquals(mapdata, "THISISUPDATEDMAP");
    assertEquals(FarmProxy.getFreeSpaceFromMap(1), 24);
    FarmProxy.updateFreeSpaceInMaps(1, 81);
    int freeSpace = FarmProxy.getFreeSpaceFromMap(1);
    assertEquals(freeSpace, 81);
    tearDown();
  }

  @Test
  public void testingUserInteractionWithMap() throws Exception {
    setUp();
    FarmProxy.insertUserInfoIntoDatabase("test", "hash", "salt", "test@gmail.com", 1, "true");
    assertNull(FarmProxy.getMapFromDataBase(1));
    assertEquals(FarmProxy.getMapIDofUserFromDataBase("test"), 1);
    FarmProxy.insertMapIntoDataBase(1, "THEMAP", 24);
    FarmProxy.updateNewUserIndication("test", "false");
    String status = FarmProxy.getStatusOfUser("test");
    assertEquals(status, "false");
    FarmProxy.insertUserInfoIntoDatabase("test2", "hash", "salt", "test@gmail.com", 2, "true");
    assertEquals(FarmProxy.getStatusOfUser("test2"), "true");
    assertEquals(FarmProxy.getMapIDofUserFromDataBase("test2"), 2);
    tearDown();
  }

  @Test
  public void testingUserLocationWithMap() throws Exception {
    setUp();
    FarmProxy.insertUserInfoIntoDatabase("test", "hash", "salt", "test@gmail.com", 1, "true");
    assertNull(FarmProxy.getMapFromDataBase(1));
    assertEquals(FarmProxy.getMapIDofUserFromDataBase("test"), 1);
    FarmProxy.insertMapIntoDataBase(1, "THEMAP", 24);
    int[] coords = FarmProxy.getRowAndColumnOfUserMapLocation("test");
    assertEquals(coords[0], -1);
    assertEquals(coords[1], -1);
    FarmProxy.updateTheRowAndColumnofUserLocationInMap("test", 1, 1);
    coords = FarmProxy.getRowAndColumnOfUserMapLocation("test");
    assertEquals(coords[0], 1);
    assertEquals(coords[1], 1);
    tearDown();
  }

  @Test
  public void testingLoadingAndSavingFarm() throws Exception {
    setUp();
    FarmProxy.insertUserInfoIntoDatabase("test", "hash", "salt", "test@gmail.com", 1, "true");
    assertNull(FarmProxy.loadFarm("test"));
    FarmProxy.initializeFarm("test");
    FarmFile farm = FarmProxy.loadFarm("test");
    assert (farm != null);
    farm.setFarmName("TEST2");
    FarmProxy.saveFarm("test", farm);
    FarmFile newfarm = FarmProxy.loadFarm("test");
    assertEquals(newfarm.getFarmName(), "TEST2");
    tearDown();
  }

  @Test
  public void testingTradingCenter() throws Exception {
    setUp();
    assertEquals(FarmProxy.getTradingCenter(), "");
    FarmProxy.updateTradingCenter("farmer joe", "corn", "2", "tomato", "4");
    assertEquals(FarmProxy.getTradingCenter(), "farmer joe,corn,2,tomato,4;");
    FarmProxy.removeTradeListing("farmer joe,corn,2,tomato,4");
    assertEquals(FarmProxy.getTradingCenter(), "");
    FarmProxy.updateTradingCenter("farmer joe", "corn", "2", "tomato", "4");
    FarmProxy.updateTradingCenter("farmer bob", "milt", "1", "wheat", "1");
    assertEquals(FarmProxy.getTradingCenter(), "farmer joe,corn,2,tomato,4;farmer bob,milt,1,wheat,1;");
    FarmProxy.removeTradeListing("farmer bob,milt,1,wheat,1");
    FarmProxy.removeTradeListing("farmer joe,corn,2,tomato,4");
    assertEquals(FarmProxy.getTradingCenter(), "");
    tearDown();
  }

}
