# Android APP習作 - 新北市路外公共停車場資訊
### 目的：
下載新北市路外公共停車場Open Data資料，顯示停車場資訊、地圖資訊。<br/>
下載網址：[https://data.gov.tw/dataset/26653](https://data.gov.tw/dataset/26653)
#### [APK下載](https://github.com/mingmintang/NewTaipeiPublicParking/raw/master/apk/parking.apk "parking.apk")
<br/>

### 實作內容：
1. 利用OkHttp和Jackson下載和解析停車場json資料並儲存進本機資料庫
2. 使用RecyclerView列表顯示停車場資訊
3. 點選列表進入詳細頁面，顯示停車場位置地圖資訊
4. 列表頁面可選擇行政區和關鍵字搜尋停車場名稱
5. 列表頁面長距離手勢下滑可作重新下載和更新資料
6. 取得所在位置後地圖框新增路線規劃按鈕，可繪製建議路線
<br/>

### 注意事項：
編譯專案需額外加入自己的Google Map和Google Directions的API Key<br/>
新增google_maps_api.xml到專案位置：app/src/debug/res/values/google_maps_api.xml，內容如下：
```
<resources>
    <string name="google_maps_key" translatable="false" templateMergeStrategy="preserve">YOUR_MAP_KEY</string>
    <string name="google_directions_key" translatable="false" templateMergeStrategy="preserve">YOUR_DIRECTIONS_KEY</string>
</resources>
```
<br>

### 程式預覽：
**主頁面**：列表顯示停車場資訊，提供搜尋功能<br/>
![GITHUB](https://github.com/mingmintang/NewTaipeiPublicParking/blob/master/screenshot/main_page.jpg "主視窗")

**詳細頁面**：顯示停車場位置地圖資訊<br/>
![GITHUB](https://github.com/mingmintang/NewTaipeiPublicParking/blob/master/screenshot/detail_page.jpg "詳細頁面")

**路線規劃**：按下路線規劃按鈕繪製建議路線<br/>
![GITHUB](https://github.com/mingmintang/NewTaipeiPublicParking/blob/master/screenshot/routes.jpg "路線規劃")

