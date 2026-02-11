#!/usr/bin/env python3
"""Format raw screenshots into polished dApp Store screenshots with Art Deco framing."""

from PIL import Image, ImageDraw, ImageFont
import math
import os

ASSETS = "/Users/heathbertram/Downloads/RoaringTrades/assets"

# Color palette
CHARCOAL = (26, 26, 46)
DEEP_BG = (15, 15, 35)
GOLD = (212, 175, 55)
DARK_GOLD = (184, 134, 11)
LIGHT_GOLD = (232, 212, 139)
CREAM = (255, 248, 231)

# Output: 1080x1920 (9:16 portrait, standard store screenshot ratio, meets 1080x1080 min)
OUT_W, OUT_H = 1080, 1920


def get_font(size, bold=False):
    font_paths = [
        "/System/Library/Fonts/Supplemental/Georgia Bold.ttf" if bold else "/System/Library/Fonts/Supplemental/Georgia.ttf",
        "/System/Library/Fonts/Supplemental/Times New Roman Bold.ttf" if bold else "/System/Library/Fonts/Supplemental/Times New Roman.ttf",
    ]
    for fp in font_paths:
        if os.path.exists(fp):
            try:
                return ImageFont.truetype(fp, size)
            except:
                continue
    return ImageFont.load_default()


def text_width(draw, text, font):
    bbox = draw.textbbox((0, 0), text, font=font)
    return bbox[2] - bbox[0]


def draw_text_centered(draw, text, y, font, fill, width):
    tw = text_width(draw, text, font)
    x = (width - tw) // 2
    draw.text((x, y), text, font=font, fill=fill)


def draw_art_deco_fan(draw, cx, cy, r, color, direction="up", num=9):
    """Draw a small Art Deco fan. direction='up' fans upward, 'down' fans downward."""
    for i in range(num):
        if direction == "up":
            angle = math.pi + math.pi * i / (num - 1)
        else:
            angle = math.pi * i / (num - 1)
        ex = cx + int(r * math.cos(angle))
        ey = cy + int(r * math.sin(angle))
        draw.line([(cx, cy), (ex, ey)], fill=color, width=2)


