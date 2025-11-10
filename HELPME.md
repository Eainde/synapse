import matplotlib.pyplot as plt
import matplotlib.patches as patches
import numpy as np

# Create figure and axes
fig, ax = plt.subplots(figsize=(10, 7))
ax.set_xlim(0, 100)
ax.set_ylim(0, 70)
ax.axis('off')

# Define styles
# 'boxstyle' can be 'round' which matches the image
common_bbox = dict(boxstyle='round,pad=0.7', ec='black', lw=1)

# --- Nodes ---

# Node 1: FF UI
box1_props = common_bbox.copy()
box1_props['fc'] = '#cce5ff' # Light Blue
ax.text(15, 50, "FF UI", ha='center', va='center', fontsize=12, bbox=box1_props, zorder=2)

# Node 2: TASK MANAGER
box2_props = common_bbox.copy()
box2_props['fc'] = '#ccffcc' # Light Green
tm_text = "TASK MANAGER\n\nIt will create outreach\ntasks for each PCU-ID"
node2 = ax.text(40, 50, tm_text, ha='center', va='center', fontsize=9, bbox=box2_props, multialignment='center', zorder=2)

# Node 3: FMR
box3_props = common_bbox.copy()
box3_props['fc'] = '#ffebcc' # Light Orange
fmr_text = "FMR\n\nNeeds are resolve\nPCU-IDS &\nfetch static data"
node3 = ax.text(70, 50, fmr_text, ha='center', va='center', fontsize=9, bbox=box3_props, multialignment='center', zorder=2)

# Node 4: DB (Cylinder)
x_db, y_db = 70, 20
db_width, db_height = 18, 10

# Draw the cylinder parts
body = patches.Rectangle((x_db - db_width / 2, y_db - db_height / 2), width=db_width, height=db_height, facecolor='#e5ccff', edgecolor='black', lw=1, zorder=1)
ax.add_patch(body)
bottom_ellipse = patches.Ellipse((x_db, y_db - db_height / 2), width=db_width, height=db_height/2.5, facecolor='#e5ccff', edgecolor='black', lw=1, zorder=0)
ax.add_patch(bottom_ellipse)
top = patches.Ellipse((x_db, y_db + db_height / 2), width=db_width, height=db_height/2.5, facecolor='#e5ccff', edgecolor='black', lw=1, zorder=2)
ax.add_patch(top)

db_text = "Saves the form\nin DB with\nForm/Section ID"
ax.text(x_db, y_db, db_text, ha='center', va='center', fontsize=8, multialignment='center', zorder=3)

# --- Arrows ---

# Arrow 1: UI -> TM
x_start_1 = 20 # Approximate right edge of UI box
x_end_1 = 33   # Approximate left edge of TM box
y_arrow_1 = 50
ax.annotate("", xy=(x_end_1, y_arrow_1), xytext=(x_start_1, y_arrow_1),
arrowprops=dict(arrowstyle='->', color='black', lw=2))
ax.text((x_start_1 + x_end_1) / 2, y_arrow_1 + 3, "PCU-IDS\nFOR OUTREACH",
ha='center', va='bottom', fontsize=9)

# Arrow 2: TM -> FMR
x_start_2 = 47 # Approximate right edge of TM box
x_end_2 = 61   # Approximate left edge of FMR box
y_arrow_2 = 50
ax.annotate("", xy=(x_end_2, y_arrow_2), xytext=(x_start_2, y_arrow_2),
arrowprops=dict(arrowstyle='->', color='black', lw=2))
ax.text((x_start_2 + x_end_2) / 2, y_arrow_2 + 2, "PCU-ID",
ha='center', va='bottom', fontsize=9)

# Arrow 3: FMR -> DB
x_arrow_3 = 70 # Aligned with FMR and DB center
y_start_3 = 43 # Approximate bottom of FMR box
y_end_3 = 26   # Approximate top of DB cylinder
ax.annotate("", xy=(x_arrow_3, y_end_3), xytext=(x_arrow_3, y_start_3),
arrowprops=dict(arrowstyle='->', color='black', lw=2))

# Save the figure
output_filename = "architecture_diagram_mpl.png"
plt.savefig(output_filename, bbox_inches='tight', dpi=150)
print(f"Diagram saved as {output_filename}")