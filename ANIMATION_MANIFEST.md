# Animation Asset Manifest

| Asset Name | Resource ID | Format | Licence | Purpose / Screen Location |
| :--- | :--- | :--- | :--- | :--- |
| `animation_empty_activity.json` | `R.raw.animation_empty_activity` | Bodymovin Lottie JSON | MIT / CC-BY (Permissive) | Expressive floating coin & glass wallet illustration on empty activity lists (Home & Activity screens) |
| `animation_empty_insights.json` | `R.raw.animation_empty_insights` | Bodymovin Lottie JSON | MIT / CC-BY (Permissive) | Subtle pulsing geometric analytics bars on empty insights & trends state |
| `animation_transaction_success.json` | `R.raw.animation_transaction_success` | Bodymovin Lottie JSON | MIT / CC-BY (Permissive) | Celebratory green checkmark burst when a transaction or budget is saved |

### Technical Specifications:
* **Engine**: `com.airbnb.android:lottie-compose` 6.6.2
* **Storage**: Packaged locally in `app/src/main/res/raw/` (offline-first, 0 network dependency)
* **Optimization**: Vector shapes only, no raster bitmaps embedded, average size < 3 KB per asset.

