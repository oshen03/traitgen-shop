import os
import requests
from pathlib import Path

# Configuration
GITHUB_USERNAME = "oshen03"
TOKEN = os.getenv("GH_TOKEN")
OUTPUT_DIR = "generated_stats"
OUTPUT_FILE = "langs.svg"

# Fetch language stats from GitHub API
url = f"https://api.github.com/users/{GITHUB_USERNAME}/repos?per_page=100"
headers = {"Authorization": f"Bearer {TOKEN}"}
response = requests.get(url, headers=headers)
repos = response.json()

# Calculate language usage
language_stats = {}
for repo in repos:
    if repo["private"] and repo["name"] == "traitgen-shop":
        continue  # Skip private repos you want to exclude
    lang = repo.get("language", "Other")
    language_stats[lang] = language_stats.get(lang, 0) + 1

# Generate SVG content 
svg_content = f"""
<svg viewBox="0 0 200 100">
  <text x="20" y="20">Language Stats</text>
  <text x="20" y="40">{language_stats}</text>
</svg>
"""

# Save to file
Path(OUTPUT_DIR).mkdir(exist_ok=True)
with open(f"{OUTPUT_DIR}/{OUTPUT_FILE}", "w") as f:
    f.write(svg_content)
