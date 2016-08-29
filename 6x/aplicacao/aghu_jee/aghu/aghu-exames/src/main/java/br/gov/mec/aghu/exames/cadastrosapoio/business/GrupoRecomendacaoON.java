package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.dao.AelGrupoRecomendacaoDAO;
import br.gov.mec.aghu.exames.dao.VAelExameMatAnaliseDAO;
import br.gov.mec.aghu.model.AelGrupoRecomendacao;
import br.gov.mec.aghu.model.AelGrupoRecomendacaoExame;
import br.gov.mec.aghu.model.VAelExameMatAnalise;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class GrupoRecomendacaoON extends BaseBusiness {


@EJB
private GrupoRecomendacaoRN grupoRecomendacaoRN;

private static final Log LOG = LogFactory.getLog(GrupoRecomendacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private VAelExameMatAnaliseDAO vAelExameMatAnaliseDAO;

@Inject
private AelGrupoRecomendacaoDAO aelGrupoRecomendacaoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7901422232100447869L;

	public enum GrupoRecomendacaoONExceptionCode implements BusinessExceptionCode {
		ERROR_TAMANHO_FILTRO_MINIMO
		;
	}
	
	public void gravar(AelGrupoRecomendacao grupoRecomendacao, List<AelGrupoRecomendacaoExame> lista) throws BaseException {
		if (grupoRecomendacao.getSeq() == null) {
			this.getGrupoRecomendacaoRN().inserir(grupoRecomendacao, lista);
		} else {
			this.getGrupoRecomendacaoRN().atualizar(grupoRecomendacao, lista);
		}
	}
	
	public void remover(Integer seqExclusao) throws BaseException {
		aelGrupoRecomendacaoDAO.removerPorId(seqExclusao);
	}
	
	public List<AelGrupoRecomendacao> pesquisaGrupoRecomendacaoPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AelGrupoRecomendacao grupoRecomendacao) {
		return getAelGrupoRecomendacaoDAO().pesquisaGrupoRecomendacaoPaginada(firstResult, maxResult, orderProperty, asc, grupoRecomendacao);
	}

	public Long pesquisaGrupoRecomendacaoPaginadaCount(AelGrupoRecomendacao grupoRecomendacao) {
		return getAelGrupoRecomendacaoDAO().pesquisaGrupoRecomendacaoPaginadaCount(grupoRecomendacao);
	}
	
	public List<VAelExameMatAnalise> obterExameMaterialAnalise(String siglaDescViewExaMatAnalise) throws ApplicationBusinessException {

		List<VAelExameMatAnalise> lista = this.getVAelExameMatAnaliseDAO().buscarVAelExameMatAnalisePelaSigla(siglaDescViewExaMatAnalise, null);
		
		if (lista == null || lista.isEmpty()) {
			lista = this.getVAelExameMatAnaliseDAO().buscaVAelExameMatAnalisePorDescricao(siglaDescViewExaMatAnalise, null);			
		}
		
		return lista;
	}

    public Long obterExameMaterialAnaliseCount(String siglaDescViewExaMatAnalise) throws ApplicationBusinessException {
        return this.getVAelExameMatAnaliseDAO().buscarVAelExameMatAnaliseCount(siglaDescViewExaMatAnalise);
    }

	public List<VAelExameMatAnalise> buscaEXADEPVAelExameMatAnalisePelaSigla(String siglaDescViewExaMatAnalise) throws ApplicationBusinessException {
		if (StringUtils.isBlank(siglaDescViewExaMatAnalise) || (siglaDescViewExaMatAnalise!=null && siglaDescViewExaMatAnalise.trim().length()<=3)) {
			// Deve ser informado mais que três caracteres pra realizar a pesquisa.
			throw new ApplicationBusinessException(GrupoRecomendacaoONExceptionCode.ERROR_TAMANHO_FILTRO_MINIMO);
		}
		List<VAelExameMatAnalise> lista = this.getVAelExameMatAnaliseDAO().buscarVAelExameMatAnalisePelaSigla(siglaDescViewExaMatAnalise, DominioSimNao.S.name());
 		
		if (lista == null || lista.isEmpty()) {
			lista = this.getVAelExameMatAnaliseDAO().buscaVAelExameMatAnalisePorDescricao(siglaDescViewExaMatAnalise, DominioSimNao.S.name());			
		}
		
		return lista;
	}

	protected VAelExameMatAnaliseDAO getVAelExameMatAnaliseDAO() {
		return vAelExameMatAnaliseDAO;
	}
	
	
	protected AelGrupoRecomendacaoDAO getAelGrupoRecomendacaoDAO() {
		return aelGrupoRecomendacaoDAO;
	}
	
	protected GrupoRecomendacaoRN getGrupoRecomendacaoRN() {
		return grupoRecomendacaoRN;
	}

	

}
