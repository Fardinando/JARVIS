# JARVIS v4

Assistente pessoal autônomo para Android — Kotlin nativo.

**Arquitetura:** MVVM + Clean Architecture  
**UI:** Jetpack Compose  
**Wake word:** Porcupine (Picovoice)  
**IA:** OpenRouter (fallchain: Gemma 4 → Nemotron → Llama)  
**Voz:** Android TTS neural + Google STT  
**DB:** Room (SQLite)  
**CI:** GitHub Actions  

## Build

```bash
./gradlew assembleDebug
```

## Estrutura

```
app/src/main/java/com/fernando/jarvis/
├── ai/          # OpenRouter + fallback + comandos locais
├── voice/       # Wake word, STT, TTS
├── engine/      # Intent, Action, Permission
├── system/      # Foreground service, Accessibility, Notificações
├── memory/      # Room DB (contatos, histórico)
├── telegram/    # Bot de logs
└── ui/          # Compose screens + components
```
