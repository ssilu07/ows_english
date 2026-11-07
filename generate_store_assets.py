import os
import math
from PIL import Image, ImageDraw, ImageFont, ImageFilter

OUTPUT_DIR = "playstore_assets"
os.makedirs(OUTPUT_DIR, exist_ok=True)

# Fonts
FONT_BOLD = "C:/Windows/Fonts/arialbd.ttf"
FONT_REGULAR = "C:/Windows/Fonts/arial.ttf"
FONT_SEGOE_BOLD = "C:/Windows/Fonts/segoeuib.ttf"
FONT_HINDI = "C:/Windows/Fonts/mangal.ttf"

def get_font(path, size):
    try:
        return ImageFont.truetype(path, size)
    except Exception:
        try:
            return ImageFont.truetype(FONT_BOLD, size)
        except Exception:
            return ImageFont.load_default()

def draw_vertical_gradient(draw, width, height, color1, color2):
    for y in range(height):
        ratio = y / height
        r = int(color1[0] * (1 - ratio) + color2[0] * ratio)
        g = int(color1[1] * (1 - ratio) + color2[1] * ratio)
        b = int(color1[2] * (1 - ratio) + color2[2] * ratio)
        draw.line([(0, y), (width, y)], fill=(r, g, b))

def draw_radial_glow(img, center_x, center_y, radius, glow_color):
    # glow_color is (R, G, B, MaxAlpha)
    glow = Image.new("RGBA", img.size, (0, 0, 0, 0))
    gdraw = ImageDraw.Draw(glow)
    steps = 30
    for i in range(steps, 0, -1):
        r = int(radius * (i / steps))
        alpha = int(glow_color[3] * (1 - (i / steps)) ** 1.5)
        gdraw.ellipse([center_x - r, center_y - r, center_x + r, center_y + r], 
                      fill=(glow_color[0], glow_color[1], glow_color[2], alpha))
    img.alpha_composite(glow)

