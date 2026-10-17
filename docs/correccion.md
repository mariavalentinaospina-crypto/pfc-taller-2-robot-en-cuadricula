# Informe de corrección: un robot en la cuadrícula

Fundamentos de Programación Funcional y Concurrente, Taller 2.

## 1. Método

El tipo `Orden` está definido recursivamente por cuatro alternativas:

- **Casos base:** `Avanzar(n)` y `Girar(s)`.
- **Casos recursivos:** `Repetir(k, o')` y `Secuencia(List(o_1, ..., o_m))`.

Para demostrar que una función $P_f$ cumple su especificación $f$ sobre todas las órdenes se usa inducción estructural: se prueba la propiedad en los dos casos base y, en los dos casos recursivos, se supone la propiedad (hipótesis de inducción, HI) para las órdenes contenidas.

Notación: $[\![ o ]\!](e)$ es el estado que la especificación del enunciado asigna a ejecutar $o$ desde $e$, y `ejecutar(e, o)` es el programa. El estado es $e = (x, y, d)$.

## 2. `girar`

**Especificación.** $\text{girar}(d, \text{Derecha})$ es la dirección siguiente en el orden $\text{Norte} \to \text{Este} \to \text{Sur} \to \text{Oeste} \to \text{Norte}$, y $\text{girar}(d, \text{Izquierda})$ la anterior.

**Argumento.** El dominio $\text{Direccion} \times \text{Sentido}$ tiene $4 \times 2 = 8$ elementos y el `match` tiene un caso por cada uno. Como `Direccion` y `Sentido` son `sealed`, el compilador comprueba que no falta ninguno. Cada caso se verifica contra la especificación:

| Pareja | Programa | Especificación |
|---|---|---|
| (Norte, Derecha) | Este | siguiente de Norte |
| (Este, Derecha) | Sur | siguiente de Este |
| (Sur, Derecha) | Oeste | siguiente de Sur |
| (Oeste, Derecha) | Norte | siguiente de Oeste |
| (Norte, Izquierda) | Oeste | anterior de Norte |
| (Oeste, Izquierda) | Sur | anterior de Oeste |
| (Sur, Izquierda) | Este | anterior de Sur |
| (Este, Izquierda) | Norte | anterior de Este |

**Propiedad útil.** Para toda dirección $d$, $\text{girar}(\text{girar}(d, \text{Derecha}), \text{Izquierda}) = d$, porque cada fila de Izquierda es la inversa de una fila de Derecha. Se usa en la sección 5.

## 3. `ejecutar`

**Especificación.**

- $[\![ \text{Avanzar}(n) ]\!](x, y, d)$ suma $n$ a $y$ si $d = \text{Norte}$, a $x$ si $d = \text{Este}$, y resta $n$ si $d = \text{Sur}$ o $d = \text{Oeste}$ en la coordenada correspondiente.
- $[\![ \text{Girar}(s) ]\!](x, y, d) = (x, y, \text{girar}(d, s))$.
- $[\![ \text{Repetir}(k, o) ]\!](e)$ es aplicar $[\![ o ]\!]$ exactamente $k$ veces, y es $e$ si $k \leq 0$.
- $[\![ \text{Secuencia}(o_1, \ldots, o_m) ]\!](e) = [\![ o_m ]\!](\cdots [\![ o_1 ]\!](e))$, y es $e$ si $m = 0$.

**Teorema.** $\forall o \in \text{Orden},\ \forall e \in \text{Estado} : \text{ejecutar}(e, o) = [\![ o ]\!](e)$.

Se prueba por inducción estructural sobre $o$.

**Caso base `Avanzar(n)`.** El programa hace `match` sobre `e.mira` y en cada dirección modifica la coordenada que dice la especificación y deja las demás intactas. Coincide con $[\![ \text{Avanzar}(n) ]\!](e)$, también para $n < 0$.

**Caso base `Girar(s)`.** El programa devuelve $(x, y, \text{girar}(d, s))$, igual a la especificación, usando la corrección de `girar` de la sección 1.

