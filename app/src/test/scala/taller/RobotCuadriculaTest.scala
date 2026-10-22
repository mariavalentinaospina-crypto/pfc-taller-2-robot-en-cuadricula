package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RobotCuadriculaTest extends AnyFunSuite {

  val r = new RobotCuadricula()
  import r._

  val cuadrado = Repetir(4, Secuencia(List(Avanzar(1), Girar(Derecha))))

  // Punto 1 -------------------------------------------------------------------

  test("girar: Norte a la derecha da Este") { assert(girar(Norte, Derecha) == Este) }
  test("girar: Norte a la izquierda da Oeste") { assert(girar(Norte, Izquierda) == Oeste) }
  test("girar: Oeste a la derecha da Norte") { assert(girar(Oeste, Derecha) == Norte) }
  test("girar: Sur a la izquierda da Este") { assert(girar(Sur, Izquierda) == Este) }

  test("girar: cuatro giros al mismo lado vuelven al principio") {
    val cuatro = List(Derecha, Derecha, Derecha, Derecha)
      .foldLeft(Norte: Direccion)((d, s) => girar(d, s))
    assert(cuatro == Norte)
  }

  test("girar: un giro y su contrario se cancelan") {
    assert(girar(girar(Este, Derecha), Izquierda) == Este)
  }
  // Punto 1 (propias) ---------------------------------------------------------

  test("propia girar: Este a la derecha da Sur") { assert(girar(Este, Derecha) == Sur) }
  test("propia girar: Sur a la derecha da Oeste") { assert(girar(Sur, Derecha) == Oeste) }
  test("propia girar: Este a la izquierda da Norte") { assert(girar(Este, Izquierda) == Norte) }
  test("propia girar: Oeste a la izquierda da Sur") { assert(girar(Oeste, Izquierda) == Sur) }

  test("propia girar: dos giros a la derecha y dos a la izquierda coinciden en toda dirección") {
    assert(todas.forall(d =>
      girar(girar(d, Derecha), Derecha) == girar(girar(d, Izquierda), Izquierda)))
  }

  test("propia girar: girar siempre cambia la dirección") {
    assert(todas.forall(d => girar(d, Derecha) != d && girar(d, Izquierda) != d))
  }

  // Punto 2 -------------------------------------------------------------------

  test("ejecutar: avanzar tres al norte") {
    assert(ejecutar(inicial, Avanzar(3)) == Estado(0, 3, Norte))
  }

  test("ejecutar: una secuencia de avance, giro y avance") {
    assert(ejecutar(inicial, Secuencia(List(Avanzar(2), Girar(Derecha), Avanzar(4)))) ==
      Estado(4, 2, Este))
  }

  test("ejecutar: el cuadrado vuelve al punto de partida") {
    assert(ejecutar(inicial, cuadrado) == Estado(0, 0, Norte))
  }

  test("ejecutar: avanzar con paso negativo retrocede") {
    assert(ejecutar(inicial, Avanzar(-2)) == Estado(0, -2, Norte))
  }

  test("ejecutar: repetir cero veces no hace nada") {
    assert(ejecutar(inicial, Repetir(0, Avanzar(5))) == inicial)
  }

  test("ejecutar: la secuencia vacía no hace nada") {
    assert(ejecutar(inicial, Secuencia(Nil)) == inicial)
  }

  test("ejecutar: avanzar al sur disminuye y") {
    assert(ejecutar(Estado(5, 5, Sur), Avanzar(2)) == Estado(5, 3, Sur))
  }

  test("ejecutar: avanzar al oeste disminuye x") {
    assert(ejecutar(Estado(5, 5, Oeste), Avanzar(2)) == Estado(3, 5, Oeste))
  }

    // Punto 2 (propias) ---------------------------------------------------------

  test("propia ejecutar: avanzar cuatro mirando al este desde (1, 1)") {
    assert(ejecutar(Estado(1, 1, Este), Avanzar(4)) == Estado(5, 1, Este))
  }

  test("propia ejecutar: girar a la izquierda no mueve al robot") {
    assert(ejecutar(inicial, Girar(Izquierda)) == Estado(0, 0, Oeste))
  }

  test("propia ejecutar: repetir tres veces un avance de dos") {
    assert(ejecutar(inicial, Repetir(3, Avanzar(2))) == Estado(0, 6, Norte))
  }

  test("propia ejecutar: repetir con veces negativas no hace nada") {
    assert(ejecutar(Estado(2, 3, Sur), Repetir(-2, Avanzar(1))) == Estado(2, 3, Sur))
  }

  test("propia ejecutar: avance, giro a la izquierda y avance") {
    assert(ejecutar(inicial, Secuencia(List(Avanzar(3), Girar(Izquierda), Avanzar(2)))) ==
      Estado(-2, 3, Oeste))
  }

  test("propia ejecutar: dos vueltas del cuadrado a medias terminan en (1, 1) mirando al sur") {
    assert(ejecutar(inicial, Repetir(2, Secuencia(List(Avanzar(1), Girar(Derecha))))) ==
      Estado(1, 1, Sur))
  }

  test("propia ejecutar: dos cuadrados seguidos vuelven al punto de partida") {
    assert(ejecutar(inicial, Repetir(2, cuadrado)) == inicial)
  }
  // Punto 3 -------------------------------------------------------------------

  test("cuantasBasicas: una orden básica cuenta uno") {
    assert(cuantasBasicas(Avanzar(3)) == 1)
  }

  test("cuantasBasicas: el cuadrado ejecuta ocho básicas") {
    assert(cuantasBasicas(cuadrado) == 8)
  }

  test("cuantasBasicas: repetir cero veces no ejecuta nada") {
    assert(cuantasBasicas(Repetir(0, cuadrado)) == 0)
  }

  test("cuantasBasicas: la secuencia vacía no ejecuta nada") {
    assert(cuantasBasicas(Secuencia(Nil)) == 0)
  }

  test("profundidad: una orden básica tiene profundidad 1") {
    assert(profundidad(Avanzar(3)) == 1)
  }

  test("profundidad: una secuencia de básicas tiene profundidad 2") {
    assert(profundidad(Secuencia(List(Avanzar(1), Girar(Derecha)))) == 2)
  }

  test("profundidad: el cuadrado tiene profundidad 3") {
    assert(profundidad(cuadrado) == 3)
  }

  test("profundidad: la secuencia vacía tiene profundidad 1") {
    assert(profundidad(Secuencia(Nil)) == 1)
  }

  test("profundidad: manda la rama más honda") {
    assert(profundidad(Secuencia(List(Avanzar(1), cuadrado))) == 4)
  }

  // Punto 3 (propias)-------------------------------------------------------------------

  test("propia cuantasBasicas: un giro cuenta uno"){
    assert(cuantasBasicas(Girar(Izquierda)) == 1)
  }

  test("propia cuantasBasicas: tres repeticiones de tres basicas"){
    assert(cuantasBasicas(Repetir(3,Secuencia(List(Avanzar(1),Avanzar(2),Girar(Derecha)))))==9)
  }

  test("propia cuantasBasicas: repetir dos veces el cuadrado ejecuta dieciséis"){
    assert(cuantasBasicas(Repetir(2, cuadrado)) == 16)
  }

  test("propia cuantasBasicas: una básica seguida del cuadrado suma nueve"){
    assert(cuantasBasicas(Secuencia(List(Avanzar(1), cuadrado))) == 9)
  }

  test("propia cuantasBasicas: repetir con veces negativas no ejecuta nada"){
    assert(cuantasBasicas(Repetir(-3, Avanzar(1))) == 0)
  }

  test("propia profundidad: un giro tiene profundidad 1"){
    assert(profundidad(Girar(Izquierda)) == 1)
  }

  test("propia profundidad: repetir una básica tiene profundidad 2"){
    assert(profundidad(Repetir(2, Avanzar(1))) == 2)
  }

  test("propia profundidad: repetir el cuadrado tiene profundidad 4"){
    assert(profundidad(Repetir(2, cuadrado)) == 4)
  }

  test("propia profundidad: secuencias anidadas vacías cuentan cada nivel"){
    assert(profundidad(Secuencia(List(Secuencia(List(Secuencia(Nil)))))) == 3)
  }

  test("propia profundidad: gana la rama más honda de la secuencia"){
    assert(profundidad(Secuencia(List(Avanzar(1), Secuencia(List(Girar(Derecha)))))) == 3)
  }




  // Punto 4 -------------------------------------------------------------------

  test("componer: deja las dos órdenes en secuencia") {
    assert(componer(Avanzar(1), Girar(Derecha)) ==
      Secuencia(List(Avanzar(1), Girar(Derecha))))
  }

  test("componer: ejecutar la compuesta es ejecutar una y después la otra") {
    assert(ejecutar(inicial, componer(Avanzar(2), Girar(Derecha))) ==
      ejecutar(ejecutar(inicial, Avanzar(2)), Girar(Derecha)))
  }

  test("rutina: currificada, el cuadrado de cuatro vuelve al origen") {
    val cuadradoDe = rutina(1)(Derecha) _
    assert(ejecutar(inicial, cuadradoDe(4)) == Estado(0, 0, Norte))
  }

  test("rutina: con los mismos argumentos construye el cuadrado del enunciado") {
    assert(rutina(1)(Derecha)(4) == cuadrado)
  }

  test("aplicarA: ejecuta la misma orden sobre varios estados") {
    assert(aplicarA(List(inicial, Estado(5, 5, Sur)), Avanzar(2)) ==
      List(Estado(0, 2, Norte), Estado(5, 3, Sur)))
  }

  test("aplicarA: sobre la lista vacía no hay nada que hacer") {
    assert(aplicarA(Nil, Avanzar(2)) == Nil)
  }

  test("transformar: duplicar los avances deja los giros como estaban") {
    val doble = transformar(cuadrado, {
      case Avanzar(n) => Avanzar(2 * n)
      case otra => otra
    })
    assert(doble == Repetir(4, Secuencia(List(Avanzar(2), Girar(Derecha)))))
  }

  test("transformar: con la identidad no cambia nada") {
    assert(transformar(cuadrado, (o: Orden) => o) == cuadrado)
  }

  // Punto 4 (propias) -------------------------------------------------------------------

  test("propia componer: la vacía con otra orden queda como secuencia de dos"){
    assert(componer(Secuencia(Nil), Avanzar(1)) == Secuencia(List(Secuencia(Nil), Avanzar(1))))
  }

  test("propia componer: componer dos veces equivale a ejecutar las tres partes"){
    val e = Estado(2, -1, Oeste)
    val compuesta = componer(componer(Avanzar(3), Girar(Izquierda)), Avanzar(2))
    assert(ejecutar(e, compuesta) ==
      ejecutar(ejecutar(ejecutar(e, Avanzar(3)), Girar(Izquierda)), Avanzar(2)))
  }

  test("propia rutina: dos pasos, izquierda, tres veces"){
    assert(rutina(2)(Izquierda)(3) == Repetir(3, Secuencia(List(Avanzar(2), Girar(Izquierda)))))
  }

  test("propia rutina: sin pasos solo gira, cinco giros dejan al robot mirando al este"){
    val sinPasos = rutina(0)(Derecha) _
    assert(ejecutar(inicial, sinPasos(5)) == Estado(0, 0, Este))
  }

  test("propia rutina: fijar pasos y giro permite reutilizar la función con distintas veces"){
    val cuadradoDe = rutina(2)(Izquierda) _
    assert(ejecutar(inicial, cuadradoDe(4)) == inicial)
    assert(ejecutar(inicial, cuadradoDe(1)) == Estado(0, 2, Oeste))
  }

  test("propia aplicarA: girar sobre varios estados"){
    assert(aplicarA(List(Estado(1, 1, Este), Estado(0, 0, Oeste)), Girar(Derecha)) ==
      List(Estado(1,1, Sur), Estado(0, 0, Norte)))
  }

  test("propia aplicarA: conserva el orden y el tamaño de la lista"){
    val estados = List(Estado(0, 0, Norte), Estado(1, 0, Norte), Estado(2, 0, Norte))
    assert(aplicarA(estados, Avanzar(1)) == List(Estado(0, 1, Norte), Estado(1, 1, Norte), Estado(2, 1, Norte)))
  }

  test("propia transformar: cambiar los giros deja los avances"){
    val f: Orden => Orden = {
    case Girar(_) => Girar(Derecha)
    case otra => otra
    }
    assert(transformar(Secuencia(List(Girar(Izquierda), Avanzar(1))), f) ==
      Secuencia(List(Girar(Derecha), Avanzar(1))))
  }

  test("propia transformar: llega hasta las básicas más hondas"){
    val f: Orden => Orden = {
      case Avanzar(n) => Avanzar(n+1)
      case otra => otra
    }
    assert(transformar(Repetir(2, Secuencia(List(Repetir(3, Avanzar(3))))), f) ==
      Repetir(2, Secuencia(List(Repetir(3, Avanzar(4))))))
  }

  test("propia transformar: no toca el número de veces de repetir"){
    val f: Orden => Orden = {
      case Avanzar(n) => Avanzar(-n)
      case otra => otra
    }
    assert(transformar(Repetir(7, Avanzar(2)), f) == Repetir(7, Avanzar(-2)))
  }

  // Punto 5 -------------------------------------------------------------------

  test("simplificar: dos avances seguidos se juntan") {
    assert(simplificar(Secuencia(List(Avanzar(2), Avanzar(3)))) == Avanzar(5))
  }

  test("simplificar: repetir una vez es la orden misma") {
    assert(simplificar(Repetir(1, Avanzar(4))) == Avanzar(4))
  }

  test("simplificar: avanzar cero desaparece de la secuencia") {
    assert(simplificar(Secuencia(List(Avanzar(0), Girar(Derecha)))) == Girar(Derecha))
  }

  test("simplificar: junta solo los avances contiguos") {
    assert(simplificar(Secuencia(List(Avanzar(1), Avanzar(2), Girar(Derecha), Avanzar(3)))) ==
      Secuencia(List(Avanzar(3), Girar(Derecha), Avanzar(3))))
  }

  test("simplificar: repetir cero veces no hace nada") {
    assert(simplificar(Repetir(0, Avanzar(9))) == Secuencia(Nil))
  }

  test("simplificar: dos avances que se cancelan desaparecen") {
    assert(simplificar(Secuencia(List(Avanzar(2), Avanzar(-2)))) == Secuencia(Nil))
  }

  test("simplificar: es idempotente") {
    val ordenes = List(cuadrado,
      Secuencia(List(Avanzar(1), Avanzar(2), Girar(Derecha), Avanzar(3))),
      Repetir(1, Secuencia(List(Avanzar(0), Avanzar(4)))),
      Secuencia(List(Secuencia(Nil), Repetir(0, Avanzar(3)))))
    assert(ordenes.forall(o => simplificar(simplificar(o)) == simplificar(o)))
  }

  test("simplificar: no cambia el resultado de ejecutar") {
    val estados = List(inicial, Estado(3, -2, Este), Estado(-1, 4, Sur))
    val ordenes = List(cuadrado,
      Secuencia(List(Avanzar(2), Avanzar(3))),
      Secuencia(List(Avanzar(1), Avanzar(2), Girar(Derecha), Avanzar(3))),
      Repetir(1, Avanzar(4)),
      Repetir(0, Avanzar(9)),
      Secuencia(List(Avanzar(0), Girar(Derecha))),
      Secuencia(List(Avanzar(2), Avanzar(-2))),
      Repetir(3, Secuencia(List(Repetir(1, Avanzar(2)), Girar(Izquierda)))))
    assert(ordenes.forall(o =>
      estados.forall(e => ejecutar(e, o) == ejecutar(e, simplificar(o)))))
  }

  test("simplificar: el anidamiento interior también se reduce") {
    assert(simplificar(Repetir(3, Repetir(1, Avanzar(2)))) == Repetir(3, Avanzar(2)))
  }
}
