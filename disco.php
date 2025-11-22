<?php
// M3U ÇIKTISI BAŞLIĞI
// Tarayıcıya veya oynatıcıya bunun bir M3U dosyası olduğunu bildirir.
header('Content-Type: application/octet-stream');
header('Content-Disposition: attachment; filename="playlist.m3u"');

// -----------------------------------------------------------
// 1. KULLANICI BİLGİLERİNİZİ BURAYA GİRİN
// -----------------------------------------------------------

// Lütfen bu kısımları kendi bilgilerinizle değiştirin.
$host = "https://goldvod.org"; // Başında http:// veya https:// olmalı
$username = "hpgdisco";
$password = "123456";

// API uç noktası ve kimlik doğrulama linkleri
$api_link = $host . "/player_api.php?username=" . $username . "&password=" . $password;
$live_categories_link = $api_link . "&action=get_live_categories";
$live_streams_link = $api_link . "&action=get_live_streams";

// M3U dosyasının başlangıcı
echo "#EXTM3U\n";

// -----------------------------------------------------------
// 2. HTTP İSTEĞİ (cURL) FONKSİYONU
// -----------------------------------------------------------
// PHP'de cURL, en güvenilir ve terminaldeki cURL komutlarına en yakın istek yöntemidir.

function curl_request($url, $custom_headers) {
    // cURL başlat
    $ch = curl_init();
    
    // URL ve Temel Ayarlar
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1); // Yanıtı string olarak döndür
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false); // SSL doğrulamayı yoksay (Gerekiyorsa, terminaldeki -k gibi)
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true); // Yönlendirmeleri takip et
    curl_setopt($ch, CURLOPT_TIMEOUT, 15); // Zaman aşımı (saniye)

    // Başlıkları Ekleme (Mobil Uygulama Taklidi)
    curl_setopt($ch, CURLOPT_HTTPHEADER, $custom_headers);

    // İsteği Çalıştır
    $response = curl_exec($ch);
    
    // Hata kontrolü
    if (curl_errno($ch)) {
        return "HATA: cURL hatası - " . curl_error($ch);
    }
    
    // cURL kapat
    curl_close($ch);
    
    return $response;
}

// Başlıklar: Terminal/Mobil uygulama taklidi (Önceki konuşmalarımızdaki en iyi sonuç veren başlıklar)
$api_headers = array(
    'User-Agent: Dalvik/2.1.0 (Linux; U; Android 9; SmartBox Build/PI)',
    'Accept: application/json',
    'Referer: ' . $host . '/',
);

// -----------------------------------------------------------
// 3. API VERİLERİNİ ÇEKME VE İŞLEME
// -----------------------------------------------------------

// A. Kategori Listesini Çek
$categories_json = curl_request($live_categories_link, $api_headers);

// B. JSON Temizleme ve Ayrıştırma (Önceki sorun giderme adımı)
$categories_json = trim($categories_json);
$categories_json = preg_replace('/^[\x{FEFF}]/', '', $categories_json); // BOM temizleme
$categories_json = preg_replace('/^.*?\{/', '{', $categories_json); // JSON başlamadan önceki çöpleri silme

$categories = json_decode($categories_json, true);

if (json_last_error() !== JSON_ERROR_NONE || !is_array($categories)) {
    // Hata oluşursa M3U dosyasına hata mesajı ekleyip durdur
    echo "# HATA: API Kategori verisi çekilemedi veya JSON bozuk: " . json_last_error_msg() . "\n";
    exit;
}

// C. Tüm Kanalları Çek
$streams_json = curl_request($live_streams_link, $api_headers);
$streams_json = trim($streams_json);
$streams_json = preg_replace('/^[\x{FEFF}]/', '', $streams_json); 
$streams_json = preg_replace('/^.*?\{/', '{', $streams_json);

$streams = json_decode($streams_json, true);

if (json_last_error() !== JSON_ERROR_NONE || !is_array($streams)) {
    echo "# HATA: API Kanal verisi çekilemedi veya JSON bozuk: " . json_last_error_msg() . "\n";
    exit;
}

// Kategori ID'leri için hızlı arama dizisi oluştur (stream_id => category_id)
$category_map = [];
foreach ($categories as $category) {
    $category_map[$category['category_id']] = $category['category_name'];
}


// -----------------------------------------------------------
// 4. M3U ÇIKTISINI OLUŞTURMA
// -----------------------------------------------------------

foreach ($streams as $stream) {
    // Gerekli bilgileri kontrol et
    if (empty($stream['stream_id']) || empty($stream['name'])) {
        continue;
    }

    $stream_id = $stream['stream_id'];
    $name = $stream['name'];
    $icon = isset($stream['stream_icon']) ? $stream['stream_icon'] : '';
    $category_id = isset($stream['category_id']) ? $stream['category_id'] : 'Diğer';
    
    // Kategori adını bul
    $category_name = isset($category_map[$category_id]) ? $category_map[$category_id] : 'Diğer';
    
    // Genişletilmiş Bilgi Satırı (#EXTINF)
    // xtream-codes formatında ek bilgiler eklenir
    echo "#EXTINF:-1 tvg-id=\"" . $stream_id . "\"";
    echo " tvg-name=\"" . $name . "\"";
    echo " tvg-logo=\"" . $icon . "\"";
    echo " group-title=\"" . $category_name . "\",";
    echo $name . "\n";

    // Akış URL'si (Xtream Codes API akış formatı)
    $container = isset($stream['container_extension']) ? $stream['container_extension'] : 'ts';
    $stream_url = $host . "/live/" . $username . "/" . $password . "/" . $stream_id . "." . $container;
    
    echo $stream_url . "\n";
}

?>
