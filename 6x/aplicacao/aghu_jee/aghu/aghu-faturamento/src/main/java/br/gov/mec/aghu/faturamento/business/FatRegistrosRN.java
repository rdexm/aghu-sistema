package br.gov.mec.aghu.faturamento.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.faturamento.dao.FatRegistrosDAO;
import br.gov.mec.aghu.model.FatRegistro;


/**
 * @author eschweigert
 */
@Stateless
public class FatRegistrosRN extends BaseBusiness {

	private static final long serialVersionUID = -3472390557979534732L;
	private static final Log LOG = LogFactory.getLog(FatRegistrosRN.class);
	
	@Inject
	private FatRegistrosDAO fatRegsitrosDAO;
	
	public void persistirFatRegistros(final FatRegistro registro){
		FatRegistro aux = this.fatRegsitrosDAO.obterPorChavePrimaria(registro.getCodigo());
		if(aux == null || aux.getCodigo() == null){
			inserir(registro);
		} else {
			alterar(registro);
		}
	}

	public void excluir(final FatRegistro registro) {
		this.fatRegsitrosDAO.remover(registro);
	}
	
	private void alterar(final FatRegistro servico) {
		this.fatRegsitrosDAO.merge(servico);
	}
	
	private void inserir(final FatRegistro regsitro){
		this.fatRegsitrosDAO.persistir(regsitro);
	}


	protected FatRegistrosDAO getFatRegistrosDAO() {
		return this.fatRegsitrosDAO;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
		
}
