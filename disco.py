from flask import Flask, Response
import requests
import json
import urllib3

app = Flask(__name__)

# -- PANEL BİLGİLERİ --
HOST = "https://ornek.paneladresi.com"
USERNAME = "KULLANICI_ADINIZ"
PASSWORD = "SIFRENIZ"

# Güvenlik / istek ayarları
VERIFY_SSL = True  # Eğer self-signed sertifika kullanıyorsan False yapabilirsin, ancak güvenlik riski vardır.
REQUEST_TIMEOUT = 10  # saniye

if not VERIFY_SSL:
    # verify=False kullanıyorsan InsecureRequestWarning'ı kapat
    urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

API_BASE = f"{HOST}/player_api.php"

# -- HTTP BAŞLIKLARI --
API_HEADERS = {
    'User-Agent': 'Dalvik/2.1.0 (Linux; U; Android 9; SmartBox Build/PI)',
    'Accept': 'application/json',
    'Referer': f'{HOST}/',
}


def _safe_json_resp(r):
    """HTTP durumunu kontrol et ve JSON parse et; hata mesajı döndür."""
    if r.status_code != 200:
        return None, f"HTTP error: {r.status_code}"
    try:
        return r.json(), None
    except json.JSONDecodeError:
        return None, "JSON decode error"


def get_streams():
    """Tüm canlı akışları ve kategorileri çek.
    Döner: (categories_list, streams_list, error_message_or_None)
    """
    # 1. Kategorileri Çekme
    params_cat = {
        'username': USERNAME,
        'password': PASSWORD,
        'action': 'get_live_categories'
    }
    try:
        r_cat = requests.get(API_BASE, params=params_cat, headers=API_HEADERS, timeout=REQUEST_TIMEOUT, verify=VERIFY_SSL)
    except requests.RequestException as e:
        return None, None, f"Kategori isteği başarısız: {e}"

    categories, err = _safe_json_resp(r_cat)
    if err:
        return None, None, f"Kategori hatası: {err}"

    # 2. Kanalları Çekme
    params_stream = {
        'username': USERNAME,
        'password': PASSWORD,
        'action': 'get_live_streams'
    }
    try:
        r_stream = requests.get(API_BASE, params=params_stream, headers=API_HEADERS, timeout=REQUEST_TIMEOUT, verify=VERIFY_SSL)
    except requests.RequestException as e:
        return None, None, f"Kanal isteği başarısız: {e}"

    streams, err = _safe_json_resp(r_stream)
    if err:
        return None, None, f"Kanal hatası: {err}"

    # Beklenen format liste olduğu için kontrol (bazı paneller dict içinde 'categories' vb dönebilir)
    if isinstance(categories, dict) and 'categories' in categories:
        categories = categories['categories']
    if isinstance(streams, dict) and 'live_streams' in streams:
        streams = streams['live_streams']

    if not isinstance(categories, list) or not isinstance(streams, list):
        return None, None, "Beklenmeyen API formatı"

    return categories, streams, None


@app.route('/m3u-dynamic')
def generate_m3u():
    categories, streams, error = get_streams()

    if error:
        return Response(error, mimetype='text/plain', status=500)

    # Kategori ID'den isme eşleştirme (türleri normalize et)
    category_map = {}
    for c in categories:
        cid = str(c.get('category_id', ''))
        cname = c.get('category_name', 'Diğer')
        category_map[cid] = cname

    m3u_lines = ["#EXTM3U"]

    for stream in streams:
        stream_id = stream.get('stream_id')
        name = stream.get('name') or f"channel_{stream_id}"
        icon = stream.get('stream_icon', '') or ''
        category_id = str(stream.get('category_id', ''))
        category_name = category_map.get(category_id, 'Diğer')
        container = stream.get('container_extension') or 'ts'

        # Basit kaçış: double-quote'ları bozmayalım
        name_esc = name.replace('"', '\\"')
        icon_esc = icon.replace('"', '\\"')

        stream_url = f"{HOST}/live/{USERNAME}/{PASSWORD}/{stream_id}.{container}"

        m3u_lines.append(f'#EXTINF:-1 tvg-id="{stream_id}" tvg-name="{name_esc}" tvg-logo="{icon_esc}" group-title="{category_name}",{name_esc}')
        m3u_lines.append(stream_url)

    m3u_content = "\n".join(m3u_lines) + "\n"

    return Response(
        m3u_content,
        mimetype='application/x-mpegurl; charset=utf-8',
        headers={"Content-Disposition": "attachment; filename=playlist.m3u"}
    )


if __name__ == '__main__':
    # Production'da gunicorn/uWSGI kullan; development için:
    app.run(host='0.0.0.0', port=5000, debug=False)
