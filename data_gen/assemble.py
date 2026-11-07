# -*- coding: utf-8 -*-
"""Assemble 1000+ SSC PYQ OWS, build assets/ssc_ows.json, and push to Firebase"""

import json
import urllib.request
import sys
import os

from part1 import DATA_PART1
from part2 import DATA_PART2
from part3 import DATA_PART3
from part4 import DATA_PART4

def assemble_and_sync():
    print("Gathering curated SSC words...")
    all_raw = DATA_PART1 + DATA_PART2 + DATA_PART3 + DATA_PART4
    print(f"Total raw entries: {len(all_raw)}")

    # Fetch existing Firebase items to preserve any custom images
    existing_images = {}
    try:
        req = urllib.request.urlopen("https://all-exams-pdf-a22c7.firebaseio.com/Notice.json")
        fb_data = json.loads(req.read().decode("utf-8"))
        if fb_data:
            for k, v in fb_data.items():
                title = v.get("title", "")
                img = v.get("image", "")
                if title and img:
                    word_part = title.split("-")[0].strip().lower()
                    existing_images[word_part] = img
            print(f"Found {len(existing_images)} existing image mappings in Firebase.")
    except Exception as e:
        print(f"Notice fetch note: {e}")

    # Remove duplicates if any (case-insensitive) and sort alphabetically
    word_dict = {}
    for item in all_raw:
        word, defn, hindi, trick, notes = item
        w_lower = word.strip().lower()
        if w_lower not in word_dict:
            word_dict[w_lower] = {
                "word": word.strip(),
                "definition": defn.strip(),
                "hindi": hindi.strip(),
                "trick": trick.strip(),
                "notes": notes.strip(),
            }

    sorted_words = sorted(word_dict.values(), key=lambda x: x["word"].lower())
    print(f"Total unique sorted words: {len(sorted_words)}")

    # Format list for App & Firebase
    formatted_list = []
    firebase_payload = {}

    for idx, item in enumerate(sorted_words, 1):
        w = item["word"]
        defn = item["definition"]
        hindi = item["hindi"]
        trick = item["trick"]
        notes = item["notes"]

        # Clean Title format: "Word - Definition (Hindi)"
        clean_title = f"{w} - {defn} ({hindi})"
        
        # Clean Key/Trick format: "Trick: ..."
        clean_key = f"{hindi} • Trick: {trick}"
        
        # Clean Description: English meaning + Hindi explanation + SSC exam citation
        clean_desc = f"{defn}. {trick}। {notes}"

        key_id = f"OWS_{idx:04d}_{w.lower()}"
        image_url = existing_images.get(w.lower(), "")

        entry = {
            "title": clean_title,
            "key_value": clean_key,
            "description": clean_desc,
            "image": image_url,
            "key": key_id
        }

        formatted_list.append(entry)
        firebase_payload[key_id] = entry

    # 1. Save local asset file in app/src/main/assets/ssc_ows.json
    assets_dir = os.path.join("app", "src", "main", "assets")
    os.makedirs(assets_dir, exist_ok=True)
    json_path = os.path.join(assets_dir, "ssc_ows.json")
    with open(json_path, "w", encoding="utf-8") as f:
        json.dump(formatted_list, f, ensure_ascii=False, indent=2)
    print(f"Saved {len(formatted_list)} words to {json_path} ({os.path.getsize(json_path)} bytes)")

    # 2. Upload to Firebase Realtime Database at /Notice.json
    print("Uploading 1000+ words to Firebase /Notice.json ...")
    try:
        url = "https://all-exams-pdf-a22c7.firebaseio.com/Notice.json"
        payload_bytes = json.dumps(firebase_payload, ensure_ascii=False).encode("utf-8")
        req = urllib.request.Request(url, data=payload_bytes, method="PUT", headers={"Content-Type": "application/json; charset=utf-8"})
        with urllib.request.urlopen(req) as resp:
            print(f"Firebase response: {resp.status} {resp.reason}")
            data_resp = json.loads(resp.read().decode("utf-8"))
            print(f"Uploaded successfully! Total items in Firebase Notice: {len(data_resp)}")
    except Exception as e:
        print(f"Error uploading to Firebase: {e}")

if __name__ == "__main__":
    assemble_and_sync()
