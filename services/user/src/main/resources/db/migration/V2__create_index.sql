-- Create index on user_id and alert_category for fast routing lookups
CREATE INDEX idx_user_prefs_user_alert ON user_preferences(user_id, alert_category);
