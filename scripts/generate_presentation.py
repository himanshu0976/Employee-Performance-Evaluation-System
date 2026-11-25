import os
import sys
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR
from pptx.enum.shapes import MSO_SHAPE

def create_deck():
    prs = Presentation()
    prs.slide_width = Inches(13.333)
    prs.slide_height = Inches(7.5)
    blank_layout = prs.slide_layouts[6]

    # Professional Corporate Palette (Navy / Royal Blue / Sky / White / Slate)
    NAVY_DARK    = RGBColor(15, 23, 42)       # #0F172A
    NAVY_MID     = RGBColor(30, 41, 59)       # #1E293B
    BLUE_PRIMARY = RGBColor(37, 99, 235)      # #2563EB
    BLUE_DARK    = RGBColor(30, 58, 138)      # #1E3A8A
    BLUE_LIGHT   = RGBColor(239, 246, 255)    # #EFF6FF
    CYAN_ACCENT  = RGBColor(14, 165, 233)     # #0EA5E9
    TEXT_DARK    = RGBColor(15, 23, 42)       # #0F172A
    TEXT_MUTED   = RGBColor(100, 116, 139)    # #64748B
    TEXT_LIGHT   = RGBColor(248, 250, 252)    # #F8FAFC
    WHITE        = RGBColor(255, 255, 255)    # #FFFFFF
    BG_LIGHT     = RGBColor(248, 250, 252)    # #F8FAFC
    CARD_BG      = RGBColor(255, 255, 255)    # #FFFFFF
    CARD_BORDER  = RGBColor(226, 232, 240)    # #E2E8F0
    GREEN_ACCENT = RGBColor(16, 185, 129)     # #10B981
    GOLD_ACCENT  = RGBColor(245, 158, 11)     # #F59E0B
    BORDER_DARK  = RGBColor(51, 65, 85)       # #334155

    screenshots_dir = r"c:\Users\hp\Downloads\EPS\docs\screenshots"

    def add_header(slide, tag_text, title_text, subtitle_text, slide_num):
        # Header background bar
        hdr = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0), Inches(0), Inches(13.333), Inches(1.15))
        hdr.fill.solid()
        hdr.fill.fore_color.rgb = WHITE
        hdr.line.color.rgb = CARD_BORDER
        hdr.line.width = Pt(1)

        # Top decorative thin primary line
        line = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0), Inches(0), Inches(13.333), Inches(0.06))
        line.fill.solid()
        line.fill.fore_color.rgb = BLUE_PRIMARY
        line.line.fill.background()

        # Tag pill
        pill = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(0.8), Inches(0.16), Inches(2.6), Inches(0.26))
        pill.fill.solid()
        pill.fill.fore_color.rgb = BLUE_LIGHT
        pill.line.color.rgb = BLUE_PRIMARY
        pill.line.width = Pt(0.75)
        ptf = pill.text_frame
        ptf.margin_top = Pt(1)
        ptf.margin_bottom = Pt(1)
        ptf.margin_left = Pt(6)
        ptf.margin_right = Pt(6)
        pp = ptf.paragraphs[0]
        pp.text = tag_text.upper()
        pp.font.name = "Segoe UI"
        pp.font.size = Pt(8.5)
        pp.font.bold = True
        pp.font.color.rgb = BLUE_PRIMARY

        # Title and Subtitle text box
        tb = slide.shapes.add_textbox(Inches(0.8), Inches(0.44), Inches(10.5), Inches(0.65))
        tf = tb.text_frame
        tf.margin_top = Pt(0)
        tf.margin_bottom = Pt(0)
        tf.margin_left = Pt(0)
        tf.margin_right = Pt(0)

        p = tf.paragraphs[0]
        p.text = title_text
        p.font.name = "Segoe UI"
        p.font.size = Pt(19)
        p.font.bold = True
        p.font.color.rgb = NAVY_DARK

        p2 = tf.add_paragraph()
        p2.text = subtitle_text
        p2.font.name = "Segoe UI"
        p2.font.size = Pt(10)
        p2.font.color.rgb = TEXT_MUTED

        # Slide Number Badge
        sn = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(12.0), Inches(0.35), Inches(0.6), Inches(0.45))
        sn.fill.solid()
        sn.fill.fore_color.rgb = BG_LIGHT
        sn.line.color.rgb = CARD_BORDER
        sn.line.width = Pt(1)
        stf = sn.text_frame
        stf.vertical_anchor = MSO_ANCHOR.MIDDLE
        sp = stf.paragraphs[0]
        sp.alignment = PP_ALIGN.CENTER
        sp.text = f"{slide_num:02d}"
        sp.font.name = "Segoe UI"
        sp.font.size = Pt(12)
        sp.font.bold = True
        sp.font.color.rgb = BLUE_PRIMARY

    def add_card(slide, left, top, width, height, title=None, bg_color=CARD_BG, border_color=CARD_BORDER):
        card = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, left, top, width, height)
        card.fill.solid()
        card.fill.fore_color.rgb = bg_color
        card.line.color.rgb = border_color
        card.line.width = Pt(1)
        if title:
            tb = slide.shapes.add_textbox(left + Inches(0.18), top + Inches(0.12), width - Inches(0.36), Inches(0.38))
            tf = tb.text_frame
            tf.margin_top = Pt(0)
            tf.margin_bottom = Pt(0)
            tf.margin_left = Pt(0)
            tf.margin_right = Pt(0)
            p = tf.paragraphs[0]
            p.text = title
            p.font.name = "Segoe UI"
            p.font.size = Pt(12.5)
            p.font.bold = True
            p.font.color.rgb = NAVY_DARK
        return card

    # =========================================================================
    # SLIDE 1: Title Slide (Dark Executive Theme with Team Table)
    # =========================================================================
    slide1 = prs.slides.add_slide(blank_layout)
    bg1 = slide1.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0), Inches(0), Inches(13.333), Inches(7.5))
    bg1.fill.solid()
    bg1.fill.fore_color.rgb = NAVY_DARK
    bg1.line.fill.background()

    # Decorative background glow panel on right
    glow = slide1.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(8.5), Inches(0.8), Inches(4.2), Inches(5.9))
    glow.fill.solid()
    glow.fill.fore_color.rgb = NAVY_MID
    glow.line.color.rgb = BORDER_DARK
    glow.line.width = Pt(1)

    # Top Program Tag
    t_badge = slide1.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(0.8), Inches(0.8), Inches(4.6), Inches(0.36))
    t_badge.fill.solid()
    t_badge.fill.fore_color.rgb = RGBColor(30, 58, 138)
    t_badge.line.color.rgb = CYAN_ACCENT
    t_badge.line.width = Pt(1)
    t_tf = t_badge.text_frame
    t_tf.vertical_anchor = MSO_ANCHOR.MIDDLE
    tp = t_tf.paragraphs[0]
    tp.text = "GUVI • HCLTech CAMPUS PROGRAM | REVIEW 2"
    tp.font.name = "Segoe UI"
    tp.font.size = Pt(10)
    tp.font.bold = True
    tp.font.color.rgb = CYAN_ACCENT

    # Main Project Title
    ttb = slide1.shapes.add_textbox(Inches(0.8), Inches(1.35), Inches(7.4), Inches(1.7))
    ttf = ttb.text_frame
    ttf.word_wrap = True
    ttf.margin_left = Pt(0)
    ttf.margin_top = Pt(0)
    p = ttf.paragraphs[0]
    p.text = "Employee Performance"
    p.font.name = "Segoe UI"
    p.font.size = Pt(34)
    p.font.bold = True
    p.font.color.rgb = WHITE

    p2 = ttf.add_paragraph()
    p2.text = "Evaluation System (EPS)"
    p2.font.name = "Segoe UI"
    p2.font.size = Pt(34)
    p2.font.bold = True
    p2.font.color.rgb = CYAN_ACCENT

    p3 = ttf.add_paragraph()
    p3.space_before = Pt(8)
    p3.text = "An Enterprise Classical MVC Web Platform for Objective Workforce Appraisals, Multi-Tier Scoring & Real-Time Performance Analytics"
    p3.font.name = "Segoe UI"
    p3.font.size = Pt(11.5)
    p3.font.color.rgb = RGBColor(203, 213, 225)

    # Separate Professional Team Members Section (Left Side)
    # Header label for Team Section
    tm_label_box = slide1.shapes.add_textbox(Inches(0.8), Inches(3.45), Inches(7.4), Inches(0.35))
    tm_ltf = tm_label_box.text_frame
    tm_ltf.margin_left = Pt(0)
    tm_ltf.margin_top = Pt(0)
    tmp = tm_ltf.paragraphs[0]
    tmp.text = "PROJECT TEAM MEMBERS"
    tmp.font.name = "Segoe UI"
    tmp.font.size = Pt(11)
    tmp.font.bold = True
    tmp.font.color.rgb = CYAN_ACCENT

    # Professional Table with Separate Columns (S.No. | Team Member Name | University Roll Number | Role)
    team_data = [
        ["S.No.", "Team Member Name", "University Roll Number", "Role"],
        ["1", "Himanshu Kumar Jaiswal", "25SCSE1180087", "Admin"],
        ["2", "Aryan Verma", "25SCSE1181197", "Member"],
        ["3", "Shivam Kumar", "25SCSE1181117", "Member"]
    ]

    t_table_shape = slide1.shapes.add_table(4, 4, Inches(0.8), Inches(3.85), Inches(7.4), Inches(1.9))
    t_table = t_table_shape.table
    t_table.columns[0].width = Inches(0.7)
    t_table.columns[1].width = Inches(2.9)
    t_table.columns[2].width = Inches(2.4)
    t_table.columns[3].width = Inches(1.4)

    for row_idx, row in enumerate(team_data):
        for col_idx, val in enumerate(row):
            cell = t_table.cell(row_idx, col_idx)
            cell.text = ""
            cell.vertical_anchor = MSO_ANCHOR.MIDDLE
            cell.margin_left = Pt(8)
            cell.margin_right = Pt(8)
            cell.margin_top = Pt(4)
            cell.margin_bottom = Pt(4)

            # Styling
            if row_idx == 0:
                cell.fill.solid()
                cell.fill.fore_color.rgb = BLUE_DARK
                p = cell.text_frame.paragraphs[0]
                p.text = val
                p.font.name = "Segoe UI"
                p.font.size = Pt(10)
                p.font.bold = True
                p.font.color.rgb = CYAN_ACCENT
                if col_idx in [0, 2, 3]:
                    p.alignment = PP_ALIGN.CENTER
                else:
                    p.alignment = PP_ALIGN.LEFT
            else:
                cell.fill.solid()
                cell.fill.fore_color.rgb = RGBColor(24, 34, 53) if row_idx % 2 == 1 else NAVY_MID
                p = cell.text_frame.paragraphs[0]
                p.text = val
                p.font.name = "Segoe UI"
                p.font.size = Pt(10)
                p.font.bold = (col_idx == 1 or col_idx == 3)
                p.font.color.rgb = WHITE if col_idx != 3 else (GOLD_ACCENT if val == "Admin" else RGBColor(147, 197, 253))
                if col_idx in [0, 2, 3]:
                    p.alignment = PP_ALIGN.CENTER
                else:
                    p.alignment = PP_ALIGN.LEFT

    # Implementation Highlights Pill below Team Table
    hi_box = slide1.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(0.8), Inches(5.95), Inches(7.4), Inches(0.75))
    hi_box.fill.solid()
    hi_box.fill.fore_color.rgb = RGBColor(24, 34, 53)
    hi_box.line.color.rgb = RGBColor(59, 130, 246)
    hi_box.line.width = Pt(1)
    hitf = hi_box.text_frame
    hitf.vertical_anchor = MSO_ANCHOR.MIDDLE
    hitf.margin_left = Pt(12)
    hip1 = hitf.paragraphs[0]
    hip1.text = "PROJECT IMPLEMENTATION STATUS"
    hip1.font.name = "Segoe UI"
    hip1.font.size = Pt(10)
    hip1.font.bold = True
    hip1.font.color.rgb = GREEN_ACCENT

    hip2 = hitf.add_paragraph()
    hip2.space_before = Pt(2)
    hip2.text = "Complete 3-Tier MVC Architecture  •  Jakarta Servlets 6.0 & JSP 3.1  •  23 Automated Tests Verified (100% Pass)"
    hip2.font.name = "Segoe UI"
    hip2.font.size = Pt(9.5)
    hip2.font.color.rgb = RGBColor(226, 232, 240)

    # Project Metadata Card (Right Side)
    s_card = slide1.shapes.add_textbox(Inches(8.8), Inches(1.05), Inches(3.6), Inches(5.4))
    stf = s_card.text_frame
    stf.word_wrap = True

    sp = stf.paragraphs[0]
    sp.text = "PROJECT METADATA"
    sp.font.name = "Segoe UI"
    sp.font.size = Pt(11)
    sp.font.bold = True
    sp.font.color.rgb = CYAN_ACCENT

    fields = [
        ("Department / School", "School of Computing Science & Eng."),
        ("University / Institution", "Galgotias University"),
        ("Program & Degree", "B.Tech Computer Science & Eng."),
        ("Industry Partner", "HCLTech / GUVI Collaboration"),
        ("Technology Track", "Java Web Technologies (JSP/Servlets)"),
        ("Architectural Style", "Classical 3-Tier MVC (Non-Spring)"),
        ("Database Engine", "MySQL 8.0 with HikariCP & H2 Fallback"),
        ("Verification Status", "23 Automated JUnit Tests Passing (0 Failures)")
    ]

    for label, val in fields:
        p_lbl = stf.add_paragraph()
        p_lbl.space_before = Pt(7)
        p_lbl.text = label.upper()
        p_lbl.font.name = "Segoe UI"
        p_lbl.font.size = Pt(8)
        p_lbl.font.bold = True
        p_lbl.font.color.rgb = RGBColor(148, 163, 184)

        p_val = stf.add_paragraph()
        p_val.text = val
        p_val.font.name = "Segoe UI"
        p_val.font.size = Pt(10)
        p_val.font.bold = True
        p_val.font.color.rgb = WHITE

    # Bottom footer accent line
    foot = slide1.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0), Inches(7.35), Inches(13.333), Inches(0.15))
    foot.fill.solid()
    foot.fill.fore_color.rgb = BLUE_PRIMARY
    foot.line.fill.background()

    # =========================================================================
    # SLIDE 2: Introduction & Project Overview
    # =========================================================================
    slide2 = prs.slides.add_slide(blank_layout)
    add_header(slide2, "Project Overview", "Introduction: Employee Performance Evaluation System",
               "A structured, automated enterprise solution modernizing workforce appraisals and accountability", 2)

    # 3 Pillar Cards
    c1 = add_card(slide2, Inches(0.8), Inches(1.5), Inches(3.7), Inches(4.3), "System Overview")
    ct1 = slide2.shapes.add_textbox(Inches(0.95), Inches(2.0), Inches(3.4), Inches(3.6))
    cf1 = ct1.text_frame
    cf1.word_wrap = True
    p = cf1.paragraphs[0]
    p.text = "• Enterprise Java Web Application strictly implementing the classical Model-View-Controller (MVC) architectural pattern.\n\n• Designed without heavy frameworks (e.g. Spring Boot) to demonstrate foundational engineering mastery of Jakarta Servlets 6.0 and JSP 3.1.\n\n• Provides role-based access for System Administrators, Managers (Evaluators), and Employees.\n\n• Centralizes employee management, department rosters, review cycles, and multi-tier evaluations."
    p.font.name = "Segoe UI"
    p.font.size = Pt(10)
    p.font.color.rgb = TEXT_DARK

    c2 = add_card(slide2, Inches(4.8), Inches(1.5), Inches(3.7), Inches(4.3), "Purpose & Importance")
    ct2 = slide2.shapes.add_textbox(Inches(4.95), Inches(2.0), Inches(3.4), Inches(3.6))
    cf2 = ct2.text_frame
    cf2.word_wrap = True
    p = cf2.paragraphs[0]
    p.text = "• Eliminate Subjectivity: Replaces arbitrary manager opinions with standardized mathematical grading across 6 key performance criteria.\n\n• Accountability & Transparency: Empowers employees with direct visibility into scoring breakdowns, manager feedback, and growth areas.\n\n• Timely Appraisals: Schedules and enforces structured evaluation cycles (Upcoming, Active, Completed) avoiding year-end review backlogs.\n\n• Audit & Compliance: Maintains tamper-proof appraisal histories and secure records."
    p.font.name = "Segoe UI"
    p.font.size = Pt(10)
    p.font.color.rgb = TEXT_DARK

    c3 = add_card(slide2, Inches(8.8), Inches(1.5), Inches(3.7), Inches(4.3), "Key Value Delivered")
    ct3 = slide2.shapes.add_textbox(Inches(8.95), Inches(2.0), Inches(3.4), Inches(3.6))
    cf3 = ct3.text_frame
    cf3.word_wrap = True
    p = cf3.paragraphs[0]
    p.text = "• Automated Scoring Engine: Computes weighted scores and maps employees automatically to 5 objective rating tiers.\n\n• Multi-Axis Competency Radar: Visualizes individual strengths and improvement dimensions using Chart.js.\n\n• Executive Analytics: Delivers organization-wide department score comparisons and rating distribution charts.\n\n• One-Click CSV Export: Instantly generates RFC-4180 compliant CSV exports for HR data integration."
    p.font.name = "Segoe UI"
    p.font.size = Pt(10)
    p.font.color.rgb = TEXT_DARK

    # Bottom Stat Highlights
    stat_card = add_card(slide2, Inches(0.8), Inches(6.0), Inches(11.7), Inches(1.0), bg_color=BLUE_LIGHT, border_color=BLUE_PRIMARY)
    stb = slide2.shapes.add_textbox(Inches(1.0), Inches(6.1), Inches(11.3), Inches(0.8))
    stf = stb.text_frame
    stf.word_wrap = True
    sp = stf.paragraphs[0]
    sp.text = "CORE METRICS AT A GLANCE"
    sp.font.name = "Segoe UI"
    sp.font.size = Pt(9)
    sp.font.bold = True
    sp.font.color.rgb = BLUE_PRIMARY

    sp2 = stf.add_paragraph()
    sp2.text = "3 Role-Based Modules (Admin, Manager, Employee)  |  6 Evaluation Dimensions (100% Weightage)  |  7 Normalized DB Tables  |  23 Automated Tests Passed"
    sp2.font.name = "Segoe UI"
    sp2.font.size = Pt(11)
    sp2.font.bold = True
    sp2.font.color.rgb = NAVY_DARK

    # =========================================================================
    # SLIDE 3: Problem Statement
    # =========================================================================
    slide3 = prs.slides.add_slide(blank_layout)
    add_header(slide3, "Problem Context", "Problem Statement: Challenges in Manual Performance Appraisals",
               "Why conventional spreadsheet and paper-based evaluation workflows fail in modern organizations", 3)

    # 4 Challenge vs Solution Blocks
    problems = [
        ("Manual Calculations & Formula Errors",
         "Spreadsheets require manual entry of weighted averages, leading to rounding mistakes, miscalculated ratings, and disputes during annual promotions.",
         "Automated scoring algorithm with precision rounding and automated classification into 5 distinct rating tiers."),
        ("Subjectivity, Bias & Lack of Standardization",
         "Different managers grade with varying leniency; without fixed weights and standardized criteria, performance standards differ wildly across departments.",
         "Unified 6-criteria matrix (Attendance, Quality, Productivity, Teamwork, Communication, Goals) summing to 100%."),
        ("Disorganized Cycles & Lack of Historical Tracking",
         "Appraisals done on paper or email threads get lost; managers and staff cannot track multi-year competency progression or growth trends.",
         "Centralized Review Cycles (Active, Completed) with full historical archives and printable review sheets."),
        ("Zero Organizational Visibility & Analytics Delays",
         "HR spends days compiling data across departments to understand talent distribution, delaying bonuses, promotions, and training initiatives.",
         "Real-time interactive Chart.js dashboards (Radar, Bar, Doughnut) plus one-click RFC-4180 CSV export for HR systems.")
    ]

    for i, (title, issue, solution) in enumerate(problems):
        row = i // 2
        col = i % 2
        l = Inches(0.8 + col * 5.95)
        t = Inches(1.5 + row * 2.7)
        w = Inches(5.75)
        h = Inches(2.5)

        card = add_card(slide3, l, t, w, h)
        tb = slide3.shapes.add_textbox(l + Inches(0.18), t + Inches(0.12), w - Inches(0.36), h - Inches(0.24))
        tf = tb.text_frame
        tf.word_wrap = True

        p = tf.paragraphs[0]
        p.text = f"{i+1}. {title}"
        p.font.name = "Segoe UI"
        p.font.size = Pt(12)
        p.font.bold = True
        p.font.color.rgb = NAVY_DARK

        p_iss_lbl = tf.add_paragraph()
        p_iss_lbl.space_before = Pt(5)
        p_iss_lbl.text = "CRITICAL PAIN POINT:"
        p_iss_lbl.font.name = "Segoe UI"
        p_iss_lbl.font.size = Pt(8)
        p_iss_lbl.font.bold = True
        p_iss_lbl.font.color.rgb = RGBColor(220, 38, 38) # Red

        p_iss = tf.add_paragraph()
        p_iss.text = issue
        p_iss.font.name = "Segoe UI"
        p_iss.font.size = Pt(9.5)
        p_iss.font.color.rgb = TEXT_DARK

        p_sol_lbl = tf.add_paragraph()
        p_sol_lbl.space_before = Pt(4)
        p_sol_lbl.text = "EPS AUTOMATED SOLUTION:"
        p_sol_lbl.font.name = "Segoe UI"
        p_sol_lbl.font.size = Pt(8)
        p_sol_lbl.font.bold = True
        p_sol_lbl.font.color.rgb = GREEN_ACCENT

        p_sol = tf.add_paragraph()
        p_sol.text = solution
        p_sol.font.name = "Segoe UI"
        p_sol.font.size = Pt(9.5)
        p_sol.font.bold = True
        p_sol.font.color.rgb = BLUE_DARK

    # =========================================================================
    # SLIDE 4: Project Objectives
    # =========================================================================
    slide4 = prs.slides.add_slide(blank_layout)
    add_header(slide4, "Project Goals", "Project Objectives: Strategic, Technical & Operational Targets",
               "Clear architectural objectives delivering measurable efficiency, transparency, and accuracy", 4)

    pillars = [
        ("Employee Performance Tracking",
         BLUE_PRIMARY,
         "Enable continuous, multi-dimensional tracking of employee competencies across defined review cycles.",
         [
             "Individual self-service dashboard for employees with real-time feedback.",
             "Multi-axis Chart.js Radar chart visualizing strengths & growth areas.",
             "Complete historical review progression across quarterly and annual reviews.",
             "Encourage continuous professional development through qualitative manager notes."
         ]),
        ("Automated Evaluation Management",
         GREEN_ACCENT,
         "Standardize appraisal scoring using a mathematically weighted 6-criteria evaluation engine.",
         [
             "Admin governance over 6 performance criteria and dynamic weightages.",
             "Manager workflow with real-time JavaScript weighted preview as scores are keyed.",
             "Draft mode allowing evaluators to pause and resume assessments.",
             "Automated rating assignment: Outstanding (4.5–5.0) down to Unsatisfactory (<1.5)."
         ]),
        ("Executive Reporting & Governance",
         CYAN_ACCENT,
         "Deliver instantaneous organization-wide intelligence, compliance, and data portability.",
         [
             "Dynamic Chart.js visualizations: Department performance bar & rating pie charts.",
             "Multi-parameter reporting filters (by review cycle, department, and rating tier).",
             "RFC-4180 streaming CSV export for seamless HRIS and payroll integration.",
             "Session-based security, BCrypt hashing, and role authorization enforcement."
         ])
    ]

    for i, (title, color, desc, bullets) in enumerate(pillars):
        l = Inches(0.8 + i * 3.95)
        t = Inches(1.5)
        w = Inches(3.8)
        h = Inches(5.5)

        card = add_card(slide4, l, t, w, h)

        # Top colored banner
        top_bar = slide4.shapes.add_shape(MSO_SHAPE.RECTANGLE, l, t, w, Inches(0.1))
        top_bar.fill.solid()
        top_bar.fill.fore_color.rgb = color
        top_bar.line.fill.background()

        tb = slide4.shapes.add_textbox(l + Inches(0.2), t + Inches(0.25), w - Inches(0.4), h - Inches(0.4))
        tf = tb.text_frame
        tf.word_wrap = True

        p = tf.paragraphs[0]
        p.text = f"Pillar 0{i+1}"
        p.font.name = "Segoe UI"
        p.font.size = Pt(9)
        p.font.bold = True
        p.font.color.rgb = color

        p_t = tf.add_paragraph()
        p_t.text = title
        p_t.font.name = "Segoe UI"
        p_t.font.size = Pt(13)
        p_t.font.bold = True
        p_t.font.color.rgb = NAVY_DARK

        p_d = tf.add_paragraph()
        p_d.space_before = Pt(6)
        p_d.text = desc
        p_d.font.name = "Segoe UI"
        p_d.font.size = Pt(9.5)
        p_d.font.color.rgb = TEXT_MUTED

        sep = tf.add_paragraph()
        sep.space_before = Pt(8)
        sep.text = "KEY DELIVERABLES:"
        sep.font.name = "Segoe UI"
        sep.font.size = Pt(8)
        sep.font.bold = True
        sep.font.color.rgb = NAVY_DARK

        for b in bullets:
            bp = tf.add_paragraph()
            bp.space_before = Pt(4)
            bp.text = f"• {b}"
            bp.font.name = "Segoe UI"
            bp.font.size = Pt(9.5)
            bp.font.color.rgb = TEXT_DARK

    # =========================================================================
    # SLIDE 5: Technology Stack
    # =========================================================================
    slide5 = prs.slides.add_slide(blank_layout)
    add_header(slide5, "Technical Blueprint", "Technology Stack & Engineering Justification",
               "Enterprise open-source stack adhering strictly to Jakarta EE 10 standards without Spring Boot overhead", 5)

    tech_categories = [
        ("Backend & Web Container",
         [("Java SE 17 LTS", "Modern language features, pattern matching, switch expressions, strong type safety."),
          ("Jakarta Servlet 6.0", "Asynchronous request processing, @WebServlet annotations, robust HTTP lifecycle."),
          ("JSP 3.1 & JSTL 3.0", "Clean server-side presentation, expression language (EL), no Java scriptlets in JSP."),
          ("Apache Tomcat 10.1.20", "Jakarta EE 10 compliant high-performance web servlet container.")]),
        ("Database & Persistence",
         [("MySQL Server 8.0+", "Relational database with InnoDB engine, foreign keys, 3NF normalization, and ACID safety."),
          ("JDBC & PreparedStatements", "100% parameterized queries eliminating SQL injection vulnerabilities."),
          ("HikariCP 5.1.0", "Lightning-fast JDBC connection pooling with optimal keep-alive and lease management."),
          ("Embedded H2 Fallback", "Automated in-memory fallback for zero-friction standalone testing and evaluation.")]),
        ("Security & Utilities",
         [("jBCrypt 0.4", "One-way cryptographic password hashing using salted Blowfish algorithms ($2a$10$)."),
          ("Session Fixation Defense", "Automatic session invalidation and re-creation upon authentication."),
          ("Google Gson 2.10.1", "Type-safe JSON serialization feeding Chart.js dynamic radar/bar components."),
          ("Apache Maven 3.9.6", "Strict dependency resolution, multi-stage testing, and WAR archive packaging.")]),
        ("Frontend & Visualization",
         [("Bootstrap 5.3.3", "Responsive mobile-first layout, flexbox grid, modern clean cards, and modal dialogs."),
          ("Chart.js 4.4.2", "Dynamic client-side visualizations: 6-axis Radar, Department Bar, and Rating Doughnut."),
          ("Bootstrap Icons 1.11.3", "Consistent vector iconography across navigation, actions, and KPI widgets."),
          ("Custom CSS & main.js", "Live client-side scoring formula calculations and interactive password visibility.")])
    ]

    for i, (cat_title, items) in enumerate(tech_categories):
        l = Inches(0.8 + i * 2.95)
        t = Inches(1.5)
        w = Inches(2.8)
        h = Inches(5.5)

        card = add_card(slide5, l, t, w, h, cat_title)
        tb = slide5.shapes.add_textbox(l + Inches(0.15), t + Inches(0.55), w - Inches(0.3), h - Inches(0.65))
        tf = tb.text_frame
        tf.word_wrap = True

        for j, (name, reason) in enumerate(items):
            p1 = tf.paragraphs[0] if j == 0 else tf.add_paragraph()
            if j > 0:
                p1.space_before = Pt(8)
            p1.text = name
            p1.font.name = "Segoe UI"
            p1.font.size = Pt(10.5)
            p1.font.bold = True
            p1.font.color.rgb = BLUE_PRIMARY

            p2 = tf.add_paragraph()
            p2.text = reason
            p2.font.name = "Segoe UI"
            p2.font.size = Pt(8.5)
            p2.font.color.rgb = TEXT_DARK

    # =========================================================================
    # SLIDE 6: System Architecture & Component Communication
    # =========================================================================
    slide6 = prs.slides.add_slide(blank_layout)
    add_header(slide6, "System Design", "System Architecture: Classical 3-Tier MVC Architecture",
               "Clear separation of concerns across presentation, business services, and database layers", 6)

    layers = [
        ("1. Presentation Tier (JSP, JSTL, Bootstrap 5, Chart.js)",
         "Client browser issues HTTP requests. JSP views render dynamically using JSTL tags. Interactive Chart.js canvas elements consume JSON endpoints for Radar, Bar, and Doughnut data visualizations.",
         BLUE_LIGHT, BLUE_PRIMARY),
        ("2. Interception & Security Layer (EncodingFilter & AuthFilter)",
         "EncodingFilter forces UTF-8 character encoding. AuthFilter validates user session state, prevents unauthorized route traversal, and strictly enforces Role-Based Access Control (/admin/*, /manager/*, /employee/*).",
         RGBColor(241, 245, 249), NAVY_MID),
        ("3. Controller & Business Service Tier (Jakarta Servlets & ServiceImpl)",
         "Front Controller Servlets (@WebServlet) handle routing and validation. Delegate business rules to Service interfaces (AuthService, EvaluationService, DepartmentService, ReportService). Encapsulates weighted scoring math.",
         BLUE_LIGHT, BLUE_DARK),
        ("4. Data Persistence & Infrastructure Tier (DAO Layer, HikariCP, MySQL 8)",
         "GenericDao<T, ID> contract implemented via JDBC PreparedStatements. HikariCP connection pool optimizes database leases. MySQL 8 stores normalized data in 3NF with automated H2 fallback resilience.",
         RGBColor(236, 253, 245), GREEN_ACCENT)
    ]

    for i, (title, desc, bg, border) in enumerate(layers):
        t = Inches(1.5 + i * 1.35)
        card = add_card(slide6, Inches(0.8), t, Inches(8.0), Inches(1.2), bg_color=bg, border_color=border)
        tb = slide6.shapes.add_textbox(Inches(1.0), t + Inches(0.12), Inches(7.6), Inches(0.95))
        tf = tb.text_frame
        tf.word_wrap = True

        p = tf.paragraphs[0]
        p.text = title
        p.font.name = "Segoe UI"
        p.font.size = Pt(11)
        p.font.bold = True
        p.font.color.rgb = border

        p2 = tf.add_paragraph()
        p2.space_before = Pt(3)
        p2.text = desc
        p2.font.name = "Segoe UI"
        p2.font.size = Pt(9)
        p2.font.color.rgb = TEXT_DARK

    # Right side: Request Flow Summary Card
    flow_card = add_card(slide6, Inches(9.1), Inches(1.5), Inches(3.4), Inches(5.25), "End-to-End Request Flow")
    ftb = slide6.shapes.add_textbox(Inches(9.25), Inches(2.05), Inches(3.1), Inches(4.5))
    ftf = ftb.text_frame
    ftf.word_wrap = True

    steps = [
        ("Step 1: User Request", "Browser sends HTTP GET/POST with credentials or form data."),
        ("Step 2: AuthFilter Check", "Intercepts URL; verifies active session and role permission."),
        ("Step 3: Servlet Dispatch", "Extracts parameters, calls ValidationUtil, delegates to Service."),
        ("Step 4: Business Execution", "Service performs calculations (e.g. weighted score & rating)."),
        ("Step 5: DAO Persistence", "Executes parameterized PreparedStatement via HikariCP."),
        ("Step 6: JSP Rendering", "Populates request attributes; forwards to view with JSTL/Chart.js.")
    ]

    for j, (stitle, sdesc) in enumerate(steps):
        p1 = ftf.paragraphs[0] if j == 0 else ftf.add_paragraph()
        if j > 0:
            p1.space_before = Pt(5)
        p1.text = stitle
        p1.font.name = "Segoe UI"
        p1.font.size = Pt(9.5)
        p1.font.bold = True
        p1.font.color.rgb = BLUE_PRIMARY

        p2 = ftf.add_paragraph()
        p2.text = sdesc
        p2.font.name = "Segoe UI"
        p2.font.size = Pt(8.5)
        p2.font.color.rgb = TEXT_MUTED

    # =========================================================================
    # SLIDE 7: Database Design & ER Relationships
    # =========================================================================
    slide7 = prs.slides.add_slide(blank_layout)
    add_header(slide7, "Data Modeling", "Database Design: 7 Normalized Relational Tables & ER Architecture",
               "Robust 3NF relational model with foreign key constraints, unique indexes, and cascade integrity", 7)

    db_tables = [
        ("users", "id (PK), username (UK), password_hash, role (ENUM), full_name, email (UK), phone, is_active", "Stores credentials (BCrypt hashed) & global role definitions."),
        ("departments", "id (PK), name (UK), code (UK), description, created_at", "Organizational units (Engineering, Marketing, Human Resources)."),
        ("employees", "id (PK), user_id (FK, UK), employee_code (UK), department_id (FK), manager_id (FK), designation, hire_date, salary", "1:1 link with users table; references department & assigned manager."),
        ("evaluation_cycles", "id (PK), title, description, start_date, end_date, status (ENUM: UPCOMING, ACTIVE, COMPLETED)", "Defines performance review windows and governance state."),
        ("evaluation_criteria", "id (PK), name (UK), description, weightage (DECIMAL), is_active", "The 6 performance evaluation dimensions summing to 100% weight."),
        ("evaluations", "id (PK), cycle_id (FK), employee_id (FK), evaluator_id (FK), total_score, rating, status (ENUM), feedback", "Master evaluation record holding calculated rating and status."),
        ("evaluation_scores", "id (PK), evaluation_id (FK), criteria_id (FK), score (1.0-5.0), feedback (TEXT)", "Detailed breakdown of scores and specific feedback per criterion.")
    ]

    t_card = add_card(slide7, Inches(0.8), Inches(1.5), Inches(8.0), Inches(5.5), "Relational Database Schema (eps_db)")
    ttb = slide7.shapes.add_textbox(Inches(1.0), Inches(2.0), Inches(7.6), Inches(4.8))
    ttf = ttb.text_frame
    ttf.word_wrap = True

    for k, (tname, tcols, tdesc) in enumerate(db_tables):
        p1 = ttf.paragraphs[0] if k == 0 else ttf.add_paragraph()
        if k > 0:
            p1.space_before = Pt(4)
        p1.text = f"• {tname.upper()}"
        p1.font.name = "Segoe UI"
        p1.font.size = Pt(9.5)
        p1.font.bold = True
        p1.font.color.rgb = BLUE_PRIMARY

        p2 = ttf.add_paragraph()
        p2.text = f"  Columns: {tcols}"
        p2.font.name = "Consolas"
        p2.font.size = Pt(8)
        p2.font.color.rgb = TEXT_DARK

        p3 = ttf.add_paragraph()
        p3.text = f"  Purpose: {tdesc}"
        p3.font.name = "Segoe UI"
        p3.font.size = Pt(8)
        p3.font.color.rgb = TEXT_MUTED

    # Right side: ER Cardinality Card
    er_card = add_card(slide7, Inches(9.1), Inches(1.5), Inches(3.4), Inches(5.5), "Entity Relationships & Cardinality")
    etb = slide7.shapes.add_textbox(Inches(9.25), Inches(2.0), Inches(3.1), Inches(4.8))
    etf = etb.text_frame
    etf.word_wrap = True

    rels = [
        ("users ↔ employees (1 : 1)", "Every employee record is strictly bound to exactly one login identity."),
        ("departments ↔ employees (1 : N)", "A department houses multiple employees; SET NULL on department deletion."),
        ("managers ↔ employees (1 : N)", "A manager supervises multiple staff; reassigned dynamically by Admin."),
        ("cycles ↔ evaluations (1 : N)", "An evaluation cycle groups multiple employee reviews (uq_cycle_employee)."),
        ("evaluations ↔ scores (1 : N)", "Each evaluation contains exactly 6 criteria scores (uq_eval_criterion)."),
        ("criteria ↔ scores (1 : N)", "A criterion is graded across numerous evaluations with historical integrity.")
    ]

    for j, (rtitle, rdesc) in enumerate(rels):
        p1 = etf.paragraphs[0] if j == 0 else etf.add_paragraph()
        if j > 0:
            p1.space_before = Pt(6)
        p1.text = rtitle
        p1.font.name = "Segoe UI"
        p1.font.size = Pt(9.5)
        p1.font.bold = True
        p1.font.color.rgb = GREEN_ACCENT

        p2 = etf.add_paragraph()
        p2.text = rdesc
        p2.font.name = "Segoe UI"
        p2.font.size = Pt(8.5)
        p2.font.color.rgb = TEXT_DARK

    # =========================================================================
    # SLIDE 8: Modules & Implemented Features
    # =========================================================================
    slide8 = prs.slides.add_slide(blank_layout)
    add_header(slide8, "Feature Matrix", "Functional Modules: Admin, Manager & Employee Portals",
               "Comprehensive breakdown of fully implemented role-based workflows and security features", 8)

    modules = [
        ("🛡️ Administrator Module",
         BLUE_PRIMARY,
         [
             ("Organization Dashboard", "Real-time summary KPIs (headcount, review cycles, average score, rating distributions)."),
             ("Departments CRUD", "Create, list, update, and delete company divisions with unique code validation."),
             ("Employees Directory", "Onboard employees, configure job designations, salaries, hire dates, and manager mappings."),
             ("Evaluation Criteria", "Configure the 6 performance criteria and adjust percentage weightages."),
             ("Review Cycles Control", "Schedule review periods; toggle statuses (UPCOMING, ACTIVE, COMPLETED)."),
             ("Executive Analytics", "Department comparison charts and multi-filtered RFC-4180 CSV export.")
         ]),
        ("👥 Manager Module",
         GREEN_ACCENT,
         [
             ("Direct Reports Roster", "View assigned team members filtered by department with evaluation status badges."),
             ("Live Evaluation Form", "Grade 6 performance criteria (1.0 to 5.0) with real-time weighted score calculation."),
             ("Qualitative Feedback", "Provide granular feedback per criterion plus overall summary, strengths & improvements."),
             ("Draft & Final Modes", "Save partial evaluation drafts or permanently submit finalized appraisals."),
             ("Employee Growth Tracking", "Monitor historical review trajectories to support promotion and mentorship decisions.")
         ]),
        ("👤 Employee Module",
         CYAN_ACCENT,
         [
             ("Personal Self-Service", "View latest evaluation score, rating badge (e.g. Outstanding), and manager appraisal."),
             ("Competency Radar Chart", "Dynamic 6-axis Chart.js radar chart mapping individual strengths vs expectations."),
             ("Evaluation History", "Chronological archive of past performance appraisals across previous review cycles."),
             ("Printable Review Sheet", "Dedicated clean print-ready layout for personal records or offline HR discussions."),
             ("Feedback Review", "Actionable improvement areas and strengths highlighted directly by direct managers.")
         ])
    ]

    for i, (mtitle, mcolor, feats) in enumerate(modules):
        l = Inches(0.8 + i * 3.95)
        t = Inches(1.5)
        w = Inches(3.8)
        h = Inches(5.5)

        card = add_card(slide8, l, t, w, h)
        top_bar = slide8.shapes.add_shape(MSO_SHAPE.RECTANGLE, l, t, w, Inches(0.08))
        top_bar.fill.solid()
        top_bar.fill.fore_color.rgb = mcolor
        top_bar.line.fill.background()

        tb = slide8.shapes.add_textbox(l + Inches(0.18), t + Inches(0.18), w - Inches(0.36), h - Inches(0.36))
        tf = tb.text_frame
        tf.word_wrap = True

        p = tf.paragraphs[0]
        p.text = mtitle
        p.font.name = "Segoe UI"
        p.font.size = Pt(13)
        p.font.bold = True
        p.font.color.rgb = NAVY_DARK

        for j, (fname, fdesc) in enumerate(feats):
            fp1 = tf.add_paragraph()
            fp1.space_before = Pt(6)
            fp1.text = f"✔ {fname}"
            fp1.font.name = "Segoe UI"
            fp1.font.size = Pt(9.5)
            fp1.font.bold = True
            fp1.font.color.rgb = mcolor

            fp2 = tf.add_paragraph()
            fp2.text = fdesc
            fp2.font.name = "Segoe UI"
            fp2.font.size = Pt(8.5)
            fp2.font.color.rgb = TEXT_DARK

    # =========================================================================
    # SLIDE 9: User Interface Showcase (Actual Screenshots)
    # =========================================================================
    slide9 = prs.slides.add_slide(blank_layout)
    add_header(slide9, "Visual Interface", "User Interface: Authentic Application Screenshots",
               "Real screenshots captured from the live deployed Employee Performance Evaluation System", 9)

    shots = [
        ("screenshot_login.png", "1. Authentication Portal", "BCrypt secure login, role demo quick-fill buttons, password visibility toggle", Inches(0.8), Inches(1.5)),
        ("screenshot_admin.png", "2. Admin Analytics Dashboard", "Department score bar chart, rating breakdown doughnut chart, recent reviews", Inches(6.8), Inches(1.5)),
        ("screenshot_manager.png", "3. Manager Direct Reports", "Assigned team roster, evaluation cycle status flags, one-click review actions", Inches(0.8), Inches(4.4)),
        ("screenshot_employee.png", "4. Employee Radar Dashboard", "Live Chart.js competency radar chart, performance rating badge, manager notes", Inches(6.8), Inches(4.4))
    ]

    for fname, stitle, sdesc, l, t in shots:
        w = Inches(5.75)
        h = Inches(2.7)

        card = add_card(slide9, l, t, w, h)

        tb = slide9.shapes.add_textbox(l + Inches(0.12), t + Inches(0.06), w - Inches(0.24), Inches(0.4))
        tf = tb.text_frame
        tf.margin_top = Pt(0)
        tf.margin_bottom = Pt(0)
        p = tf.paragraphs[0]
        p.text = stitle
        p.font.name = "Segoe UI"
        p.font.size = Pt(10.5)
        p.font.bold = True
        p.font.color.rgb = BLUE_PRIMARY

        p2 = tf.add_paragraph()
        p2.text = sdesc
        p2.font.name = "Segoe UI"
        p2.font.size = Pt(8)
        p2.font.color.rgb = TEXT_MUTED

        img_path = os.path.join(screenshots_dir, fname)
        if os.path.exists(img_path):
            img_top = t + Inches(0.5)
            img_h = Inches(2.1)
            img_w = Inches(5.5)
            slide9.shapes.add_picture(img_path, l + Inches(0.12), img_top, img_w, img_h)

    # =========================================================================
    # SLIDE 10: Code Quality & Testing (Zero Marks Reference)
    # =========================================================================
    slide10 = prs.slides.add_slide(blank_layout)
    add_header(slide10, "Quality Assurance", "Code Quality & Rigorous Testing: Architecture & Verification",
               "Clean object-oriented principles, modular exception hierarchy, and 100% automated test verification", 10)

    # 3 Detailed Cards
    q1 = add_card(slide10, Inches(0.8), Inches(1.5), Inches(3.7), Inches(4.2), "Object-Oriented Design")
    qt1 = slide10.shapes.add_textbox(Inches(0.95), Inches(2.0), Inches(3.4), Inches(3.5))
    qf1 = qt1.text_frame
    qf1.word_wrap = True
    p = qf1.paragraphs[0]
    p.text = "• Abstract User Entity: Base abstract class User encapsulates identity; extended polymorphically by Admin, Manager, and Employee.\n\n• Dynamic Polymorphic Routing: Abstract methods getDashboardUrl() and getRoleDisplayName() eliminate procedural switch statements.\n\n• Generic DAO Abstraction: GenericDao<T, ID> defines standardized CRUD contracts, fully decoupling persistence from business logic.\n\n• Encapsulation of Domain Math: Evaluation entity encapsulates weighted calculations and boundary checks."
    p.font.name = "Segoe UI"
    p.font.size = Pt(9.5)
    p.font.color.rgb = TEXT_DARK

    q2 = add_card(slide10, Inches(4.8), Inches(1.5), Inches(3.7), Inches(4.2), "Security & Defensive Coding")
    qt2 = slide10.shapes.add_textbox(Inches(4.95), Inches(2.0), Inches(3.4), Inches(3.5))
    qf2 = qt2.text_frame
    qf2.word_wrap = True
    p = qf2.paragraphs[0]
    p.text = "• 100% Parameterized SQL: Every single query utilizes JDBC PreparedStatement; zero string concatenation, eliminating SQL injection.\n\n• Salted BCrypt Hashing: Passwords secured with jBCrypt ($2a$10$), preventing credential harvesting even in case of data breaches.\n\n• Session Fixation Protection: Existing HTTP session destroyed and regenerated upon login; 30-min timeout.\n\n• Custom Exception Hierarchy: Base AppException extended by ValidationException, AuthenticationException, ResourceNotFoundException, and DatabaseException."
    p.font.name = "Segoe UI"
    p.font.size = Pt(9.5)
    p.font.color.rgb = TEXT_DARK

    q3 = add_card(slide10, Inches(8.8), Inches(1.5), Inches(3.7), Inches(4.2), "Automated Testing Suite")
    qt3 = slide10.shapes.add_textbox(Inches(8.95), Inches(2.0), Inches(3.4), Inches(3.5))
    qf3 = qt3.text_frame
    qf3.word_wrap = True
    p = qf3.paragraphs[0]
    p.text = "• Unit Testing: EvaluationCalculationTest verifies mathematical weighting, rounding rules, and rating band thresholds.\n\n• Authentication & Security Testing: UserAuthenticationTest verifies password hashing, bad credentials, and session validations.\n\n• Integration & State Flow Testing: IntegrationFlowTest and EvaluationCycleServiceTest verify complete lifecycle state-machines.\n\n• Live HTTP Container Simulation: E2EHttpFlowTest verifies real Tomcat routing, session cookies, and 403 Forbidden role guards."
    p.font.name = "Segoe UI"
    p.font.size = Pt(9.5)
    p.font.color.rgb = TEXT_DARK

    # Bottom Verified Results Banner
    res_card = add_card(slide10, Inches(0.8), Inches(5.9), Inches(11.7), Inches(1.2), bg_color=RGBColor(236, 253, 245), border_color=GREEN_ACCENT)
    rtb = slide10.shapes.add_textbox(Inches(1.0), Inches(6.0), Inches(11.3), Inches(1.0))
    rtf = rtb.text_frame
    rtf.word_wrap = True
    rp = rtf.paragraphs[0]
    rp.text = "OFFICIAL MAVEN BUILD & SUREFIRE TEST RESULTS (VERIFIED 100% SUCCESS)"
    rp.font.name = "Segoe UI"
    rp.font.size = Pt(10)
    rp.font.bold = True
    rp.font.color.rgb = GREEN_ACCENT

    rp2 = rtf.add_paragraph()
    rp2.space_before = Pt(3)
    rp2.text = "TOTAL TESTS RUN: 23   |   FAILURES: 0   |   ERRORS: 0   |   SKIPPED: 0   |   SUCCESS RATE: 100.0%   |   BUILD: SUCCESS"
    rp2.font.name = "Segoe UI"
    rp2.font.size = Pt(12)
    rp2.font.bold = True
    rp2.font.color.rgb = NAVY_DARK

    rp3 = rtf.add_paragraph()
    rp3.text = "Verified Suites: Unit Testing (EvaluationTest, PasswordUtilTest) • Security Testing (UserAuthenticationTest) • Integration Testing (IntegrationFlowTest, H2FallbackTest, E2EHttpFlowTest)"
    rp3.font.name = "Segoe UI"
    rp3.font.size = Pt(9)
    rp3.font.color.rgb = TEXT_MUTED

    # =========================================================================
    # SLIDE 11: Teamwork & Innovation (Dedicated Team Table & Zero Marks)
    # =========================================================================
    slide11 = prs.slides.add_slide(blank_layout)
    add_header(slide11, "Team & Innovation", "Team Collaboration & System Innovation: Engineering Ownership & Breakthroughs",
               "Clear team responsibilities, collaborative Git practices, and standout technical capabilities", 11)

    # Left Column: Team Structure & Collaboration
    c_team = add_card(slide11, Inches(0.8), Inches(1.5), Inches(5.8), Inches(5.5), "Team Organization & Roles")
    
    # Team Members Table in Slide 11
    s11_team_data = [
        ["S.No.", "Team Member Name", "Roll Number", "Role"],
        ["1", "Himanshu Kumar Jaiswal", "25SCSE1180087", "Admin"],
        ["2", "Aryan Verma", "25SCSE1181197", "Member"],
        ["3", "Shivam Kumar", "25SCSE1181117", "Member"]
    ]

    s11_table_shape = slide11.shapes.add_table(4, 4, Inches(0.95), Inches(2.05), Inches(5.5), Inches(1.6))
    s11_table = s11_table_shape.table
    s11_table.columns[0].width = Inches(0.6)
    s11_table.columns[1].width = Inches(2.4)
    s11_table.columns[2].width = Inches(1.5)
    s11_table.columns[3].width = Inches(1.0)

    for row_idx, row in enumerate(s11_team_data):
        for col_idx, val in enumerate(row):
            cell = s11_table.cell(row_idx, col_idx)
            cell.text = ""
            cell.vertical_anchor = MSO_ANCHOR.MIDDLE
            cell.margin_left = Pt(6)
            cell.margin_right = Pt(6)
            cell.margin_top = Pt(3)
            cell.margin_bottom = Pt(3)

            if row_idx == 0:
                cell.fill.solid()
                cell.fill.fore_color.rgb = BLUE_PRIMARY
                p = cell.text_frame.paragraphs[0]
                p.text = val
                p.font.name = "Segoe UI"
                p.font.size = Pt(9.5)
                p.font.bold = True
                p.font.color.rgb = WHITE
                if col_idx in [0, 2, 3]:
                    p.alignment = PP_ALIGN.CENTER
                else:
                    p.alignment = PP_ALIGN.LEFT
            else:
                cell.fill.solid()
                cell.fill.fore_color.rgb = BLUE_LIGHT if row_idx % 2 == 1 else WHITE
                p = cell.text_frame.paragraphs[0]
                p.text = val
                p.font.name = "Segoe UI"
                p.font.size = Pt(9)
                p.font.bold = (col_idx == 1 or col_idx == 3)
                p.font.color.rgb = NAVY_DARK if col_idx != 3 else (BLUE_PRIMARY if val == "Admin" else TEXT_DARK)
                if col_idx in [0, 2, 3]:
                    p.alignment = PP_ALIGN.CENTER
                else:
                    p.alignment = PP_ALIGN.LEFT

    # Collaborative Engineering Practices below Table
    ctb = slide11.shapes.add_textbox(Inches(0.95), Inches(3.75), Inches(5.5), Inches(3.1))
    ctf = ctb.text_frame
    ctf.word_wrap = True

    t_points = [
        ("Modular Architecture Ownership",
         "Himanshu Kumar Jaiswal: Admin, Core MVC controllers, AuthenticationFilter & security.\nAryan Verma: Manager workflows, Service business logic, and validation layer.\nShivam Kumar: Employee dashboard, DAO persistence, and automated JUnit test suite."),
        ("Git Workflow & Version Control",
         "Collaborative branch management, structured commit messages, PR reviews, and automated Maven package builds ensuring zero code conflicts."),
        ("Peer Code Reviews & Standards",
         "Strict Google Java Style conventions, SonarLint static analysis, complete Javadoc documentation, and zero scriptlet rule inside JSP pages.")
    ]

    for j, (title, desc) in enumerate(t_points):
        p1 = ctf.paragraphs[0] if j == 0 else ctf.add_paragraph()
        if j > 0:
            p1.space_before = Pt(6)
        p1.text = f"✔ {title}"
        p1.font.name = "Segoe UI"
        p1.font.size = Pt(10)
        p1.font.bold = True
        p1.font.color.rgb = BLUE_PRIMARY

        p2 = ctf.add_paragraph()
        p2.text = desc
        p2.font.name = "Segoe UI"
        p2.font.size = Pt(8.5)
        p2.font.color.rgb = TEXT_DARK

    # Right Column: Technical Innovation
    c_innov = add_card(slide11, Inches(6.8), Inches(1.5), Inches(5.75), Inches(5.5), "Technical Innovation & Standout Features")
    itb = slide11.shapes.add_textbox(Inches(7.0), Inches(2.05), Inches(5.35), Inches(4.7))
    itf = itb.text_frame
    itf.word_wrap = True

    i_points = [
        ("Dual-Mode Connection Pool Resilience",
         "Engineered DBUtil to attempt MySQL 8 connection first; if offline or unauthenticated, it seamlessly boots an embedded H2 database with automatic schema/seed execution—guaranteeing 100% zero-crash uptime during evaluations!"),
        ("Interactive Chart.js Competency Radar",
         "Introduced multi-axis radar chart mapping individual strengths vs baseline expectations, transforming dry tables into actionable growth visualizations."),
        ("Live Client-Side Weighted Score Preview",
         "Custom JavaScript calculation engine in evaluate-form.jsp computes real-time score and rating badge as managers adjust inputs before submission."),
        ("RFC-4180 Compliant CSV Streaming",
         "Custom CSV streaming export service handling proper field quoting, multi-parameter filtering, and direct HTTP file attachment delivery.")
    ]

    for j, (title, desc) in enumerate(i_points):
        p1 = itf.paragraphs[0] if j == 0 else itf.add_paragraph()
        if j > 0:
            p1.space_before = Pt(8)
        p1.text = f"★ {title}"
        p1.font.name = "Segoe UI"
        p1.font.size = Pt(10.5)
        p1.font.bold = True
        p1.font.color.rgb = GOLD_ACCENT

        p2 = itf.add_paragraph()
        p2.text = desc
        p2.font.name = "Segoe UI"
        p2.font.size = Pt(9)
        p2.font.color.rgb = TEXT_DARK

    # =========================================================================
    # SLIDE 12: Conclusion & Future Scope (Executive Dark, Zero Marks)
    # =========================================================================
    slide12 = prs.slides.add_slide(blank_layout)
    bg12 = slide12.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0), Inches(0), Inches(13.333), Inches(7.5))
    bg12.fill.solid()
    bg12.fill.fore_color.rgb = NAVY_DARK
    bg12.line.fill.background()

    # Header Box
    h_tb = slide12.shapes.add_textbox(Inches(0.8), Inches(0.6), Inches(11.7), Inches(0.9))
    htf = h_tb.text_frame
    hp = htf.paragraphs[0]
    hp.text = "CONCLUSION & FUTURE ROADMAP"
    hp.font.name = "Segoe UI"
    hp.font.size = Pt(11)
    hp.font.bold = True
    hp.font.color.rgb = CYAN_ACCENT

    hp2 = htf.add_paragraph()
    hp2.text = "Delivering Sustainable Value & Engineering Excellence"
    hp2.font.name = "Segoe UI"
    hp2.font.size = Pt(22)
    hp2.font.bold = True
    hp2.font.color.rgb = WHITE

    # Left Box: Key Achievements
    card_ach = slide12.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(0.8), Inches(1.7), Inches(5.6), Inches(3.9))
    card_ach.fill.solid()
    card_ach.fill.fore_color.rgb = NAVY_MID
    card_ach.line.color.rgb = RGBColor(51, 65, 85)
    card_ach.line.width = Pt(1)

    atb = slide12.shapes.add_textbox(Inches(1.0), Inches(1.85), Inches(5.2), Inches(3.5))
    atf = atb.text_frame
    atf.word_wrap = True
    ap = atf.paragraphs[0]
    ap.text = "PROJECT ACHIEVEMENTS"
    ap.font.name = "Segoe UI"
    ap.font.size = Pt(12)
    ap.font.bold = True
    ap.font.color.rgb = GREEN_ACCENT

    achievements = [
        "100% Completed System: Implemented all role requirements across Admin, Manager, and Employee portals.",
        "Classical MVC Integrity: Zero Spring Boot reliance, showcasing foundational Java Servlet 6.0 and JSP excellence.",
        "Verified Test Coverage: 23 automated unit, integration, and E2E HTTP container tests passing with 0 failures.",
        "Production-Grade Security: BCrypt hashing, session fixation mitigation, and RBAC AuthFilter security.",
        "Data Transparency: Automated weighted rating engine eliminating human bias and manual spreadsheet errors."
    ]

    for item in achievements:
        p = atf.add_paragraph()
        p.space_before = Pt(6)
        p.text = f"✔ {item}"
        p.font.name = "Segoe UI"
        p.font.size = Pt(9.5)
        p.font.color.rgb = RGBColor(226, 232, 240)

    # Right Box: Future Scope
    card_fut = slide12.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(6.8), Inches(1.7), Inches(5.7), Inches(3.9))
    card_fut.fill.solid()
    card_fut.fill.fore_color.rgb = NAVY_MID
    card_fut.line.color.rgb = RGBColor(51, 65, 85)
    card_fut.line.width = Pt(1)

    ftb = slide12.shapes.add_textbox(Inches(7.0), Inches(1.85), Inches(5.3), Inches(3.5))
    ftf = ftb.text_frame
    ftf.word_wrap = True
    fp = ftf.paragraphs[0]
    fp.text = "FUTURE ENHANCEMENTS ROADMAP"
    fp.font.name = "Segoe UI"
    fp.font.size = Pt(12)
    fp.font.bold = True
    fp.font.color.rgb = CYAN_ACCENT

    roadmap = [
        "360-Degree Peer Feedback: Incorporate anonymous peer reviews, upward subordinate ratings, and self-appraisals.",
        "AI Sentiment & Summary Insights: Integrate LLM APIs for automated qualitative feedback summarization.",
        "Automated SMTP Notifications: Email alerts for cycle kick-off, pending reviews, and appraisal approvals.",
        "Mobile PWA Companion: Progressive Web App enabling offline review drafting and real-time push alerts.",
        "Single Sign-On (SSO): OAuth2 / SAML integration with corporate Active Directory / Google Workspace."
    ]

    for item in roadmap:
        p = ftf.add_paragraph()
        p.space_before = Pt(6)
        p.text = f"★ {item}"
        p.font.name = "Segoe UI"
        p.font.size = Pt(9.5)
        p.font.color.rgb = RGBColor(226, 232, 240)

    # Bottom Thank You & Assessment Banner (No Marks)
    ty_box = slide12.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(0.8), Inches(5.8), Inches(11.7), Inches(1.2))
    ty_box.fill.solid()
    ty_box.fill.fore_color.rgb = RGBColor(24, 34, 53)
    ty_box.line.color.rgb = BLUE_PRIMARY
    ty_box.line.width = Pt(1.5)

    ty_tb = slide12.shapes.add_textbox(Inches(1.0), Inches(5.9), Inches(11.3), Inches(1.0))
    tyf = ty_tb.text_frame
    tyf.word_wrap = True
    typ = tyf.paragraphs[0]
    typ.alignment = PP_ALIGN.CENTER
    typ.text = "THANK YOU!  •  QUESTIONS & TECHNICAL DEMO"
    typ.font.name = "Segoe UI"
    typ.font.size = Pt(15)
    typ.font.bold = True
    typ.font.color.rgb = WHITE

    typ2 = tyf.add_paragraph()
    typ2.alignment = PP_ALIGN.CENTER
    typ2.space_before = Pt(4)
    typ2.text = "Team: Himanshu Kumar Jaiswal (25SCSE1180087)  •  Aryan Verma (25SCSE1181197)  •  Shivam Kumar (25SCSE1181117)"
    typ2.font.name = "Segoe UI"
    typ2.font.size = Pt(11)
    typ2.font.bold = True
    typ2.font.color.rgb = CYAN_ACCENT

    typ3 = tyf.add_paragraph()
    typ3.alignment = PP_ALIGN.CENTER
    typ3.space_before = Pt(2)
    typ3.text = "Galgotias University (SCSE)  |  HCLTech • GUVI Campus Collaboration"
    typ3.font.name = "Segoe UI"
    typ3.font.size = Pt(9.5)
    typ3.font.color.rgb = RGBColor(148, 163, 184)

    # Save Presentation
    output_pptx = r"c:\Users\hp\Downloads\EPS\Employee_Performance_Evaluation_System_Review2.pptx"
    prs.save(output_pptx)
    print(f"Presentation saved successfully to: {output_pptx}")

if __name__ == "__main__":
    create_deck()
