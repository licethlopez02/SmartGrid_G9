# Resumen ejecutivo - Smart Grid

Pedimos: aprobar la continuidad del sistema con su diseño actual de tres módulos independientes y destinar el siguiente trabajo a tres mejoras de fiabilidad y seguridad.

**Por qué:** las empresas de energía necesitan saber cuánto consume cada hogar, detectar consumos anormales y avisar a la persona a tiempo, y cobrar cada mes solo cuando los datos del período están completos. Hacerlo a mano o con un único programa grande es lento y propenso a errores.

**Qué proponemos:** un sistema dividido en tres módulos que trabajan por separado: uno para las cuentas de las personas, uno que recibe las lecturas de los medidores y detecta consumos anormales, y uno que genera las facturas. Cada módulo guarda sus propios datos y se avisan entre sí cuando ocurre algo importante. Cuando se detecta un consumo anormal, la persona recibe un correo y lo ve en su pantalla.

**Qué ganamos / qué cuesta:**
- Ganamos: cada módulo se puede desarrollar y probar sin tocar los otros; las facturas no se generan con datos incompletos; el aviso al usuario no frena la recepción de lecturas.
- Cuesta: más piezas que mantener funcionando (tres módulos, un coordinador de mensajes y tres bases de datos) y más trabajo para un equipo de dos personas que un solo programa.

**Principales riesgos y cómo los controlamos:**
1. Un correo de alerta puede no llegar o llegar repetido: si falla el envío no se reintenta, y un mensaje reentregado puede duplicarse. La anomalía igual queda visible en la pantalla del usuario; agregaremos reintentos y control de duplicados si el proyecto sigue.
2. La comunicación entre módulos no tiene un control de acceso propio. Hoy corre solo en entorno de pruebas; se debe agregar antes de usarlo con datos reales.
3. Las contraseñas y claves de prueba vienen escritas como valores por defecto en la configuración, incluida la de la cuenta de correo que envía las alertas. Se pueden sustituir con variables protegidas; antes de cualquier despliegue se quitarán esos valores y la del correo se revoca al terminar la entrega.

**Siguiente paso:** el sistema ya se validó de punta a punta el 2026-11-15 (lecturas simuladas, aviso por correo recibido). Sigue que el equipo aplique las tres mejoras anteriores.
