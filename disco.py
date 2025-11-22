from flask import Flask, Response
import requests
import json

app = Flask(__name__)

# -- PANEL BİLGİLERİ --
HOST = "https://ornek.paneladresi.com"
USERNAME = "KULLANICI_ADINIZ"
PASSWORD = "SIFRENIZ"

API_LINK = f"{HOST}/player_api.php?username={USERNAME}&password={PASSWORD}"

# -- HTTP BAŞLIKLARI --
API_HEADERS = {
    'User-Agent': 'Dalvik/2.1.0 (Linux; U; Android 9; SmartBox Build/PI)',
    'Accept': 'application/json',
    'Referer': f'{HOST}/',
}

def get_streams():
    """Tüm canlı akışları ve kategorileri çek."""
    
    # 1. Kategorileri Çekme
    categories_url = API_LINK + "&action=get_live_categories"
    r_cat = requests.get(categories_url, headers=API_HEADERS, verify=False) # verify=False, SSL hatalarını yoksayabilir
    
    try:
        categories = r_cat.json()
    except json.JSONDecodeError:
        return None, "Kategori JSON hatası"

    # 2. Kanalları Çekme
    streams_url = API_LINK + "&action=get_live_streams"
    r_stream = requests.get(streams_url, headers=API_HEADERS, verify=False)
    
    try:
        streams = r_stream.json()
    except json.JSONDecodeError:
        return None, "Kanal JSON hatası"

    return categories, streams


@app.route('/m3u-dynamic')
def generate_m3u():
    categories, streams = get_streams()

    if streams is None:
        # JSON çekilemezse hata döndür
        return Response(streams, mimetype='text/plain', status=500)

    # Kategori ID'den isme eşleştirme
    category_map = {c['category_id']: c['category_name'] for c in categories}

    m3u_content = "#EXTM3U\n"

    for stream in streams:
        stream_id = stream.get('stream_id')
        name = stream.get('name')
        icon = stream.get('stream_icon', '')
        category_id = stream.get('category_id')
        
        category_name = category_map.get(category_id, 'Diğer')
        container = stream.get('container_extension', 'ts')

        stream_url = f"{HOST}/live/{USERNAME}/{PASSWORD}/{stream_id}.{container}"

        # M3U Satırları
        m3u_content += f"#EXTINF:-1 tvg-id=\"{stream_id}\" tvg-name=\"{name}\" tvg-logo=\"{icon}\" group-title=\"{category_name}\",{name}\n"
        m3u_content += f"{stream_url}\n"

    # M3U çıktısını uygun başlıklarla döndürme
    return Response(
        m3u_content,
        mimetype='application/x-mpegurl',  # M3U formatı için doğru MIME tipi
        headers={"Content-Disposition": "attachment; filename=playlist.m3u"}
    )

if __name__ == '__main__':
    # Flask uygulamasını çalıştırma (Genellikle bir gunicorn/uWSGI gibi bir sunucu ile kullanılır)
    app.run(host='0.0.0.0', port=5000)
