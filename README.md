# Android APP習作 - 新北市路外公共停車場資訊

## 目的：

下載新北市路外公共停車場Open Data資料，顯示停車場資訊、地圖資訊。

下載網址：[https://data.gov.tw/dataset/26653](https://data.gov.tw/dataset/26653)

[APK下載](https://github.com/mingmintang/NewTaipeiPublicParking/raw/master/apk/parking.apk "parking.apk")

## 實作內容：

1. 利用Retrofit和Jackson下載和解析停車場json資料並儲存進本機資料庫
2. 使用RecyclerView列表顯示停車場資訊
3. 點選列表進入詳細頁面，顯示停車場位置地圖資訊
4. 列表頁面可選擇行政區和關鍵字搜尋停車場名稱
5. 列表頁面頂端手勢下滑可作重新下載和更新資料
6. 取得所在位置後地圖框新增路線規劃按鈕，可繪製建議路線

## 套件功能：

parking_list

* 顯示停車場清單
* 透過區域和關鍵字搜尋停車場
* 清單在頂點向下滑動更新所有資料
* 點擊清單項目開啟parking_detail

parking_detail

* 列表顯示停車場資訊
* 顯示停車場所在地圖
* 有路線規劃功能

map

* 載入Google地圖
* 載入我的位置

data

* 下載和解析Open Data停車場資料
* 停車場本地資料庫存取
* 下載和解析Google Directions API路線規劃路徑

update_all_data

* 開啟前景服務通知顯示下載狀態
* 下載並儲存所有停車場資料

notification

* 定義update_all_data通知頻道

## 套件關聯圖：

![GITHUB](https://github.com/mingmintang/NewTaipeiPublicParking/blob/master/image/package_relationship.jpg "套件關聯圖")

## 注意事項：

編譯專案需額外加入自己的Google Map和Google Directions的API Key

新增google_maps_api.xml到專案位置"app/src/debug/res/values/google_maps_api.xml"，內容如下：

```xml
<resources>
    <string name="google_maps_key" translatable="false" templateMergeStrategy="preserve">YOUR_MAP_KEY</string>
    <string name="google_directions_key" translatable="false" templateMergeStrategy="preserve">YOUR_DIRECTIONS_KEY</string>
</resources>
```

## 程式預覽：

**主頁面**：列表顯示停車場資訊，提供搜尋功能

![GITHUB](https://github.com/mingmintang/NewTaipeiPublicParking/blob/master/image/main_page.jpg "主視窗")

**詳細頁面**：顯示停車場位置地圖資訊

![GITHUB](https://github.com/mingmintang/NewTaipeiPublicParking/blob/master/image/detail_page.jpg "詳細頁面")

**路線規劃**：按下路線規劃按鈕繪製建議路線

![GITHUB](https://github.com/mingmintang/NewTaipeiPublicParking/blob/master/image/routes.jpg "路線規劃")
