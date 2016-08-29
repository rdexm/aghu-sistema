package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.faturamento.dao.FatTipoCaractItensDAO;


@RequestScoped
public class FatTipoCaractItemSeqCache implements Serializable {
	
	@Inject
	private FatTipoCaractItensDAO fatTipoCaractItensDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5477738508875717703L;
	private ConcurrentHashMap<DominioFatTipoCaractItem, List<Integer>> seqs = new ConcurrentHashMap<DominioFatTipoCaractItem, List<Integer>>();

	public List<Integer> listarSeqsPorCaracteristica(DominioFatTipoCaractItem caracteristica) {
		List<Integer> resultList = this.seqs.get(caracteristica);

		if (resultList == null) {
			resultList = this.getFatTipoCaractItensDAO().listarSeqsPorCaracteristica(caracteristica);
			this.seqs.put(caracteristica, resultList);
		}

		return resultList;
	}

	protected FatTipoCaractItensDAO getFatTipoCaractItensDAO() {
		return fatTipoCaractItensDAO;
	}

}