def generate_viral_icon_1():
    # 512 x 512 Modern Electric Violet / Cyan Gradient with Lightning & Brain
    size = 512
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Rounded Squircle base
    pad = 16
    rect = [pad, pad, size - pad, size - pad]
    radius = 110

    # Create base gradient inside squircle
    base_canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    bdraw = ImageDraw.Draw(base_canvas)
    
    # Gradient background
    for y in range(size):
        ratio = y / size
        # #4338CA (Indigo) to #0F172A (Deep Slate)
        r = int(67 * (1 - ratio) + 15 * ratio)
        g = int(56 * (1 - ratio) + 23 * ratio)
        b = int(202 * (1 - ratio) + 42 * ratio)
        bdraw.line([(0, y), (size, y)], fill=(r, g, b, 255))
    
    # Mask to rounded rect
    mask = Image.new("L", (size, size), 0)
    mdraw = ImageDraw.Draw(mask)
    mdraw.rounded_rectangle(rect, radius=radius, fill=255)
    
    icon_base = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    icon_base.paste(base_canvas, (0, 0), mask)

    # Ambient Glows inside
    draw_radial_glow(icon_base, 256, 160, 200, (56, 189, 248, 120)) # Sky cyan glow
    draw_radial_glow(icon_base, 256, 320, 180, (245, 158, 11, 100)) # Amber glow

    # Draw Inner Rim border
    idraw = ImageDraw.Draw(icon_base)
    idraw.rounded_rectangle(rect, radius=radius, outline=(255, 255, 255, 70), width=4)

    # Draw Central Concept: Glowing Stylized 'V' (Vocab) + Brain Silhouette & Lightning Bolt
    # Let's draw stylized Brain waves / lobes with cyan glow
    center_x, center_y = 256, 210

    # Brain lobes circles
    lobes = [
        (center_x - 70, center_y - 20, 48),
        (center_x + 70, center_y - 20, 48),
        (center_x - 50, center_y - 65, 42),
        (center_x + 50, center_y - 65, 42),
        (center_x - 85, center_y + 25, 40),
        (center_x + 85, center_y + 25, 40),
        (center_x - 45, center_y + 60, 44),
        (center_x + 45, center_y + 60, 44),
    ]
    for lx, ly, lr in lobes:
        idraw.ellipse([lx - lr, ly - lr, lx + lr, ly + lr], fill=(14, 165, 233, 210), outline=(224, 242, 254, 240), width=3)
    idraw.rounded_rectangle([center_x - 65, center_y - 50, center_x + 65, center_y + 50], radius=35, fill=(14, 165, 233, 240))

    # Center Lightning Bolt in Vibrant Electric Gold (#FBBF24 & #F59E0B)
    bolt_points = [
        (center_x + 20, center_y - 110),
        (center_x - 45, center_y + 10),
        (center_x, center_y + 10),
        (center_x - 25, center_y + 115),
        (center_x + 55, center_y - 5),
        (center_x + 10, center_y - 5),
        (center_x + 45, center_y - 110)
    ]
    # Bolt Shadow / Glow
    bolt_glow = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    bgdraw = ImageDraw.Draw(bolt_glow)
    bgdraw.polygon(bolt_points, fill=(251, 191, 36, 180))
    bolt_glow = bolt_glow.filter(ImageFilter.GaussianBlur(8))
    icon_base.alpha_composite(bolt_glow)

    # Actual Bolt
    idraw = ImageDraw.Draw(icon_base)
    idraw.polygon(bolt_points, fill=(254, 240, 138, 255), outline=(245, 158, 11, 255))

    # Bottom Pill Badge: "OWS TRICKS"
    pill_w, pill_h = 290, 68
    pill_x1 = (size - pill_w) // 2
    pill_y1 = 370
    pill_x2 = pill_x1 + pill_w
    pill_y2 = pill_y1 + pill_h

    # Pill background
    idraw.rounded_rectangle([pill_x1, pill_y1, pill_x2, pill_y2], radius=34, 
                            fill=(15, 23, 42, 240), outline=(56, 189, 248, 200), width=3)

    # Text in pill
    font_pill = get_font(FONT_SEGOE_BOLD, 30)
    text = "OWS TRICKS"
    bbox = font_pill.getbbox(text)
    tw = bbox[2] - bbox[0]
    th = bbox[3] - bbox[1]
    idraw.text(((size - tw) // 2, pill_y1 + (pill_h - th) // 2 - 4), text, fill=(255, 255, 255), font=font_pill)

    path = os.path.join(OUTPUT_DIR, "viral_icon_option1_lightning.png")
    icon_base.save(path, "PNG")
    print("Saved:", path)

def generate_viral_icon_2():
    # 512 x 512 Emerald & Gold Master Key + Brain
    size = 512
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    pad = 16
    rect = [pad, pad, size - pad, size - pad]
    radius = 110

    base_canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    bdraw = ImageDraw.Draw(base_canvas)
    for y in range(size):
        ratio = y / size
        # #0F766E (Teal) to #030712 (Dark Night)
        r = int(15 * (1 - ratio) + 3 * ratio)
        g = int(118 * (1 - ratio) + 7 * ratio)
        b = int(110 * (1 - ratio) + 18 * ratio)
        bdraw.line([(0, y), (size, y)], fill=(r, g, b, 255))

    mask = Image.new("L", (size, size), 0)
    mdraw = ImageDraw.Draw(mask)
    mdraw.rounded_rectangle(rect, radius=radius, fill=255)

    icon_base = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    icon_base.paste(base_canvas, (0, 0), mask)

    draw_radial_glow(icon_base, 256, 180, 210, (52, 211, 153, 110))
    draw_radial_glow(icon_base, 256, 300, 180, (251, 191, 36, 110))

    idraw = ImageDraw.Draw(icon_base)
    idraw.rounded_rectangle(rect, radius=radius, outline=(255, 255, 255, 80), width=4)

    # Draw Big 3D Glowing "VOCAB" Book with Golden Memory Key
    # Open book silhouette
    bx, by = 256, 210
    idraw.rounded_rectangle([bx - 120, by - 80, bx + 120, by + 80], radius=24, 
                            fill=(30, 41, 59, 230), outline=(52, 211, 153, 220), width=4)
    # Book spine & pages
    idraw.line([(bx, by - 75), (bx, by + 75)], fill=(148, 163, 184, 180), width=4)
    for offset_y in [-40, -10, 20, 50]:
        idraw.line([(bx - 95, by + offset_y), (bx - 20, by + offset_y)], fill=(52, 211, 153, 160), width=5)
        idraw.line([(bx + 20, by + offset_y), (bx + 95, by + offset_y)], fill=(52, 211, 153, 160), width=5)

    # Golden Memory Key diagonally across the book
    key_glow = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    kgdraw = ImageDraw.Draw(key_glow)
    kgdraw.ellipse([bx - 70, by - 60, bx - 10, by], outline=(251, 191, 36, 220), width=16)
    kgdraw.line([(bx - 25, by - 15), (bx + 85, by + 65)], fill=(251, 191, 36, 220), width=16)
    kgdraw.line([(bx + 50, by + 35), (bx + 65, by + 20)], fill=(251, 191, 36, 220), width=14)
    kgdraw.line([(bx + 75, by + 55), (bx + 90, by + 40)], fill=(251, 191, 36, 220), width=14)
    key_glow = key_glow.filter(ImageFilter.GaussianBlur(6))
    icon_base.alpha_composite(key_glow)

    idraw = ImageDraw.Draw(icon_base)
    idraw.ellipse([bx - 70, by - 60, bx - 10, by], outline=(254, 240, 138, 255), width=12)
    idraw.line([(bx - 25, by - 15), (bx + 85, by + 65)], fill=(254, 240, 138, 255), width=12)
    idraw.line([(bx + 50, by + 35), (bx + 65, by + 20)], fill=(254, 240, 138, 255), width=10)
    idraw.line([(bx + 75, by + 55), (bx + 90, by + 40)], fill=(254, 240, 138, 255), width=10)

    # Bottom Pill
    pill_w, pill_h = 310, 68
    pill_x1 = (size - pill_w) // 2
    pill_y1 = 370
    idraw.rounded_rectangle([pill_x1, pill_y1, pill_x1 + pill_w, pill_y1 + pill_h], radius=34, 
                            fill=(15, 23, 42, 240), outline=(251, 191, 36, 220), width=3)
    font_pill = get_font(FONT_SEGOE_BOLD, 28)
    text = "VOCAB TRICKS"
    bbox = font_pill.getbbox(text)
    tw = bbox[2] - bbox[0]
    th = bbox[3] - bbox[1]
    idraw.text(((size - tw) // 2, pill_y1 + (pill_h - th) // 2 - 4), text, fill=(255, 255, 255), font=font_pill)

    path = os.path.join(OUTPUT_DIR, "viral_icon_option2_memory_key.png")
    icon_base.save(path, "PNG")
    print("Saved:", path)

def create_phone_mockup(screen_img, target_w=680, target_h=1380):
    # Returns an RGBA Image with a sleek modern smartphone frame
    mockup = Image.new("RGBA", (target_w, target_h), (0, 0, 0, 0))
    mdraw = ImageDraw.Draw(mockup)

    bezel = 18
    corner_r = 54

    # Phone outer frame shadow
    shadow = Image.new("RGBA", (target_w, target_h), (0, 0, 0, 0))
    sdraw = ImageDraw.Draw(shadow)
    sdraw.rounded_rectangle([15, 15, target_w - 15, target_h - 15], radius=corner_r, fill=(0, 0, 0, 120))
    shadow = shadow.filter(ImageFilter.GaussianBlur(16))
    mockup.alpha_composite(shadow)

    # Phone titanium body
    mdraw = ImageDraw.Draw(mockup)
    mdraw.rounded_rectangle([10, 10, target_w - 10, target_h - 10], radius=corner_r, 
                            fill=(26, 32, 44, 255), outline=(100, 116, 139, 255), width=3)

    # Phone screen area
    screen_rect = [10 + bezel, 10 + bezel, target_w - 10 - bezel, target_h - 10 - bezel]
    screen_w = screen_rect[2] - screen_rect[0]
    screen_h = screen_rect[3] - screen_rect[1]

    # Resize screen_img to fit screen_w x screen_h
    resized_screen = screen_img.resize((screen_w, screen_h), Image.Resampling.LANCZOS).convert("RGBA")

    # Round screen corners
    screen_mask = Image.new("L", (screen_w, screen_h), 0)
    smdraw = ImageDraw.Draw(screen_mask)
    smdraw.rounded_rectangle([0, 0, screen_w, screen_h], radius=corner_r - bezel, fill=255)

    mockup.paste(resized_screen, (screen_rect[0], screen_rect[1]), screen_mask)

    # Dynamic Island / Camera cutout at top center of screen
    cam_w, cam_h = 130, 32
    cam_x = target_w // 2 - cam_w // 2
    cam_y = screen_rect[1] + 12
    mdraw.rounded_rectangle([cam_x, cam_y, cam_x + cam_w, cam_y + cam_h], radius=16, fill=(10, 10, 10, 255))
    # camera lens dot
    mdraw.ellipse([cam_x + cam_w - 28, cam_y + 8, cam_x + cam_w - 12, cam_y + 24], fill=(20, 20, 40, 255))

    # Bottom home indicator bar
    bar_w, bar_h = 180, 5
    bar_x = target_w // 2 - bar_w // 2
    bar_y = screen_rect[3] - 16
    mdraw.rounded_rectangle([bar_x, bar_y, bar_x + bar_w, bar_y + bar_h], radius=3, fill=(180, 180, 180, 200))

    return mockup

def generate_feature_graphic():
    # Google Play Store Feature Graphic: 1024 x 500 px
    w, h = 1024, 500
    canvas = Image.new("RGBA", (w, h), (0, 0, 0, 255))
    draw = ImageDraw.Draw(canvas)

    # Background gradient: Deep Slate / Indigo (#0B0F19 -> #1E1B4B -> #0F172A)
    for y in range(h):
        ratio = y / h
        r = int(11 * (1 - ratio) + 30 * ratio)
        g = int(15 * (1 - ratio) + 27 * ratio)
        b = int(25 * (1 - ratio) + 75 * ratio)
        draw.line([(0, y), (w, y)], fill=(r, g, b, 255))

    # Glows
    draw_radial_glow(canvas, 200, 150, 250, (56, 189, 248, 90)) # Cyan top-left
    draw_radial_glow(canvas, 800, 250, 300, (147, 51, 234, 100)) # Purple right
    draw_radial_glow(canvas, 450, 400, 220, (245, 158, 11, 70)) # Gold center-bottom

    # Left text content
    draw = ImageDraw.Draw(canvas)

    # Top Eyebrow Tag
    tag_x, tag_y = 60, 48
    draw.rounded_rectangle([tag_x, tag_y, tag_x + 360, tag_y + 36], radius=18, 
                           fill=(15, 23, 42, 220), outline=(56, 189, 248, 220), width=2)
    font_tag = get_font(FONT_SEGOE_BOLD, 17)
    draw.text((tag_x + 18, tag_y + 8), "⚡ #1 VOCAB TRICK APP FOR EXAMS", fill=(56, 189, 248), font=font_tag)

    # Main App Title
    font_title = get_font(FONT_SEGOE_BOLD, 52)
    draw.text((60, 100), "OWS English", fill=(255, 255, 255), font=font_title)

    font_sub = get_font(FONT_SEGOE_BOLD, 32)
    draw.text((60, 168), "One Word Substitution with Tricks", fill=(251, 191, 36), font=font_sub)

    # Viral Benefit Line
    font_body = get_font(FONT_SEGOE_BOLD, 22)
    draw.text((60, 220), "🧠 Visual Mnemonics & Desi Memory Keys", fill=(226, 232, 240), font=font_body)
    draw.text((60, 254), "🚀 10x Retention | Stop Rote Learning", fill=(148, 163, 184), font=font_body)

    # Exam Badges Row
    badge_y = 310
    exams = ["SSC CGL", "CHSL", "BANK PO", "NDA/CDS", "CUET"]
    curr_x = 60
    font_badge = get_font(FONT_SEGOE_BOLD, 16)
    for exam in exams:
        bw = font_badge.getbbox(exam)[2] - font_badge.getbbox(exam)[0] + 28
        draw.rounded_rectangle([curr_x, badge_y, curr_x + bw, badge_y + 34], radius=17, 
                               fill=(30, 41, 59, 230), outline=(100, 116, 139, 200), width=1)
        draw.text((curr_x + 14, badge_y + 7), exam, fill=(255, 255, 255), font=font_badge)
        curr_x += bw + 12

    # Free & Offline Pill
    font_callout = get_font(FONT_SEGOE_BOLD, 20)
    pill_y = 375
    draw.rounded_rectangle([60, pill_y, 60 + 260, pill_y + 54], radius=27, 
                           fill=(16, 185, 129, 255))
    draw.text((85, pill_y + 14), "✔ 100% FREE APP", fill=(255, 255, 255), font=font_callout)

    # Right side: Angled Smartphone Preview using real app screenshot
    sample_img_path = "playstore_assets/screenshot_2.png"
    if os.path.exists(sample_img_path):
        screen_img = Image.open(sample_img_path)
        phone = create_phone_mockup(screen_img, target_w=310, target_h=620)
        # Position phone nicely on the right (partially cropped at bottom for modern cut look)
        canvas.paste(phone, (680, 40), phone)

    # Floating badge on phone
    fb_x, fb_y = 610, 160
    draw.rounded_rectangle([fb_x, fb_y, fb_x + 190, fb_y + 50], radius=25, 
                           fill=(239, 68, 68, 255), outline=(255, 255, 255, 255), width=2)
    font_fb = get_font(FONT_SEGOE_BOLD, 17)
    draw.text((fb_x + 16, fb_y + 14), "🔥 NEVER FORGET!", fill=(255, 255, 255), font=font_fb)

    path = os.path.join(OUTPUT_DIR, "viral_feature_graphic_1024x500.png")
    canvas.save(path, "PNG")
    print("Saved:", path)

def generate_viral_screenshots():
    # 6 Panoramic Play Store Screenshots: 1080 x 1920 px
    slides = [
        {
            "num": 1,
            "badge": "⚡ #1 VOCAB REVOLUTION",
            "title_en": "10x Faster Vocab Memory!",
            "title_hi": "याद करो कभी न भूलने वाली Tricks से",
            "feature_pill": "🔥 Desi Mnemonics & Visual Association",
            "bg_color1": (15, 23, 42), # Deep Navy
            "bg_color2": (67, 56, 202), # Indigo
            "glow_color": (56, 189, 248, 140),
            "screen_file": "playstore_assets/screenshot_2.png"
        },
        {
            "num": 2,
            "badge": "🎯 100% EXAM-TARGETED",
            "title_en": "1000+ Most Repeated Words",
            "title_hi": "SSC CGL, CHSL, BANK PO & NDA",
            "feature_pill": "📊 Previous Year Questions (PYQ) Covered",
            "bg_color1": (30, 27, 75), # Deep Purple
            "bg_color2": (124, 58, 237), # Violet
            "glow_color": (168, 85, 247, 140),
            "screen_file": "playstore_assets/screenshot_1.png"
        },
        {
            "num": 3,
            "badge": "🧠 VISUAL BRAIN CARDS",
            "title_en": "Photo + Hindi Key + Meaning",
            "title_hi": "दिमाग में हमेशा के लिए छप जाएगी तस्वीर",
            "feature_pill": "🖼️ High-Res Memory Illustrations",
            "bg_color1": (17, 24, 39), # Slate
            "bg_color2": (13, 148, 136), # Teal
            "glow_color": (45, 212, 191, 140),
            "screen_file": "playstore_assets/screenshot_4.png"
        },
        {
            "num": 4,
            "badge": "🔍 SMART PINCH & ZOOM",
            "title_en": "Full Screen Visual Learning",
            "title_hi": "हर Word का Complete Postmortem",
            "feature_pill": "🔍 Zoom In, Read Details & Examples",
            "bg_color1": (15, 23, 42),
            "bg_color2": (2, 132, 199), # Sky Blue
            "glow_color": (56, 189, 248, 140),
            "screen_file": "playstore_assets/screenshot_2.png"
        },
        {
            "num": 5,
            "badge": "🚀 VIRAL 1-TAP SHARING",
            "title_en": "Share Daily Tricks on WhatsApp",
            "title_hi": "दोस्तों और Study Groups में भेजें",
            "feature_pill": "📲 One-Click Social Share & Study Together",
            "bg_color1": (6, 78, 59), # Dark Emerald
            "bg_color2": (16, 185, 129), # Emerald
            "glow_color": (52, 211, 153, 140),
            "screen_file": "playstore_assets/screenshot_3.png"
        },
        {
            "num": 6,
            "badge": "💯 100% FREE & CLOUD SYNC",
            "title_en": "Daily New Vocabulary Added",
            "title_hi": "बिना Update किए रोज़ नए Words!",
            "feature_pill": "⚡ Real-Time Firebase Database Sync",
            "bg_color1": (88, 28, 135), # Dark Violet
            "bg_color2": (217, 70, 239), # Fuchsia
            "glow_color": (244, 114, 182, 140),
            "screen_file": "playstore_assets/screenshot_1.png"
        }
    ]

    w, h = 1080, 1920

    for s in slides:
        canvas = Image.new("RGBA", (w, h), (0, 0, 0, 255))
        draw = ImageDraw.Draw(canvas)

        # Gradient
        for y in range(h):
            ratio = y / h
            r = int(s["bg_color1"][0] * (1 - ratio) + s["bg_color2"][0] * ratio)
            g = int(s["bg_color1"][1] * (1 - ratio) + s["bg_color2"][1] * ratio)
            b = int(s["bg_color1"][2] * (1 - ratio) + s["bg_color2"][2] * ratio)
            draw.line([(0, y), (w, y)], fill=(r, g, b, 255))

        # Ambient top glow
        draw_radial_glow(canvas, 540, 300, 480, s["glow_color"])

        draw = ImageDraw.Draw(canvas)

        # 1. Top Category Pill Badge
        badge_text = s["badge"]
        font_b = get_font(FONT_SEGOE_BOLD, 28)
        bb = font_b.getbbox(badge_text)
        bw = bb[2] - bb[0] + 44
        bx = (w - bw) // 2
        by = 80
        draw.rounded_rectangle([bx, by, bx + bw, by + 56], radius=28, 
                               fill=(15, 23, 42, 230), outline=(255, 255, 255, 200), width=2)
        draw.text((bx + 22, by + 11), badge_text, fill=(255, 255, 255), font=font_b)

        # 2. Main Catchy Title (English)
        font_title = get_font(FONT_SEGOE_BOLD, 54)
        t_en = s["title_en"]
        tb = font_title.getbbox(t_en)
        tw = tb[2] - tb[0]
        draw.text(((w - tw) // 2, 160), t_en, fill=(255, 255, 255), font=font_title)

        # 3. Subtitle (Hindi / Desi Hook)
        font_hi = get_font(FONT_HINDI, 36)
        t_hi = s["title_hi"]
        hb = font_hi.getbbox(t_hi)
        hw = hb[2] - hb[0]
        draw.text(((w - hw) // 2, 235), t_hi, fill=(254, 240, 138), font=font_hi)

        # 4. Feature Highlights Pill
        feat = s["feature_pill"]
        font_f = get_font(FONT_SEGOE_BOLD, 26)
        fb = font_f.getbbox(feat)
        fw = fb[2] - fb[0] + 48
        fx = (w - fw) // 2
        fy = 295
        draw.rounded_rectangle([fx, fy, fx + fw, fy + 52], radius=26, 
                               fill=(0, 0, 0, 160), outline=(255, 255, 255, 90), width=1)
        draw.text((fx + 24, fy + 11), feat, fill=(226, 232, 240), font=font_f)

        # 5. Device Mockup in Center/Bottom
        screen_file = s["screen_file"]
        if os.path.exists(screen_file):
            s_img = Image.open(screen_file)
        else:
            s_img = Image.open("playstore_assets/screenshot_1.png")
        
        # Phone mockup dimensions
        phone_w = 780
        phone_h = 1540
        phone = create_phone_mockup(s_img, target_w=phone_w, target_h=phone_h)

        # Paste phone in lower part (cut off cleanly at bottom for modern look)
        canvas.paste(phone, ((w - phone_w) // 2, 380), phone)

        # Save
        filename = f"viral_screenshot_{s['num']}_1080x1920.png"
        out_path = os.path.join(OUTPUT_DIR, filename)
        canvas.save(out_path, "PNG")
        print("Saved Screenshot:", out_path)

if __name__ == "__main__":
    print("Generating Viral App Assets...")
    generate_viral_icon_1()
    generate_viral_icon_2()
    generate_feature_graphic()
    generate_viral_screenshots()
    print("All Play Store Assets Generated Successfully!")
