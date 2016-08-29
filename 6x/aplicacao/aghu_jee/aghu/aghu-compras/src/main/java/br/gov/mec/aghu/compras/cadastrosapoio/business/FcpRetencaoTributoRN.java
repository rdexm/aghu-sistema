package br.gov.mec.aghu.compras.cadastrosapoio.business;


import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.compras.dao.FcpRetencaoTributoDAO;
import br.gov.mec.aghu.compras.dao.FcpTributoDAO;
import br.gov.mec.aghu.model.FcpRetencaoTributo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class FcpRetencaoTributoRN extends BaseBusiness {

	private static final long serialVersionUID = 5363078226437771845L;
	
	@Inject
	private FcpRetencaoTributoDAO fcpRetencaoTributoDAO;
	
	@Inject
	private FcpTributoDAO fcpTributoDAO;

	@Override
	protected Log getLogger() {
		return null;
	}
	
	public List obterListaCodigoRecolhimento(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FcpRetencaoTributo fcpRetencaoTributo) {
		return this.fcpRetencaoTributoDAO.obterListaCodigoRecolhimento(firstResult, maxResult, orderProperty, asc, fcpRetencaoTributo);
	}
	
	public Long obterCountCodigoRecolhimento(FcpRetencaoTributo fcpRetencaoTributo) {
		return this.fcpRetencaoTributoDAO.obterCountCodigoRecolhimento(fcpRetencaoTributo);
	}

	public void inserirCodigoRecolhimento(FcpRetencaoTributo fcpRetencaoTributo) {
		this.fcpRetencaoTributoDAO.persistir(fcpRetencaoTributo);
	}

	public void atualizarCodigoRecolhimento(FcpRetencaoTributo fcpRetencaoTributo) {
		this.fcpRetencaoTributoDAO.atualizar(fcpRetencaoTributo);
	}

	public void excluirCodigoRecolhimento(Integer codigoRecolhimento) throws ApplicationBusinessException {
		
		if (fcpTributoDAO.obterRetencaoAliquotaPorFriCodigo(codigoRecolhimento)) {
			
			this.fcpRetencaoTributoDAO.removerPorId(codigoRecolhimento);
		
		} else {
			
			throw new ApplicationBusinessException("Código de Recolhimento está sendo utilizado no sistema e não pode ser excluído.", Severity.ERROR, codigoRecolhimento);
		}
		
	}
	
	public List<FcpRetencaoTributo> pesquisarRecolhimentoPorCodigoOuDescricao(Object parametro)  {
		
		if( (!CoreUtil.isNumeroInteger(parametro)) && (parametro instanceof String) ){
			String descricao = (String) parametro;
			if(descricao != null && (descricao.trim().toUpperCase().equals("F") || descricao.trim().toUpperCase().equals("FEDERAL"))){
				descricao = "F";
				parametro = descricao;
			}
			if(descricao != null && (descricao.trim().toUpperCase().equals("M") || descricao.trim().toUpperCase().equals("MUNICIPAL"))){
				descricao = "M";
				parametro = descricao;
			}
			if(descricao != null && (descricao.trim().toUpperCase().equals("P") || descricao.trim().toUpperCase().equals("PREVIDENCIARIO") ||
					descricao.trim().toUpperCase().equals("PREVIDENCIÁRIO"))){
				descricao = "P";
				parametro = descricao;
			}
		}
		
		return this.fcpRetencaoTributoDAO.pesquisarRecolhimentoPorCodigoOuDescricao(parametro);
	}
	
	public Long pesquisarRecolhimentoPorCodigoOuDescricaoCount(final String parametro) {
		  return this.fcpRetencaoTributoDAO.obterTributoPesquisaPorCodigoOuDescricaoCount(parametro);
	}
	
	/**
	 * @return the fcpRetencaoTributoDAO
	 */
	public FcpRetencaoTributoDAO getFcpRetencaoTributoDAO() {
		return fcpRetencaoTributoDAO;
	}

	/**
	 * @param fcpRetencaoTributoDAO the fcpRetencaoTributoDAO to set
	 */
	public void setFcpRetencaoTributoDAO(FcpRetencaoTributoDAO fcpRetencaoTributoDAO) {
		this.fcpRetencaoTributoDAO = fcpRetencaoTributoDAO;
	}

}