def create_framed_screenshot(input_path, output_name, caption, subtitle=None):
    """Create a store-ready screenshot with Art Deco frame and caption."""

    # Load original screenshot
    raw = Image.open(input_path)
    raw_w, raw_h = raw.size  # 1200x2670

    # Create output canvas
    img = Image.new('RGB', (OUT_W, OUT_H))
    draw = ImageDraw.Draw(img)

    # Gradient background
    for y in range(OUT_H):
        ratio = y / OUT_H
        r = int(CHARCOAL[0] * (1 - ratio) + DEEP_BG[0] * ratio)
        g = int(CHARCOAL[1] * (1 - ratio) + DEEP_BG[1] * ratio)
        b = int(CHARCOAL[2] * (1 - ratio) + DEEP_BG[2] * ratio)
        draw.line([(0, y), (OUT_W, y)], fill=(r, g, b))

    # === TOP CAPTION AREA ===
    caption_area_h = 260

    # Top decorative fan
    draw_art_deco_fan(draw, OUT_W // 2, 15, 18, GOLD, "up", 7)

    # Outer border (just top portion framing)
    bm = 14
    draw.rectangle([bm, bm, OUT_W - bm, OUT_H - bm], outline=GOLD, width=2)
    # Inner border
    im = 26
    draw.rectangle([im, im, OUT_W - im, OUT_H - im], outline=DARK_GOLD, width=1)

    # Corner accents
    accent = 45
    for cx, cy, dx, dy in [(im, im, 1, 1), (OUT_W - im, im, -1, 1),
                            (im, OUT_H - im, 1, -1), (OUT_W - im, OUT_H - im, -1, -1)]:
        draw.line([(cx, cy), (cx + accent * dx, cy)], fill=GOLD, width=3)
        draw.line([(cx, cy), (cx, cy + accent * dy)], fill=GOLD, width=3)

    # Corner diamonds
    cs = 5
    for cx, cy in [(bm, bm), (OUT_W - bm, bm), (bm, OUT_H - bm), (OUT_W - bm, OUT_H - bm)]:
        draw.polygon([(cx, cy - cs), (cx + cs, cy), (cx, cy + cs), (cx - cs, cy)], fill=GOLD)

    # Caption text
    font_caption = get_font(48, bold=True)
    draw_text_centered(draw, caption, 65, font_caption, GOLD, OUT_W)

    if subtitle:
        font_sub = get_font(24, bold=False)
        draw_text_centered(draw, subtitle, 130, font_sub, DARK_GOLD, OUT_W)

    # Decorative line under caption
    line_y = 170 if subtitle else 140
    draw.line([(80, line_y), (OUT_W - 80, line_y)], fill=GOLD, width=2)
    draw.line([(100, line_y + 5), (OUT_W - 100, line_y + 5)], fill=DARK_GOLD, width=1)

    # Small diamonds on the line
    for dx in range(80, OUT_W - 80, 50):
        ds = 3
        draw.polygon([(dx, line_y - ds), (dx + ds, line_y), (dx, line_y + ds), (dx - ds, line_y)], fill=GOLD)

    # === SCREENSHOT AREA ===
    # Phone mockup area - place screenshot with rounded-corner mask and subtle border
    phone_margin_x = 60
    phone_top = caption_area_h
    phone_bottom = OUT_H - 60
    phone_w = OUT_W - 2 * phone_margin_x
    phone_h = phone_bottom - phone_top

    # Scale the screenshot to fit within the phone area
    # Original is 1200x2670, we need to fit in phone_w x phone_h
    scale = min(phone_w / raw_w, phone_h / raw_h)
    new_w = int(raw_w * scale)
    new_h = int(raw_h * scale)

    # Center the screenshot
    ss_x = (OUT_W - new_w) // 2
    ss_y = phone_top + (phone_h - new_h) // 2

    # Resize screenshot with high quality
    resized = raw.resize((new_w, new_h), Image.LANCZOS)

    # Draw a subtle gold border around the screenshot
    border_w = 3
    draw.rectangle(
        [ss_x - border_w, ss_y - border_w, ss_x + new_w + border_w, ss_y + new_h + border_w],
        outline=GOLD, width=border_w
    )
    # Inner subtle border
    draw.rectangle(
        [ss_x - 1, ss_y - 1, ss_x + new_w + 1, ss_y + new_h + 1],
        outline=DARK_GOLD, width=1
    )

    # Paste the screenshot
    img.paste(resized, (ss_x, ss_y))

    # Bottom fan
    draw_art_deco_fan(draw, OUT_W // 2, OUT_H - 15, 18, GOLD, "down", 7)

    # Save
    output_path = os.path.join(ASSETS, output_name)
    img.save(output_path, "PNG")
    print(f"  {output_name}: {OUT_W}x{OUT_H}, {os.path.getsize(output_path) / 1024:.1f}KB")
    return output_path


if __name__ == "__main__":
    print("Formatting screenshots for dApp Store...")

    screenshots = [
        ("1. Screenshot Garage.png",  "screenshot_1_garage.png",  "Garage", "Upgrade your ride from On Foot to Zeppelin"),
        ("2. Screenshot Market.png",  "screenshot_2_market.png",  "Market", "Trade bootleg spirits across Chicago"),
        ("3. Screenshot Travel.png",  "screenshot_3_travel.png",  "Travel", "Explore 6 Chicago neighborhoods"),
        ("4. Screenshot Status.png",  "screenshot_4_status.png",  "Status", "Track your empire & visit speakeasies"),
    ]

    for raw_name, out_name, caption, subtitle in screenshots:
        input_path = os.path.join(ASSETS, raw_name)
        create_framed_screenshot(input_path, out_name, caption, subtitle)

    print("\nDone! All formatted screenshots ready.")
