
public enum EventType {

	/** Vypis error kdyz se nepodari task */
	ErrorTask,
	
	/** Doplneni kosu do skladu */
	StorageRefill,
	
	/** Prichod noveho pozadacku */
	NewTask,
	
	/** Ve skladu se velbloud zacina pripravovat na cestu */
	CamelDeparting,
	
	/** Velbloud se dostavil do spravne oazy a zacal vykladat kose */
	CamelFinished,
	
	/** Velbloud dosel do oazy/skladu, kde se napije */
	CamelDrinks,
	
	/** Velbloud prochazi oazou, ve ktere nepije */
	CamelTransit,
	
	/** Velbloud se uspesne vratil domu do skladu */
	CamelHome
}
