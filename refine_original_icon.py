import os
from PIL import Image, ImageDraw, ImageFont, ImageFilter

OUTPUT_DIR = "playstore_assets"
os.makedirs(OUTPUT_DIR, exist_ok=True)

# Fonts available on system
FONT_ARIAL_BOLD = "C:/Windows/Fonts/arialbd.ttf"
FONT_IMPACT = "C:/Windows/Fonts/impact.ttf"
FONT_SEGOE_BOLD = "C:/Windows/Fonts/segoeuib.ttf"
FONT_SEGOE_BLACK = "C:/Windows/Fonts/seguibl.ttf" # if exists, or segoeuib

def get_font(path, size):
    try:
        return ImageFont.truetype(path, size)
    except Exception:
        try:
            return ImageFont.truetype(FONT_ARIAL_BOLD, size)
        except Exception:
            return ImageFont.load_default()

def create_refined_playstore_orange_icon():
    """
    Refines the original Play Store icon:
    Original had:
    - Orange background (#FF914D)
    - Top image/visual card
    - Text: "English Vocab" (Black) + "with Trick" (White) + "Key : Example / Synonyms"
    Refined version:
    - Premium vibrant Sunset Orange gradient with soft vignette and corner rounding
    - Polished card frame at top with a crisp visual memory book / flashcard icon
    - Crisp, ultra-sharp typography: "ENGLISH VOCAB" in high-contrast deep black with subtle 3D bevel
    - High-visibility pill badge: "WITH TRICK" in crisp white / electric yellow on dark navy pill
    - Bottom feature tags: "KEY • EXAMPLE • SYNONYMS"
    """
    size = 512
    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(canvas)

    # Rounded Squircle base (Google Play icon spec: 512x512)
    # Background: Premium rich orange gradient (#FF7A00 -> #FFA726 -> #FF5722)
    bg = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    bdraw = ImageDraw.Draw(bg)
    for y in range(size):
        ratio = y / size
        # Vibrant rich orange
        r = int(255 * (1 - ratio*0.2) + 245 * (ratio*0.2))
        g = int(122 * (1 - ratio) + 85 * ratio)
        b = int(24 * (1 - ratio) + 15 * ratio)
        bdraw.line([(0, y), (size, y)], fill=(r, g, b, 255))

    # Mask to rounded rect (radius 96 for modern squircle)
    mask = Image.new("L", (size, size), 0)
    mdraw = ImageDraw.Draw(mask)
    mdraw.rounded_rectangle([0, 0, size, size], radius=96, fill=255)
    canvas.paste(bg, (0, 0), mask)

    # Inner soft rim highlight
    idraw = ImageDraw.Draw(canvas)
    idraw.rounded_rectangle([2, 2, size - 2, size - 2], radius=94, outline=(255, 255, 255, 90), width=3)

    # 1. TOP VISUAL CARD (Replacing blurry image with crisp high-definition Illustrated Flashcard)
    card_x1, card_y1 = 64, 40
    card_x2, card_y2 = size - 64, 185
    card_w = card_x2 - card_x1
    card_h = card_y2 - card_y1

    # Card drop shadow
    shadow = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    sdraw = ImageDraw.Draw(shadow)
    sdraw.rounded_rectangle([card_x1 + 4, card_y1 + 8, card_x2 + 4, card_y2 + 8], radius=22, fill=(0, 0, 0, 60))
    shadow = shadow.filter(ImageFilter.GaussianBlur(8))
    canvas.alpha_composite(shadow)

    # Card body: Pure crisp white with subtle sky-blue/amber content
    idraw = ImageDraw.Draw(canvas)
    idraw.rounded_rectangle([card_x1, card_y1, card_x2, card_y2], radius=20, 
                            fill=(255, 255, 255, 255), outline=(255, 255, 255, 200), width=2)

    # Inside Card: Mini vocabulary visual mnemonic mockup
    # Top card header strip (Soft Sky Blue)
    idraw.rounded_rectangle([card_x1 + 6, card_y1 + 6, card_x2 - 6, card_y1 + 38], radius=14, fill=(241, 245, 249, 255))
    font_card_head = get_font(FONT_SEGOE_BOLD, 17)
    idraw.text((card_x1 + 20, card_y1 + 11), "📖 Visual Memory Card", fill=(30, 41, 59), font=font_card_head)
    # Mini bulb / trick spark dot
    idraw.ellipse([card_x2 - 36, card_y1 + 13, card_x2 - 18, card_y1 + 31], fill=(245, 158, 11, 255))

    # Inside Card content: Sample word & memory link
    font_card_word = get_font(FONT_SEGOE_BOLD, 26)
    font_card_trick = get_font(FONT_SEGOE_BOLD, 18)
    font_card_mean = get_font(FONT_ARIAL_BOLD, 15)

    idraw.text((card_x1 + 20, card_y1 + 46), "Word: Aberration", fill=(15, 23, 42), font=font_card_word)
    idraw.text((card_x1 + 20, card_y1 + 80), "⚡ Trick: Ab + Ration -> Unusual", fill=(217, 119, 6), font=font_card_trick)
    idraw.text((card_x1 + 20, card_y1 + 108), "Meaning: Departure from normal", fill=(100, 116, 139), font=font_card_mean)

    # 2. MIDDLE TYPOGRAPHY: "ENGLISH VOCAB"
    # Bold, prominent black letters matching original icon intent, but beautifully kerned & centered
    font_title = get_font(FONT_IMPACT if os.path.exists(FONT_IMPACT) else FONT_ARIAL_BOLD, 52)
    title_text = "ENGLISH VOCAB"
    tb = font_title.getbbox(title_text)
    tw = tb[2] - tb[0]
    tx = (size - tw) // 2
    ty = 210

    # Text subtle drop shadow for crisp pop
    idraw.text((tx + 2, ty + 2), title_text, fill=(180, 50, 0, 180), font=font_title)
    idraw.text((tx, ty), title_text, fill=(15, 23, 42, 255), font=font_title)

    # 3. HIGH-IMPACT PILL: "WITH TRICK"
    pill_w, pill_h = 320, 66
    pill_x1 = (size - pill_w) // 2
    pill_y1 = 280
    pill_x2 = pill_x1 + pill_w
    pill_y2 = pill_y1 + pill_h

    # Pill Shadow
    pshadow = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    psdraw = ImageDraw.Draw(pshadow)
    psdraw.rounded_rectangle([pill_x1 + 2, pill_y1 + 4, pill_x2 + 2, pill_y2 + 4], radius=33, fill=(0, 0, 0, 80))
    pshadow = pshadow.filter(ImageFilter.GaussianBlur(6))
    canvas.alpha_composite(pshadow)

    # Pill Body: Deep Midnight Slate (#0F172A) with gold/cyan border
    idraw = ImageDraw.Draw(canvas)
    idraw.rounded_rectangle([pill_x1, pill_y1, pill_x2, pill_y2], radius=33, 
                            fill=(15, 23, 42, 255), outline=(255, 255, 255, 220), width=2)

    font_trick = get_font(FONT_SEGOE_BOLD, 34)
    trick_text = "WITH TRICK"
    trb = font_trick.getbbox(trick_text)
    trw = trb[2] - trb[0]
    trh = trb[3] - trb[1]
    idraw.text(((size - trw) // 2, pill_y1 + (pill_h - trh) // 2 - 4), trick_text, fill=(255, 215, 0), font=font_trick)

    # 4. BOTTOM FEATURE BADGES: "Key • Example • Synonyms"
    # Modern rounded tag at bottom
    tag_w, tag_h = 420, 48
    tag_x1 = (size - tag_w) // 2
    tag_y1 = 365
    idraw.rounded_rectangle([tag_x1, tag_y1, tag_x1 + tag_w, tag_y1 + tag_h], radius=24, 
                            fill=(255, 255, 255, 230), outline=(255, 255, 255, 255), width=1)

    font_sub = get_font(FONT_SEGOE_BOLD, 18)
    sub_text = "KEY  •  EXAMPLE  •  SYNONYMS"
    sb = font_sub.getbbox(sub_text)
    sw = sb[2] - sb[0]
    idraw.text(((size - sw) // 2, tag_y1 + 13), sub_text, fill=(15, 23, 42), font=font_sub)

    # Bottom Tagline
    font_bot = get_font(FONT_SEGOE_BOLD, 15)
    bot_text = "★ CRAZY WAY TO LEARN ENGLISH ★"
    bb = font_bot.getbbox(bot_text)
    bw = bb[2] - bb[0]
    idraw.text(((size - bw) // 2, 435), bot_text, fill=(255, 255, 255), font=font_bot)

    out_path = os.path.join(OUTPUT_DIR, "playstore_icon_refined_orange.png")
    canvas.save(out_path, "PNG")
    print("Saved:", out_path)
    return canvas

def create_refined_original_yellow_icon():
    """
    Refines the original app/src/main/res/drawable/vocab_icon.png:
    Original had:
    - Bright Yellow background (#FFDE59)
    - Bold black text:
      "ONE"
      "WORDS"
      "WITH"
      "TRICK"
    Refined version:
    - Rich Royal Golden Yellow gradient (#FFE033 -> #FFC107 -> #FFA000)
    - Ultra-clean, bold modern grotesque typography
    - Crisp 3D shadow layers (no jagged edges)
    - Sleek modern badge accents: "ONE WORDS" + "WITH TRICK"
    - High contrast, instantly legible at 48x48 on any phone home screen!
    """
    size = 512
    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))

    # Base gradient: Radiant Golden Yellow
    bg = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    bdraw = ImageDraw.Draw(bg)
    for y in range(size):
        ratio = y / size
        # #FFEB3B to #FF9800
        r = int(255 * (1 - ratio) + 255 * ratio)
        g = int(230 * (1 - ratio) + 160 * ratio)
        b = int(45 * (1 - ratio) + 10 * ratio)
        bdraw.line([(0, y), (size, y)], fill=(r, g, b, 255))

    # Squircle mask (radius 96)
    mask = Image.new("L", (size, size), 0)
    mdraw = ImageDraw.Draw(mask)
    mdraw.rounded_rectangle([0, 0, size, size], radius=96, fill=255)
    canvas.paste(bg, (0, 0), mask)

    idraw = ImageDraw.Draw(canvas)
    # Inner rim highlight
    idraw.rounded_rectangle([2, 2, size - 2, size - 2], radius=94, outline=(255, 255, 255, 120), width=3)

    # Top Eyebrow Tag: "ENGLISH VOCABULARY"
    eyebrow_w, eyebrow_h = 280, 36
    ex1 = (size - eyebrow_w) // 2
    ey1 = 38
    idraw.rounded_rectangle([ex1, ey1, ex1 + eyebrow_w, ey1 + eyebrow_h], radius=18, 
                            fill=(15, 23, 42, 230), outline=(255, 255, 255, 100), width=1)
    font_eye = get_font(FONT_SEGOE_BOLD, 16)
    eye_text = "ENGLISH VOCABULARY"
    eb = font_eye.getbbox(eye_text)
    ew = eb[2] - eb[0]
    idraw.text(((size - ew) // 2, ey1 + 8), eye_text, fill=(255, 255, 255), font=font_eye)

    # BIG BOLD ICONIC WORD: "ONE"
    font_huge = get_font(FONT_IMPACT if os.path.exists(FONT_IMPACT) else FONT_ARIAL_BOLD, 92)
    one_text = "ONE"
    ob = font_huge.getbbox(one_text)
    ow = ob[2] - ob[0]
    ox = (size - ow) // 2
    oy = 82
    # 3D shadow
    idraw.text((ox + 3, oy + 4), one_text, fill=(180, 100, 0, 180), font=font_huge)
    idraw.text((ox, oy), one_text, fill=(15, 23, 42, 255), font=font_huge)

    # BIG BOLD ICONIC WORD: "WORDS"
    font_words = get_font(FONT_IMPACT if os.path.exists(FONT_IMPACT) else FONT_ARIAL_BOLD, 82)
    words_text = "WORDS"
    wb = font_words.getbbox(words_text)
    ww = wb[2] - wb[0]
    wx = (size - ww) // 2
    wy = 182
    # 3D shadow
    idraw.text((wx + 3, wy + 4), words_text, fill=(180, 100, 0, 180), font=font_words)
    idraw.text((wx, wy), words_text, fill=(15, 23, 42, 255), font=font_words)

    # HIGH-CONTRAST BADGE: "WITH TRICK"
    pill_w, pill_h = 380, 80
    px1 = (size - pill_w) // 2
    py1 = 285
    px2 = px1 + pill_w
    py2 = py1 + pill_h

    # Shadow
    pshadow = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    psdraw = ImageDraw.Draw(pshadow)
    psdraw.rounded_rectangle([px1 + 2, py1 + 6, px2 + 2, py2 + 6], radius=40, fill=(0, 0, 0, 90))
    pshadow = pshadow.filter(ImageFilter.GaussianBlur(8))
    canvas.alpha_composite(pshadow)

    idraw = ImageDraw.Draw(canvas)
    # Dark Midnight Base
    idraw.rounded_rectangle([px1, py1, px2, py2], radius=40, fill=(15, 23, 42, 255), 
                            outline=(255, 255, 255, 230), width=3)

    # Text in pill: "WITH TRICK ⚡"
    font_pill = get_font(FONT_SEGOE_BOLD, 42)
    pill_text = "WITH TRICK ⚡"
    pb = font_pill.getbbox(pill_text)
    pw = pb[2] - pb[0]
    ph = pb[3] - pb[1]
    idraw.text(((size - pw) // 2, py1 + (pill_h - ph) // 2 - 5), pill_text, fill=(255, 215, 0), font=font_pill)

    # Bottom Exam Badge
    sub_w, sub_h = 420, 52
    sx1 = (size - sub_w) // 2
    sy1 = 390
    idraw.rounded_rectangle([sx1, sy1, sx1 + sub_w, sy1 + sub_h], radius=26, 
                            fill=(255, 255, 255, 240), outline=(15, 23, 42, 200), width=2)
    font_sub = get_font(FONT_SEGOE_BOLD, 20)
    sub_text = "SSC  •  BANK  •  NDA  •  CDS"
    sb = font_sub.getbbox(sub_text)
    sw = sb[2] - sb[0]
    idraw.text(((size - sw) // 2, sy1 + 14), sub_text, fill=(15, 23, 42), font=font_sub)

    # Save
    out_path = os.path.join(OUTPUT_DIR, "vocab_icon_refined_yellow.png")
    canvas.save(out_path, "PNG")
    print("Saved:", out_path)

    # Also update app/src/main/res/drawable/vocab_icon.png and app/src/main/ic_launcher-playstore.png!
    return canvas

if __name__ == "__main__":
    print("Refining original icon designs...")
    orange_icon = create_refined_playstore_orange_icon()
    yellow_icon = create_refined_original_yellow_icon()

    # Save both directly into app/src/main/res/drawable and root assets
    yellow_icon.save("app/src/main/res/drawable/vocab_icon.png", "PNG")
    print("Updated: app/src/main/res/drawable/vocab_icon.png")

    orange_icon.save("app/src/main/ic_launcher-playstore.png", "PNG")
    print("Updated: app/src/main/ic_launcher-playstore.png")
    print("All refined icons generated successfully!")
