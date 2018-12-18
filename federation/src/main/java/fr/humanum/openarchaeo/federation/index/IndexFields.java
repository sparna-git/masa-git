package fr.humanum.openarchaeo.federation.index;

public class IndexFields {

	/**
	 * L'URI de la source de cette entrée d'index
	 */
	public static final String SOURCE_FIELD = "source";
	
	/**
	 * L'index correspondant à cette entrée, concaténation du domain, property et range
	 */
	public static final String INDEX_FIELD = "index";
	
	/**
	 * L'IRI de l'entrée associée au label indexé. L'IRI n'est pas unique si elle a plusieurs label
	 */
	public static final String IRI_FIELD = "iri";
	
	/**
	 * Le libellé indexé, qui peut être un pref ou un alt
	 */
	public static final String LABEL_FIELD = "label";
	
	/**
	 * Le libellé d'affichage préféré dans le cas où le label est un synonyme
	 */
	public static final String PREF_LABEL_FIELD = "prefLabel";
	
	/**
	 * La langue du libellé, ou chaine vide s'il n'y a pas de langue
	 */
	public static final String LANG_FIELD = "lang";
	
	/**
	 * Concaténation de index+iri+label+lang (tel libellé dans telle langue de telle IRI dans tel index)
	 */
	public static final String PRIMARY_KEY_FIELD = "pk";	
	
}
