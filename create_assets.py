#!/usr/bin/env python3
"""Generate Roaring Trades dApp Store assets with 1920s Art Deco design."""

from PIL import Image, ImageDraw, ImageFont
import math
import os

OUTPUT_DIR = "/Users/heathbertram/Downloads/RoaringTrades/assets"
os.makedirs(OUTPUT_DIR, exist_ok=True)

# Color palette from the app
CHARCOAL = (26, 26, 46)        # #1A1A2E
GOLD = (212, 175, 55)          # #D4AF37
DARK_GOLD = (184, 134, 11)     # #B8860B
LIGHT_GOLD = (232, 212, 139)   # #E8D48B
CREAM = (255, 248, 231)        # #FFF8E7
BURGUNDY = (114, 47, 55)       # #722F37
DARK_BURGUNDY = (74, 31, 36)   # #4A1F24
DEEP_BG = (15, 15, 35)         # Even darker for depth


def draw_art_deco_corner(draw, x, y, size, color, flip_h=False, flip_v=False):
    """Draw an Art Deco corner ornament."""
    s = size
    lines = []
    # Fan lines from corner
    for i in range(5):
        angle = math.pi / 2 * i / 4
        ex = x + int(s * math.cos(angle)) * (-1 if flip_h else 1)
        ey = y + int(s * math.sin(angle)) * (-1 if flip_v else 1)
        lines.append(((x, y), (ex, ey)))
    for line in lines:
        draw.line(line, fill=color, width=2)


def draw_art_deco_border(draw, bbox, color, width=2, inset=0):
    """Draw an Art Deco style border with corner accents."""
    x1, y1, x2, y2 = bbox[0] + inset, bbox[1] + inset, bbox[2] - inset, bbox[3] - inset
    draw.rectangle([x1, y1, x2, y2], outline=color, width=width)
    # Corner diamonds
    cs = 12
    for cx, cy in [(x1, y1), (x2, y1), (x1, y2), (x2, y2)]:
        draw.polygon([(cx, cy - cs), (cx + cs, cy), (cx, cy + cs), (cx - cs, cy)], fill=color)


def draw_sunburst(draw, cx, cy, radius, color, num_rays=24, ray_width=2):
    """Draw an Art Deco sunburst pattern."""
    for i in range(num_rays):
        angle = 2 * math.pi * i / num_rays
        ex = cx + int(radius * math.cos(angle))
        ey = cy + int(radius * math.sin(angle))
        draw.line([(cx, cy), (ex, ey)], fill=color, width=ray_width)


