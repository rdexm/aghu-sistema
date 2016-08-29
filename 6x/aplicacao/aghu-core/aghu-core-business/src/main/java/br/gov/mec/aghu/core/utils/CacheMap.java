package br.gov.mec.aghu.core.utils;

import java.util.LinkedHashMap;


public class CacheMap<T, U> extends LinkedHashMap<T, U> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4379430999423481426L;
	protected int maxsize;

	public CacheMap(int maxsize) {
		super(maxsize, 0.75f, true);
		this.maxsize = maxsize;
	}

	/**
	 * Faz com que o elemento mais velho seja removido no caso do tamanho ser
	 * atingido
	 */
	protected boolean removeEldestEntry() {
		return (this.size() > maxsize);
	}

}
