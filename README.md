# Countula

Countula ist eine vollständige Android-App in **Kotlin + Jetpack Compose (Material 3)**, mit der du beliebig viele farbige Counter-Kacheln verwalten kannst.

## Features

- Kacheln mit Titel, Preis (Euro), Farbe und Counter
- Tap auf Kachel erhöht Counter um 1
- Kachel erstellen, bearbeiten, löschen
- Kacheln nach oben/unten verschieben (Neuordnung)
- Gesamtsumme: `Summe(counter * preis)`
- Alle Counter auf 0 zurücksetzen
- Bestätigungsdialog vor Löschen und vor globalem Reset
- Lokale Persistenz mit Room-Datenbank
- MVVM-Architektur

## Projektstruktur

- `app/src/main/java/com/example/countula/data` – Datenmodell, DAO, Repository, Room DB
- `app/src/main/java/com/example/countula/ui` – Screen, ViewModel, Formatter
- `app/src/main/java/com/example/countula/ui/components` – wiederverwendbare Compose-Komponenten
- `app/src/main/java/com/example/countula/ui/theme` – Material 3 Theme

## Startanleitung


> Hinweis: Dieses Repository enthält **keine Binärdateien**. Falls `gradle/wrapper/gradle-wrapper.jar` fehlt, einmalig im Projektordner ausführen:
>
> ```bash
> gradle wrapper --gradle-version 8.7
> ```

1. Projekt in Android Studio (Ladybug oder neuer) öffnen.
2. Gradle-Sync ausführen.
3. Emulator oder Gerät (API 26+) auswählen.
4. App starten.

## Validierung

- Name darf nicht leer sein.
- Preis muss numerisch und > 0 sein.
- Euro-Werte werden sauber über `NumberFormat` formatiert.
