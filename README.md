# Svoj@SKB

Xposed modul, ki pomiri mobilno aplikacijo Moj@SKB, da naprava v resnici ni rootana.

## Kako in kaj

Aplikacija Moj@SKB ne deluje, če zazna, da teče na rootani napravi.
Ta modul se zgolj zlaže bančni aplikaciji da v resnici naprava ni rootana.

## Pozor

Gre za bančno aplikacijo. Boš res zaupal neznancu, da ta aplikacija zgolj
skriva root bančni aplikaciji in v ozadju ne prenaša sredstev na avtorjev
bančni račun :P ? Preden nameščaš take stvari, **PREVERI** izvorno kodo.
Najdeš jo v mapi [`app/src/main`](app/src/main/java/).