def draw_chevron_pattern(draw, y_start, y_end, width, color, spacing=20):
    """Draw horizontal Art Deco chevron/zigzag pattern."""
    for y in range(y_start, y_end, spacing):
        points = []
        for x in range(0, width, spacing):
            points.append((x, y))
            points.append((x + spacing // 2, y + spacing // 2))
        if len(points) >= 2:
            draw.line(points, fill=color, width=1)


def draw_vertical_lines(draw, x_start, x_end, y_start, y_end, color, spacing=6):
    """Draw vertical parallel lines (Art Deco motif)."""
    for x in range(x_start, x_end, spacing):
        draw.line([(x, y_start), (x, y_end)], fill=color, width=1)


def draw_rt_monogram(draw, cx, cy, size, gold, dark_gold):
    """Draw the RT monogram at given center and size."""
    # Scale factor
    s = size / 50  # base design is ~50px tall

    # R letter
    r_left = cx - int(30 * s)
    r_top = cy - int(22 * s)

    # R - vertical bar
    draw.rectangle([r_left, r_top, r_left + int(8 * s), cy + int(22 * s)], fill=gold)
    # R - top horizontal
    draw.rectangle([r_left, r_top, r_left + int(20 * s), r_top + int(8 * s)], fill=gold)
    # R - middle horizontal
    mid_y = cy - int(2 * s)
    draw.rectangle([r_left, mid_y, r_left + int(20 * s), mid_y + int(8 * s)], fill=gold)
    # R - right curve top section
    draw.rectangle([r_left + int(16 * s), r_top, r_left + int(24 * s), mid_y + int(8 * s)], fill=gold)
    # R - diagonal leg
    leg_points = [
        (r_left + int(12 * s), mid_y + int(8 * s)),
        (r_left + int(20 * s), mid_y + int(8 * s)),
        (r_left + int(28 * s), cy + int(22 * s)),
        (r_left + int(20 * s), cy + int(22 * s)),
    ]
    draw.polygon(leg_points, fill=gold)

    # T letter
    t_left = cx + int(4 * s)
    # T - top bar
    draw.rectangle([t_left, r_top, t_left + int(28 * s), r_top + int(8 * s)], fill=gold)
    # T - vertical bar
    t_center = t_left + int(10 * s)
    draw.rectangle([t_center, r_top, t_center + int(8 * s), cy + int(22 * s)], fill=gold)


def draw_decorative_frame(draw, x1, y1, x2, y2, color, dark_color):
    """Draw a decorative Art Deco double frame with corner ornaments."""
    # Outer frame
    draw.rectangle([x1, y1, x2, y2], outline=color, width=3)
    # Inner frame
    gap = 8
    draw.rectangle([x1 + gap, y1 + gap, x2 - gap, y2 - gap], outline=dark_color, width=2)

    # Corner ornaments - small squares at corners
    cs = 6
    for cx, cy in [(x1, y1), (x2, y1), (x1, y2), (x2, y2)]:
        draw.rectangle([cx - cs, cy - cs, cx + cs, cy + cs], fill=color)

    # Midpoint ornaments - small diamonds
    mid_x = (x1 + x2) // 2
    mid_y = (y1 + y2) // 2
    ds = 5
    for dx, dy in [(mid_x, y1), (mid_x, y2), (x1, mid_y), (x2, mid_y)]:
        draw.polygon([(dx, dy - ds), (dx + ds, dy), (dx, dy + ds), (dx - ds, dy)], fill=dark_color)


def create_gradient_bg(img, color1, color2):
    """Create a vertical gradient background."""
    draw = ImageDraw.Draw(img)
    w, h = img.size
    for y in range(h):
        ratio = y / h
        r = int(color1[0] * (1 - ratio) + color2[0] * ratio)
        g = int(color1[1] * (1 - ratio) + color2[1] * ratio)
        b = int(color1[2] * (1 - ratio) + color2[2] * ratio)
        draw.line([(0, y), (w, y)], fill=(r, g, b))


def get_font(size, bold=False):
    """Try to get a nice font, fall back gracefully."""
    font_paths = [
        "/System/Library/Fonts/Supplemental/Georgia Bold.ttf" if bold else "/System/Library/Fonts/Supplemental/Georgia.ttf",
        "/System/Library/Fonts/Supplemental/Times New Roman Bold.ttf" if bold else "/System/Library/Fonts/Supplemental/Times New Roman.ttf",
        "/System/Library/Fonts/Helvetica.ttc",
    ]
    for fp in font_paths:
        if os.path.exists(fp):
            try:
                return ImageFont.truetype(fp, size)
            except:
                continue
    return ImageFont.load_default()


def text_width(draw, text, font):
    """Get text width."""
    bbox = draw.textbbox((0, 0), text, font=font)
    return bbox[2] - bbox[0]


def text_height(draw, text, font):
    """Get text height."""
    bbox = draw.textbbox((0, 0), text, font=font)
    return bbox[3] - bbox[1]


def draw_text_centered(draw, text, y, font, fill, width):
    """Draw text centered horizontally."""
    tw = text_width(draw, text, font)
    x = (width - tw) // 2
    draw.text((x, y), text, font=font, fill=fill)


def draw_text_with_shadow(draw, text, y, font, fill, width, shadow_color=None, offset=2):
    """Draw text centered with drop shadow."""
    tw = text_width(draw, text, font)
    x = (width - tw) // 2
    if shadow_color:
        draw.text((x + offset, y + offset), text, font=font, fill=shadow_color)
    draw.text((x, y), text, font=font, fill=fill)


# ============================================================
# 1. APP ICON (512x512)
# ============================================================
def create_icon():
    print("Creating icon 512x512...")
    size = 512
    img = Image.new('RGB', (size, size))
    create_gradient_bg(img, CHARCOAL, DEEP_BG)
    draw = ImageDraw.Draw(img)

    # Subtle sunburst behind monogram
    draw_sunburst(draw, size // 2, size // 2, 220, (30, 30, 55), num_rays=36, ray_width=1)

    # Art Deco decorative border
    border_margin = 24
    draw_decorative_frame(draw, border_margin, border_margin,
                         size - border_margin, size - border_margin, GOLD, DARK_GOLD)

    # Second inner border
    inner_margin = 44
    draw.rectangle([inner_margin, inner_margin, size - inner_margin, size - inner_margin],
                   outline=DARK_GOLD, width=1)

    # Horizontal line accents at top and bottom
    for y_pos in [70, 75]:
        draw.line([(60, y_pos), (size - 60, y_pos)], fill=GOLD, width=1)
    for y_pos in [size - 70, size - 75]:
        draw.line([(60, y_pos), (size - 60, y_pos)], fill=GOLD, width=1)

    # Draw RT monogram large and centered
    draw_rt_monogram(draw, size // 2, size // 2 - 15, 120, GOLD, DARK_GOLD)

    # "ROARING TRADES" text below monogram
    font_title = get_font(28, bold=True)
    draw_text_centered(draw, "ROARING", size // 2 + 100, font_title, LIGHT_GOLD, size)

    font_sub = get_font(22, bold=True)
    draw_text_centered(draw, "TRADES", size // 2 + 132, font_sub, GOLD, size)

    # Small decorative dots/diamonds
    dot_y = size // 2 + 90
    for dx in [-40, -20, 0, 20, 40]:
        cx = size // 2 + dx
        ds = 3
        draw.polygon([(cx, dot_y - ds), (cx + ds, dot_y), (cx, dot_y + ds), (cx - ds, dot_y)], fill=GOLD)

    # Art Deco fan/arch at top center
    fan_cx = size // 2
    fan_cy = 52
    for i in range(7):
        angle = math.pi + math.pi * i / 6
        r = 18
        ex = fan_cx + int(r * math.cos(angle))
        ey = fan_cy + int(r * math.sin(angle))
        draw.line([(fan_cx, fan_cy), (ex, ey)], fill=GOLD, width=2)

    # Art Deco fan at bottom center
    fan_cy = size - 52
    for i in range(7):
        angle = math.pi * i / 6
        r = 18
        ex = fan_cx + int(r * math.cos(angle))
        ey = fan_cy + int(r * math.sin(angle))
        draw.line([(fan_cx, fan_cy), (ex, ey)], fill=GOLD, width=2)

    img.save(os.path.join(OUTPUT_DIR, "icon_512x512.png"), "PNG")
    print(f"  Saved icon_512x512.png")


# ============================================================
# 2. BANNER (1200x600)
# ============================================================
def create_banner():
    print("Creating banner 1200x600...")
    w, h = 1200, 600
    img = Image.new('RGB', (w, h))
    create_gradient_bg(img, CHARCOAL, DEEP_BG)
    draw = ImageDraw.Draw(img)

    # Sunburst from center-left
    draw_sunburst(draw, 300, h // 2, 500, (30, 30, 55), num_rays=48, ray_width=1)

    # Art Deco border
    bm = 16
    draw_decorative_frame(draw, bm, bm, w - bm, h - bm, GOLD, DARK_GOLD)

    # Inner border
    im = 32
    draw.rectangle([im, im, w - im, h - im], outline=DARK_GOLD, width=1)

    # Vertical Art Deco line accents on left side
    draw_vertical_lines(draw, 50, 90, 50, h - 50, (40, 40, 65), spacing=8)

    # Vertical lines on right side
    draw_vertical_lines(draw, w - 90, w - 50, 50, h - 50, (40, 40, 65), spacing=8)

    # RT Monogram on left
    draw_rt_monogram(draw, 220, h // 2 - 10, 90, GOLD, DARK_GOLD)

    # Decorative diamond divider
    div_x = 370
    for dy in range(-80, 81, 20):
        ds = 4
        cy = h // 2 + dy
        draw.polygon([(div_x, cy - ds), (div_x + ds, cy), (div_x, cy + ds), (div_x - ds, cy)], fill=GOLD)
    draw.line([(div_x, h // 2 - 100), (div_x, h // 2 + 100)], fill=DARK_GOLD, width=1)

    # Title text on the right
    font_roaring = get_font(72, bold=True)
    font_trades = get_font(72, bold=True)
    font_sub = get_font(26, bold=False)
    font_tagline = get_font(20, bold=False)

    text_x = 420

    # "ROARING"
    draw.text((text_x, h // 2 - 110), "ROARING", font=font_roaring, fill=GOLD)

    # "TRADES"
    draw.text((text_x, h // 2 - 35), "TRADES", font=font_trades, fill=LIGHT_GOLD)

    # Decorative line under title
    line_y = h // 2 + 55
    draw.line([(text_x, line_y), (text_x + 400, line_y)], fill=GOLD, width=2)
    draw.line([(text_x, line_y + 5), (text_x + 400, line_y + 5)], fill=DARK_GOLD, width=1)

    # Subtitle
    draw.text((text_x, h // 2 + 75), "CHICAGO, 1920s", font=font_sub, fill=CREAM)

    # Tagline
    draw.text((text_x, h // 2 + 115), "Buy low, sell high. Build your empire in 30 days.",
              font=font_tagline, fill=DARK_GOLD)

    # Decorative dots under tagline
    dot_y = h // 2 + 155
    for dx in range(0, 200, 20):
        ds = 2
        cx = text_x + 100 + dx
        draw.polygon([(cx, dot_y - ds), (cx + ds, dot_y), (cx, dot_y + ds), (cx - ds, dot_y)], fill=GOLD)

    # Top decorative arch/fan
    fan_cx = w // 2
    for i in range(9):
        angle = math.pi + math.pi * i / 8
        r = 22
        ex = fan_cx + int(r * math.cos(angle))
        ey = 20 + int(r * math.sin(angle))
        draw.line([(fan_cx, 20), (ex, ey)], fill=GOLD, width=2)

    # Bottom decorative arch/fan
    for i in range(9):
        angle = math.pi * i / 8
        r = 22
        ex = fan_cx + int(r * math.cos(angle))
        ey = h - 20 + int(r * math.sin(angle))
        draw.line([(fan_cx, h - 20), (ex, ey)], fill=GOLD, width=2)

    # Corner accent lines (Art Deco style)
    accent_len = 60
    for corner_x, corner_y, dx, dy in [(50, 50, 1, 1), (w-50, 50, -1, 1),
                                         (50, h-50, 1, -1), (w-50, h-50, -1, -1)]:
        draw.line([(corner_x, corner_y), (corner_x + accent_len * dx, corner_y)], fill=GOLD, width=2)
        draw.line([(corner_x, corner_y), (corner_x, corner_y + accent_len * dy)], fill=GOLD, width=2)

    img.save(os.path.join(OUTPUT_DIR, "banner_1200x600.png"), "PNG")
    print(f"  Saved banner_1200x600.png")


# ============================================================
# 3. EDITOR'S CHOICE GRAPHIC (1200x1200)
# ============================================================
def create_editors_choice():
    print("Creating editor's choice 1200x1200...")
    size = 1200
    img = Image.new('RGB', (size, size))

    # Rich gradient background
    draw = ImageDraw.Draw(img)
    for y in range(size):
        ratio = y / size
        # Gradient from deep charcoal to dark burgundy-tinted
        r = int(CHARCOAL[0] * (1 - ratio) + DARK_BURGUNDY[0] * ratio * 0.5 + DEEP_BG[0] * ratio * 0.5)
        g = int(CHARCOAL[1] * (1 - ratio) + DARK_BURGUNDY[1] * ratio * 0.5 + DEEP_BG[1] * ratio * 0.5)
        b = int(CHARCOAL[2] * (1 - ratio) + DARK_BURGUNDY[2] * ratio * 0.5 + DEEP_BG[2] * ratio * 0.5)
        draw.line([(0, y), (size, y)], fill=(r, g, b))

    # Large sunburst from center
    draw_sunburst(draw, size // 2, size // 2, 520, (35, 35, 60), num_rays=60, ray_width=1)

    # Outer Art Deco border
    bm = 20
    draw_decorative_frame(draw, bm, bm, size - bm, size - bm, GOLD, DARK_GOLD)

    # Middle border
    im = 40
    draw.rectangle([im, im, size - im, size - im], outline=DARK_GOLD, width=1)

    # Inner border
    im2 = 50
    draw.rectangle([im2, im2, size - im2, size - im2], outline=GOLD, width=2)

    # Vertical line accents on sides
    draw_vertical_lines(draw, 62, 100, 62, size - 62, (40, 40, 65), spacing=8)
    draw_vertical_lines(draw, size - 100, size - 62, 62, size - 62, (40, 40, 65), spacing=8)

    # === TOP SECTION: Decorative header ===
    # Art Deco arch/fan at top
    fan_cx = size // 2
    for i in range(13):
        angle = math.pi + math.pi * i / 12
        r = 35
        ex = fan_cx + int(r * math.cos(angle))
        ey = 40 + int(r * math.sin(angle))
        draw.line([(fan_cx, 40), (ex, ey)], fill=GOLD, width=2)

    # Top decorative line
    line_y = 90
    draw.line([(120, line_y), (size - 120, line_y)], fill=GOLD, width=2)
    draw.line([(120, line_y + 5), (size - 120, line_y + 5)], fill=DARK_GOLD, width=1)

    # Diamond accents on top line
    for dx in range(0, size - 240, 40):
        ds = 4
        cx = 120 + dx
        draw.polygon([(cx, line_y - ds), (cx + ds, line_y), (cx, line_y + ds), (cx - ds, line_y)], fill=GOLD)

    # === MAIN CONTENT ===

    # "ROARING" - large
    font_main = get_font(110, bold=True)
    draw_text_with_shadow(draw, "ROARING", 140, font_main, GOLD, size, shadow_color=(100, 80, 20), offset=3)

    # "TRADES" - large
    draw_text_with_shadow(draw, "TRADES", 265, font_main, LIGHT_GOLD, size, shadow_color=(100, 80, 20), offset=3)

    # Decorative separator
    sep_y = 400
    draw.line([(180, sep_y), (size - 180, sep_y)], fill=GOLD, width=3)
    draw.line([(200, sep_y + 8), (size - 200, sep_y + 8)], fill=DARK_GOLD, width=1)

    # Center diamond on separator
    ds = 10
    draw.polygon([(size // 2, sep_y - ds), (size // 2 + ds, sep_y + 4),
                   (size // 2, sep_y + 4 + ds), (size // 2 - ds, sep_y + 4)], fill=GOLD)

    # RT Monogram centered
    draw_rt_monogram(draw, size // 2, 530, 100, GOLD, DARK_GOLD)

    # Circle around monogram
    for r in [105, 110]:
        for angle_step in range(360):
            angle = math.radians(angle_step)
            x = size // 2 + int(r * math.cos(angle))
            y = 530 + int(r * math.sin(angle))
            draw.point((x, y), fill=GOLD if r == 110 else DARK_GOLD)

    # "CHICAGO, 1920s" subtitle
    font_sub = get_font(40, bold=True)
    draw_text_centered(draw, "CHICAGO, 1920s", 670, font_sub, CREAM, size)

    # Tagline
    font_tag = get_font(28, bold=False)
    draw_text_centered(draw, "Buy low, sell high.", 740, font_tag, DARK_GOLD, size)
    draw_text_centered(draw, "Build your trading empire in 30 days.", 778, font_tag, DARK_GOLD, size)

    # Feature highlights
    font_feat = get_font(22, bold=False)
    features = [
        "Bootleg 5 types of Prohibition-era spirits",
        "Explore 6 Chicago neighborhoods & speakeasies",
        "Upgrade from On Foot to Zeppelin",
        "Outsmart rival gangs & evade the law",
        "Solana wallet leaderboard integration",
    ]
    feat_y = 850
    for i, feat in enumerate(features):
        # Gold diamond bullet
        bx = 250
        by = feat_y + i * 35
        ds = 4
        draw.polygon([(bx, by + 8 - ds), (bx + ds, by + 8), (bx, by + 8 + ds), (bx - ds, by + 8)], fill=GOLD)
        draw.text((bx + 12, by), feat, font=font_feat, fill=CREAM)

    # Bottom decorative section
    bot_line_y = size - 110
    draw.line([(120, bot_line_y), (size - 120, bot_line_y)], fill=GOLD, width=2)
    draw.line([(120, bot_line_y + 5), (size - 120, bot_line_y + 5)], fill=DARK_GOLD, width=1)

    # Publisher credit
    font_pub = get_font(20, bold=False)
    draw_text_centered(draw, "A MIDMIGHTBIT GAMES PRODUCTION", size - 80, font_pub, DARK_GOLD, size)

    # Bottom fan
    for i in range(13):
        angle = math.pi * i / 12
        r = 35
        ex = fan_cx + int(r * math.cos(angle))
        ey = size - 40 + int(r * math.sin(angle))
        draw.line([(fan_cx, size - 40), (ex, ey)], fill=GOLD, width=2)

    # Corner accent flourishes
    accent_len = 80
    for corner_x, corner_y, dx, dy in [(60, 60, 1, 1), (size-60, 60, -1, 1),
                                         (60, size-60, 1, -1), (size-60, size-60, -1, -1)]:
        draw.line([(corner_x, corner_y), (corner_x + accent_len * dx, corner_y)], fill=GOLD, width=3)
        draw.line([(corner_x, corner_y), (corner_x, corner_y + accent_len * dy)], fill=GOLD, width=3)
        # Small diagonal
        draw.line([(corner_x, corner_y),
                   (corner_x + int(accent_len * 0.5 * dx), corner_y + int(accent_len * 0.5 * dy))],
                  fill=DARK_GOLD, width=2)

    img.save(os.path.join(OUTPUT_DIR, "editors_choice_1200x1200.png"), "PNG")
    print(f"  Saved editors_choice_1200x1200.png")


# ============================================================
# Run all
# ============================================================
if __name__ == "__main__":
    create_icon()
    create_banner()
    create_editors_choice()
    print("\nAll assets created in:", OUTPUT_DIR)
    for f in sorted(os.listdir(OUTPUT_DIR)):
        if f.endswith('.png'):
            fp = os.path.join(OUTPUT_DIR, f)
            img = Image.open(fp)
            print(f"  {f}: {img.size[0]}x{img.size[1]}, {os.path.getsize(fp) / 1024:.1f}KB")
