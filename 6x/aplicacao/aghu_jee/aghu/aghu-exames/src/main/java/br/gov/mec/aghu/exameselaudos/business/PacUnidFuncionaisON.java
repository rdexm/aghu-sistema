package br.gov.mec.aghu.exameselaudos.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelPacUnidFuncionaisDAO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelPacUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PacUnidFuncionaisON extends BaseBusiness {


@EJB
private PacUnidFuncionaisRN pacUnidFuncionaisRN;

private static final Log LOG = LogFactory.getLog(PacUnidFuncionaisON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@Inject
private AelPacUnidFuncionaisDAO aelPacUnidFuncionaisDAO;

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4518932407322174654L;
	
	
	private enum PacUnidFuncionaisONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_INFO_SOLICITACAO_NAO_ENCONTRADA;
	}
	
	public void inserir(AelPacUnidFuncionais elemento) throws ApplicationBusinessException {
		this.verificarNumeroSolicitacao(elemento);
		
		this.getPacUnidFuncionaisRN().inserir(elemento);
	}
	
	
	public void atualizar(AelPacUnidFuncionais elemento) throws ApplicationBusinessException {
		final AelPacUnidFuncionais oldElemento = 
			this.getAelPacUnidFuncionaisDAO().obterOriginal(elemento);
		
		if(CoreUtil.modificados(elemento.getItemSolicitacaoExames(), 
				oldElemento.getItemSolicitacaoExames())
				|| CoreUtil.modificados(elemento.getItemSolicitacaoExames()
						.getId().getSoeSeq(), oldElemento
						.getItemSolicitacaoExames().getId().getSoeSeq())
				|| CoreUtil.modificados(
						elemento.getItemSolicitacaoExames()
						.getId().getSeqp(), oldElemento
						.getItemSolicitacaoExames().getId().getSeqp())) {
			this.verificarNumeroSolicitacao(elemento);
		}
		
		this.getPacUnidFuncionaisRN().atualizar(elemento);
	}
	
	public void verificarNumeroSolicitacao(AelPacUnidFuncionais elemento) throws ApplicationBusinessException {
		
		if(elemento.getItemSolicitacaoExames() != null && elemento.getItemSolicitacaoExames().getId().getSoeSeq() != null) {
			AelItemSolicitacaoExames itemSolicitacaoExames = this.getAelItemSolicitacaoExameDAO()
				.pesquisarNumeroSolicitacaoExame(
						elemento.getAelProtocoloInternoUnids().getId().getUnidadeFuncional()
							.getSeq(), elemento.getUnfExecutaExames().getId().getEmaExaSigla(), 
						elemento.getUnfExecutaExames().getId().getEmaManSeq(), 
						elemento.getAelProtocoloInternoUnids().getId().getPacCodigo(), 
						elemento.getItemSolicitacaoExames().getId().getSoeSeq(),
						elemento.getItemSolicitacaoExames().getId().getSeqp());
			if(itemSolicitacaoExames == null) {
				throw new ApplicationBusinessException(PacUnidFuncionaisONExceptionCode.MENSAGEM_INFO_SOLICITACAO_NAO_ENCONTRADA);
			} else {
				//obter aelitemsolicexames
				elemento.setItemSolicitacaoExames(itemSolicitacaoExames);
			}
		} else {
			elemento.setItemSolicitacaoExames(null);
		}
	}
	
	
	
	public void excluir(AelPacUnidFuncionais elemento) throws ApplicationBusinessException {
		if(elemento.getItemSolicitacaoExames() != null
				&& elemento.getItemSolicitacaoExames().getId() != null
				&& elemento.getItemSolicitacaoExames().getId().getSoeSeq() == null) {
			elemento.setItemSolicitacaoExames(null);
		}
		this.getPacUnidFuncionaisRN().excluir(elemento);
	}
	
	
	/** GET **/
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected AelPacUnidFuncionaisDAO getAelPacUnidFuncionaisDAO() {
		return aelPacUnidFuncionaisDAO;
	}
	
	protected PacUnidFuncionaisRN getPacUnidFuncionaisRN() {
		return pacUnidFuncionaisRN;
	}
	
}
