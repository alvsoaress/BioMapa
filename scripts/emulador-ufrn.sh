#!/usr/bin/env bash
# Coloca o GPS do emulador Android no campus central da UFRN.
# Uso: scripts/emulador-ufrn.sh  (com o emulador já iniciado ou iniciando)
set -euo pipefail

ADB="${ANDROID_HOME:-$HOME/Android/Sdk}/platform-tools/adb"

"$ADB" wait-for-device
until [ "$("$ADB" shell getprop sys.boot_completed | tr -d '\r')" = "1" ]; do
  sleep 2
done

# geo fix recebe longitude antes da latitude.
"$ADB" emu geo fix -35.2000 -5.8400 >/dev/null
echo "Emulador posicionado na UFRN (-5.8400, -35.2000)."