**Caso `Repetir(k, o')`.** HI: $\text{ejecutar}(e', o') = [\![ o' ]\!](e')$ para todo $e'$. Se hace una segunda inducción sobre $k$.

- Si $k \leq 0$, el programa devuelve $e$, que es lo que dice la especificación.
- Si $k \geq 1$: el programa calcula $\text{ejecutar}(\text{ejecutar}(e, o'), \text{Repetir}(k-1, o'))$. Por HI, $\text{ejecutar}(e, o') = [\![ o' ]\!](e) =: e_1$. Por la hipótesis de la inducción sobre $k$, $\text{ejecutar}(e_1, \text{Repetir}(k-1, o')) = [\![ o' ]\!]^{k-1}(e_1)$. Entonces el resultado es $[\![ o' ]\!]^{k-1}([\![ o' ]\!](e)) = [\![ o' ]\!]^{k}(e)$.

**Caso `Secuencia(o_1, ..., o_m)`.** HI: la propiedad vale para cada $o_i$. Se prueba por inducción sobre $m$ que $\text{foldLeft}$ calcula $[\![ o_m ]\!](\cdots [\![ o_1 ]\!](e))$:

- $m = 0$: `foldLeft` sobre la lista vacía devuelve $e$.
- $m = j + 1$: `foldLeft` sobre $(o_1, \ldots, o_{j+1})$ es `ejecutar` de $o_{j+1}$ sobre el resultado de `foldLeft` de los primeros $j$. Por la inducción sobre $m$ ese resultado es $e_j = [\![ o_j ]\!](\cdots [\![ o_1 ]\!](e))$, y por HI sobre $o_{j+1}$ el resultado final es $[\![ o_{j+1} ]\!](e_j)$.

Por lo tanto $\text{ejecutar}(e, o) = [\![ o ]\!](e)$ para toda orden. $\blacksquare$

**Ejemplo de comprobación.** Con $o = \text{Repetir}(4, S)$ y $S = (\text{Avanzar}(1), \text{Girar}(\text{Derecha}))$, una vuelta de $S$ desde $(x, y, d)$ lleva a $(x', y', \text{girar}(d, \text{Derecha}))$. Cuatro vueltas desde el norte suman los cuatro desplazamientos $(0, 1), (1, 0), (0, -1), (-1, 0)$, que dan $(0, 0)$, y cuatro giros a la derecha devuelven la dirección a Norte.

## 4. `cuantasBasicas` y `profundidad`

**Especificación.**

$$
\text{cb}(o) = \begin{cases}
1 & o = \text{Avanzar}(n) \text{ o } o = \text{Girar}(s) \\
\max(k, 0) \cdot \text{cb}(o') & o = \text{Repetir}(k, o') \\
\sum_{i=1}^{m} \text{cb}(o_i) & o = \text{Secuencia}(o_1, \ldots, o_m)
\end{cases}
$$

$$
\text{prof}(o) = \begin{cases}
1 & o \text{ es básica} \\
1 + \text{prof}(o') & o = \text{Repetir}(k, o') \\
1 + \max(\{0\} \cup \{\text{prof}(o_i)\}) & o = \text{Secuencia}(o_1, \ldots, o_m)
\end{cases}
$$

**Teorema.** `cuantasBasicas` y `profundidad` calculan $\text{cb}$ y $\text{prof}$ para toda orden.

**Casos base.** `Avanzar(_)` y `Girar(_)` devuelven $1$ en ambas funciones, igual que la especificación.

**Caso `Repetir(k, o')`.** Por HI, `cuantasBasicas(o')` $= \text{cb}(o')$ y `profundidad(o')` $= \text{prof}(o')$. El programa devuelve $\max(k, 0) \cdot \text{cb}(o')$ y $1 + \text{prof}(o')$, que son las especificaciones.

**Caso `Secuencia(o_1, ..., o_m)`.** Por HI cada llamada sobre $o_i$ devuelve el valor de la especificación. `map` las aplica a toda la lista, `sum` suma (da $0$ si $m = 0$) y `foldLeft(0)` con `math.max` calcula el máximo con valor inicial $0$. El resultado coincide con $\sum \text{cb}(o_i)$ y con $1 + \max(\{0\} \cup \{\text{prof}(o_i)\})$. $\blacksquare$

**Consistencia con `ejecutar`.** Para $o = \text{Repetir}(4, S)$ el programa da $\text{cb} = 4 \cdot 2 = 8$ y $\text{prof} = 1 + 2 = 3$, como pide el enunciado.

## 5. `componer`, `rutina`, `aplicarA`, `transformar`

### 5.1 `componer`: la rutina compuesta equivale a ejecutar sus partes

**Teorema.** Para todo estado $e$ y órdenes $o_1, o_2$:
$$\text{ejecutar}(e, \text{componer}(o_1, o_2)) = \text{ejecutar}(\text{ejecutar}(e, o_1), o_2)$$

**Demostración.** Por definición, $\text{componer}(o_1, o_2) = \text{Secuencia}(\text{List}(o_1, o_2))$. Por el caso `Secuencia` de la sección 2, `ejecutar` sobre esa secuencia es `foldLeft` de la lista de dos órdenes empezando en $e$:

$$\text{foldLeft}(e)(o_1, o_2) = \text{ejecutar}(\text{ejecutar}(e, o_1), o_2)$$

que es exactamente el lado derecho. $\blacksquare$

**Generalización.** Por inducción sobre el número de composiciones, componer tres órdenes de cualquiera de las dos formas, $\text{componer}(\text{componer}(o_1, o_2), o_3)$ o $\text{componer}(o_1, \text{componer}(o_2, o_3))$, ejecuta $o_1$, luego $o_2$ y luego $o_3$. Es decir, la composición es asociativa en su efecto aunque los términos `Orden` sean distintos.

### 5.2 `rutina`

**Teorema.** $\text{rutina}(p)(g)(v) = \text{Repetir}(v, \text{Secuencia}(\text{List}(\text{Avanzar}(p), \text{Girar}(g))))$.

Es verdadero por la definición del programa: el currificado solo fija $p$ y $g$ y deja $v$ para después. Aplicar `rutina(p)(g)` a $v$ equivale a aplicar `rutina(p)(g)(v)`, porque Scala define `def f(a)(b)(c)` como una función que devuelve funciones de una variable. Con $p = 1$, $g = \text{Derecha}$ y $v = 4$ se obtiene el cuadrado del enunciado, y por la sección 2 deja al robot en el estado de partida.

### 5.3 `aplicarA`

**Teorema.** Para toda lista de estados $L = (e_1, \ldots, e_m)$ y toda orden $o$:
$\text{aplicarA}(L, o) = (\text{ejecutar}(e_1, o), \ldots, \text{ejecutar}(e_m, o))$.

Se prueba por inducción sobre la lista. Para $L = \text{Nil}$, `map` devuelve `Nil`. Para $L = e_1 :: L'$, `map` produce $\text{ejecutar}(e_1, o) :: \text{map}(L')$, y por HI el resto cumple la propiedad. $\blacksquare$

### 5.4 `transformar`

**Especificación.** $\text{transformar}(o, f)$ reemplaza cada orden básica $b$ de $o$ por $f(b)$ y reconstruye las compuestas con el mismo número de veces en `Repetir` y el mismo orden en `Secuencia`.

**Demostración por inducción estructural.**

- Casos base: para `Avanzar` y `Girar` el programa devuelve $f(o)$, que es el reemplazo de la especificación.
- `Repetir(k, o')`: por HI, `transformar(o', f)` hace el reemplazo en $o'$. El programa devuelve `Repetir(k, transformar(o', f))`, que conserva $k$.
- `Secuencia(o_1, ..., o_m)`: por HI se cumple en cada $o_i$, y `map` conserva el orden y la longitud de la lista. $\blacksquare$

**Caso particular.** Con $f = \text{identidad}$, $\text{transformar}(o, f) = o$, que también se demuestra por inducción sustituyendo $f(b) = b$ en cada caso.

## 6. `simplificar` no cambia el resultado

**Teorema.** Para toda orden $o$ y todo estado $e$:
$$\text{ejecutar}(e, o) = \text{ejecutar}(e, \text{simplificar}(o))$$

Como `simplificar` es una repetición de `pasada`, basta probar que una `pasada` conserva el resultado, y después concluir por inducción sobre el número de pasadas.

### 6.1 Equivalencia regla por regla

Se escribe $o \equiv o'$ cuando $\forall e : \text{ejecutar}(e, o) = \text{ejecutar}(e, o')$. Se usa que $\equiv$ es una congruencia respecto a `Repetir` y `Secuencia`: si se cambian las órdenes contenidas por otras $\equiv$, la orden que las contiene sigue siendo $\equiv$. Esto se sigue directamente del teorema de la sección 2, porque `ejecutar` de una compuesta solo depende del efecto de sus partes.

**Regla 1a: $\text{Avanzar}(0) \equiv \text{Secuencia}(\text{Nil})$.** Avanzar $0$ casillas suma $0$ a la coordenada correspondiente en cualquier dirección y deja la dirección igual: $\text{ejecutar}(e, \text{Avanzar}(0)) = e$. La secuencia vacía devuelve $e$.

**Regla 1b: $\text{Repetir}(k, o') \equiv \text{Secuencia}(\text{Nil})$ si $k \leq 0$.** Por la sección 2, `ejecutar` de un `Repetir` con $k \leq 0$ devuelve $e$ sin importar $o'$.

**Regla 2: $\text{Secuencia}(\ldots, \text{Avanzar}(a), \text{Avanzar}(b), \ldots) \equiv \text{Secuencia}(\ldots, \text{Avanzar}(a + b), \ldots)$.** Los dos avances consecutivos se ejecutan con la misma dirección, porque `Avanzar` no la cambia. Sea $d$ esa dirección. En la coordenada que corresponde a $d$, el efecto de los dos avances es sumar $a$ y luego $b$ (o restar, según la dirección), que es sumar $a + b$ (o restar $a + b$). La otra coordenada y la dirección no cambian. Por lo tanto, $\text{ejecutar}(\text{ejecutar}(e, \text{Avanzar}(a)), \text{Avanzar}(b)) = \text{ejecutar}(e, \text{Avanzar}(a + b))$. Como esto es un cambio de dos elementos contiguos de la lista, el `foldLeft` da el mismo estado antes y después del par, y el resto de la secuencia recibe el mismo estado. Además, `juntar` recorre la lista de derecha a izquierda, por lo que una racha de avances $a_1, \ldots, a_j$ se vuelve $\text{Avanzar}(a_1 + \cdots + a_j)$ aplicando esta equivalencia $j - 1$ veces.

**Regla 3: $\text{Repetir}(1, o') \equiv o'$.** Por la sección 2, `ejecutar(e, Repetir(1, o'))` es `ejecutar(ejecutar(e, o'), Repetir(0, o'))`, y como $0 \leq 0$ el segundo paso devuelve su argumento. El resultado es `ejecutar(e, o')`.

**Regla 4: $\text{Secuencia}(\text{List}(o')) \equiv o'$.** `foldLeft` con un solo elemento es `ejecutar(e, o')`.

**Regla 5: quitar de una secuencia una orden $z \equiv \text{Secuencia}(\text{Nil})$.** Si $z$ no hace nada, entonces $\text{ejecutar}(e', z) = e'$ para todo $e'$, y en el `foldLeft` el estado pasa por $z$ sin cambiar. Quitar $z$ de la lista no altera el estado que reciben las órdenes siguientes. En el programa, `nada(x)` solo reconoce `Secuencia(Nil)`, que cumple $z \equiv \text{Secuencia}(\text{Nil})$ por definición.

### 6.2 Una pasada conserva el resultado

**Lema.** Para toda orden $o$: $\text{pasada}(o) \equiv o$.

Por inducción estructural sobre $o$:

- `Avanzar(0)`: `pasada` devuelve `Secuencia(Nil)`; vale por la regla 1a.
- `Avanzar(n)` con $n \neq 0$ y `Girar(s)`: `pasada` devuelve la misma orden.
- `Repetir(k, o')`: por HI, $\text{pasada}(o') \equiv o'$. Hay tres posibilidades:
  - $k \leq 0$: se devuelve `Secuencia(Nil)`; vale por la regla 1b.
  - $k = 1$: se devuelve $\text{pasada}(o') \equiv o'$, y $\text{Repetir}(1, o') \equiv o'$ por la regla 3.
  - $k \geq 2$: se devuelve `Repetir(k, pasada(o'))`, que es $\equiv$ a `Repetir(k, o')` por congruencia.
- `Secuencia(o_1, ..., o_m)`: por HI, $\text{pasada}(o_i) \equiv o_i$ para cada $i$, así que `Secuencia(pasada(o_1), ..., pasada(o_m))` es $\equiv$ a la original. Luego se quitan las que valen `Secuencia(Nil)` (regla 5), se juntan los avances contiguos (regla 2) y, según quede la lista, se devuelve `Secuencia(Nil)` si es vacía (que es el efecto de la lista vacía), el único elemento si hay uno (regla 4) o la secuencia con varios. En cada paso se cambió por algo $\equiv$. $\blacksquare$

### 6.3 Conclusión

`simplificar(o)` es `o` o es `simplificar(pasada(o))`. Por inducción sobre el número de llamadas, $\text{simplificar}(o) \equiv \text{pasada}(o) \equiv o$ usando el lema y la transitividad de $\equiv$. Por lo tanto $\text{ejecutar}(e, o) = \text{ejecutar}(e, \text{simplificar}(o))$ para todo $e$. $\blacksquare$

### 6.4 Terminación

Se define la medida $m(o) = \text{nodos}(o) + \text{avanzares}(o)$, donde $\text{nodos}$ cuenta todos los constructores de la orden y $\text{avanzares}$ cuenta los `Avanzar`.

- Ninguna regla aumenta $m$.
- La regla 1a no cambia $\text{nodos}$ pero quita un `Avanzar`, así que $m$ baja en $1$.
- Las reglas 1b, 2, 3, 4 y 5 eliminan al menos un nodo, y $\text{avanzares}$ no sube.

Por lo tanto $m(\text{pasada}(o)) \leq m(o)$, con desigualdad estricta cuando $\text{pasada}(o) \neq o$. Como $m$ toma valores naturales, la cadena de pasadas no puede bajar para siempre y tiene que llegar a una orden que `pasada` ya no cambia, momento en el que `simplificar` se detiene.

### 6.5 Ejemplo

Para $o = \text{Secuencia}(\text{Avanzar}(1), \text{Repetir}(1, \text{Secuencia}(\text{Avanzar}(0), \text{Avanzar}(2))), \text{Avanzar}(-3))$ el informe de proceso muestra que `simplificar` llega a `Secuencia(Nil)`. Efectivamente, el robot avanza $1 + 2 - 3 = 0$ casillas en la dirección en que mira, que es el efecto de `Secuencia(Nil)`.