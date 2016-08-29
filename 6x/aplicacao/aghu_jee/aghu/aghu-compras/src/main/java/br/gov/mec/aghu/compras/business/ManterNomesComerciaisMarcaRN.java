package br.gov.mec.aghu.compras.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoNomeComercialDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoNomeComercial;
import br.gov.mec.aghu.model.ScoNomeComercialId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterNomesComerciaisMarcaRN extends BaseBusiness {
	
	private static final long serialVersionUID = 4855353816484169453L;
	
	@Inject
	private ScoNomeComercialDAO scoNomeComercialDAO;
	
	protected ScoNomeComercialDAO getScoNomeComercialDAO() {
		return scoNomeComercialDAO;
	}

	protected void setScoNomeComercialDAO(ScoNomeComercialDAO scoNomeComercialDAO) {
		this.scoNomeComercialDAO = scoNomeComercialDAO;
	}

	public enum ManterNomesComerciaisMarcaRNExceptionCode implements BusinessExceptionCode {
		MESSAGE_NOMES_COMERCIAS_MARCA_M6;
	}

	public List<ScoNomeComercial> buscaMarcasComeriais(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, ScoMarcaComercial marcaComercial) {
		return getScoNomeComercialDAO().buscaMarcasComeriais(firstResult, maxResult, orderProperty, asc, marcaComercial);
	}

	public Long buscaMarcasComeriaisCount(ScoMarcaComercial marcaComercial) {
		return getScoNomeComercialDAO().buscaMarcasComeriaisCount(marcaComercial);
	}
	
	public void cadastrarNomeComercialDaMarca(ScoMarcaComercial marcaComercial, String nome, boolean active) throws BaseException {
		ScoNomeComercial entity = new ScoNomeComercial(montaIdNomeComercial(marcaComercial));
		entity.setMarcaComercial(marcaComercial);
		entity.setNome(nome);
		entity.setSituacao(getDomainSituacao(active));
		if(isValidoParaCadastrar(entity, entity.getNome())){
			getScoNomeComercialDAO().persistir(entity);
		}else{
			throw new ApplicationBusinessException(ManterNomesComerciaisMarcaRNExceptionCode.MESSAGE_NOMES_COMERCIAS_MARCA_M6);
		}
	}

	private boolean isValidoParaCadastrar(ScoNomeComercial entity, String nome) {
		return getScoNomeComercialDAO().isValidoParaCadastrar(entity, nome);		
	}

	private DominioSituacao getDomainSituacao(boolean active) {
		if(active){
			return DominioSituacao.A;
		}else{
			return DominioSituacao.I;
		}
	}

	public void alterarNovoNomeComercialDaMarca(ScoNomeComercial nomeSobEdicao, String nome, boolean active) throws ApplicationBusinessException {
		ScoNomeComercial nomeComercialOriginal = this.getScoNomeComercialDAO().obterOriginal(nomeSobEdicao);
		if (buscaSituacao(active).equals(nomeComercialOriginal.getSituacao())){
			if(!isValidoParaCadastrar(nomeSobEdicao, nome)){
				throw new ApplicationBusinessException(ManterNomesComerciaisMarcaRNExceptionCode.MESSAGE_NOMES_COMERCIAS_MARCA_M6);
			}
		}
		ScoNomeComercial nomeComercialUpdate = this.getScoNomeComercialDAO().obterPorChavePrimaria(nomeSobEdicao.getId());
		nomeComercialUpdate.setNome(nome);
		nomeComercialUpdate.setSituacao(buscaSituacao(active));
		getScoNomeComercialDAO().persistir(nomeComercialUpdate);
		
	}
	
	private ScoNomeComercialId montaIdNomeComercial(ScoMarcaComercial marcaComercial) {
		return new ScoNomeComercialId(marcaComercial.getCodigo(), getNextNumero(marcaComercial));
	}

	private DominioSituacao buscaSituacao(boolean editionActive) {
		if(editionActive){
			return DominioSituacao.A;
		}else{
			return DominioSituacao.I;
		}
	}
	
	private Integer getNextNumero(ScoMarcaComercial marcaComercial) {
		return getScoNomeComercialDAO().getNextNumero(marcaComercial.getCodigo());
	}
	
	private static final Log LOG = LogFactory.getLog(ManterNomesComerciaisMarcaRN.class);
	
	@Override
	protected Log getLogger() {
		
		return LOG;
	}


}
