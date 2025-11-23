from flask import Flask, Response, request
import requests
import json
import urllib3
import os

app = Flask(__name__)

# -- KONFİGÜRASYON --
# Panel bilgilerini buraya yaz veya çevre değişkenlerinden oku
HOST = os.getenv("XTREAM_HOST", "https://ornek.paneladresi.com")
USERNAME = os.getenv("XTREAM_USER", "KULLANICI_ADINIZ")
PASSWORD = os.getenv("XTREAM_PASS", "SIFRENIZ")

API_BASE = f"{HOST.rstrip('/')}/player_api.php"

# Güvenlik / istek ayarları
VERIFY_SSL = os.getenv("VERIFY_SSL", "true").lower() in ("1", "true", "yes")
REQUEST_TIMEOUT = int(os.getenv("REQUEST_TIMEOUT", "10"))

if not VERIFY_SSL:
    # self-signed veya test ortamı için, üretimde kullanmayın
    urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

HEADERS = {
    'User-Agent': 'Dalvik/2.1.0 (Linux; U; Android 9; SmartBox Build/PI)',
    'Accept': 'application/json',
    'Referer': f'{HOST.rstrip("/")}/',
}


def safe_get_json(params):
    """Requests GET, kontrol ve JSON parse. Döner: (obj, error_str_or_None)."""
    try:
        r = requests.get(API_BASE, params=params, headers=HEADERS, timeout=REQUEST_TIMEOUT, verify=VERIFY_SSL)
    except requests.RequestException as e:
        return None, f"Istek hatası: {e}"

    if r.status_code != 200:
        return None, f"HTTP {r.status_code}: {r.text[:500]}"

    try:
        data = r.json()
    except json.JSONDecodeError:
        return None, "JSON parse hatası"

    return data, None


def get_categories_and_streams(username=USERNAME, password=PASSWORD):
    """Player API'den kategoriler ve kanalları çek.
    Döner: (categories_list, streams_list, error_or_None)
    """
    params_cat = {'username': username, 'password': password, 'action': 'get_live_categories'}
    categories, err = safe_get_json(params_cat)
    if err:
        return None, None, f"Kategori hatası: {err}"

    params_streams = {'username': username, 'password': password, 'action': 'get_live_streams'}
    streams, err = safe_get_json(params_streams)
    if err:
        return None, None, f"Kanal hatası: {err}"

    # Bazı panel sürümleri dict içinde liste dönebilir; normalize et
    if isinstance(categories, dict):
        if 'categories' in categories and isinstance(categories['categories'], list):
            categories = categories['categories']
        else:
            # Eğer dict, ama gerçek liste değilse, hata ver
            return None, None, "Beklenmeyen kategori formatı"

    if isinstance(streams, dict):
        # Xtream türevleri bazen key 'live_streams' veya 'streams' kullanır
        if 'live_streams' in streams and isinstance(streams['live_streams'], list):
            streams = streams['live_streams']
        elif 'streams' in streams and isinstance(streams['streams'], list):
            streams = streams['streams']
        else:
            return None, None, "Beklenmeyen kanal formatı"

    if not isinstance(categories, list) or not isinstance(streams, list):
        return None, None, "Beklenmeyen API formatı (liste bekleniyor)"

    return categories, streams, None


def escape_attr(s: str) -> str:
    """Basit attribute kaçış: çift tırnakları ters bölü ile kaçır."""
    if not isinstance(s, str):
        return ""
    return s.replace('"', '\\"')


@app.route('/m3u-dynamic')
def m3u_dynamic():
    """
    Dinamik M3U oluşturur. Opsiyonel query parametreleri:
    - user : kullanıcı adı (opsiyonel; varsayılan USERNAME)
    - pass : parola (opsiyonel; varsayılan PASSWORD)
    """
    user = request.args.get('user', USERNAME)
    pwd = request.args.get('pass', PASSWORD)

    categories, streams, error = get_categories_and_streams(username=user, password=pwd)
    if error:
        return Response(error + "\n", mimetype='text/plain', status=500)

    # category_id -> category_name map (türleri normalize ederek)
    category_map = {}
    for c in categories:
        cid = str(c.get('category_id', '')).strip()
        cname = c.get('category_name') or c.get('local_name') or 'Diğer'
        category_map[cid] = cname

    lines = ["#EXTM3U"]

    for s in streams:
        stream_id = s.get('stream_id') or s.get('stream_id')
        if stream_id is None:
            # atla, id yok
            continue

        name = s.get('name') or s.get('stream_name') or f"channel_{stream_id}"
        icon = s.get('stream_icon') or s.get('tv_logo') or ""
        cat_id = str(s.get('category_id') or s.get('category') or "").strip()
        group = category_map.get(cat_id, 'Diğer')
        container = s.get('container_extension') or s.get('container') or 'ts'

        # Bazı kanallar zaten tam stream_url içerir; varsa kullan
        candidate_stream_url = (s.get('stream_url') or s.get('stream_direct') or "").strip()
        if candidate_stream_url:
            stream_url = candidate_stream_url
        else:
            # Default Xtream format
            stream_url = f"{HOST.rstrip('/')}/live/{user}/{pwd}/{stream_id}.{container}"

        name_esc = escape_attr(name)
        icon_esc = escape_attr(icon)
        group_esc = escape_attr(group)

        extinf = f'#EXTINF:-1 tvg-id="{stream_id}" tvg-name="{name_esc}" tvg-logo="{icon_esc}" group-title="{group_esc}",{name}'
        lines.append(extinf)
        lines.append(stream_url)

    m3u_text = "\n".join(lines) + "\n"
    headers = {
        "Content-Disposition": "attachment; filename=playlist.m3u"
    }
    return Response(m3u_text, mimetype='application/x-mpegurl; charset=utf-8', headers=headers)


if __name__ == "__main__":
    # Development server. Production'da gunicorn/uWSGI kullanın.
    app.run(host="0.0.0.0", port=5000, debug=False)
