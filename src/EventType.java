import java.util.concurrent.Delayed;

public enum EventType {

	/** Vypis error kdyz se nepodari task */
	ErrorTask,
	
	/** Doplneni kosu do skladu */
	StorageRefill,
	
	/** Ve skladu se velbloud zacina pripravovat na cestu */
	CamelDeparting,
	
	/** Velbloud dosel do oazy/skladu, kde se napije */
	CamelDrinks,
	
	/** Velbloud prochazi oazou, ve ktere nepije */
	CamelTransit,
	
	/** Velbloud se dostavil do spravne oazy a zacal vykladat kose */
	CamelFinished,
	
	/** Velbloud se uspesne vratil domu do skladu */
	CamelHome,
	
	/** Prichod noveho pozadacku */
	NewTask,

	/** Event kdyz sklad pri prochodu pozadavku nema dostatek kosu
	 * vytvori se tento dealyed task a vykona se a task se vykona kdyz sklad
	 * ma dostatek kosu */
	DelayedTask
}
