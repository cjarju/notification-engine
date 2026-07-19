import os
import re
from datetime import datetime, timezone

import requests
from tabulate import tabulate

OWNER, REPO = os.environ["GITHUB_REPOSITORY"].split("/")
TOKEN = os.environ["GITHUB_TOKEN"]

HEADERS = {
    "Accept": "application/vnd.github+json",
}

token = os.getenv("GITHUB_TOKEN")
if token:
    HEADERS["Authorization"] = f"Bearer {token}"

README_FILE = "README.md"


def github_get(url: str):
    """Execute a GitHub REST API request."""
    response = requests.get(url, headers=HEADERS)
    response.raise_for_status()
    return response.json()


def get_milestones():
    """Fetch all milestones ordered by milestone number."""
    milestones = github_get(
        f"https://api.github.com/repos/{OWNER}/{REPO}/milestones?state=all"
    )

    # Sort by milestone order
    milestones.sort(key=lambda m: m["number"])

    return milestones


def get_milestone_issues(milestone_number: int):
    """Fetch all issues for a milestone."""

    issues = github_get(
        f"https://api.github.com/repos/{OWNER}/{REPO}/issues"
        f"?milestone={milestone_number}&state=all&per_page=100"
    )

    # Ignore pull requests
    issues = [issue for issue in issues if "pull_request" not in issue]

    issues.sort(key=lambda i: i["number"])

    return issues


def calculate_progress(milestone):
    """Calculate milestone progress."""

    open_count = milestone["open_issues"]
    closed_count = milestone["closed_issues"]

    total = open_count + closed_count

    progress = 0 if total == 0 else round(closed_count / total * 100)

    if progress == 100:
        icon = "✅"
        status = "Complete"
    elif progress == 0:
        icon = "⏳"
        status = "Planned"
    else:
        icon = "🚧"
        status = "In Progress"

    return {
        "progress": progress,
        "icon": icon,
        "status": status,
        "open": open_count,
        "closed": closed_count,
    }


def build_roadmap_table(milestones):
    """Generate the roadmap summary table."""

    rows = []

    for milestone in milestones:

        stats = calculate_progress(milestone)

        rows.append([
            milestone["title"],
            f'{stats["icon"]} {stats["progress"]}%',
            stats["open"],
            stats["closed"],
            stats["status"],
        ])

    return tabulate(
        rows,
        headers=[
            "Milestone",
            "Progress",
            "Open",
            "Closed",
            "Status",
        ],
        tablefmt="github",
    )


def build_milestone_sections(milestones):
    """Generate detailed milestone sections."""

    sections = []

    for milestone in milestones:

        stats = calculate_progress(milestone)

        sections.append(
            f"### {stats['icon']} {milestone['title']}\n"
        )

        if milestone["description"]:
            sections.append(milestone["description"].strip())
            sections.append("")

        issues = get_milestone_issues(milestone["number"])

        for issue in issues:

            checkbox = "x" if issue["state"] == "closed" else " "

            sections.append(
                f"- [{checkbox}] {issue['title']}"
            )

        sections.append("")

    return "\n".join(sections)


def build_roadmap():
    """Build the complete roadmap markdown."""

    milestones = get_milestones()

    table = build_roadmap_table(milestones)

    sections = build_milestone_sections(milestones)

    timestamp = datetime.now(timezone.utc).strftime(
        "%Y-%m-%d %H:%M UTC"
    )

    return f"""_Last updated: {timestamp}_

{table}

---

{sections}
"""


def update_readme(roadmap):
    """Update README if roadmap changed."""

    with open(README_FILE, encoding="utf-8") as f:
        readme = f.read()

    pattern = (
        r"<!-- ROADMAP:START -->.*?<!-- ROADMAP:END -->"
    )

    current = re.search(
        pattern,
        readme,
        flags=re.DOTALL,
    )

    if current is None:
        raise RuntimeError(
            "Roadmap markers not found."
        )

    current_content = current.group(0)

    # Ignore timestamp when comparing
    normalized_current = re.sub(
        r"_Last updated: .*?_\n\n",
        "",
        current_content,
    )

    normalized_new = re.sub(
        r"_Last updated: .*?_\n\n",
        "",
        roadmap,
    )

    if normalized_new in normalized_current:
        print("Roadmap unchanged.")
        return

    replacement = (
        "<!-- ROADMAP:START -->\n\n"
        f"{roadmap}\n"
        "<!-- ROADMAP:END -->"
    )

    updated = re.sub(
        pattern,
        replacement,
        readme,
        flags=re.DOTALL,
    )

    with open(README_FILE, "w", encoding="utf-8") as f:
        f.write(updated)

    print("README updated.")


def main():
    roadmap = build_roadmap()
    update_readme(roadmap)


if __name__ == "__main__":
    main()
